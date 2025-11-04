
FROM eclipse-temurin:11-jre
ARG PROJECT_VERSION=0.1.0
RUN mkdir -p /home/app
WORKDIR /home/app
ENV SPRING_PROFILES_ACTIVE dev
COPY payment-service/ .
ADD payment-service/target/*.jar payment-service.jar
EXPOSE 8400
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "payment-service.jar"]


