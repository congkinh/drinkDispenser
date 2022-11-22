FROM adoptopenjdk/openjdk11:alpine-jre
RUN adduser -S sdworx
USER sdworx
WORKDIR /home/sdworx
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /home/sdworx/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
