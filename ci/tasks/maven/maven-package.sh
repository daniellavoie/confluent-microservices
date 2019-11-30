#!/bin/sh

set -x

SRC_FOLDER=src
BUILD_FOLDER=build
VERSION_FOLDER=version

ROOT_DIR=`pwd`

cd $SRC_FOLDER && \
  ./mvnw -B -f $BUILD_PATH/pom.xml package && \
  mkdir -p $VERSION_FOLDER && \
  ./mvnw -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec > $ROOT_DIR/$VERSION_FOLDER/version && \
  cd $ROOT_DIR && \
  cp -a $SRC_FOLDER/$BUILD_PATH/* $BUILD_FOLDER