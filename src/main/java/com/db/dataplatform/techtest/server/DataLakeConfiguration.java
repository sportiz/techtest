package com.db.dataplatform.techtest.server;

import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@EnableRetry
@Configuration
@RequiredArgsConstructor
public class DataLakeConfiguration {

    private final RestTemplateBuilder restTemplateBuilder;
    @Bean
    @Qualifier("serverRestTemplate")
    public RestTemplate serverRestTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
                                           StringHttpMessageConverter stringHttpMessageConverter) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        CloseableHttpClient client = HttpClients.createDefault();
        RestTemplate template= new RestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        template.setMessageConverters(Arrays.asList(mappingJackson2HttpMessageConverter, stringHttpMessageConverter));

        return restTemplate;
    }
}
