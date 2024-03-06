#!/bin/bash
set -x

source pre-start-monitoring.sh

docker compose --profile monitoring,mongo,hello-service up -d 
