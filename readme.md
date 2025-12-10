# Microservices Demonstration Project 
--------------------------------------------

# [ Click here - how-to-run.md](./how-to%20run.md)

--------------------------------------------

## Index

- [Project Structure](#project-structure)
- [Project Architecture](#architecture-using-spring-cloud)
- [Recap Workflow](#recap-workflow) **


0. [Zipkin Server](#zipkin-server-distributed-tracing)
1. [Config Server](#a-config-server)
2. [Service Registry (Eureka Server)](#b-service-registry-eureka-server)
3. [API Gateway](#c-api-gateway-non-reactive)
4. [Business Services & Microservices Communication](#d-business-services-and-microservices-communication)
    - [Department Service](#d-1-department-service)
    - [Employee Service](#d-2-employeee-service)

<hr>

- [Documentions Folder](./documentation/)
- [Microservice-Notes.md](./documentation/readme%20notes/Microservices-Notes.md)
- [WebClientConfig-Reusability.md](./documentation/readme%20notes/WebClientConfig%20Reusability.md)


<br>

--------------------------------------------
# Project Structure
--------------------------------------------

```
 root
    ├── config-server
    ├── service-registery (eureka server)
    ├── api-gateway
    ├── department-service
    ├── employee-service
    └──docker-compose.yml    # contains zipkin
```

<br>

--------------------------------------------
# Architecture (Using Spring Cloud)
--------------------------------------------

![architecture](./documentation/Project%20Architecture.jpg)

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

<br>

---

`⛓️‍💥NOTE:` `Ordered by execution flow; explanations are simplified instead of step-by-step.`
<br>

--------------------------------------------
# zipkin-server (Distributed Tracing)
--------------------------------------------
 
#### Running Zipkin using Docker
- `docker run -d -p 9411:9411 openzipkin/zipkin`
- Zipkin UI is accessible at: http://localhost:9411.

<br>

- Spring Boot 3 tracing does not use spring.zipkin.base-url (legacy Sleuth property, ignored in Boot 3).
- ✅ `management.zipkin.tracing.endpoint=http://<zipkin-host>:9411` `/api/v2/spans` **
- ❌ `spring.zipkin.base-url=http://<zipkin-host>:9411`


<br>

--------------------------------------------
# A. config-server
--------------------------------------------

#### Main dependency:
- Config Server : `spring-cloud-config-server`.
- No other Dependency required in this project

<br>

#### Annotation: `@EnableConfigServer`

<br>

#### Config properties 
-  1️⃣ Using Local File System (native) (Inside Project)
    ```properties
    spring.application.name=config-server
    server.port=8088

    spring.profiles.active=native **********
    ```
-  2️⃣ Using External GitHub Repo (Recommended for real setups)....

<br>

#### How to add centralized config 

- Requires the same service name as `spring.application.name`
- Config files placed under:
    ```
    resources/config
          │     │── api-gateway.properties 
          │     │── employee-service.properties
          │     └── department-service.properties
          │
          └── application.yml (config-server's self)

    ```
- Each microservice loads only its matching file.
- Here properties of `config/...-service` are shown in their respective component sections.

<br>

--------------------------------------------
# B. service-registry (Eureka Server)
--------------------------------------------

#### Main dependency:
- Eureka Server : `spring-cloud-starter-netflix-eureka-server` (Web UI comes built-in)
- No other Dependency required for this server

<br>

#### Annotations: `@EnableEurekaServer`

<br>

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

<br>

--------------------------------------------
# C. api-gateway (non-Reactive)
--------------------------------------------

#### Main dependency:
- Gateway : `spring-cloud-starter-` `gateway`
- Eureka Discovery Client : `spring-cloud-starter-` `netflix-eureka-client`

##### others - required for project
- Spring Boot Actuator
- Config Client
- Zipkin (Spring gives diffrent dipendency for diffrent versions)

<br>

#### Annotations: `@EnableDiscoveryClient`

<br>

#### properties 
```properties
spring.application.name=api-gateway
server.port=8060

# Eureka Client
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
# Spring Config
spring.config.import=optional:configserver:http://localhost:8088
# Zipkin
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
management.tracing.sampling.probability=1.0


#API-GATEWAY ********* api's for expose

#for department-service
spring.cloud.gateway.routes[1].id=department-service
spring.cloud.gateway.routes[1].uri=lb://department-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/department/**

#for employee-service
spring.cloud.gateway.routes[0].id=employee-service
spring.cloud.gateway.routes[0].uri=lb://employee-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/employee/**
```

- It exposes the api's
- Gateway is the first entry point
- It propagates Sleuth/Zipkin trace IDs
- `API Gateway → Eureka → Business Services API's`


<br>

--------------------------------------------
# D. Business Services and Microservices Communication
--------------------------------------------

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


<br>

#### Common Required Annotations Used: 
1. `@EnableDiscoveryClient`.
2. No annotation❌ is needed for Config Client.

<br>

#### common properties 
```properties
# Eureka Client
eureka.instance.hostname=localhost  #otherwise maybe error
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
# Spring Config
spring.config.import=optional:configserver:http://localhost:8088
#Zipkin 
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
management.tracing.sampling.probability=1.0    #Trace ALL(0-1.0)
```

<br>

--------------------------------------------
## D-1. department-service 
### `Calling employee-service api using WebClient (`Reactive`)`
--------------------------------------------

#### Main Dependecy: `spring-boot-starter-` `webflux`

<br>

#### remaining properties 
```properties
spring.application.name=department-service
server.port=8081
```

<br>

#### Annotations used to call emp-service api
- `@HttpExchange` and `@GetExchange,` `...`for Employee `Client interface`

<br>

#### Project Structure
```
java/department-service 
      ├── controller, model, repository etc
      ├── client  ──> EmployeeClient.java (Client-Interface)
      └── config  ──> WebClientConfig.java
```

---

<br>

- <strong> Let's call the API `findByDepartment()` of `employee-service`</strong>

<br>

#### EmployeeClient.java `Client-interface `
- `@HttpExchange` tells Spring to automatically create a dynamic proxy for the `interface`.
- Needs method declarations only.
- `@GetExchange`(`"full link"`), `@PostExchange...` - should be above method declarations.

```java
@HttpExchange
public interface EmployeeClient {

    @GetExchange("/employee/department/{departmentId}")
    public List<Employee> findByDepartment(@PathVariable Long departmentId);
}
```
<br>

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

<br>

#### Then in Controller

```java
@RequiredArgsConstructor
@RestController
@RequestMapping("/department")
public class DepartmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);
    private final DepartmentRepository departmentRepository;
    private final EmployeeClient employeeClient;

    .... //Check Project for extra api's

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

<br>

--------------------------------------------
## D-2. employeee-service
--------------------------------------------

#### Project Structure
```
java/department-service 
      ├── controller (contains findByDepartment() API)
      ├── model, repository etc
      └── service  ──> DataInitialization.java
```

- findByDepartment() is `non-reactive`
    - It returns a List<Employee>
    - That is blocking / synchronous
    - It does not stream data
    - not returns `Flux<Employee>` or `Mono<List<Employee>>`

<br>

--------------------------------------------
# RECAP WORKFLOW
--------------------------------------------

1. Start Eureka Server (service registry)
2. Start Config Server (central config)
3. Start Zipkin (tracing)
4. Run employee-service & department-service
5. Both import config from Config Server
6. Both register to Eureka
7. Both send traces to Zipkin
8. Run API Gateway
9. Registers to Eureka
10. Routes traffic using lb://

ALL IMPORTANT DASHBOARD URLs
- Eureka UI: http://localhost:8761/
- Zipkin UI: http://localhost:9411/zipkin/

<br>

--------------------------------------------
--------------------------------------------
# References and Resources:
https://www.youtube.com/watch?v=HFl2dzhVuUo&t=15s