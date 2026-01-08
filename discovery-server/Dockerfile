# Usamos una imagen base ligera de Java 17 (coincidiendo con el POM padre)
FROM openjdk:17-jdk-slim

# Metadatos del mantenedor
LABEL maintainer="Architecture Team"

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el artefacto generado (el JAR) al contenedor
# Nota: El nombre del jar dependerá de tu build, aquí asumimos el estándar
COPY target/discovery-server-1.0.0-SNAPSHOT.jar app.jar

# Exponemos el puerto definido en application.properties
EXPOSE 8761

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
