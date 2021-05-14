FROM maven:3-jdk-11

ADD . /abetta-xp
WORKDIR /abetta-xp

RUN ls -l
RUN mvn clean install -DskipTests

FROM openjdk:11.0-jre-slim

VOLUME /tmp
COPY --from=0 /abetta-xp/target/abetta-xp*.jar app.jar

CMD ["sh", "-c", "java -Dserver.port=$PORT -Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker,cloud -jar /app.jar"]
