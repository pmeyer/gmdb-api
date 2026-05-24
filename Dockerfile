# syntax=docker/dockerfile:1

FROM eclipse-temurin:23-jre

WORKDIR /app

ARG JAR_FILE

COPY ${JAR_FILE} /app/gmdb-api.jar

ENV FILE_SERVICE_ROOT=/var/lib/gmdb/file-repo \
    SERVER_PORT=8080 \
    SPRING_R2DBC_MYBATIS_USERNAME=gmdb_app_user

RUN mkdir -p /var/lib/gmdb/file-repo

VOLUME ["/var/lib/gmdb/file-repo"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/gmdb-api.jar"]
