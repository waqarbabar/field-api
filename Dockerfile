FROM openjdk:11.0.6-jre-slim
CMD mkdir /opt/field-api
WORKDIR /opt/field-api
COPY target/field-api-0.0.1-SNAPSHOT.jar ./field-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT java -Dspring.datasource.url=jdbc:postgresql://field_db:5432/${POSTGRES_DB} \
           -Dspring.datasource.username=${POSTGRES_USER} \
           -Dspring.datasource.password=${POSTGRES_PASSWORD} \
           -Dspring.profiles.active=${ACTIVE_PROFILE} \
           -jar ./field-api-0.0.1-SNAPSHOT.jar
