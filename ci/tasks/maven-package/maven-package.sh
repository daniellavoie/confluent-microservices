#!/bin/sh

SRC_FOLDER=src
BUILD_FOLDER=build
VERSION_FOLDER=version

cd $SRC_FOLDER && \
  ./mvnw -B package && \
  ./mvnw mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec > ../$VERSION_FOLDER/version && \ 
  cd .. && \
  cp -a $SRC_FOLDER/* $BUILD_FOLDER