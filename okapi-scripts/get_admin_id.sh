#! /bin/sh

AUTH_TOKEN=`./okapi-login`
RESPONSE=$(curl -sSL -H "X-Okapi-Tenant:diku" -H "X-Okapi-Token:$AUTH_TOKEN" \
    -H "Content-Type: application/json" \
    -X GET "http://localhost:9130/users?query=username=diku_admin")
echo "Curl response: $RESPONSE"	
USER_ID=$(echo $RESPONSE | jq -r '.users | .[0] | .id')
echo $USER_ID
