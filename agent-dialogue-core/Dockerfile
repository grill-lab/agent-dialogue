FROM openjdk:8

RUN apt-get update && apt-get install -y maven

COPY . /usr/src/app

WORKDIR /code

ADD . /code

RUN ["mvn", "install"]

EXPOSE 8070

CMD ["java", "-jar", "target/agent-dialogue-core-0.1-SNAPSHOT.jar",
"file:///Users/Adam/Documents/Internship/GitHub/agent-dialogue/agent-dialogue-core/src/main/resources/config.json"]