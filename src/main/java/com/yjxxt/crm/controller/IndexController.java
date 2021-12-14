package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.service.PermissionService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController{

    @Resource
    private UserService userService;

    @Resource
    private PermissionService permissionService;

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }

    @RequestMapping("main")
    public String main(HttpServletRequest req){
        //通过工具类，从cookie中获取userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //调用对应Service层的方法，通过userId主键查询用户对象
        User user = userService.selectByPrimaryKey(userId);
        //将用户对象设置到request作用域中
        req.setAttribute("user",user);
        //将用户的权限码存储到session
        List<String> permissions = permissionService.queryUserHasRolesHasPermissions(userId);
        req.getSession().setAttribute("permissions",permissions);
        return "main";
    }
}
