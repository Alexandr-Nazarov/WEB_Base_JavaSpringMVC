package org.example.frontend.rest_client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class BackServiceConfig {

    @Value("${back-service.base-url}")
    private String baseUrl;

    @Value("${back-service.timeouts.connect}")
    private Duration connectTimeout;

    @Bean
    public RestClient backRestClient(){
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
