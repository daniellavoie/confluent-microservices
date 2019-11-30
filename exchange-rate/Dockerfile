FROM openjdk:11

MAINTAINER Daniel Lavoie <dlavoie@live.ca>

ADD target/exchange-rate.jar /app/exchange-rate.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/exchange-rate.jar"]