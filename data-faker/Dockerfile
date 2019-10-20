FROM openjdk:11

MAINTAINER Daniel Lavoie <dlavoie@live.ca>

ADD target/data-faker.jar /app/data-faker.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/data-faker.jar"]