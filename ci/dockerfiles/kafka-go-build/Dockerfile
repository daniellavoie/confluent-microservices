FROM ubuntu

ENV GOROOT=/usr/local/go
ENV PATH=$GOPATH/bin:$GOROOT/bin:$PATH

RUN apt-get update
RUN apt-get install -y wget
RUN wget "https://dl.google.com/go/go1.12.7.linux-amd64.tar.gz" 
RUN tar -xvf go1.12.7.linux-amd64.tar.gz 
RUN mv go /usr/local
RUN apt-get install -y pkg-config
RUN apt-get install -y software-properties-common 
RUN apt-get install -y git
RUN wget -qO - https://packages.confluent.io/deb/5.3/archive.key | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://packages.confluent.io/deb/5.3 stable main" 
RUN apt-get update 
RUN apt-get install -y librdkafka-dev