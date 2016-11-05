package com.pangbohao.server.rest.filter;

import com.pangbohao.server.cache.RedisKeys;
import com.pangbohao.server.db.BeanProvider;
import com.pangbohao.server.db.bean.RedisRegistrionBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.concurrent.TimeUnit;

public class EtagCacheService {

	private static EtagCacheService _CacheService = new EtagCacheService();

	RedisRegistrionBean redisBean = (RedisRegistrionBean) BeanProvider
			.instance().getRegistrationBean("redisRegistrionBean");
	private ValueOperations<String, String> eTagCache;
	private SetOperations<String, String> eTagKeys;

	public static EtagCacheService getInstance() {
		return _CacheService;
	}

	private EtagCacheService() {
		eTagCache = redisBean.getValueOps();
		eTagKeys = redisBean.getSetOps();
	}

	public void shutdown() {
	}

	public String getETag(ContainerRequestContext requestContext) {
		String key = getETagCacheKey(requestContext);
		return eTagCache.get(key);
	}

	public void putIfAbsentETag(ContainerRequestContext requestContext,
			String eTag) {
		String key = getETagCacheKey(requestContext);
		if (eTagCache.setIfAbsent(key, eTag)) {
			eTagCache.set(key, eTag, 6, TimeUnit.HOURS);
		}
		eTagKeys.add(RedisKeys.ETAG_MEMBER_SET, key);

	}

	public void replaceETag(ContainerRequestContext requestContext, String eTag) {
		String key = getETagCacheKey(requestContext);
		eTagCache.set(key, eTag, 6, TimeUnit.HOURS);
	}

	public void removeETag(ContainerRequestContext requestContext) {
		String key = getETagCacheKey(requestContext);
		// eTagCache.remove(key);// remove
		redisBean.getStringTemplate().delete(key);

	}

	private String getETagCacheKey(ContainerRequestContext requestContext) {
		String uri = requestContext.getUriInfo().getPath();
		StringBuilder sb = new StringBuilder();
		sb.append(uri);
		String regionId = requestContext.getHeaderString("region");
		if (regionId != null) {
			sb.append("-");
			sb.append(regionId);
		}
		if (StringUtils.endsWith(uri, "adgroup/home")) {// 首页每个用户不同
			String token = requestContext.getHeaderString("token");
			if (token != null) {
				sb.append("-");
				sb.append(token);
			}
		}
		
		return String.format(RedisKeys.ETAG_URI, sb.toString());
	}
}