#!/bin/sh

set -x
SRC_FOLDER=src
BUILD_FOLDER=build

ROOT_DIR=`pwd`

cd $SRC_FOLDER/$BUILD_PATH && \
  go build 
  cd $ROOT_DIR && \
  cp -a $SRC_FOLDER/$BUILD_PATH/* $BUILD_FOLDER