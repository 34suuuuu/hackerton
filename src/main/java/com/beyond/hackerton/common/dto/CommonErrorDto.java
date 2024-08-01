package com.beyond.hackerton.common.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonErrorDto {
	private int status;
	private String message;

	public CommonErrorDto(HttpStatus status, String message) {
		this.status = status.value();
		this.message = message;
	}
}
