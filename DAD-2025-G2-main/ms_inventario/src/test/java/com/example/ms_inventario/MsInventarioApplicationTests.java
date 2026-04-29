package com.example.ms_inventario;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"feign.producto.url=http://localhost:8080"
})
class MsInventarioApplicationTests {

	@Test
	void contextLoads() {
	}
}