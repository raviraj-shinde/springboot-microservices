# Microservices Demonstration Project 
---

## Index
1. Project Readme
2. Microservices Readme

## Project Structure
```
microservices-root/
├── config-server
├── eureka-server
├── api-gateway
├── department-service
├── employee-service
├── docker-compose.yml    # contains zipkin service + others
└── README.md
```
---

## Architecture (Using Spring Cloud)

![architecture](./documentation/Project%20Architecture.png)

---

##### A. Service Registry (Eureka / Netflix OSS)
- Keeps track of all running microservice instances.
- Helps in load balancing, scaling, and auto-discovery of services.

##### B. Config Server
- Provides a central place to store shared configs for all microservices.
- Allows updating configs without rebuilding every service.

##### C. API Gateway
- Single entry point for all client requests.
- Handles routing, filtering, authentication, rate-limiting, etc.

##### D. Zipkin (Distributed Tracing)
- Captures and shows how a request flows across microservices.
- Provides a dashboard to analyze latency, errors, and cross-service logs.

##### E. Business Services (Employee-Service, Department-Service)
- Each service contains its own domain logic and database (if needed).
- They focus purely on business functionality.

---

`⛓️‍💥NOTE:` `Ordered by execution flow; explanations are simplified instead of step-by-step.`

---

## zipkin-server

---

## A. config-server
---

#### Main dependency:
- Config Server : `spring-cloud-config-server` (Web UI comes built-in)
- No other Dependency required in this project
---

#### Annotations: `@EnableConfigServer`

---

#### How to add centralized config 

- Requires the same service name as `spring.application.name`
- Config files placed under:
    ```
    resources/config
                │── application.yml (config-server's self)
                │── employee-service.properties
                │── department-service.properties
    ```
- Each microservice loads only its matching file.
- Here properties of `config/...-service` are shown in their respective component sections.

---

#### Config properties 
-  1️⃣ Using Local File System (native) (Inside Project)
    ```properties
    spring.application.name=config-server
    server.port=8088

    spring.profiles.active=native **********
    ```
-  2️⃣ Using External GitHub Repo (Recommended for real setups)....

--------------------------------------------

## B. eureka-server
--------------------------------------------

#### Main dependency:
- Eureka Server : `spring-cloud-starter-netflix-eureka-server` (Web UI comes built-in)
- No other Dependency required in this service/server
---

#### Annotations: `@EnableEurekaServer`

---

#### Server Properties 
- Register itself🫡 as a `server`.

```properties
spring.application.name=service-registry
server.port=8761

eureka.instance.hostname=localhost //Most-IMP
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka/
```
---



## C. api-gateway

---
#### Main dependency:
- Gateway : `spring-cloud-starter-gateway`

##### others - required for project
- Eureka Discovery Client 
- Spring Boot Actuator
- Config Client
- Zipkin (Spring Gives diffrent dipendency for diffrent versions)

---

#### Annotations: `@EnableConfigServer`

---

#### How to add centralized config 

- Requires the same service name as `spring.application.name`
- Config files placed under:
    ```
    resources/config
                │── application.yml (config-server's self)
                │── employee-service.properties
                │── department-service.properties
    ```
- Each microservice loads only its matching file.
- Here properties of `config/...-service` are shown in their respective component sections.

---

#### properties 
```properties
spring.application.name=api-gateway
server.port=8060

# Eureka Client
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
# Spring Config
spring.config.import=optional:configserver:http://localhost:8088
# Zipkin
management.tracing.sampling.probability=1.0


#API-GATEWAY *********

#for employee-service
spring.cloud.gateway.routes[0].id=employee-service
spring.cloud.gateway.routes[0].uri=lb://employee-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/employee
spring.cloud.gateway.routes[0].predicates[1]=Path=/employee/**

#for department-service
spring.cloud.gateway.routes[1].id=department-service
spring.cloud.gateway.routes[1].uri=lb://department-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/department
spring.cloud.gateway.routes[1].predicates[1]=Path=/department/**
```
--------------------------------------------


## D. Business Services and Microservices Communication

---
- Here are some common things `required` for to run the buisiness services `in microservices`.

#### Common dependencies for `buisness-micro-services`, To work:

##### 1. main
- `Eureka Discovery Client`***
- `Config Client`
- `Zipkin` 

##### 2. others - required for services/project
- `Spring web` - for api's
- Lombok - reduces boilerplate code
- Spring Boot Actuator



---

#### Common Required Annotations Used: 
1. `@EnableDiscoveryClient`
2. No annotation❌ is needed for Config Clients

------------------

#### common properties 
```properties
# Eureka Client
eureka.instance.hostname=localhost  #🙏🏻otherwise.. error🫡
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
# Spring Config
spring.config.import=optional:configserver:http://localhost:8088
#Zipkin 
spring.zipkin.base-url=http://localhost:9411
management.tracing.sampling.probability=1.0    #Trace ALL(0-1.0)
```

