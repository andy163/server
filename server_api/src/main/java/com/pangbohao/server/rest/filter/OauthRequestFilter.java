package com.pangbohao.server.rest.filter;

import com.pangbohao.server.rest.model.PostResult;
import com.pangbohao.server.utils.Oauth;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by ps_an on 2016/8/29.
 */
public class OauthRequestFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger
            .getLogger(OauthRequestFilter.class);

    private static final ServerResponse ACCESS_DENIED = new ServerResponse(
            new PostResult<Headers<Object>>(999, "接口授权存在异常"), 999,
            new Headers<Object>());

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get request headers
        final MultivaluedMap<String, String> headers = requestContext
                .getHeaders();

        Method resourceInfomethod = resourceInfo.getResourceMethod();
        if (!resourceInfomethod.isAnnotationPresent(Authorization.class)) {
            return;
        }

        String method = requestContext.getMethod();
        String body = "";
        try {
            if (("POST".equals(method) || "PUT"
                    .equals(method)
                    && requestContext.getEntityStream() != null)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(requestContext.getEntityStream(), baos);
                byte[] bytes = baos.toByteArray();
                body = new String(bytes, "UTF-8");
                logger.debug(" Body:{0}", body);
                requestContext.setEntityStream(new ByteArrayInputStream(
                        bytes));
            }else {
                logger.debug("method is get.");
                return;
            }
        } catch (Exception e) {
            logger.error("log:", e);
        }

        // Fetch authorization header
        final List<String> authorization = headers.get("Authorization");
        // If no authorization information present; block access
        if (authorization == null || authorization.isEmpty()) {
            requestContext.abortWith(ACCESS_DENIED);
            logger.debug("Authorization is empty.");
            return;
        }

        final List<String> appid  = headers.get("appid");
        if (appid == null || appid.isEmpty()) {
            requestContext.abortWith(ACCESS_DENIED);
            logger.debug("appid is empty.");
            return;
        }

        //查询配置文件里面是否存在这个appid的秘钥
        if (StringUtils.isEmpty(Oauth.getSecret(appid.get(0)))){
            requestContext.abortWith(ACCESS_DENIED);
            logger.debug("Secret is empty.");
            return;
        }

        //验证签名是否正确
        if (!Oauth.verifyRequest(appid.get(0),authorization.get(0),headers,body)) {
            logger.debug("signature is wrong.");
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }else {
            logger.debug("signature is right..");
            return;
        }

    }
}
