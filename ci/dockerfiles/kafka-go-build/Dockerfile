FROM ubuntu

ENV GOROOT=/usr/local/go
ENV PATH=$GOPATH/bin:$GOROOT/bin:$PATH

RUN apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y wget pkg-config software-properties-common && \
  wget -qO - https://packages.confluent.io/deb/5.3/archive.key | apt-key add - && \
  add-apt-repository "deb [arch=amd64] https://packages.confluent.io/deb/5.3 stable main" && \
  apt-get update && \
  apt-get install -y librdkafka-dev && \
  wget "https://dl.google.com/go/go1.12.7.linux-amd64.tar.gz" && \
  tar -xvf go1.12.7.linux-amd64.tar.gz && \
  mv go /usr/local