package com.beyond.hackerton.common.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResDto {
	private int status;
	private String message;
	private Object result;

	public CommonResDto(HttpStatus status, String message, Object result) {
		this.status = status.value();
		this.message = message;
		this.result = result;
	}
}
