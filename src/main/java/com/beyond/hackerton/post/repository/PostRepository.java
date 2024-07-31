package com.beyond.hackerton.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyond.hackerton.member.domain.Member;
import com.beyond.hackerton.post.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByMember(Member member);
}
