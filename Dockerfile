FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/paymentApp-0.0.1-SNAPSHOT.jar ./demo.paymentApp.jar
EXPOSE 8080
CMD ["java","-jar","demo-paymentApp.jar"]
