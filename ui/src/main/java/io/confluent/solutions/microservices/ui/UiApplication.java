package io.confluent.solutions.microservices.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SpringBootApplication
public class UiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UiApplication.class, args);
	}

	@GetMapping({ "/", "/account", "/wallet/{account}", "/wallet/{account}/funds/deposit",
			"/wallet/{account}/funds/withdraw", "/wallet/{currency}" })
	public String getIndexPage() {
		return "forward:/index.html";
	}
}