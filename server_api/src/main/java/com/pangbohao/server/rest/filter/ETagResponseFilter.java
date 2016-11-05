package com.pangbohao.server.rest.filter;

import com.pangbohao.server.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.LoggableFailure;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

//import org.jboss.resteasy.spi.CorsHeaders;

public class ETagResponseFilter implements ContainerResponseFilter {
	private static final Logger logger = Logger
			.getLogger(ETagResponseFilter.class);

	private static final String CACHE_CONTROL = "Cache-Control";
	private static final String ENTITY_TAG = "ETag";

	@Context
	private ResourceInfo resourceInfo;

	private EtagCacheService cacheService = EtagCacheService.getInstance();

	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {

		log(requestContext);

		try {
			// String origin =
			// requestContext.getHeaderString(CorsHeaders.ORIGIN);
			// if (origin != null) {// 允许跨域请求
			// responseContext.getHeaders().putSingle(
			// CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			// responseContext.getHeaders().putSingle(
			// CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
			//
			// String requestMethods = requestContext
			// .getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD);
			// if (requestMethods != null) {
			// responseContext.getHeaders().putSingle(
			// CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS,
			// requestMethods);
			// }
			// String allowHeaders = requestContext
			// .getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
			// if (allowHeaders != null) {
			// responseContext.getHeaders().putSingle(
			// CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
			// allowHeaders);
			// }
			// }

			// etag
			if (responseContext.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				Method method = resourceInfo.getResourceMethod();
				if (method.getAnnotation(ETagCache.class) != null) {

					for (Annotation annotation : method
							.getDeclaredAnnotations()) {
						if (annotation instanceof GET) {
							addETagIfAbsent(requestContext, responseContext);
						} else if (annotation instanceof PUT) {
							updateETag(requestContext, responseContext);
						} else if (annotation instanceof DELETE) {
							removeETag(requestContext);
						}
					}
				}
			}

		} catch (LoggableFailure ex) {

		} catch (Exception ex) {
			logger.error("Exception:", ex);
		}
		log(responseContext);

	}

	/*
	 * Adds ETag to the response header and adds it to the server cache if it
	 * does not exist.
	 */
	private void addETagIfAbsent(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) {
		Object entity = responseContext.getEntity();
		if (entity != null) {// 为null或为空都不添加
			boolean isEmpty = false;
			if (entity instanceof String
					&& StringUtils.length((String) entity) == 0) {
				isEmpty = true;
			} else if (entity instanceof Collection
					&& ((Collection<?>) entity).isEmpty()) {
				isEmpty = true;
			} else if (entity instanceof Map<?, ?>
					&& ((Map<?, ?>) entity).isEmpty()) {
				isEmpty = true;
			}
			if (!isEmpty) {
				int hashCode = entity.hashCode();
				if (hashCode != -1) {
					String eTag = String.valueOf(hashCode);
					// String eTag = UUID.randomUUID().toString();
					responseContext.getHeaders().add(ENTITY_TAG, eTag);
					cacheService.putIfAbsentETag(requestContext, eTag);
				}
			}
		}
		responseContext.getHeaders().add(CACHE_CONTROL, "no-cache");

	}

	/*
	 * Adds ETag to the response header and replaces it on the server cache.
	 */
	private void updateETag(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) {
		Object entity = responseContext.getEntity();
		if (entity != null) {
			int hashCode = entity.hashCode();
			if (hashCode != -1) {
				String eTag = String.valueOf(hashCode);
				// String eTag = UUID.randomUUID().toString();
				responseContext.getHeaders().add(ENTITY_TAG, eTag);
				cacheService.replaceETag(requestContext, eTag);
			}
		}
		responseContext.getHeaders().add(CACHE_CONTROL, "no-cache");
	}

	/*
	 * Removes ETag from the server cache.
	 */
	private void removeETag(ContainerRequestContext requestContext) {
		cacheService.removeETag(requestContext);
	}

	private void log(ContainerResponseContext responseContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("Response Status:{0}", responseContext.getStatus()
					+ " " + responseContext.getStatusInfo() + "");
			logger.debug("Response Headers:{0}", responseContext.getHeaders());
			logger.debug("Response Body:{0}",
					Util.toJson(responseContext.getEntity()));
		}
	}

	private void log(ContainerRequestContext requestContext) {
		if (logger.isDebugEnabled()) {
			String requestMethod = requestContext.getMethod();
			if (StringUtils.equals(requestMethod, "OPTIONS")) {
				MultivaluedMap<String, String> headers = requestContext
						.getHeaders();
				logger.debug("Request {0} {1}", requestMethod, requestContext
						.getUriInfo().getRequestUri());
				logger.debug("Request MediaType:"
						+ requestContext.getMediaType());
				logger.debug("Request Headers:" + headers);
			}

		}
	}

}