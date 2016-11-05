package com.pangbohao.server.rest.filter;

import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 检查ETag
 * 
 */
public class ETagRequestFilter implements ContainerRequestFilter {
	private static final Logger logger = Logger
			.getLogger(ETagRequestFilter.class);

	private static final String IF_NONE_MATCH = "If-None-Match";
	private static final String IF_MATCH = "If-Match";

	private EtagCacheService cacheService = EtagCacheService.getInstance();

	@Context
	private ResourceInfo resourceInfo;

	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		log(requestContext);

		Method method = resourceInfo.getResourceMethod();
		if (method.getAnnotation(ETagCache.class) == null) {
			return;
		}

		for (Annotation annotation : method.getDeclaredAnnotations()) {
			if (annotation instanceof GET) {
				if (isNotModified(requestContext)) {
					requestContext.abortWith(Response.status(
							Response.Status.NOT_MODIFIED).build());
					return;
				}
			} else if (annotation instanceof PUT
					|| annotation instanceof DELETE) {
				if (isPreconditionFailed(requestContext)) {
					requestContext.abortWith(Response.status(
							Response.Status.PRECONDITION_FAILED).build());
					return;
				}
			}
		}
	}

	/*
	 * Returns true if the ETag value in the If-None-Match header matches the
	 * ETag of the resource in the server cache. Meaning the requesting client
	 * has the latest version of the resource.
	 */
	private boolean isNotModified(ContainerRequestContext requestContext) {
		String eTag = getETagFromRequest(requestContext, IF_NONE_MATCH);
		String cachedETag = cacheService.getETag(requestContext);

		if (!Strings.isNullOrEmpty(eTag) && !Strings.isNullOrEmpty(cachedETag)) {
			if (eTag.equals(cachedETag)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Returns true if the ETag value in the If-Match header does not match the
	 * latest ETag of the resource in the server cache. Meaning the requesting
	 * client is updating or deleting an out-dated version of the resource.
	 */
	private boolean isPreconditionFailed(ContainerRequestContext requestContext) {
		String eTag = getETagFromRequest(requestContext, IF_MATCH);
		String cachedETag = cacheService.getETag(requestContext);

		if (!Strings.isNullOrEmpty(eTag) && !Strings.isNullOrEmpty(cachedETag)) {
			if (!eTag.equals(cachedETag)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Returns the ETag value in the given header from the provided request
	 * context. Returns null if there is no ETag or there are multiple ETAG
	 * values.
	 */
	private String getETagFromRequest(ContainerRequestContext requestContext,
			String header) {
		List<String> eTags = requestContext.getHeaders().get(header);
		if (eTags != null && eTags.size() == 1) {
			return eTags.get(0);
		}
		return null;
	}

	private void log(ContainerRequestContext requestContext) {
		if (logger.isDebugEnabled()) {

			MultivaluedMap<String, String> headers = requestContext
					.getHeaders();

			String requestMethod = requestContext.getMethod();

			logger.debug("Request {0} {1}", requestMethod, requestContext
					.getUriInfo().getRequestUri());
			logger.debug("Request MediaType:" + requestContext.getMediaType());
			logger.debug("Request Headers:" + headers);
			try {
				if (("POST".equals(requestMethod) || "PUT"
						.equals(requestMethod)
						&& requestContext.getEntityStream() != null)) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					IOUtils.copy(requestContext.getEntityStream(), baos);
					byte[] bytes = baos.toByteArray();
					logger.debug(" Body:{0}", new String(bytes, "UTF-8"));
					requestContext.setEntityStream(new ByteArrayInputStream(
							bytes));
				}
			} catch (Exception e) {
				logger.error("log:", e);
			}
		}
	}
}