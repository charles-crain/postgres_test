#!/bin/bash

docker kill postgres
docker rm -v postgres
docker run --name postgres \
    -p 5432:5432 \
	-e POSTGRES_DB=kukaconnect \
	-e POSTGRES_USER=pgadmin \
	-e POSTGRES_PASSWORD=\!skcus1N \
	-d postgres:9.6

