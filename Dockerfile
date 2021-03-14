#Tell Docker to use a given image, tagged with version
FROM openjdk:11

#Arguements only available during image build
ARG JAR_FILE=target/stockcomp-0.0.1-SNAPSHOT.jar

#Copy the argument jar file into the image as app.jar
COPY ${JAR_FILE} app.jar

#Telling Docker which port our application is using. Port will be published to host
EXPOSE 8080

#Specifies the executable to start when the container is booting
ENTRYPOINT ["java","-jar","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","/app.jar"]