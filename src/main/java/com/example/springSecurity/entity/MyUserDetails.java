package com.example.springSecurity.entity;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.RequiredArgsConstructor;

// Spring Security가 로그인 post 요청을 낚아채서 로그인을 진행
// 로컬 로그인 implements -> UserDetails 구현
// 소셜 로그인 implements -> OAuth2User 구현

@RequiredArgsConstructor
public class MyUserDetails implements UserDetails{
	private final SecurityUser securityUser;

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

}
