# Usa un'immagine con Java 21
FROM eclipse-temurin:21-jdk

# Imposta la working directory
WORKDIR /app

# Copia il wrapper Maven e dai permessi
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Copia il pom.xml e scarica le dipendenze (per cache)
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# Copia tutto il progetto
COPY . .

# Compila il progetto
RUN ./mvnw clean package -DskipTests

# Espone la porta (Render usa 8080 di default)
EXPOSE 8080

# Comando di avvio
CMD ["java", "-jar", "target/travel-mate-0.0.1-SNAPSHOT.jar"]
