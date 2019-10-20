#!/bin/bash

echo "AVRO_SCHEMA_FILE: $AVRO_SCHEMA_FILE"
echo "AVRO_SCHEMA_NAME: $AVRO_SCHEMA_NAME"
echo "SCHEMA_REGISTRY_URL: $SCHEMA_REGISTRY_URL"

if [ -z "$AVRO_SCHEMA_FILE" ]; then
  echo "AVRO_SCHEMA_FILE is undefined."
  exit -1
fi

if [ -z "$AVRO_SCHEMA_NAME" ]; then
  echo "AVRO_SCHEMA_NAME is undefined."
  exit -1
fi

if [ -z "$SCHEMA_REGISTRY_URL" ]; then
  echo "SCHEMA_REGISTRY_URL is undefined."
  exit -1
fi

AVRO_SCHEMA=`cat $AVRO_SCHEMA_FILE`

doubleQuote="\""
escapedQuote="\\\""

REQUEST_BODY="{\"schema\": \"${AVRO_SCHEMA//$doubleQuote/$escapedQuote}\"}"
REQUEST_BODY=$(echo $REQUEST_BODY|tr -d '\n')

curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  --data "$REQUEST_BODY" \
   $SCHEMA_REGISTRY_URL/subjects/$AVRO_SCHEMA_NAME-value/versions