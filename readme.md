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

![architecture](./images/Project%20Architecture.png)

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

## A. zipkin-server

---

## B. config-server
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

## C. eureka-server
--------------------------------------------

#### Main dependency:
- Eureka Server : `spring-cloud-starter-netflix-eureka-server` (Web UI comes built-in)
- No other Dependency required in this project
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



## D. api-gateway


---
#### Main dependency:
- Gateway : `spring-cloud-starter-gateway`
- Eureka Client : `spring-cloud-starter-netflix-eureka-client` (to discover services)
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


## A. eureka-server

---
















---
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