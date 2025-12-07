# How to Run

#### 1. Build all microservice `JARs`
- Run inside each service directory:
    
```
cd service-registry
./mvnw clean package -DskipTests
cd ..

cd config-server
./mvnw clean package -DskipTests
cd ..

cd employee-service
./mvnw clean package -DskipTests
cd ..

cd department-service
./mvnw clean package -DskipTests
cd ..

cd api-gateway
./mvnw clean package -DskipTests
cd ..
```

### OR

- Run script `build-all jars.bat` (Only for windows)
- bat content txt - [Check here](./documentation/build-all%20jars%20-%20bat%20text.txt)

----------------------

#### 2. Start entire architecture using Docker Compose
 - From project root:
    ```docker-compose up --build```

----------------------


#### 3. Access dashboards and APIs

Eureka Dashboard: ```http://localhost:8761/```
Zipkin Dashboard: ```http://localhost:9411/zipkin/```


----------------------


#### 4. API Gateway (entry point for all business APIs): 
```http://localhost:8060/```
- API Gateway → Eureka → Business Service


----------------------


#### 5. Stop all services
```docker-compose down```