------------------

### D-1. department-service calling employee-service api using WebClient (`Reactive`)
---

#### remaining properties 
```properties
spring.application.name=department-service
server.port=8081
```

#### Annotations used to call emp-service api
- `@HttpExchange` and `@GetExchange,` `...`for Client interface

#### Project Structure
```
java/department-service 
      ├── controller, model, repository etc
      ├── client  ──> EmployeeClient.java
      └── config  ──> WebClientConfig.java
```

#### EmployeeClient.java `@HttpExchange - interface `
- `@HttpExchange` tells Spring to automatically create a dynamic proxy for the `interface`.
- `@GetExchange("full link"), @PostExchange...` - above methods.

```java
@HttpExchange
public interface EmployeeClient {

    @GetExchange("/employee/department/{departmentId}")
    public List<Employee> findByDepartment(@PathVariable Long departmentId);
}
```

#### WebClientConfig.java `Creating a @Beans`

```java
@Configuration 
@RequiredArgsConstructor
public class WebClientConfig {

    private final LoadBalancedExchangeFilterFunction filterFunction;

    @Bean
    public WebClient employeeWebClient(){
        return WebClient.builder()
                .baseUrl("http://EMPLOYEE-SERVICE")  // ✅ service name registered in Eureka
                .filter(filterFunction) //resolve the service name through Eureka instead of DNS.
                .build();
    }

    @Bean
    public EmployeeClient employeeClient(){
        WebClientAdapter adapter = WebClientAdapter.create(employeeWebClient());

        HttpServiceProxyFactory proxyFactory =
                proxyFactory.builder()
                        .exchangeAdapter(adapter)
                        .build();

        return proxyFactory.createClient(EmployeeClient.class);
    }
}
```
- [WebClientConfig With Reusability](./documentation/readme%20notes/WebClientConfig%20Reusability.md) -  Click (readme)
- HttpServiceProxyFactory `turns the interface` EmployeeClient into a runtime HTTP client.
- `The final result:` EmployeeClient looks like a normal Java interface, but under the hood it uses `WebClient + Eureka + Load Balancing + dynamic proxy` for HTTP calls.

#### Then in Controller

```java
@RequiredArgsConstructor
@RestController
@RequestMapping("/department")
public class DepartmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);
    private final DepartmentRepository departmentRepository;
    private final EmployeeClient employeeClient;

    .... //Check Project for other code

    @GetMapping("/{deptId}/employees")
    public ResponseEntity<Department> getEmployeesByDepartment(@PathVariable Long deptId){
        LOGGER.info("getEmployeesByDepartment department id = {}", deptId);
        Department department = departmentRepository.findById(deptId);
        department.setEmployees(employeeClient.findByDepartment(deptId));
        return ResponseEntity.ok().body(department);
    }
}

```

- `LOGGER.info("")`:- Why we have added? :- for Zipkin, so that we can track the flow in microservices.
- Now you can get a bean of `EmployeeClient employeeClient` as we have created it, in config and client folders.
- Here `Department` is a [model (click here to see code)](./department-service/src/main/java/com/ravi/department_service/model/Department.java)



--------------------------------------------














--------------------------------------------

# Other Notes




1️⃣ RestTemplate is old, blocking, and officially deprecated — not recommended for new microservices.
2️⃣ WebClient is modern, non-blocking, reactive, and ideal for high-performance or external API calls.
3️⃣ OpenFeign is declarative, simple, and the preferred choice for microservice-to-microservice communication.
4️⃣ Feign integrates perfectly with Spring Cloud (Eureka, Load Balancing, Resilience4j).
5️⃣ WebClient offers full control and supports streaming, async calls, and better scalability.
6️⃣ Real-world rule: Use Feign inside microservices; use WebClient for external APIs; avoid RestTemplate for new development.


-- Each service runs separately but needs to talk to each other.
-- Spring Cloud gives solutions for common problems in microservices:

🧰 Main Components of Spring Cloud (Super Simple)
1️⃣ Eureka Server (Service Registry)
2️⃣ Spring Cloud Config Server    
3️⃣ Spring Cloud Gateway (API Gateway)
4️⃣ Spring Cloud OpenFeign (Client-to-client calls) / WebClient / RestTemplate
5️⃣ Resilience4j (Circuit Breaker)
6️⃣ Zipkin + Sleuth (Distributed Tracing)

```
A typical Spring Cloud microservices setup:
Gateway ← first entry
Eureka ← service registry
Config Server ← central configs
Microservices ← Employee/Department/etc
Feign ← service calls
Sleuth + Zipkin ← tracing
Resilience4j ← fault handling
```