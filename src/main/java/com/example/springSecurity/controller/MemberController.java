package com.example.springSecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.springSecurity.entity.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j		//log 를 선언하지 않고 사용가능
@Controller
@RequestMapping("/member")
public class MemberController {
	//서버 접속 => filter 에 들어가서 자동으로 로그인창이 나옴 => 마음대로 액세스 불가
	
	@ResponseBody
	@GetMapping("/detail/{mid}")
	public String detail(@PathVariable int mid) {
		Member member = new Member();
		//자동으로 만들어주지만 내가 다른 생성자를 만들면 다시 만들어야 함.
		log.info("detail");
		return "";
	}
	
	@ResponseBody
	@GetMapping("/insert")
	public String insert() {
		Member m1 = new Member();
		m1.setName("James"); 
		m1.setEmail("james@gmail.com");
		log.info(m1.toString());
		//Builder pattern 으로 생성자 만들기
		Member m2 = Member.builder()	//static method;
				//원하는 filed 값 세팅 하기
				.name("Maria").email("maria@naver.com")
				.build();
		log.info(m2.toString());
		return m1.toString() + "<br>" + m2.toString();
	}
	
	@ResponseBody
	@GetMapping("/update")
	public String update() {
		Member member = Member.builder()
				.mid(1).name("Brian").email("brian@human.com")
				.build();		
		log.info(member.toString());
		return member.toString();
	}

}
