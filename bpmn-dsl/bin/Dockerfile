# https://hub.docker.com/_/maven
FROM maven:3.6.2-jdk-8

RUN apt-get update \
  #    && apt-get upgrade -y \
  && apt-get install -y build-essential

#Build LoLA 2
RUN mkdir /opt/lola \
  && cd /opt/lola/ \
  && wget http://service-technology.org/files/lola/lola-2.0.tar.gz \
  && tar xvfz lola-2.0.tar.gz \
  && cd lola-2.0 \
  && mkdir build \
  && cd build \
  && ../configure \
  && make -j$(nproc) \
  && make install