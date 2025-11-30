# ETAPA 1: CONSTRUCCIÓN
# Usamos una imagen que tiene Maven instalado para poder compilar
FROM maven:3.8.5-openjdk-17 AS build

# Creamos una carpeta de trabajo dentro de Docker
WORKDIR /app

# Copiamos todos tus archivos (src, pom.xml) de GitHub a Docker
COPY . .

# Ejecutamos el comando para crear el .jar (igual que en Eclipse)
RUN mvn clean package -DskipTests

# ETAPA 2: EJECUCIÓN
# Usamos la imagen ligera de Amazon Corretto solo para correr la app
FROM amazoncorretto:17

# Opcional: El volumen temporal que tenías
VOLUME /tmp

# Le decimos: "Ve a la etapa 'build', busca en la carpeta target y tráeme el archivo"
COPY --from=build /app/target/proyectoUno-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
