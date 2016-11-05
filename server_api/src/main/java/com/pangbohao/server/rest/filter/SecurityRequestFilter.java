package com.pangbohao.server.rest.filter;

import com.pangbohao.server.cache.TokenCache;
import com.pangbohao.server.db.BeanProvider;
import com.pangbohao.server.db.bean.RedisRegistrionBean;
import com.pangbohao.server.rest.model.PostResult;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 检查Token是否过期
 * 
 */
public class SecurityRequestFilter implements ContainerRequestFilter {

	// private static final Logger logger = Logger
	// .getLogger(SecurityRequestFilter.class);

	RedisRegistrionBean redisRegistrionBean = (RedisRegistrionBean) BeanProvider
			.instance().getRegistrationBean("redisRegistrionBean");

	private static final ServerResponse ACCESS_DENIED = new ServerResponse(
			new PostResult<Headers<Object>>(401, "用户未授权"), 401,
			new Headers<Object>());

	private static final Object AUTHORIZATION_PROPERTY = "token";

	@Context
	private ResourceInfo resourceInfo;

	public void filter(ContainerRequestContext requestContext) {

		Method method = resourceInfo.getResourceMethod();
		if (!method.isAnnotationPresent(Secured.class)) {
			return;
		}

		// Get request headers
		final MultivaluedMap<String, String> headers = requestContext
				.getHeaders();

		// Fetch authorization header
		final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

		// If no authorization information present; block access
		if (authorization == null || authorization.isEmpty()) {
			requestContext.abortWith(ACCESS_DENIED);
			return;
		}

		// Get encoded username and password
		final String token = authorization.get(0);

		boolean isTokenExpired = TokenCache.getInstance().isTokenExpired(token);

		if (isTokenExpired) {
			requestContext.abortWith(ACCESS_DENIED);
		}

	}

}
