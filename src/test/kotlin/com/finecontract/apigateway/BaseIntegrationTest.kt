package com.finecontract.apigateway

import com.mongodb.assertions.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class BaseIntegrationTest {

	@Test
	fun `should load context`() {
		assertTrue(true)
	}
}
