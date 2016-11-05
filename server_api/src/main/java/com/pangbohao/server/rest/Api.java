package com.pangbohao.server.rest;

import com.pangbohao.server.cache.TokenCache;
import com.pangbohao.server.db.model.AuthUser;
import com.pangbohao.server.rest.filter.Secured;
import com.pangbohao.server.rest.model.PostResult;
import com.pangbohao.server.rest.model.RegistResult;
import com.pangbohao.server.service.OauthService;

import javax.ws.rs.*;
import java.util.Map;

/**
 * Created by ps_an on 2016/11/1.
 */
@Path("v1/")
@Produces("application/json;charset=utf-8")
@Consumes("application/json;charset=utf-8")
public class Api {

    /**
     * 注册---密码方式
     */
    @POST
    @Path("regist/password")
    public RegistResult registFromPassword(
            Map<String, String> map,
            @HeaderParam("appid") String appid) {
        return OauthService.getInstance().doRegistFromPassword(map);
    }

    /**
     * 登录---密码方式
     */
    @POST
    @Path("login/password")
    public PostResult<AuthUser> loginFromPassword(
            Map<String, String> map,
            @HeaderParam("appid") String appid) {

        return OauthService.getInstance().doLoginFromPassword(map);
    }

    /**
     *2.7.1	获取用户信息
     */
    @POST
    @Path("user/info")
    public PostResult<AuthUser> getUserInfo(
            Map<String, String> map,
            @HeaderParam("token") String token,
            @HeaderParam("appid") String appid) {
        if (map!=null && map.get("username")!=null){
            String username = map.get("username");
            return OauthService.getInstance().getUserInfoByUsername(username);
        }
        if (map!=null && map.get("userid")!=null){
            String userid = map.get("userid");
            return OauthService.getInstance().getUserInfoByUserId(userid);
        }
        boolean isTokenExpired = TokenCache.getInstance().isTokenExpired(token);

        if (isTokenExpired) {
            PostResult<AuthUser> result = new PostResult<AuthUser>();
            result.setStatusCode(PostResult.FAILED);
            result.setMsg("token无效");
            return result;
        }
        return OauthService.getInstance().getUserInfo(TokenCache.getInstance().getUserId(token));
    }

    /**
     *3.7.3	修改用户信息
     */
    @POST
    @Path("user/modify")
    public PostResult modifyUserInfo(
            Map<String, String> map,
            @HeaderParam("token") String token,
            @HeaderParam("appid") String appid) {
        return OauthService.getInstance().modifyUserInfo(map);
    }

    /**
     *3.7.2	修改用户密码
     */
    @POST
    @Secured
    @Path("user/modify/password")
    public PostResult modifyPassword(
            Map<String, String> map,
            @HeaderParam("token") String token,
            @HeaderParam("appid") String appid) {
        return OauthService.getInstance().modifyPassword(TokenCache.getInstance().getUserId(token),map);
    }

    /**
     *判断token是否有效,如果有效返回用户相关信息
     */
    @POST
    @Path("user/token/valid")
    public PostResult<AuthUser> isTokenValid(
            Map<String, String> map,
            @HeaderParam("token") String token,
            @HeaderParam("appid") String appid) {
        return OauthService.getInstance().isTokenValid(token,map);
    }

    /**
     * 登录---免密码方式
     */
    @POST
    @Path("login/nopassword")
    public PostResult<AuthUser> loginFromNoPassword(
            Map<String, String> map,
            @HeaderParam("appid") String appid) {

        return OauthService.getInstance().doLoginFromNoPassword(map);
    }
}
