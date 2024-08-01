package com.beyond.hackerton.view.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;

@Service
public class ViewService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public ViewService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Long addView(Long postId) {
		String postIdString = String.valueOf(postId);    // key:value - posId:(set)으로 들어온 사용자의 이메알 혹은, 아이피주소
		SetOperations<String, Object> setOperation = redisTemplate.opsForSet();

		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!user.contains("@")) {	// Bearer 토큰이 존재한다면
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			String clientIp = request.getHeader("X-Forwarded-For");
			if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
				user = request.getHeader("Proxy-Client-IP");
			}
			if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
				user = request.getHeader("WL-Proxy-Client-IP");
			}
			if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
				user = request.getHeader("HTTP_CLIENT_IP");
			}
			if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
				user = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
				user = request.getHeader("X-Real-IP");
			}
			if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
				user = request.getHeader("X-RealIP");
			}
			if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
				user = request.getHeader("REMOTE_ADDR");
			}
			if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
				user = request.getRemoteAddr();
			}
		}
		setOperation.add(postIdString, user);
		return setOperation.size(postIdString);
	}





}
