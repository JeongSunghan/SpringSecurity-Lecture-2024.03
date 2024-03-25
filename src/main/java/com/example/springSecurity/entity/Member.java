package com.example.springSecurity.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {
	//lombok 을 사용하면 편하게 생성자, toString, get/setter 를 만들기 편하다.
	private int mid;
	private String name;
	private LocalDate regDate;
	private String email;
	
	//기본 생성자 = NoArgsConstructor
	
}
