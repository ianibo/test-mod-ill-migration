#!/bin/bash

#Build the image with ./gradlew jibDockerBuild
# Check build with docker image ls | grep ill | grep latest


docker run \
  --add-host=host.docker.internal:host-gateway \
  -e DATASOURCE_USERNAME='folio_admin' \
  -e DATASOURCE_PASSWORD='folio_admin' \
  -e DATASOURCE_URL='jdbc:postgresql://host.docker.internal:54321/okapi_modules' \
  -e DB_DATABASE='okapi_modules' \
  -e DB_HOST='host.docker.internal' \
  -e DB_MAXPOOLSIZE='10' \
  -e DB_PASSWORD='folio_admin' \
  -e DB_PORT='5432' \
  -e DB_USERNAME='folio_admin' \
  -e EVENTS_CONSUMER_ZK_CONNECT='host.docker.internal:2181' \
  -e EVENTS_CONSUMER_BOOTSTRAP_SERVERS='host.docker.internal:29092' \
  -e EVENTS_PUBLISHER_ZK_CONNECT='host.docker.internal:2181' \
  -e EVENTS_PUBLISHER_BOOTSTRAP_SERVERS='host.docker.internal:29092' \
  docker.libsdev.k-int.com/knowledgeintegration/mod-ill:latest

