package com.beyond.hackerton.post.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.beyond.hackerton.member.domain.Member;
import com.beyond.hackerton.member.repository.MemberRepository;
import com.beyond.hackerton.post.domain.Img;
import com.beyond.hackerton.post.domain.Post;
import com.beyond.hackerton.post.dto.PhotoInfoDto;
import com.beyond.hackerton.post.dto.PostResDto;
import com.beyond.hackerton.post.dto.PostSaveDto;
import com.beyond.hackerton.post.repository.ImgRepository;
import com.beyond.hackerton.post.repository.PostRepository;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Service
public class PostService {

	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final ImgRepository imgRepository;


	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Autowired
	public PostService(PostRepository postRepository, MemberRepository memberRepository,ImgRepository imgRepository) {
		this.postRepository = postRepository;
		this.memberRepository = memberRepository;
		this.imgRepository = imgRepository;
	}


	// @Transactional
	// public List<PhotoInfoDto> postCreate(PostSaveDto postSaveDto, List<String> imgUrls) {
	// 	// MultipartFile image = postSaveDto.getProductImage();
	// 	// PhotoInfoDto:
	// 	String email = SecurityContextHolder.getContext().getAuthentication().getName();	// 사용자 이메일
	// 	Member member = memberRepository.findByEmail(email)
	// 		.orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));	// 사용자랑 이미지를 같이 넘겨줘서 toEntity
	//
	// 	String inputFileName = multipartFile.getOriginalFilename();
	// 	//파일 형식 구하기
	// 	String ext = inputFileName.substring(inputFileName.lastIndexOf(".") + 1).toLowerCase();
	// 	String contentType;
	//
	// 	//content type을 지정해서 올려주지 않으면 자동으로 "application/octet-stream"으로 고정이 되서 링크 클릭시 웹에서 열리는게 아니라 자동 다운이 시작됨.
	// 	switch (ext) {
	// 		case "jpeg":
	// 			contentType = "image/jpeg";
	// 			break;
	// 		case "png":
	// 			contentType = "image/png";
	// 			break;
	// 		case "jpg":
	// 			contentType = "image/jpg";
	// 			break;
	// 		default:
	// 			throw new IllegalArgumentException("Only image files (jpeg, png, jpg) are allowed.");   // 안뜸
	// 	}
	//
	// 	ObjectMetadata metadata = new ObjectMetadata();
	// 	metadata.setContentType(contentType);   // ObjectMetadata에 contentType 입력
	// 	String uuidFileName = UUID.randomUUID().toString() + "." + ext;// 파일명 UUID로 변환 후 파일 타입 붙여주기
	//
	// 	amazonS3.putObject(new PutObjectRequest(bucketName, uuidFileName, files.getInputStream(), metadata)
	// 		.withCannedAcl(CannedAccessControlList.PublicRead));
	//
	// 	Post post = postSaveDto.toEntity(member, )
	//
	// 	String url = amazonS3.getUrl(bucketName, uuidFileName).toString();
	// 	PhotoInfoDto photoInfoDto = PhotoInfoDto.builder().
	// 		id(photo.getPhotoId())
	// 		.url(url)
	// 		.build();
	// }

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
			imgRepository.save(img);
			imgList.add(img.getImgUrl());
		}
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


