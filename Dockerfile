FROM openjdk:17
EXPOSE 8082
ADD target/upload-service.jar upload-service.jar
CMD ["java","-jar","upload-service.jar"]