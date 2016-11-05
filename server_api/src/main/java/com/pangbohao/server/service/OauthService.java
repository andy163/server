package com.pangbohao.server.service;

import com.pangbohao.server.cache.TokenCache;
import com.pangbohao.server.db.BeanProvider;
import com.pangbohao.server.db.bean.AuthUserBean;
import com.pangbohao.server.db.bean.RedisRegistrionBean;
import com.pangbohao.server.db.model.AuthUser;
import com.pangbohao.server.rest.model.PostResult;
import com.pangbohao.server.rest.model.RegistResult;
import com.pangbohao.server.utils.MD5Util;
import com.pangbohao.server.utils.Oauth;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.logging.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ps_an on 2016/8/30.
 */
public class OauthService {

    private static final Logger logger = Logger.getLogger(OauthService.class);
    private static OauthService INSTANCE = new OauthService();

    private RedisRegistrionBean redisBean = (RedisRegistrionBean) BeanProvider
            .instance().getRegistrationBean("redisRegistrionBean");

    private AuthUserBean authUserBean = (AuthUserBean) BeanProvider.instance()
            .getRegistrationBean("authUserBean");

    public static OauthService getInstance() {
        return INSTANCE;
    }

    private PostResult<AuthUser> getLoginResult(AuthUser user){
        PostResult<AuthUser> result = new PostResult<AuthUser>();
        result.setStatusCode(PostResult.SUCC);
        result.setMsg("登录成功");
        String token = TokenCache.getInstance().generateToken(user.getId());
        user.setToken(token);
        result.setData(user);
        return  result;
    }

    /**
     * 密码方式登录
     * @return
     */
    public PostResult<AuthUser> doLoginFromPassword(Map<String, String> map){
        String phoneNumber = map.get("phoneNumber");
        String userkey = map.get("userkey");
        String password = map.get("password");
        AuthUser user = authUserBean.getRepository().findByUser(phoneNumber);
        if (user == null) {
            //用户已经不存在，登录失败
            PostResult result = new PostResult();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("用户不存在，登录失败");
            return  result;
        }
        if (StringUtils.isEmpty(password) && !StringUtils.isEmpty(userkey) &&
                !StringUtils.equals(userkey,user.getUserKey())){
            PostResult result = new PostResult();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("用户的key不正确，登录失败");
            return  result;
        }

        if (!StringUtils.isEmpty(password) &&
                !StringUtils.equals(MD5Util.string2MD5(password),user.getPass())){
            PostResult result = new PostResult();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("用户的password不正确，登录失败");
            return  result;
        }

        user.setLastLogin(new Date());
        authUserBean.getRepository().save(user);
        return  getLoginResult(user);
    }

    /**
     * 密码方式注册
     * @return
     */
    public RegistResult doRegistFromPassword(Map<String, String> map){
        String phoneNumber = map.get("phoneNumber");
        String password = map.get("password");
        String introducerCode = map.get("introducerCode");
        String unionid = map.get("unionid");

        AuthUser user = authUserBean.getRepository().findByUser(phoneNumber);
        boolean isNewUser = false;
        if (user == null) {// 新用户
            //创建新用户
            user = createNewAuthUser(phoneNumber);
            user.setPass(MD5Util.string2MD5(password));
            authUserBean.getRepository().save(user);
            isNewUser = true;
        }else {
            //用户已经存在，注册失败
            RegistResult result = new RegistResult();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("用户已经存在，注册失败。");
            isNewUser = false;
            return  result;
        }

        user.setNewuser(isNewUser);
        RegistResult result = new RegistResult();
        result.setStatusCode(PostResult.SUCC);
        result.setUsername(user.getUsername());
        result.setMsg("注册成功");
        result.setUserkey(user.getUserKey());
        return  result;
    }

    private AuthUser createNewAuthUser(String phoneNumber){
        AuthUser user = new AuthUser();
        if (!StringUtils.isEmpty(phoneNumber)
                && phoneNumber.length()==11
                && StringUtils.isNumeric(phoneNumber)){
            user.setUsername(phoneNumber);
        }
        user.setUsername(phoneNumber);
        user.setNickname(phoneNumber);
        user.setUserKey(Oauth.randomString(32));
        user.setPass(MD5Util.string2MD5(user.getUserKey()));
        user = authUserBean.getRepository().save(user);
        user.setDateJoined(new Date());
        user.setLastLogin(new Date());
        return user;
    }

    public PostResult<AuthUser> getUserInfo(int userid) {
        AuthUser user = authUserBean.getRepository().findById(userid);
        logger.debug("getUserInfo()");
        logger.debug("userid="+userid);
        if (user == null) {// 新用户
            //用户不存在
            PostResult<AuthUser> result = new PostResult<AuthUser>();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("用户不存在");
            return  result;
        }
        PostResult<AuthUser> result = new PostResult<AuthUser>();
        result.setStatusCode(PostResult.SUCC);
        result.setMsg("成功获取用户信息");
        result.setData(user);
        logger.debug("user="+user.toString());
        return result;
    }

