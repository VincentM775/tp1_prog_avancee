# ---- Stage 1 : Build ----
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copier le wrapper Maven et le pom.xml en premier (cache des dépendances)
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml

# Rendre le wrapper exécutable et télécharger les dépendances
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copier le code source
COPY src src

# Build du jar (sans les tests, ils sont exécutés séparément en CI)
RUN ./mvnw package -DskipTests -B

# ---- Stage 2 : Runtime ----
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le jar depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Port exposé
EXPOSE 8080

# Health check via Actuator
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Lancement de l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
