# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM amazoncorretto:25 AS builder
WORKDIR /build

RUN dnf install -y unzip tar gzip && dnf clean all

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q -DskipTests package

# ---- Runtime stage ----
FROM amazoncorretto:25-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder --chown=spring:spring /build/target/bcnc-inditex-*.jar app.jar

USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
