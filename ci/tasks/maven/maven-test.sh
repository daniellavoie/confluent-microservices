#!/bin/bash

set -x

SRC_FOLDER=src

pushd $SRC_FOLDER && \
  ./mvnw -B -f $BUILD_PATH/pom.xml test && \
popd