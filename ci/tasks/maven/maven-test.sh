#!/bin/sh

SRC_FOLDER=src

ROOT_DIR=`pwd`

if [ -f $ENVIRONMENT_FILE ]; then
  . $ENVIRONMENT_FILE
fi

cd $SRC_FOLDER/$BUILD_PATH && \
  ./mvnw -B test