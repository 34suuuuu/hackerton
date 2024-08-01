package com.beyond.hackerton.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.beyond.hackerton.common.auth.JwtTokenProvider;
import com.beyond.hackerton.common.dto.CommonErrorDto;
import com.beyond.hackerton.common.dto.CommonResDto;
import com.beyond.hackerton.member.domain.Member;
import com.beyond.hackerton.member.dto.MemberLoginDto;
import com.beyond.hackerton.member.dto.MemberSaveDto;
import com.beyond.hackerton.member.dto.memberRefreshDto;
import com.beyond.hackerton.member.service.MemberService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MemberController {

	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${jwt.secretKeyRt}")
	private String secretKeyRt;

	@Autowired
	public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider,
		RedisTemplate<String, Object> redisTemplate) {
		this.memberService = memberService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisTemplate = redisTemplate;
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
			// member가 존재하면 token 생성
			String jwtToken = jwtTokenProvider.createToken(memberLoginDto.getEmail(), member.getRole
			().toString());
		Map<String, Object> logInfo = new HashMap<>();
		logInfo.put("email",member.getEmail());
		logInfo.put("token", jwtToken);

		return new ResponseEntity<>(
			new CommonResDto(HttpStatus.OK, "로그인", logInfo) ,HttpStatus.OK);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> generateNewAccesstoken(@RequestBody memberRefreshDto dto) {
		String rt = dto.getRefreshToken();
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(secretKeyRt).parseClaimsJws(rt).getBody();
		} catch (Exception e) {
			return new ResponseEntity<>(new CommonErrorDto(HttpStatus.UNAUTHORIZED, "invalids refresh token"),
				HttpStatus.UNAUTHORIZED);
		}
		String email = claims.getSubject();
		String role = claims.get("role").toString();

		Object obj = redisTemplate.opsForValue().get(email);
		if (obj == null || !obj.toString().equals(rt)) {
			return new ResponseEntity<>(new CommonErrorDto(HttpStatus.UNAUTHORIZED, "invalids refresh token"),
				HttpStatus.UNAUTHORIZED);
		}
		String newAt = jwtTokenProvider.createToken(email, role);
		Map<String, Object> info = new HashMap<>();
		info.put("token", newAt);

		// 생성된 토큰을 comonResDto에 담아서 사용자에게 리턴
		return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "at is renewed", info), HttpStatus.OK);

	}
}
