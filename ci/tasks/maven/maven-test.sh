#!/bin/sh

set -x

SRC_FOLDER=src

cd $SRC_FOLDER/$BUILD_PATH && \
  ./mvnw -B test