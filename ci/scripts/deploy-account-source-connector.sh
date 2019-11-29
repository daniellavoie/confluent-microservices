#!/bin/bash

#set -x

CONNECT_URL=$1
POSTGRES_JDBC_URL=$2
POSTGRES_USER=$3
POSTGRES_PASSWORD=$3

echo "CONNECT_URL : $CONNECT_URL"
echo "POSTGRES_JDBC_URL : $POSTGRES_JDBC_URL"
echo "POSTGRES_USER : $POSTGRES_USER"

if [ -z "$CONNECT_URL" ]; then
  echo "CONNECT_URL is undefined."

  exit 1
fi;

if [ -z "$POSTGRES_JDBC_URL" ]; then
  echo "POSTGRES_JDBC_URL is undefined."

  exit 1
fi;

if [ -z "$POSTGRES_USER" ]; then
  echo "POSTGRES_USER is undefined."

  exit 1
fi;

if [ -z "$POSTGRES_PASSWORD" ]; then
  echo "POSTGRES_PASSWORD is undefined."

  exit 1
fi;

set -x

curl -X POST $CONNECT_URL/connectors \
  -H 'Content-Type: application/json' \
  -d @- << EOF
{
  "name": "account-jdbc-source",  
  "config": {
    "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": false,
    "tasks.max": "1",
    "connection.url": "$POSTGRES_JDBC_URL",
    "connection.user": "$POSTGRES_USER",
    "connection.password": "$POSTGRES_PASSWORD",
    "mode": "timestamp",
    "timestamp.column.name": "update_date",
    "table.whitelist": "account",
    "topic.prefix": "",
    "name": "account-jdbc-source",
    "transforms": "ValueToKey,RenameField",
    "transforms.ValueToKey.type": "org.apache.kafka.connect.transforms.ValueToKey",
    "transforms.ValueToKey.fields": "number",
    "transforms.RenameField.type": "org.apache.kafka.connect.transforms.ReplaceField\$Value",
    "transforms.RenameField.renames": "city_address:cityAddress,country_address:countryAddress,creation_date:creationDate,first_name:firstName,last_name:lastName,number_address:numberAddress,street_address:streetAddress,update_date:updateDate"
  }
}
EOF