package com.beyond.hackerton.post.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.beyond.hackerton.common.configs.S3Config;
import com.beyond.hackerton.common.service.S3Service;
import com.beyond.hackerton.member.domain.Member;
import com.beyond.hackerton.member.repository.MemberRepository;
import com.beyond.hackerton.post.domain.Post;
import com.beyond.hackerton.post.dto.PostResDto;
import com.beyond.hackerton.post.dto.PostSaveDto;
import com.beyond.hackerton.post.repository.PostRepository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class PostService {

	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final S3Service s3Service;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Autowired
	public PostService(PostRepository postRepository, MemberRepository memberRepository
		S3Service s3Service) {
		this.postRepository = postRepository;
		this.memberRepository = memberRepository;
		this.s3Service = s3Service;
	}

	// public Post postCreate(PostSaveDto postSaveDto) {
	// 	String email = SecurityContextHolder.getContext().getAuthentication().getName();
	// 	Member member = memberRepository.findByEmail(email)
	// 		.orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
	// 	return postRepository.save(postSaveDto.toEntity(member));
	// }

	public Post postCreate(PostSaveDto postSaveDto, List<MultipartFile> files) {
		// MultipartFile image = postSaveDto.getProductImage();
		Post post = null;

		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
		List<String> uploadFiles = new ArrayList<>();	//업로드 할 파일
		try {
			// local에 저장
			post = postRepository.save(postSaveDto.toEntity(member));
			s3Service.upload(post.getId(), files);


			for (MultipartFile file : files) {
				byte[] bytes = file.getBytes();
				String fileName = post.getId() + "_" + file.getOriginalFilename();
				Path path = Paths.get("/Users/suhyun/Desktop/hackerton_img/",fileName);
				uploadFiles.add(path, bytes);
				Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

			}
			// aws에 저장된 파일을 업로드
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(fileName)
				.build();
			PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
			String s3Path = s3Client.utilities().getUrl(a -> a.bucket(bucketName).key(fileName)).toExternalForm();
			post.updateImagePath(s3Path);
		} catch (IOException e) {
			throw new RuntimeException("이미지 저장 실패");
		}
		return post;
	}

	// public Product createAwsProduct(ProductSaveReqDto dto) {
	// 	MultipartFile image = dto.getProductImage();
	// 	Product product = null;
	// 	try {
	// 		product = productRepository.save(dto.toEntity());
	// 		byte[] bytes = image.getBytes();
	// 		String fileName = product.getId() + "_" + image.getOriginalFilename();
	// 		Path path = Paths.get("/Users/suhyun/Desktop/tmp/",fileName);
	// 		// local pc에 저장
	// 		Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	//
	// 		// aws에 pc에 저장된 파일을 업로드
	// 		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
	// 			.bucket(bucketName)
	// 			.key(fileName)
	// 			.build();
	// 		PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
	// 		String s3Path = s3Client.utilities().getUrl(a -> a.bucket(bucketName).key(fileName)).toExternalForm();
	// 		product.updateImagePath(s3Path);
	// 	} catch (IOException e) {
	// 		throw new RuntimeException("이미지 저장 실패");
	// 	}
	// 	return product;
	//
	// }


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
