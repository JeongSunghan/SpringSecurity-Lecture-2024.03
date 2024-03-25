package com.example.springSecurity.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import com.example.springSecurity.entity.SecurityUser;
import com.example.springSecurity.service.SecurityUserService;
import com.example.springSecurity.util.ImageUtil;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class SecurityUserController {
	private final SecurityUserService securityService;
	private final BCryptPasswordEncoder bCryptEncoder;
	private final ImageUtil imageUtil;
	@Value("${spring.servlet.multipart.location}") private String uploadDir;
	
	@GetMapping("/list")
	public String list(@RequestParam(name = "p", defaultValue = "1") int page,
			HttpSession session, Model model){
		List<SecurityUser> securityUserList = securityService.getSecurityUserList(page);
		
		// 전체 게시물 수를 가져오기 위해 게시물 서비스의 getBoardCount 메서드 호출
				int totalUserCount = securityService.getSecurityUserCount();
				// 전체 페이지 수 계산 (한 페이지당 게시물 수를 나눈 뒤 올림 처리)
				int totalPages = (int) Math.ceil(totalUserCount / (double) securityService.COUNT_PER_PAGE);
				// 시작 페이지 계산
				int startPage = (int) Math.ceil((page - 0.5) / securityService.PAGE_PER_SCREEN - 1) * securityService.PAGE_PER_SCREEN
						+ 1;
				// 종료 페이지 계산 (총 페이지 수와 시작 페이지를 기준으로 계산)
				int endPage = Math.min(totalPages, startPage + securityService.PAGE_PER_SCREEN - 1);
				// 페이지 번호 목록 생성을 위한 리스트
				List<Integer> pageList = new ArrayList<>();
				// 시작 페이지부터 종료 페이지까지 반복하면서 페이지 목록에 추가
				for (int i = startPage; i <= endPage; i++)
					pageList.add(i);
		
		model.addAttribute("SecurityUserList", securityUserList);		
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("pageList", pageList);
		return "user/list2";
	}

	@GetMapping("/login")
	public String login() {
		return "user/login";
	}
	
	@GetMapping("/register")
	public String registerForm() {
		return "user/register";
	}
	
	@PostMapping("/register")
	public String registerProc(String uid, String pwd, String pwd2, String uname,
			String email, MultipartHttpServletRequest req, Model model) {
		String filename = null;
		MultipartFile filePart = req.getFile("picture");
		
		SecurityUser securityUser = securityService.getUserByUid(uid);
		if (securityUser != null) {
			model.addAttribute("msg", "사용자 ID가 중복되었습니다.");
			model.addAttribute("url", "/ss/user/register");
			return "common/alertMsg";
		}
		if (pwd == null || !pwd.equals(pwd2)) {
			model.addAttribute("msg", "패스워드 입력이 잘못되었습니다.");
			model.addAttribute("url", "/ss/user/register");
			return "common/alertMsg";
		}
		if (filePart.getContentType().contains("image")) {
			filename = filePart.getOriginalFilename();
			String path = uploadDir + "profile/" + filename;
			try {
				filePart.transferTo(new File(path));
			} catch (Exception e) {
				e.printStackTrace();
			}
			filename = imageUtil.squareImage(uid, filename);
		}
		String hashedPwd = bCryptEncoder.encode(pwd);
		securityUser = SecurityUser.builder()
				.uid(uid).pwd(hashedPwd).uname(uname).email(email).provider("ck world")
				.picture("/ss/file/download/profile/" + filename)
				.build();
		securityService.insertSecurityUser(securityUser);
		model.addAttribute("msg", "등록을 마쳤습니다. 로그인하세요.");
		model.addAttribute("url", "/ss/user/login");
		return "common/alertMsg";
	}
	
	@ResponseBody
	@GetMapping("/loginSuccess")
	public String loginSuccess() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//세션의 현재 사용자 아이디
		String uid = authentication.getName();	
		
		return "loginSuccess - " + uid;
	}
}