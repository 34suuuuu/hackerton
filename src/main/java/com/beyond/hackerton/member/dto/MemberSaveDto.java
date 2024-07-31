package com.beyond.hackerton.member.dto;

import com.beyond.hackerton.member.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSaveDto {
	private String name;
	private String email;
	private String password;

	public Member toEntity(String encodedPassword) {
		return Member.builder()
			.name(this.name)
			.email(this.email)
			.password(encodedPassword)
			.build();
	}
}
