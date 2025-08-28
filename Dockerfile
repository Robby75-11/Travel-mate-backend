# Usa un'immagine con Maven + Java 21 già installati
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia pom.xml e scarica dipendenze
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia tutto il progetto e compila
COPY . .
RUN mvn clean package -DskipTests

# ------------------------
# Secondo stage: immagine leggera solo con Java
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia il jar costruito nello stage precedente
COPY --from=build /app/target/travel-mate-0.0.1-SNAPSHOT.jar app.jar

# Avvia l'app
CMD ["java", "-jar", "app.jar"]
