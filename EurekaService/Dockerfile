FROM maven:3.9.16-amazoncorretto-11-al2023 as BUILD
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM amazoncorretto:11.0.32-al2-native-jdk
WORKDIR /app
COPY --from=BUILD ./build/target/*.jar ./app.jar
EXPOSE 8761
ENTRYPOINT java -jar app.jar