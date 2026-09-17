FROM maven:3.9.16-amazoncorretto-17-al2023 as BUILD
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.20-al2-native-jdk
WORKDIR /app
COPY --from=BUILD ./build/target/*.jar ./app.jar
ARG EUREKA_SERVER_USER=ms-eureka-user
ARG EUREKA_SERVER_PASS=123456
EXPOSE 8761
ENTRYPOINT java -jar -Dspring.profiles.active=production app.jar


