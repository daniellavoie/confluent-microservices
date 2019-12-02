#!/bin/bash

#set -x

echo "CONNECTOR_CONFIG_FILE: $CONNECTOR_CONFIG_FILE"

curl -X POST $CONNECT_URL/connectors \
  -H 'Content-Type: application/json' \
  --data-binary "@$CONNECTOR_CONFIG_FILE"