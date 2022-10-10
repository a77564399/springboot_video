package com.project.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.project.bilibili.dao.UserDao;
import com.project.bilibili.domain.PageResult;
import com.project.bilibili.domain.RefreshTokenDetail;
import com.project.bilibili.domain.User;
import com.project.bilibili.domain.UserInfo;
import com.project.bilibili.domain.auth.UserAuthorities;
import com.project.bilibili.domain.constant.UserConstant;
import com.project.bilibili.exception.ConditionException;
import com.project.bilibili.service.utils.MD5Util;
import com.project.bilibili.service.utils.RSAUtil;
import com.project.bilibili.service.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.provider.MD5;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;
    /**
     * 添加用户，把每一项信息set上，然后传过去add即可
     * @param user
     */
    public void addUser(User user) {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone))
        {
            throw new ConditionException("手机号不能为空！");
        }
        User dbUser = this.getUserByPhone(phone);
        if(dbUser != null)
        {
            throw new ConditionException("该手机号已被注册");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword;
//        前端传过来的是经过RSA加密的password，解密之后使用MD5加密，放入数据库
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);

//      添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
//     调用写死的值可以方便的一处修改-全部修改
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_UNKNOW);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);

//      给当前用户添加默认角色
        userAuthService.addDefaultUserRole(user.getId());

    }

    /**
     * 通过手机号获取用户
     * @param phone
     * @return
     */
    public User getUserByPhone(String phone)
    {
        return userDao.getUserByPhone(phone);
    }

    /**
     * 登录：主要是核对用户密码需要RSA解密、MD5加密，然后返回token
     * @param user
     * @return
     * @throws Exception
     */
    public String login(User user) throws Exception {
        String phone = user.getPhone();
//      判断手机号
        if(StringUtils.isNullOrEmpty(phone))
        {
            throw new ConditionException("手机号不能为空！");
        }
//        获取用户
        User dbUser = this.getUserByPhone(phone);
        if(dbUser==null)
        {
            throw new ConditionException("当前用户不存在！");
        }
//        获取用户密码
        String password = user.getPassword();
//        获取前端密码并解密
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
//        盐值MD5加密并验证
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if(!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误！");
        }

        return TokenUtil.generateToken(dbUser.getId());
    }

    /**
     * 根据用户ID获取用户信息
     * @param userId
     * @return
     */
    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoById(userId);
//      将user与userInfo进行整合，将info整合到user中
        user.setUserInfo(userInfo);
        return user;
    }

    /**
     * 更新用户信息
     * @param userInfo
     */
    public void updateUserInfos(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);
    }

    /**
     * 更新用户
     * @param user
     * @throws Exception
     */
    public void updateUsers(User user) throws Exception {
        Long id = user.getId();
        User dbUser = userDao.getUserById(id);
        if(dbUser==null)
        {
            throw new ConditionException("用户不存在!");
        }
        if(!StringUtils.isNullOrEmpty(user.getPassword()))
        {
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        user.setUpdateTime(new Date());
        userDao.updateUsers(user);
    }

    /**
     * 通过id获取用户（主要是查询关注用户使用）
     * @param followingId
     * @return
     */
    public User getUserById(Long followingId) {
        return userDao.getUserById(followingId);
    }

    /**
     * 通过id获取用户信息
     * @param followingIds
     * @return
     */
    public List<UserInfo> getUserInfoByUserIds(Set<Long> followingIds) {
        return userDao.getUserInfoByUserIds(followingIds);
    }

    /**
     *分页查询用户信息：传入页码和长度（昵称模糊查询可有可无），返回分页的用户信息
     * @param jsonObject
     * @return
     */
    public PageResult<UserInfo> pageListUserInfos(JSONObject jsonObject) {
        Integer no = jsonObject.getInteger("no");
        Integer size = jsonObject.getInteger("size");
//      计算要查询数据的起始和终止位置:no页（传入的no是从1开始的）
        jsonObject.put("start",(no-1)*size);
//      要查询的条数
        jsonObject.put("limit",size);
//      作用：根据输入的条件判断符合条件的数据有多少条
        Integer total = userDao.getCountUserInfos(jsonObject);
        List<UserInfo> userInfoList = new ArrayList<>();
        if(total>0)
        {
//           查询分页信息
            userInfoList = userDao.pageListUserInfos(jsonObject);
        }
        return new PageResult<>(total,userInfoList);
    }

    @Transactional
    public Map<String, Object> loginForDts(User user) throws Exception {
        String phone = user.getPhone();
//      判断手机号
        if(StringUtils.isNullOrEmpty(phone))
        {
            throw new ConditionException("手机号不能为空！");
        }
//        获取用户
        User dbUser = this.getUserByPhone(phone);
        if(dbUser==null)
        {
            throw new ConditionException("当前用户不存在！");
        }
//        获取用户密码
        String password = user.getPassword();
//        获取前端密码并解密
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
//        盐值MD5加密并验证
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if(!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误！");
        }

        Long userId = dbUser.getId();
        String accessToken = TokenUtil.generateToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
//      将refreshToken和userId存入数据库，方便后续用户想要延迟刷新，可以在数据库中查找相关联的refreshToken，如果存在就可以刷新，没找到就需要重新登录
//      带userId是为了再次确认，此token是和userId相关联的
        userDao.deleteRefreshToken(refreshToken,userId);
        userDao.addRefreshToken(refreshToken,userId,new Date());
        Map<String,Object>  result = new HashMap<>();
        result.put("accessToken",accessToken);
        result.put("refreshToken",refreshToken);
        return result;
    }

    /**
     * 用户退出登录
     * @param refreshToken
     * @param userId
     */
    public void logout(String refreshToken,Long userId) {
        userDao.deleteRefreshToken(refreshToken,userId);
    }

    /**
     * 刷新用户token
     */
    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshTokenDetail(refreshToken);
//      刷新token不存在，token过期，提示与之前的token过期保持一致
        if(refreshTokenDetail==null)
        {
            throw new ConditionException("555","token过期！");
        }
//      刷新token存在，那么就生成一个新的用户token并返回
        else {
            Long userId = refreshTokenDetail.getUserId();
            return TokenUtil.generateToken(userId);
        }
    }

    public List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.batchGetUserInfoByUserIds(userIdList);
    }
}
