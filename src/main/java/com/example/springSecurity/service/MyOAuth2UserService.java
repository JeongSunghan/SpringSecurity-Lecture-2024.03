package com.example.springSecurity.service;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.springSecurity.entity.MyUserDetails;
import com.example.springSecurity.entity.SecurityUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyOAuth2UserService extends DefaultOAuth2UserService {
	private final SecurityUserService securityService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	// Provide(구글, 깃허브)로 부터받은 userRequest 데이터에 대해 후처리하는 메소드
	@Override
	// 소셜 로그인 정보를 담는 메서드
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		String uid, email, uname, picture;
		String hashedPwd = bCryptPasswordEncoder.encode("Social Login");
		SecurityUser securityUser = null;

		OAuth2User oAuth2User = super.loadUser(userRequest); // 소셜에서의 사용자 정보를 가져오는 요청
		log.info("getAttributes(): " + oAuth2User.getAttributes());

		String provider = userRequest.getClientRegistration().getRegistrationId();
		switch (provider) {
		case "google": // id(int), name, picture, email
			String gid = oAuth2User.getAttribute("sub");
			uid = provider + "_" + gid;
			securityUser = securityService.getUserByUid(uid); // 이미 가입된 사용자인지 확인
			if (securityUser == null) { // 가입이 안되어 있으므로 가입 진행
				
				// 사용자 정보 가져오기
				uname = oAuth2User.getAttribute("name");
				uname = (uname == null) ? "google_user" : uname;
				email = oAuth2User.getAttribute("email");
				picture = oAuth2User.getAttribute("picture");
				
				// 새로운 계정 생성 및 DB 저장
				securityUser = SecurityUser.builder()
						.uid(uid).pwd(hashedPwd).uname(uname).email(email).picture(picture)
						.provider(provider).build();
				securityService.insertSecurityUser(securityUser);
				
				// 저장된 사용자 정보 다시 불러오기
				securityUser = securityService.getUserByUid(uid);
				log.info("구글 계정을 통해 회원가입이 되었습니다.");
			}

			break;

		case "github": // id(int) , name, picture, email
			int id = oAuth2User.getAttribute("id");
			uid = provider + "-" + id;
			securityUser = securityService.getUserByUid(uid); 
			if (securityUser == null) { 
				uname = oAuth2User.getAttribute("name");
				uname = (uname == null) ? "github_user" : uname;
				email = oAuth2User.getAttribute("email");
				picture = oAuth2User.getAttribute("avatar_url");

				securityUser = SecurityUser.builder()
						.uid(uid).pwd(hashedPwd).uname(uname).email(email).picture(picture)
						.provider(provider).build();
				securityService.insertSecurityUser(securityUser);
				securityUser = securityService.getUserByUid(uid);
				log.info("깃허브 계정을 통해 회원가입이 되었습니다.");
			}
			break;

		case "naver":
			Map<String, Object> response = (Map) oAuth2User.getAttribute("response");
			String nid = (String) response.get("id");
			uid = provider + "_" + nid;
			securityUser = securityService.getUserByUid(uid);
			if (securityUser == null) { 
				uname = (String) response.get("nickname");
				uname = (uname == null) ? "naver_user" : uname;
				email = (String) response.get("email");
				picture = (String) response.get("profile_image");

				securityUser = SecurityUser.builder()
						.uid(uid).pwd(hashedPwd).uname(uname).email(email).picture(picture)
						.provider(provider).build();
				securityService.insertSecurityUser(securityUser);
				securityUser = securityService.getUserByUid(uid);
				log.info("네이버 계정을 통해 회원가입이 되었습니다.");
			}
			
			break;

		case "kakao": // id(int), name, picture, email
			long kid = (long) oAuth2User.getAttribute("id");
			uid = provider + "_" + kid;
			securityUser = securityService.getUserByUid(uid);
			if (securityUser == null) { 
				Map<String, String> properties = (Map) oAuth2User.getAttribute("properties");
				//account는 권한을 받아야 함			
//				Map<String, Object> account = (Map) oAuth2User.getAttribute("kakao_account");
				uname = (String) properties.get("nickname");
				uname = (uname == null) ? "kakao_user" : uname;
//				email = (String) account.get("email");
				email = "sh2683@naver.com";
				picture = (String) properties.get("profile_image");

				securityUser = SecurityUser.builder()
						.uid(uid).pwd(hashedPwd).uname(uname).email(email).picture(picture)
						.provider(provider).build();
				securityService.insertSecurityUser(securityUser);
				securityUser = securityService.getUserByUid(uid);
				log.info("카카오 계정을 통해 회원가입이 되었습니다.");
			}
			break;

		}
		return new MyUserDetails(securityUser, oAuth2User.getAttributes());
	}

}
