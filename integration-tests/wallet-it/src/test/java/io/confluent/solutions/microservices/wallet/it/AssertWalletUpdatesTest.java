package io.confluent.solutions.microservices.wallet.it;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WalletIntegrationTestsApplication.class)
public class AssertWalletUpdatesTest {
	@Value("${wallet.url}")
	private String walletUrl;

	@Autowired
	private OperationSource source;

	@Test
	public void test() {
		String uuid = UUID.randomUUID().toString();
		source.output().send(MessageBuilder
				.withPayload(new Operation(uuid, "deposit", null, null, BigDecimal.valueOf(100000), "USD")).build());

		RestTemplate restTemplate = new RestTemplate();

		Wallet wallet = restTemplate.exchange(walletUrl + "/api/wallet/" + uuid, HttpMethod.GET, null, Wallet.class)
				.getBody();

		Assert.assertEquals(
				BigDecimal.valueOf(100000).doubleValue(), wallet.getEntries().stream()
						.filter(entry -> entry.getCurrency().equals("USD")).findAny().get().getBalance().doubleValue(),
				0.0001d);
	}
}
