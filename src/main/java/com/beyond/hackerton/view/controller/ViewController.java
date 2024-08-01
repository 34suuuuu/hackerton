package com.beyond.hackerton.view.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.beyond.hackerton.common.dto.CommonResDto;
import com.beyond.hackerton.view.service.ViewService;

@Controller
public class ViewController {

	private final ViewService viewService;

	public ViewController(ViewService viewService) {
		this.viewService = viewService;
	}

	@PostMapping("/view/{postId}")
	public ResponseEntity<?> view(@PathVariable Long postId) {
		return new ResponseEntity<>(
			new CommonResDto(HttpStatus.OK, "조회수 증가", viewService.addView(postId)) ,HttpStatus.OK);
	}
}
