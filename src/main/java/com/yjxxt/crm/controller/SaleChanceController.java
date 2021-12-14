package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private UserService userService;

    /**
     * 多条件分页查询营销机会
     * @param query
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> querySaleChanceByParams (SaleChanceQuery query) {
        //调用方法获取数据
        Map<String, Object> map = saleChanceService.querySaleChanceByParams(query);
        //map --> json
        return map;
    }

    @RequestMapping("index")
    public String index () {
        return "saleChance/sale_chance";
    }

    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdate (Integer id, Model model) {
        if (id != null){
            //查询用户信息
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //存储
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 添加营销机会数据
     * @param request
     * @param saleChance
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveSaleChance(HttpServletRequest request, SaleChance saleChance){
        // 获取用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 获取用户的真实姓名
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        // 设置营销机会的创建人
        saleChance.setCreateMan(trueName);
        // 添加营销机会的数据
        saleChanceService.saveSaleChance(saleChance);
        //返回目标对象
        return success("营销机会数据添加成功！");
    }

    /**
     * 修改（编辑）营销机会数据
     * @param saleChance
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance){
        //添加操作
        saleChanceService.changeSaleChance(saleChance);
        //返回目标对象
        return success("修改成功了");
    }

    /**
     * 删除营销机会数据
     * @param ids
     * @return
     */
    @RequestMapping("dels")
    @ResponseBody
    public ResultInfo deletes(Integer[] ids){
        //添加操作
        saleChanceService.removeSaleChanceIds(ids);
        //返回目标对象
        return success("删除成功了");
    }
}

