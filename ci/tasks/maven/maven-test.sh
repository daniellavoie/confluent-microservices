#!/bin/sh

set -x

SRC_FOLDER=src

echo "ENVIRONMENT_FILE: $ENVIRONMENT_FILE"

ROOT_DIR=`pwd`

if [ -f $ENVIRONMENT_FILE ]; then
  . $ENVIRONMENT_FILE
fi

cd $SRC_FOLDER/$BUILD_PATH && \
  ./mvnw -B test