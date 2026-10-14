# Para publicar el juego en Render (u otro servicio que use Docker).
# 1) Compila el proyecto con Maven. 2) Lo corre con Java 17.
# Render pone el https por su cuenta, así que acá el juego corre sin https,
# en el puerto que Render indica en la variable PORT.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 10000
CMD ["sh", "-c", "java -Dserver.port=${PORT:-10000} -Dserver.ssl.enabled=false -jar app.jar"]
