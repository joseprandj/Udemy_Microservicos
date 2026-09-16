FROM maven:3.9.16-amazoncorretto-11-al2023 as BUILD
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM amazoncorretto:11.0.32-al2-native-jdk
WORKDIR /app
COPY --from=BUILD ./build/target/*.jar ./app.jar
ARG RABBITMQ_SERVER=rabbitmq-host
ARG EUREKA_SERVER=localhost
ENTRYPOINT java -jar app.jar