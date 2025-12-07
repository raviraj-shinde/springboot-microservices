@echo off
echo Building service-registry...
cd service-registry
call .\mvnw clean package -DskipTests
cd ..

echo Building config-server...
cd config-server
call .\mvnw clean package -DskipTests
cd ..

echo Building employee-service...
cd employee-service
call .\mvnw clean package -DskipTests
cd ..

echo Building department-service...
cd department-service
call .\mvnw clean package -DskipTests
cd ..

echo Building api-gateway...
cd api-gateway
call .\mvnw clean package -DskipTests
cd ..

echo All build attempts complete.