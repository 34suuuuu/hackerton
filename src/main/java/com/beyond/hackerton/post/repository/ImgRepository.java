package com.beyond.hackerton.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyond.hackerton.post.domain.Img;

@Repository
public interface ImgRepository extends JpaRepository<Img, Long> {
}
