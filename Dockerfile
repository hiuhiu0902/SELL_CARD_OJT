# --- Giai đoạn 1: Build file JAR bằng Maven và JDK 18 ---
FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /app
COPY . .
# Lệnh này sẽ đóng gói code thành file .jar và bỏ qua bước test để build nhanh hơn
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng bằng JDK 18 ---
FROM openjdk:18-slim
WORKDIR /app
# Copy file .jar từ giai đoạn build sang giai đoạn chạy
COPY --from=build /app/target/*.jar app.jar

# Spring Boot mặc định chạy cổng 8080
EXPOSE 8080

# Lệnh chạy app
ENTRYPOINT ["java","-jar","app.jar"]