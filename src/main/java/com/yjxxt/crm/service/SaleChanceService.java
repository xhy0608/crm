package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.mapper.SaleChanceMapper;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 营销机会数据查询（多条件查询）
     * @param query
     * @return
     */
    public Map<String, Object> querySaleChanceByParams (SaleChanceQuery query) {
        //实例化map
        Map<String, Object> map = new HashMap<>();
        //分页
        PageHelper.startPage(query.getPage(), query.getLimit());
        //开始分页
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(query));
        //准备数据
        map.put("code",0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 营销机会数据添加
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSaleChance(SaleChance saleChance) {
        // 1.参数校验
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        // 2.设置相关参数默认值
        if(StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }
        //已经分配
        if(StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        //设定默认值 state,devResult(0--未开发，1-开发中，2-开发成功了，3,-开发失败)
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        saleChance.setIsValid(1);
        // 3.执行添加 判断结果
        AssertUtil.isTrue(insertSelective(saleChance) < 1, "营销机会数据添加失败！");
    }

    /**
     * 基本参数校验
     * @param customerName:客户名称
     * @param linkMan:联系人
     * @param linkPhone:联系电话
     */
    private void checkParams(String customerName, String linkMan, String linkPhone)
    {
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "请输入客户名！");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "请输入联系人！");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "请输入手机号！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号格式不正确！");
    }

    /**
     * 营销机会数据修改
     * @param saleChance：营销机会
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeSaleChance (SaleChance saleChance) {
        // 1.参数校验
        // 通过id查询记录
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        // 判断是否为空
        AssertUtil.isTrue(null == temp, "待更新记录不存在！");
        // 校验基础参数
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        // 2. 设置相关参数值
        //原来  未分配 state,devResult
        if(StringUtils.isBlank(temp.getAssignMan())&& StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        //已经分配
        if(StringUtils.isNotBlank(temp.getAssignMan())&& StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //设定默认值
        saleChance.setUpdateDate(new Date());
        // 3.执行更新 判断结果
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1, "营销机会数据更新失败！");
    }

    /**
     * 批量删除营销机会数据
     * @param ids：批量删除数据的条数
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeSaleChanceIds(Integer[] ids){
        //验证
        AssertUtil.isTrue(ids==null || ids.length==0,"请选择要删除的数据");
        //删除是否成功
        AssertUtil.isTrue(deleteBatch(ids)!= ids.length,"批量删除失败了");
    }


}
