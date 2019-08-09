#!/bin/sh

set -x
SRC_FOLDER=src
BUILD_FOLDER=build
VERSION_FOLDER=version

ROOT_DIR=`pwd`

cd $SRC_FOLDER/$BUILD_PATH && \
  ./mvnw -B package && \
  ./mvnw mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec > ../$VERSION_FOLDER/version && \ 
  cd $ROOT_DIR && \
  cp -a $SRC_FOLDER/$BUILD_PATH/* $BUILD_FOLDER