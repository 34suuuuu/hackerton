package com.beyond.hackerton.post.dto;

import com.beyond.hackerton.common.domain.BaseTimeEntity;
import com.beyond.hackerton.member.domain.Grade;
import com.beyond.hackerton.post.domain.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResDto {
	private String name;
	private String contents;
	private String imagePath;
	private Grade grade;

	public PostResDto fromEntity(Post post) {
		return PostResDto.builder()
			.name(post.getName())
			.contents(post.getContents())
			.grade(post.getMember().getGrade())
			// 이미지 부분 추가
			.build();
	}

}
