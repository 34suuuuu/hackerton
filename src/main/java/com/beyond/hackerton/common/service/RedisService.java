// package com.beyond.hackerton.common.service;
//
// import java.time.Duration;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.core.ValueOperations;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;
//
// import lombok.RequiredArgsConstructor;
//
// @Component
// public class RedisService {
//
// 	private final RedisTemplate<String, Object> redisTemplate;
//
// 	@Autowired
// 	public RedisService(RedisTemplate<String, Object> redisTemplate) {
// 		this.redisTemplate = redisTemplate;
// 	}
//
// 	public void setValues(String key, String data, Duration duration) {
// 		ValueOperations<String, Object> values = redisTemplate.opsForValue();
// 		values.set(key, data, duration);
// 	}
//
// 	@Transactional(readOnly = true)
// 	public String getValues(String key) {
// 		ValueOperations<String, Object> values = redisTemplate.opsForValue();
// 		if (values.get(key) == null) {
// 			return "false";
// 		}
// 		return (String) values.get(key);
// 	}
//
// 	public void setDateExpire(String key, String value, long duration) {
// 		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
// 		Duration expireDuration = Duration.ofSeconds(duration);
// 		valueOperations.set(key, value, expireDuration);
// 	}
//
// 	public void deleteValues(String key) {
// 		redisTemplate.delete(key);
// 	}
//
// 	public boolean checkExistsValue(String value) {
// 		return !value.equals("false");
// 	}
// }
