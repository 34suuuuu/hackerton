package com.beyond.ordersystem.common.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.beyond.ordersystem.common.auth.JwtAuthFilter;

@Configuration
@EnableWebSecurity	// security 관련한 코드
@EnableGlobalMethodSecurity(prePostEnabled = true)	// pre: 사전 검증, post: 사후 검증
public class SecurityConfigs {

	@Autowired
	private JwtAuthFilter jwtAuthFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
			.csrf().disable()
			.cors().and()//CORS 활성화
			.httpBasic().disable()
			.authorizeRequests()
			.antMatchers("/member/create", "/", "/doLogin", "/refresh-token", "/product/list")
			.permitAll()
			.anyRequest().authenticated()
			.and()
			// 세션 로그인이 아닌 stateless한 토큰 로그인을 사용하겠다는 의미
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			// 로그인시 사용자는 서버로부터 토큰을 발급받고, 매요청마다 해당 토큰을 http 헤더에 넣어 요청
			// 사용자로부터 받아온 토큰이 정상인지 아닌지를 검증하는 코드
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}



}
