# --- Giai đoạn 1: Build file JAR ---
# Sử dụng Maven với JDK 18 (Eclipse Temurin)
FROM maven:3.8.6-eclipse-temurin-18 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng ---
# Thay đổi quan trọng: Dùng eclipse-temurin thay vì openjdk:18-slim (đã lỗi)
FROM eclipse-temurin:18-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]