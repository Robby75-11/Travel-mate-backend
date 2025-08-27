# Usa un'immagine con Java 21
FROM eclipse-temurin:21-jdk

# Imposta la working directory
WORKDIR /app

# Copia solo i file necessari per la cache
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# ✅ Dai i permessi di esecuzione al wrapper Maven
RUN chmod +x mvnw

# Scarica le dipendenze (usa il wrapper maven)
RUN ./mvnw dependency:go-offline -B

# Copia tutto il progetto
COPY . .

# Compila il progetto
RUN ./mvnw clean package -DskipTests

# Espone la porta (Render usa 8080 di default)
EXPOSE 8080

# Comando di avvio
CMD ["java", "-jar", "target/travel-mate-0.0.1-SNAPSHOT.jar"]
