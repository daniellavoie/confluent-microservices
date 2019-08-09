#!/bin/sh

set -x
SRC_FOLDER=src
BUILD_FOLDER=build
VERSION_FOLDER=version

ROOT_DIR=`pwd`

cd $SRC_FOLDER/$BUILD_PATH && \
  go build 
  cat version > ../$VERSION_FOLDER/version && \ 
  cd $ROOT_DIR && \
  cp -a $SRC_FOLDER/$BUILD_PATH/* $BUILD_FOLDER