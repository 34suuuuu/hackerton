package com.beyond.hackerton.member.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.beyond.hackerton.common.domain.BaseTimeEntity;
import com.beyond.hackerton.post.domain.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Role role = Role.USER;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Grade grade = Grade.BRONZE;

	@OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
	@Builder.Default
	private List<Post> posts = new ArrayList<>();

	public void updateGrade() {
		if (posts.size() < 10) {
			this.grade = Grade.BRONZE;
		} else if (posts.size() < 30) {
			this.grade = Grade.SILVER;
		} else {
			this.grade = Grade.GOLD;
		}
	}
}
