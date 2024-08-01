package com.beyond.hackerton.post.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beyond.hackerton.member.domain.Member;
import com.beyond.hackerton.member.repository.MemberRepository;
import com.beyond.hackerton.member.service.MemberService;
import com.beyond.hackerton.post.domain.Img;
import com.beyond.hackerton.post.domain.Post;
import com.beyond.hackerton.post.dto.PostResDto;
import com.beyond.hackerton.post.dto.PostSaveDto;
import com.beyond.hackerton.post.repository.ImgRepository;
import com.beyond.hackerton.post.repository.PostRepository;


@Service
public class PostService {

	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final ImgRepository imgRepository;
	private final MemberService memberService;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Autowired
	public PostService(PostRepository postRepository, MemberRepository memberRepository,ImgRepository imgRepository,
		MemberService memberService) {
		this.postRepository = postRepository;
		this.memberRepository = memberRepository;
		this.imgRepository = imgRepository;
		this.memberService = memberService;
	}

	@Transactional
	public void postCreate(PostSaveDto postSaveDto, List<String> imgPaths) {
		postBlankCheck(imgPaths);

		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException("회원 정보 없음"));

		Post post = postSaveDto.toEntity(member);
		postRepository.save(post);

		List<String> imgList = new ArrayList<>();
		for (String path : imgPaths) {
			Img img = Img.builder().imgUrl(path).post(post).build();
			// imgRepository.save(img);
			imgList.add(img.getImgUrl());
		}
		member.updateGrade();
	}

	private void postBlankCheck(List<String> imgPaths) {
		if(imgPaths == null || imgPaths.isEmpty()){ //.isEmpty()도 되는지 확인해보기
			throw new IllegalArgumentException("파일명 확인필요");
		}
	}

	// 내가 작성한 게시글 조회
	// paging처리 필요
	public List<PostResDto> myPosts(){
		// 토큰의 이메일 정보로 member객체 get
		Member member = memberRepository.findByEmail(
			SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 이메일입니다."));
		List<Post> posts = postRepository.findByMember(member);	//member를 통해 사용자가 쓴 게시글 가져오기
		List<PostResDto> postResDtos = new ArrayList<>();
		for  (Post post : posts){
			postResDtos.add(post.fromEntity());
		}
		return postResDtos;
	}

	// 게시글 삭제
	public Post postDelete(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new EntityNotFoundException("일치하는 게시글을 찾을 수 없습니다"));
		return post.updateDelYn();
	}

}


