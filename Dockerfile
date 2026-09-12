# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM amazoncorretto:25 AS builder
WORKDIR /build

RUN dnf install -y unzip tar gzip && dnf clean all

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw -q -o dependency:go-offline || ./mvnw -q dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests clean package

# ---- Runtime stage ----
FROM amazoncorretto:25-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder --chown=spring:spring /build/target/bcnc-inditex-*.jar app.jar

USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS=""

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
