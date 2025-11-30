FROM amazoncorretto:17
VOLUME /tmp

# Nombre del JAR
ARG JAR_FILE=target/proyectoUno-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]