package com.msntt.MSAccountService;

import com.msntt.MSAccountService.infraestructure.restclient.IBusinessPartnerClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MsAccountServiceApplicationTests {
	@Autowired
	IBusinessPartnerClient client;
	@Test
	public void testOpenFeign(){
		assertNotNull(client.findById("P08652725").block());
	}
}
