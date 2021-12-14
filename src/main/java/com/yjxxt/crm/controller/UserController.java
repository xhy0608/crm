package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     *系统设置--用户管理模块--前后端连接点1
     * * @return
     */
    @RequestMapping("index")
    public String index() {
        return "User/user";
    }


    //跳到前端页面：修改基本资料
    @RequestMapping("toSettingPage")
    public String setting(HttpServletRequest req){
        //获取用户的ID
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //调用方法
        User user = userService.selectByPrimaryKey(userId);
        //存储
        req.setAttribute("user", user);
        //转发
        return "user/setting";
    }

    @RequestMapping("toPasswordPage")
    public String updatePwd(){
        return "user/password";
    }

    @PostMapping ("login")
    @ResponseBody
    public ResultInfo say(User user){
        ResultInfo resultInfo = new ResultInfo();
            //调用Service层的登录方法，得到返回的用户对象,(由于可能出现错误，所以捕获异常)
            UserModel userModel = userService.userLogin(user.getUserName(), user.getUserPwd());
            //将返回的UserModel对象设置到 ResultInfo 对象中
            resultInfo.setResult(userModel);

        return resultInfo;
    }

    @PostMapping("updatePwd")
    @ResponseBody
    public ResultInfo updatePwd(HttpServletRequest request, String oldPassword, String newPassword, String confirmPwd){
        ResultInfo resultInfo = new ResultInfo();
        //获取cookie中的userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //修改密码操作
            userService.updateUserPwd(userId,oldPassword,newPassword,confirmPwd);


        return resultInfo;
    }

    @RequestMapping("setting")
    @ResponseBody
    public ResultInfo sayUpdate(User user) {
        ResultInfo resultInfo = new ResultInfo();
        //修改信息
        userService.updateByPrimaryKeySelective(user);
        //返回目标数据对象
        return resultInfo;
    }

    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String,Object>> findSales() {
        //修改信息
        List<Map<String, Object>> maps = userService.querySales();
        //返回目标数据对象
        return maps;
    }

    /**
     * 返回框架列表
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    @RequiredPermission(code = "6010")
    public Map<String,Object> queryUserByParams(UserQuery userQuery) {
        return userService.queryUserByParams(userQuery);
    }

    /**
     * 添加、修改用户功能（点击添加用户按钮-->弹出页面框）
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id,Model model){
        System.out.println("addOrUpdateUserPage");
        if (id != null){
            model.addAttribute("user",userService.selectByPrimaryKey(id));
        }
        System.out.println("执行了if（id != null）");
        return "User/add_update";
    }

    /**
     * 添加用户
     * @param request
     * @param user
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveUser(HttpServletRequest request,User user){
        userService.saveUser(user);
        return success("用户添加成功！！");
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户更新成功！！");
    }

    /**
     * 删除用户
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteUserById(ids);
        return success("用户删除成功！！");
    }






}


