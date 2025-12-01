# --- Giai đoạn 1: Build file JAR ---
# Sửa thành JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng ---
# Sửa thành JDK 17 (Bản Alpine siêu nhẹ)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]