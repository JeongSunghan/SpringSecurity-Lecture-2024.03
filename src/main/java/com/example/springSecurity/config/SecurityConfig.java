package com.example.springSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	//글에 선을 그어져있으면, 람다함수 사용
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		// 괄호 안에 람다함수를 사용해야 함
		http.csrf(auth -> auth.disable())
				
			.headers(x -> x.frameOptions(y -> y.disable())) //CK Editor image upload
			.authorizeHttpRequests(auth -> auth
					
					//튕겨서 다시 forword 유형은 통과시켜준다 => premitAll
					.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
					
					//지정된 경로는 인증 없이 누구는 접근허용
					.requestMatchers("user/register",
							"/img/**", "/css/**", "/js/**", "/error/**").permitAll()
					
					//어드민 권한을 갖고 있는 사람만 들어갈 수 있다. (열쇠 혹은 허가증이라 생각하기)
					.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
					
					//위에 지정된 경로를 제외한 나머지는 인증된 사용자만 받는다.
					.anyRequest().authenticated()														
			)							
			.formLogin(auth -> auth
				.loginPage("/user/login")			// 로그인 폼
				.loginProcessingUrl("/user/login")	//내가 만든 게 아닌 스프링 시큐리티가 낚아챈다. UserDetailsService 구현객체에서 처리해야 함.				
				.usernameParameter("uid")
				.passwordParameter("pwd")
				.defaultSuccessUrl("/user/loginSuccess", true)	// 내가 로그인 후 해야할 일, 세션 세팅, 오늘의 메시지 등등
																//사용자가 로그인에 성공했을 때 지정된 URL로 리다이렉션
				.permitAll()				
			)
			.logout(auth -> auth
				.logoutUrl("/user/logout")
				.invalidateHttpSession(true)		// 로그아웃시 세션을 초기화
				.deleteCookies("JSESSIONID")		//로그아웃시 쿠키 삭제
				.logoutSuccessUrl("/user/login")
			);
		;
		
		return http.build();
	}
	

}
