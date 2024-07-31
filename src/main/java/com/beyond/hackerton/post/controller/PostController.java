package com.beyond.hackerton.post.controller;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.beyond.hackerton.common.dto.CommonResDto;
import com.beyond.hackerton.post.dto.PostResDto;
import com.beyond.hackerton.post.dto.PostSaveDto;
import com.beyond.hackerton.post.service.PostService;

@RestController
public class PostController {

	private final PostService postService;

	@Autowired
	public PostController(PostService postService) {
		this.postService = postService;
	}

	// 	게시글 작성
	@PostMapping("/post/create")
	public ResponseEntity<?> postCreate(@RequestBody PostSaveDto postSaveDto,
		@RequestPart(value = "file", required = false) List<MultipartFile> files) {
		return new ResponseEntity<CommonResDto>(
			new CommonResDto(HttpStatus.OK, "게시글 작성 성공", postService.postCreate(postSaveDto, files)), HttpStatus.OK);
	}

	// @PostMapping("/product/create/aws")
	// public ResponseEntity<?> createAwsProduct(@ModelAttribute ProductSaveReqDto dto) {
	// 	Product product = productService.createAwsProduct(dto);
	// 	return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED, "product is successfully created", product),
	// 		HttpStatus.CREATED);
	// }

	// 	게시글 삭제
	@DeleteMapping("/post/delete")
	public ResponseEntity<?> postDelete(@PathVariable Long postId){
		return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "내가 쓴 글", postService.postDelete(postId).getId()),
			HttpStatus.OK);
	}

	// 	자신의 글 조회
	@GetMapping("/post/myPosts")
	public ResponseEntity<?> myPosts() {
		List<PostResDto> posts = postService.myPosts();
		return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "내가 쓴 글", posts),
			HttpStatus.OK);
	}
	// 	글 전체 조회

}
