package com.example.ms_compra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.ms_compra.feign")
public class MsCompraApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsCompraApplication.class, args);
	}
}