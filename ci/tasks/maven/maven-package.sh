#!/bin/bash

set -x

SRC_FOLDER=src
BUILD_FOLDER=build
VERSION_FOLDER=version

ROOT_DIR=`pwd`

mkdir -p $VERSION_FOLDER

if [ $? -ne 0 ]; then
  echo "Failed to create version folder. Interrupting build."
  exit 1
fi

pushd $SRC_FOLDER
  ./mvnw -B -f $BUILD_PATH/pom.xml package && \
  ./mvnw -q -f $BUILD_PATH/pom.xml -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec > $ROOT_DIR/$VERSION_FOLDER/version
  
  if [ $? -ne 0 ]; then
      echo "Maven task failed. Interrupting build."
      exit 1
  fi
popd

cp -a $SRC_FOLDER/$BUILD_PATH/* $BUILD_FOLDER