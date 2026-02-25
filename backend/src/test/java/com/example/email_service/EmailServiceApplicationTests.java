package com.example.email_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Disabled context load test for CI")
class EmailServiceApplicationTests {

	@Test
	void contextLoads() {
		// intentionally disabled
	}
}