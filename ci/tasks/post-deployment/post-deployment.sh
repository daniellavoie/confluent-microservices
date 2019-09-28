#!/bin/bash

set -x

echo "TRANSACTION_URL : $TRANSACTION_URL"

curl -X POST $TRANSACTION_URL/funds -d "{\"account\": \"10001\", \"action\": \"DEPOSIT\", \"currency\": \"USD\", \"amount\": 20000, \"creditCardNumber\": \"1001000100011101\" }"