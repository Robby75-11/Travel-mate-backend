# Usa un'immagine con Java 21
FROM eclipse-temurin:21-jdk

# Imposta la working directory
WORKDIR /app

# Copia il pom.xml e lo script maven wrapper
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Scarica le dipendenze (per caching)
RUN ./mvnw dependency:go-offline -B

# Copia tutto il progetto
COPY . .

# Compila il progetto (senza test)
RUN ./mvnw clean package -DskipTests

# Espone la porta (Render usa 8080 di default)
EXPOSE 8080

# Comando di avvio
CMD ["java", "-jar", "target/travel-mate-0.0.1-SNAPSHOT.jar"]
