package com.beyond.hackerton.post.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.beyond.hackerton.common.domain.BaseTimeEntity;
import com.beyond.hackerton.member.domain.Member;
import com.beyond.hackerton.post.dto.PostResDto;
import com.beyond.hackerton.post.dto.PostSaveDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Post extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String contents;
	private String imagePath;	//이미지 제한 4개

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private DelYn delYn = DelYn.N;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;	// Member-Post (1:N)

	public PostResDto fromEntity(){
		return PostResDto.builder()
			.name(this.name)
			.contents(this.contents)
			// .imagePath(this.imagePath)	// 이미지 부분 추후 수정
			.build();
	}

	public Post updateDelYn() {
		this.delYn = DelYn.Y;
		return this;
	}

	public void updateImagePath(String imagePath){
		this.imagePath = imagePath;
	}

}
