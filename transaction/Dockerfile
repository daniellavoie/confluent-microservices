FROM openjdk:11

MAINTAINER Daniel Lavoie <dlavoie@live.ca>

ADD target/transaction.jar /app/transaction.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/transaction.jar"]