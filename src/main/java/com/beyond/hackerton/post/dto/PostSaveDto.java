package com.beyond.hackerton.post.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.beyond.hackerton.member.domain.Member;
import com.beyond.hackerton.post.domain.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSaveDto {
	private String name;
	private String contents;
	private List<MultipartFile> files;

	public Post toEntity(Member member){
		return Post.builder()
			.name(member.getName())	// 토큰 -> 이메일 -> 사용자 이름
			.contents(this.contents)
			.member(member)
			// 추후에 이미지 파일 처리 필요
			.build();
	}

}
