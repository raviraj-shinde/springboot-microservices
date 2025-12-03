package com.ravi.department_service.config;

import com.ravi.department_service.client.EmployeeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final LoadBalancedExchangeFilterFunction filterFunction;

    @Bean
    public WebClient employeeWebClient(){
        return WebClient
                .builder()
                .baseUrl("http://EMPLOYEE-SERVICE")  // ✅ service name registered in Eureka
                .filter(filterFunction)
                .build();
    }

    @Bean
    public EmployeeClient employeeClient(){
        WebClientAdapter adapter = WebClientAdapter.create(employeeWebClient());

        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory
                        .builder()
                        .exchangeAdapter(adapter)
                        .build();

        return httpServiceProxyFactory.createClient(EmployeeClient.class);
    }
}
