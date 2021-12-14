package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.Md5Util;
import com.yjxxt.crm.utils.PhoneUtil;
import com.yjxxt.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    public UserModel userLogin(String userName,String userPwd){
        //验证参数
        checkUserLoginParams(userName,userPwd);
        //用户是否存在
        User temp = userMapper.selectUserByName(userName);
        AssertUtil.isTrue(null == temp,"用户不存在");
        //用户的密码是否正确
        checkUserPwd(userPwd,temp.getUserPwd());
        //构建返回目标对象
        return buildUserInfo(temp);
    }

    /**
     * 构建返回目标对象
     * @param user
     * @return
     */
    private UserModel buildUserInfo(User user) {
        //实例化对象
        UserModel userModel = new UserModel();
        //设置用户信息（将 userId 加密）
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;

    }

    /**
     * 验证用户登录参数
     * @param userName
     * @param userPwd
     */
    private void checkUserLoginParams(String userName, String userPwd) {
        //用户非空
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        //密码非空
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空");
    }

    /**
     * 验证登录密码
     * @param userPwd：前台传递的密码
     * @param userPwd1：数据库中查询到的密码
     */
    private void checkUserPwd(String userPwd, String userPwd1) {
        //对密码进行加密
        userPwd = Md5Util.encode(userPwd);
        //将加密后的密码和数据库中的该用户密码进行比对
        AssertUtil.isTrue(!userPwd.equals(userPwd1),"用户密码不正确");

    }

    public void updateUserPwd(Integer userId, String oldPassword, String newPassword, String confirmPassword){
        //通过userId获取用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        //参数校验
        checkPasswordParams(user,oldPassword,newPassword,confirmPassword);
        //设置用户新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        // 执行更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"用户密码更新失败");

    }

    /**
     * 修改密码的验证
     */
    private void checkPasswordParams(User user, String oldPassword, String newPassword, String confirmPassword) {
        AssertUtil.isTrue(null == user,"用户不存在");
        //原始密码非空
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原始密码");
        //原始密码是否正确
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))),"原始密码不正确！");
        //新密码非空
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码！");
        //新密码和原始密码不能相同
        AssertUtil.isTrue(newPassword.equals(oldPassword),"新密码不能与原始密码相同！");
        //确认密码非空
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"确认密码不能为空！");
        //确认密码和新密码保持一致
        AssertUtil.isTrue(!confirmPassword.equals(newPassword),"新密码和原始密码保持一致！");
    }

    public List<Map<String,Object>> querySales(){
        return userMapper.selectSales();
    }

    /**
     * 用户模块的列表查询
     * @param query
     * @return
     */
    public Map<String,Object> queryUserByParams(UserQuery query){
        //实例化map
        Map<String,Object> map = new HashMap<>();
        //分页
        PageHelper.startPage(query.getPage(),query.getLimit());
        //开始分页
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectByParams(query));
        //
        map.put("code",0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加用户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user){
        //校验参数
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        //设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        //执行添加，判断结果
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"用户添加失败");
        System.out.println(user.getId()+">>"+user.getRoleIds());
        //关联用户角色
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 联用户角色的方法
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //准备集合存储对象
        List<UserRole> urlist=new ArrayList<UserRole>();
        //userId,roleId;
        AssertUtil.isTrue(StringUtils.isBlank(roleIds),"请选择角色信息");
        //统计当前用户有多少个角色
        int count=userRoleMapper.countUserRoleNum(userId);
        //删除当前用户的角色
        if(count>0){
            AssertUtil.isTrue( userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色删除失败");
        }
        //删除原来的角色
        String[] RoleStrId = roleIds.split(",");
        //遍历
        for (String rid:RoleStrId) {
            //准备对象
            UserRole userRole=new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(Integer.parseInt(rid));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            //存放到集合
            urlist.add(userRole);
        }
        //批量添加
        AssertUtil.isTrue(userRoleMapper.insertBatch(urlist)!=urlist.size(),"用户角色分配失败");
    }


    /**
     * 添加用户（参数检验）
     * @param userName
     * @param email
     * @param phone
     */
    private void checkParams(String userName, String email, String phone) {
        //用户名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空！！！");
        //用户已存在
        User temp = userMapper.selectUserByName(userName);
        AssertUtil.isTrue(null != temp,"用户名已存在！！！");
        //邮箱不能为空
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
        //手机号不能为空
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空");
        //手机号格式不正确
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号格式不正确");
    }

    /**
     * 更新用户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        //在数据库中查询用户（通过id)是否存在
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(null == temp,"待更新记录不存在！！");
        //参数检验（用户名、邮箱、手机号）
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        //设置默认值（更新时间）
        user.setUpdateDate(new Date());
        //判断用户更新是否成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"用户更新失败！！");
        //
        relationUserRole(user.getId(),user.getRoleIds());
    }


    /**
     * 删除用户
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserById(Integer[] ids){
        //判断是否选择待删除的用户记录
        AssertUtil.isTrue(null == ids || ids.length == 0,"请选择待删除的用记录！！");
        //判断要删除的用户记录是否成功
        AssertUtil.isTrue(deleteBatch(ids) != ids.length,"用户记录删除失败！！");

    }
}
