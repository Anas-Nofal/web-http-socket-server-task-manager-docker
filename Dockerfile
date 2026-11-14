
FROM eclipse-temurin:8-jdk-alpine

LABEL maintainer="Wagner Franchin"

WORKDIR /webserver

COPY src/ .

RUN mkdir -p out && javac -d out WebServerSocket.java WebServerHttp.java handler/*.java singleton/*.java task/*.java

EXPOSE 8000

CMD ["java", "-cp", "out", "WebServerSocket"]
