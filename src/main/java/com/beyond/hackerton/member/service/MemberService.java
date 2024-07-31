package com.beyond.hackerton.member.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.beyond.hackerton.member.domain.Member;
import com.beyond.hackerton.member.dto.MemberLoginDto;
import com.beyond.hackerton.member.dto.MemberSaveDto;
import com.beyond.hackerton.member.repository.MemberRepository;

@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Member memberCreate(MemberSaveDto memberSaveDto) {
		if (memberRepository.findByEmail(memberSaveDto.getEmail()).isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
		}
		return memberRepository.save(memberSaveDto.toEntity(passwordEncoder.encode(memberSaveDto.getPassword())));
	}

	public Member memberLogin(MemberLoginDto memberLoginDto) {
		// 	이메일의 존재 여부 확인
		Member member = memberRepository.findByEmail(memberLoginDto.getEmail())
			.orElseThrow(() -> new EntityNotFoundException("일치하는 회원정보가 존재하지 않습니다."));

		// 	존재한다면 비밀번호를 암호화해서 비교
		if(!passwordEncoder.matches(memberLoginDto.getPassword(), member.getPassword())) {
		// 일치하지 않는다면
			throw new IllegalArgumentException();
		}
		return member;
	}
}
