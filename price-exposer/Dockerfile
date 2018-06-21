FROM openjdk:8-jdk-alpine AS base_alpine_jdk8
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} priceexposer.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/priceexposer.jar"]