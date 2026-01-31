FROM maven:3.9.9-eclipse-temurin-21 as builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre AS runner

WORKDIR /app

COPY --from=builder ./app/target/*.jar ./app.jar

EXPOSE 4004

CMD ["java", "-jar", "app.jar"]