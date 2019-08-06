FROM ubuntu

MAINTAINER Daniel Lavoie <dlavoie@live.ca>

EXPOSE 8080

CMD ["bash", "-c", "/app/microservice-operation"]

RUN apt-get update
RUN apt-get install -y wget
RUN apt-get install -y pkg-config
RUN apt-get install -y software-properties-common 
RUN wget -qO - https://packages.confluent.io/deb/5.3/archive.key | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://packages.confluent.io/deb/5.3 stable main" 
RUN apt-get update 
RUN apt-get install -y librdkafka-dev

ADD microservice-operation /app/microservice-operation