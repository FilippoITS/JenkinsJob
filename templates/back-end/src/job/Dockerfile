# ----------------------------
# Fase 1: Build del JAR con Maven
# ----------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copia il file pom.xml e scarica le dipendenze
COPY templates/back-end/src/job/pom.xml ./
RUN mvn dependency:go-offline

# Copia il resto del codice e builda il progetto
COPY templates/back-end/src/job/src ./src
RUN mvn clean package -DskipTests -X

# ----------------------------
# Fase 2: Creazione immagine runtime
# ----------------------------
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copia il jar dalla fase builder
COPY --from=builder /app/target/*.jar app.jar

# Esponi la porta
EXPOSE 5000

# Avvio dell'applicazione
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

