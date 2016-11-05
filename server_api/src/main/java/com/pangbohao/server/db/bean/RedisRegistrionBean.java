package com.pangbohao.server.db.bean;

import org.jboss.resteasy.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisRegistrionBean {
	private static final Logger logger = Logger
			.getLogger(RedisRegistrionBean.class);

	@Autowired
	private StringRedisTemplate stringTemplate;

	@Resource(name = "stringRedisTemplate")
	private ListOperations<String, String> listOps;

	@Resource(name = "stringRedisTemplate")
	private ValueOperations<String, String> valueOps;

	@Resource(name = "stringRedisTemplate")
	private HashOperations<String, Object, Object> hashOps;

	@Resource(name = "stringRedisTemplate")
	private SetOperations<String, String> setOps;

	public ValueOperations<String, String> getValueOps() {
		return valueOps;
	}

	public StringRedisTemplate getStringTemplate() {
		return stringTemplate;
	}

	public void setStringTemplate(StringRedisTemplate stringTemplate) {
		this.stringTemplate = stringTemplate;
	}

	// public ListOperations<String, String> getListOps() {
	// return listOps;
	// }

	// public HashOperations<String, Object, Object> getHashOps() {
	// return hashOps;
	// }

	public SetOperations<String, String> getSetOps() {
		return setOps;
	}

	public Object getHashValue(String key, String hashKey) {
		Object value = hashOps.get(key, hashKey);
		logger.debug("redis: getHashValue({0},{1}) = {2}", key, hashKey, value);
		if (value == null) {
			logger.warn("redis: getHashValue({0},{1}) = {2}", key, hashKey,
					value);
		}
		return value;
	}
	public void delHashValue(String key, String hashKey) {
		hashOps.delete(key, hashKey);
	}
	
	
	public String getHashValueNoWarn(String key, String hashKey) {
		Object value = hashOps.get(key, hashKey);
		logger.debug("redis: getHashValue({0},{1}) = {2}", key, hashKey, value);
		return (String) value;
	}

	public String getHashStringValue(String key, String hashKey) {
		return (String) getHashValue(key, hashKey);
	}

	public void putHashValue(String key, String hashKey, Object value) {
		logger.debug("redis: putHashValue({0},{1},{2})", key, hashKey, value);

		hashOps.put(key, hashKey, value);
	}

	public String getStringValue(String key) {
		String value = valueOps.get(key);
		logger.debug("redis: getHashValue({0}) = {1}", key, value);

		if (value == null) {
			logger.warn("redis: getStringValue({0}) = {1}", key, value);
		}
		return value;
	}

	public String getStringValueNoWarn(String key) {
		String value = valueOps.get(key);
		logger.debug("redis: getStringValueNoWarn({0}) = {1}", key, value);
		return value;
	}

	public void setStringValue(String key, String value, int timeout,
			TimeUnit unit) {
		logger.debug("redis: setStringValue({0},{1},{2},{3})", key, value,
				timeout, unit);
		valueOps.set(key, value, timeout, unit);
	}

	public void setStringValue(String key, String value) {
		logger.debug("redis: setStringValue({0},{1})", key, value);
		valueOps.set(key, value);
	}

	public Set<String> members(String key) {
		Set<String> value = setOps.members(key);
		logger.debug("redis: members({0}) = {1}", key, value);
		if (value == null) {
			logger.warn("redis: members({0}) = {1}", key, value);
		}
		return value;
	}

	public Boolean isMember(String key, Object o) {
		logger.debug("redis: isMember({0}) = {1}", key, o);
		return setOps.isMember(key, o);
	}

	public List<String> range(String key, long start, long end) {
		List<String> value = listOps.range(key, start, end);
		logger.debug("redis: range({0},{1},{2}) = {3}", key, start, end, value);
		if (value == null || value.isEmpty()) {
			logger.warn("redis: range({0},{1},{2}) = {3}", key, start, end,
					value);
		}
		return value;
	}

	public long size(String key) {
		return listOps.size(key);
	}
}
