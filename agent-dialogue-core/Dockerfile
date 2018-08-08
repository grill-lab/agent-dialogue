FROM openjdk:8

RUN apt-get update && apt-get install -y maven

COPY . /usr/src/app

WORKDIR /code

ADD . /code

RUN ["mvn", "install"]

EXPOSE 8070

CMD ["java", "-jar", "target/agent-dialogue-core-0.1-SNAPSHOT.jar", "https://raw.githubusercontent.com/gla-ial/agent-dialogue/configuration/agent-dialogue-core/src/main/resources/config.json?token=Adv8zzTgrD-LOsuTjZekUfaPb0qFp36Qks5bdEFIwA%3D%3D"]