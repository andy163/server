package com.pangbohao.server.cache;

import com.pangbohao.server.db.BeanProvider;
import com.pangbohao.server.db.bean.RedisRegistrionBean;
import com.pangbohao.server.utils.Util;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TokenCache {

	private static TokenCache _CacheService = new TokenCache();

	RedisRegistrionBean redisBean = (RedisRegistrionBean) BeanProvider
			.instance().getRegistrationBean("redisRegistrionBean");
	private ValueOperations<String, String> tokenCache;

	public static TokenCache getInstance() {
		return _CacheService;
	}

	private TokenCache() {
		tokenCache = redisBean.getValueOps();
	}

	public boolean isTokenExpired(String token) {
		if (token != null) {
			int userId = getUserId(token);// 从redis取token
			if (userId != -1) {
				return false;
			}
		}
		return true;
	}

	public int getUserId(String token) {
		String userId = tokenCache.get(Util.messageFormat(RedisKeys.TOKEN_STR, token));
		if (userId == null) {
			userId = tokenCache.get(token);
			if(userId ==null)
			  return -1;
		}
		return Integer.parseInt(userId);
	}

	public String generateToken(int userId) {
		String token = UUID.randomUUID().toString();
		saveTokenToRedis(userId, token);
		return token;
	}

	private void saveTokenToRedis(int userId, String token) {
		tokenCache.set(Util.messageFormat(RedisKeys.TOKEN_STR, token), String.valueOf(userId), 24, TimeUnit.HOURS);
	}
}