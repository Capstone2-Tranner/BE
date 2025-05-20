package com.tranner.external_api_proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = "com.tranner.external_api_proxy")
public class ExternalApiProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExternalApiProxyApplication.class, args);
	}

}
