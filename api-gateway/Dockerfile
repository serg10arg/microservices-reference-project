# Usamos Java 17 (OpenJDK)
FROM openjdk:17-jdk-slim

LABEL maintainer="Architecture Team"

WORKDIR /app

# Copiamos el artefacto
COPY target/api-gateway-1.0.0-SNAPSHOT.jar app.jar

# Exponemos el puerto del Gateway
# Nota: Si usaste puerto 80 en properties, cambia esto a 80.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
