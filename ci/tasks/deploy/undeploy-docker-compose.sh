#!/bin/bash

set -x

# Resources
CI=ci
VERSION_FILE=version/version

# Variables
SERVER_DEPLOYMENT_FOLDER=/tmp/$DEPLOYMENT_NAME
SERVER_DOCKER_COMPOSE_FILE=$SERVER_DEPLOYMENT_FOLDER/docker-compose.yml
PRIVATE_KEY_FILE=/tmp/privatekey

echo "DEPLOYMENT_NAME : $DEPLOYMENT_NAME"
echo "DOCKER_COMPOSE_FILE : $DOCKER_COMPOSE_FILE"
echo "SERVER_DEPLOYMENT_FOLDER : $SERVER_DEPLOYMENT_FOLDER"
echo "SERVER_DOCKER_COMPOSE_FILE : $SERVER_DOCKER_COMPOSE_FILE"
echo "DOCKER_SERVER : $DOCKER_SERVER"
echo "DOCKER_SSH_USER : $DOCKER_SSH_USER"

# Writing private key to a file
echo "$DOCKER_SSH_PRIVATE_KEY" > $PRIVATE_KEY_FILE
chmod 400 $PRIVATE_KEY_FILE

# Run docker-compose down
ssh -o StrictHostKeyChecking=no -i $PRIVATE_KEY_FILE $DOCKER_SSH_USER@$DOCKER_SERVER "RATE_VERSION= TRANSACTION_VERSION= WALLET_VERSION= UI_VERSION= DATA_FAKER_VERSION= CCLOUD_BOOTSTRAP_SERVERS= CCLOUD_API_KEY= CCLOUD_API_SECRET= SCHEMA_REGISTRY_URL= SCHEMA_REGISTRY_KEY= SCHEMA_REGISTRY_SECRET= docker-compose -f $SERVER_DOCKER_COMPOSE_FILE down"

EXIT_VAL=$?

rm $PRIVATE_KEY_FILE

exit $EXIT_VAL