package io.confluent.solutions.microservices.operation.at;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OperationIntegrationTestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class AssertOperationTest {

	@Autowired
	private OperationListener listener;

	@Value("${operation.url}")
	private String operationUrl;

	@Test
	public void assertOperationAfterDeposit() throws InterruptedException {
		Thread.sleep(5000);

		FundRequest fundRequest = new FundRequest("10001", "deposit", "USD", BigDecimal.valueOf(20000), null,
				"111111000001000111");

		ResponseEntity<Void> response = new RestTemplate().exchange(operationUrl + "/funds", HttpMethod.POST,
				new HttpEntity<FundRequest>(fundRequest), Void.class);

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

		Assert.assertEquals(1, listener.getOperations().size());
	}
}
