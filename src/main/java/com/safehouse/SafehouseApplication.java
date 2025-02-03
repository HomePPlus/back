package com.safehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling	// 이메일 토큰 만료 시 삭제
public class SafehouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafehouseApplication.class, args);
	}

}```