    public PostResult<AuthUser> getUserInfoByUsername(String username) {
        AuthUser user = authUserBean.getRepository().findByUser(username);
        logger.debug("getUserInfo()");
        logger.debug("username="+username);
        if (user == null) {// 新用户
            //用户不存在
            PostResult<AuthUser> result = new PostResult<AuthUser>();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("用户不存在");
            return  result;
        }
        PostResult<AuthUser> result = new PostResult<AuthUser>();
        result.setStatusCode(PostResult.SUCC);
        result.setMsg("成功获取用户信息");
        result.setData(user);
        logger.debug("user="+user.toString());
        return result;
    }

    public PostResult<AuthUser> modifyUserInfo(Map<String, String> map) {
        logger.debug("modifyUserInfo()");
        AuthUser user = null;
        if (!StringUtils.isEmpty(map.get("id"))){
            int userid = Integer.valueOf(map.get("id"));
            user = authUserBean.getRepository().findById(userid);
        }
        if (user==null && !StringUtils.isEmpty(map.get("username"))){
            String username = map.get("username");
            user = authUserBean.getRepository().findByUser(username);
        }
        
        if (user != null) {
            String birth = map.get("birth");
            String sex = map.get("sex");
            String email = map.get("email");
            String headimgurl = map.get("headimgurl");
            String nickname = map.get("nickname");
            if (!StringUtils.isEmpty(birth)){
                user.setBirth(birth);
            }
            if (!StringUtils.isEmpty(sex) && StringUtils.isNumeric(sex)){
                user.setSex(Integer.valueOf(sex));
            }
            if (!StringUtils.isEmpty(email)){
                user.setEmail(email);
            }
            if (!StringUtils.isEmpty(headimgurl)){
                user.setHeadimgurl(headimgurl);
            }
            if (!StringUtils.isEmpty(nickname)){
                user.setNickname(nickname);
            }
            authUserBean.getRepository().save(user);

            PostResult result = new PostResult();
            result.setStatusCode(PostResult.SUCC);
            result.setMsg("成功修改用户信息");
            result.setData(user);
            logger.debug("user="+user.toString());
            return result;
        }

        //用户不存在
        PostResult<AuthUser> result = new PostResult<AuthUser>();
        result.setStatusCode(PostResult.FAILED);
        result.setMsg("用户不存在");
        return  result;
    }

    public PostResult modifyPassword(int userid,Map<String, String> map) {
        AuthUser user = authUserBean.getRepository().findById(userid);
        logger.debug("modifyPassword()");
        if (user == null) {// 新用户
            //用户不存在
            PostResult<AuthUser> result = new PostResult<AuthUser>();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("用户不存在");
            return  result;
        }
        String newpassword = map.get("newpassword");
        String oldpassword = map.get("oldpassword");
        if (!StringUtils.isEmpty(oldpassword) && StringUtils.equals(MD5Util.string2MD5(oldpassword),user.getPass())){
            user.setPass(newpassword);
        }
        authUserBean.getRepository().save(user);
        PostResult result = new PostResult();
        result.setStatusCode(PostResult.SUCC);
        result.setMsg("成功修改用户信息");
        result.setData(user);
        logger.debug("user="+user.toString());
        return result;
    }

    public PostResult isTokenValid(String token, Map<String, String> map) {
        PostResult result = new PostResult();
        int userid =  TokenCache.getInstance().getUserId(token);
        if (userid>0){
            result.setStatusCode(PostResult.SUCC);
            result.setMsg("token有效");
            AuthUser user = new AuthUser();
            user.setId(userid);
            result.setData(user);
        }else {
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("token无效");
        }
        return result;
    }

    public PostResult<AuthUser> getUserInfoByUserId(String userid) {
        int id = -1;
        try {
            id = Integer.valueOf(userid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AuthUser user = authUserBean.getRepository().findById(id);
        logger.debug("getUserInfoByUserId()");
        logger.debug("id="+id);
        if (user == null) {// 新用户
            //用户不存在
            PostResult<AuthUser> result = new PostResult<AuthUser>();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("用户不存在");
            return  result;
        }
        PostResult<AuthUser> result = new PostResult<AuthUser>();
        result.setStatusCode(PostResult.SUCC);
        result.setMsg("成功获取用户信息");
        result.setData(user);
        logger.debug("user="+user.toString());
        return result;
    }

    /**
     * 免密码方式登录
     * @return
     */
    public PostResult<AuthUser> doLoginFromNoPassword(Map<String, String> map){
        String phoneNumber = map.get("phoneNumber");
        String userkey = map.get("userkey");
        String password = map.get("password");
        AuthUser user = authUserBean.getRepository().findByUser(phoneNumber);
        if (user == null) {
            //用户已经不存在，登录失败
            PostResult result = new PostResult();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("用户不存在，登录失败");
            return  result;
        }
        user.setLastLogin(new Date());
        authUserBean.getRepository().save(user);
        return  getLoginResult(user);
    }
}
