package com.nagarro.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;

import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
	
	@Bean
    public HttpClientCustomizer httpClientResolverCustomizer() {
        return new HttpClientCustomizer() {

            @Override
            public HttpClient customize(HttpClient httpClient) {
                return httpClient.resolver(DefaultAddressResolverGroup.INSTANCE);
            }
        };
    }

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
