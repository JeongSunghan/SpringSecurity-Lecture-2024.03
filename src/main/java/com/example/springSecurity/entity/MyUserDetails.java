package com.example.springSecurity.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.RequiredArgsConstructor;

// Spring Security가 로그인 post 요청을 낚아채서 로그인을 진행
// 로컬 로그인 implements -> UserDetails 구현
// 소셜 로그인 implements -> OAuth2User 구현

public class MyUserDetails implements UserDetails, OAuth2User{
	private SecurityUser securityUser;		//로컬일때는 괜찮지만, 소셜일때는 final 사용이 안됨
	private Map<String, Object> attributes;
	
	
	//생성자
	public MyUserDetails() {}
	
	//로컬 로그인 - 스프링이 생성자 방식으로 의존성 주입
	public MyUserDetails(SecurityUser securityUser) {
		this.securityUser = securityUser;
	}
	
	//소셜 로그인 - 로컬과 동일함
	public MyUserDetails(SecurityUser securityUser, Map<String, Object> attributes) {
		this.securityUser = securityUser;
		this.attributes = attributes;
	}
	

	//사용자의 권한을 저장할 컬렉션 (관리자 혹은 사용자냐?)
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {		//열쇠모음 느낌
		//GrantedAuthority를 상속받은 놈 = ?
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {

			@Override
			public String getAuthority() {
				return securityUser.getRole();
			}
			
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return securityUser.getPwd();
	}

	@Override
	public String getUsername() {		//username이 아닌 uid
		return securityUser.getUid();
	}

	@Override
	public boolean isAccountNonExpired() {		//계정이 만료 되었는지
		if (securityUser.getIsDeleted() == 0)
			return true;
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {		//계정이 잠겨있는지
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {		//계정 인증정보가 면료되었는지
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {		
		return attributes;
	}

	@Override
	public String getName() {
		return null;
	}
	
	public SecurityUser getSecurityUser() {
		return securityUser;
	}

}
