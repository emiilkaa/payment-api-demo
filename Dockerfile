FROM eclipse-temurin:11-focal
ARG APP_HOME=/app
WORKDIR $APP_HOME
COPY build/libs/payment-api-demo-0.0.1-POSTGRES.jar $APP_HOME/app.jar
ENTRYPOINT java -jar -Dspring.profiles.active=dev --add-opens java.base/java.nio=ALL-UNNAMED app.jar
EXPOSE 8080
EXPOSE 8090
