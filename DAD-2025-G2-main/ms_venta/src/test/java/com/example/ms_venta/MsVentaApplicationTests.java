package com.example.ms_venta;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"feign.cliente.url=http://localhost:8081",
		"feign.producto.url=http://localhost:8082",
		"feign.inventario.url=http://localhost:8083"
})
class MsVentaApplicationTests {

	@Test
	void contextLoads() {
	}
}