# ==============================================================================
# ACTIVIDAD 3: DOCKERFILE MULTI-ETAPA (JAVA + MYSQL)
# Automatización de Compilación, Pruebas Unitarias (JUnit 5) y Despliegue Java
# Alumno: Adrián González Gil | Módulo: Introducción a DevOps (2º DAM)
# ==============================================================================

# ------------------------------------------------------------------------------
# ETAPA 1: BUILD & TEST (Compilación y Validación Automatizada)
# ------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# 1. Copiar pom.xml y descargar dependencias (incluyendo mysql-connector-j)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copiar el código fuente y los tests JUnit
COPY src ./src

# 3. Compilar, ejecutar tests unitarios y empaquetar el JAR ejecutable
RUN mvn clean test package -B

# ------------------------------------------------------------------------------
# ETAPA 2: RUNTIME / DESPLIEGUE (Imagen Final Ultraligera)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Adrián González Gil"
LABEL description="Aplicación Java de Gestión de Servidores DevOps con MySQL"
LABEL version="1.0.0"

WORKDIR /app

# Usuario no privilegiado por seguridad
RUN addgroup -S devopsgroup && adduser -S devopsuser -G devopsgroup

# Copiar únicamente el JAR ejecutable generado en la Etapa 1
COPY --from=builder --chown=devopsuser:devopsgroup /build/target/gestion-servidores-devops-1.0.0.jar app.jar

USER devopsuser

ENTRYPOINT ["java", "-jar", "app.jar"]
