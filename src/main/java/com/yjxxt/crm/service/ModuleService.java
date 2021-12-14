package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 查询所有的资源信息
      * @return
     */
    public List<TreeDto> findModules(){
        return  moduleMapper.selectModules();
    }

    /**
     * 角色资源回显
     * @param roleId
     * @return
     */
    public List<TreeDto> findModulesByRoleId(Integer roleId){
        //获取所有资源信息
        List<TreeDto> tlist = moduleMapper.selectModules();
        //获取当前角色的拥有的资源信息
        List<Integer> roleHasModules=permissionMapper.selectModelByRoleId(roleId);
        //遍历
        for (TreeDto treeDto: tlist) {
            /*若当前角色的资源信息（roleHasModules）包含在（TreeDto）中，就选中前面的勾选框*/
            if(roleHasModules.contains(treeDto.getId())){
                treeDto.setChecked(true);
            }
        }
        //判断比对，checked=true;
        return tlist;
    }


    public Map<String, Object> queryModules() {
        //准备数据
        Map<String,Object> result = new HashMap<String,Object>();
        //查询所有资源
        List<Module> mlist =moduleMapper.selectAllModules();
        //转杯数据项
        result.put("count",mlist.size());
        result.put("data",mlist);
        result.put("code",0);
        result.put("msg","success");
        //返回目标map
        return result;
    }
}
