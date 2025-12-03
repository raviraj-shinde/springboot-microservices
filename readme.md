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