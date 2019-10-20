#!/bin/bash

# Resources
CI=ci
VERSION_FILE=version/version

# Variables
SERVER_DEPLOYMENT_FOLDER=/tmp/$DEPLOYMENT_NAME
SERVER_DOCKER_COMPOSE_FILE=$SERVER_DEPLOYMENT_FOLDER/docker-compose.yml
PRIVATE_KEY_FILE=/tmp/privatekey

if [ -f spotter-version/version ]; then
  SPOTTER_VERSION=`cat spotter-version/spotter-version`
fi
if [ -f operation-version/version ]; then
  OPERATION_VERSION=`cat operation-version/operation-version`
fi
if [ -f wallet-version/version ]; then
  WALLET_VERSION=`cat wallet-version/wallet-version`
fi
if [ -f ui-version/version ]; then
  UI_VERSION=`cat ui-version/ui-version`
fi
if [ -f data-faker-version/version ]; then
  DATA_FAKER_VERSION=`cat data-faker-version/data-faker-version`
fi

echo "DEPLOYMENT_NAME : $DEPLOYMENT_NAME"
echo "DOCKER_COMPOSE_FILE : $DOCKER_COMPOSE_FILE"
echo "SERVER_DEPLOYMENT_FOLDER : $SERVER_DEPLOYMENT_FOLDER"
echo "SERVER_DOCKER_COMPOSE_FILE : $SERVER_DOCKER_COMPOSE_FILE"
echo "DOCKER_SERVER : $DOCKER_SERVER"
echo "DOCKER_SSH_USER : $DOCKER_SSH_USER"
echo "REGISTRY_URL : $REGISTRY_URL"
echo "REGISTRY_USERNAME : $REGISTRY_USERNAME"
echo "KAFKA_PORT: $KAFKA_PORT"
echo "SPOTTER_VERSION : $SPOTTER_VERSION"
echo "OPERATION_VERSION : $OPERATION_VERSION"
echo "WALLET_VERSION : $WALLET_VERSION"
echo "UI_VERSION : $UI_VERSION"
echo "DATA_FAKER_VERSION : $DATA_FAKER_VERSION"

# Writing private key to a file
echo "$DOCKER_SSH_PRIVATE_KEY" > $PRIVATE_KEY_FILE
chmod 400 $PRIVATE_KEY_FILE

# Create the target temp folder
ssh -o StrictHostKeyChecking=no -i $PRIVATE_KEY_FILE $DOCKER_SSH_USER@$DOCKER_SERVER mkdir -p $SERVER_DEPLOYMENT_FOLDER && \
# Copy the docker-compose file to target server.
scp -o StrictHostKeyChecking=no -i $PRIVATE_KEY_FILE $DOCKER_COMPOSE_FILE $DOCKER_SSH_USER@$DOCKER_SERVER:$SERVER_DOCKER_COMPOSE_FILE && \
# Login to the registry
ssh -o StrictHostKeyChecking=no -i $PRIVATE_KEY_FILE $DOCKER_SSH_USER@$DOCKER_SERVER "docker login --username $REGISTRY_USERNAME --password $REGISTRY_PASSWORD $REGISTRY_URL" && \
# Pull latest version for all container images
ssh -o StrictHostKeyChecking=no -i $PRIVATE_KEY_FILE $DOCKER_SSH_USER@$DOCKER_SERVER "SPOTTER_VERSION=$SPOTTER_VERSION OPERATION_VERSION=$OPERATION_VERSION WALLET_VERSION=$WALLET_VERSION UI_VERSION=$UI_VERSION DATA_FAKER_VERSION=$DATA_FAKER_VERSION KAFKA_PORT=$KAFKA_PORT docker-compose -f $SERVER_DOCKER_COMPOSE_FILE pull" && \
# Run docker-compose up
ssh -o StrictHostKeyChecking=no -i $PRIVATE_KEY_FILE $DOCKER_SSH_USER@$DOCKER_SERVER "SPOTTER_VERSION=$SPOTTER_VERSION OPERATION_VERSION=$OPERATION_VERSION WALLET_VERSION=$WALLET_VERSION UI_VERSION=$UI_VERSION DATA_FAKER_VERSION=$DATA_FAKER_VERSION KAFKA_PORT=$KAFKA_PORT docker-compose -f $SERVER_DOCKER_COMPOSE_FILE up -d"

EXIT_VAL=$?

rm $PRIVATE_KEY_FILE

exit $EXIT_VAL