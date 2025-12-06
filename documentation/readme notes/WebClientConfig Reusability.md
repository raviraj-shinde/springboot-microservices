# ♻️ WebClientConfig With Reusability (Employee + Student)

```java
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final LoadBalancedExchangeFilterFunction filterFunction;

    @Bean
    public WebClient commonWebClient() {
        return WebClient.builder()
                .filter(filterFunction)   // Eureka + Load Balancing
                .build();
    }

    @Bean
    public EmployeeClient employeeClient() {
        WebClient employeeWebClient = commonWebClient()
                .mutate()
                .baseUrl("http://EMPLOYEE-SERVICE")
                .build();

        WebClientAdapter adapter = WebClientAdapter.create(employeeWebClient);

        HttpServiceProxyFactory proxyFactory =
                HttpServiceProxyFactory.builder()
                        .exchangeAdapter(adapter)
                        .build();

        return proxyFactory.createClient(EmployeeClient.class);
    }

    @Bean
    public StudentClient studentClient() {
        WebClient studentWebClient = commonWebClient()
                .mutate()
                .baseUrl("http://STUDENT-SERVICE")
                .build();

        WebClientAdapter adapter = WebClientAdapter.create(studentWebClient);

        HttpServiceProxyFactory proxyFactory =
                HttpServiceProxyFactory.builder()
                        .exchangeAdapter(adapter)
                        .build();

        return proxyFactory.createClient(StudentClient.class);
    }
}
```