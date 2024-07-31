package com.beyond.hackerton.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.beyond.hackerton.common.auth.JwtTokenProvider;
import com.beyond.hackerton.common.dto.CommonResDto;
import com.beyond.hackerton.member.domain.Member;
import com.beyond.hackerton.member.domain.Role;
import com.beyond.hackerton.member.dto.MemberLoginDto;
import com.beyond.hackerton.member.dto.MemberSaveDto;
import com.beyond.hackerton.member.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MemberController {

	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
		this.memberService = memberService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	// 	회원가입
	@PostMapping("/member/create")
	public ResponseEntity<?> memberCreate(@RequestBody MemberSaveDto memberSaveDto) {
		return new ResponseEntity<>(
			new CommonResDto(HttpStatus.OK, "회원가입 성공", memberService.memberCreate(memberSaveDto).getId()),
			HttpStatus.OK);
	}

	// 	로그인
		@PostMapping("/doLogin")
		public ResponseEntity<?> memberLogin(@RequestBody MemberLoginDto memberLoginDto) {
			Member member = memberService.memberLogin(memberLoginDto);	// 비밀번호가 일치하면 return
			System.out.println("heeer");
			// member가 존재하면 token 생성
			String jwtToken = jwtTokenProvider.createAccessToken(memberLoginDto.getEmail(), member.getRole
			().toString());
		Map<String, Object> logInfo = new HashMap<>();
		logInfo.put("email",member.getEmail());
		logInfo.put("token", jwtToken);

		return new ResponseEntity<>(
			new CommonResDto(HttpStatus.OK, "로그인", logInfo) ,HttpStatus.OK);
	}
}
