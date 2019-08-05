package io.confluent.solutions.microservices.ui;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/urls")
public class UrlsController {
	private UrlsConfiguration urlsConfiguration;

	public UrlsController(UrlsConfiguration urlsConfiguration) {
		this.urlsConfiguration = urlsConfiguration;
	}

	@GetMapping
	@ResponseBody
	public UrlsConfiguration urlsConfiguration() {
		return urlsConfiguration;
	}
}
