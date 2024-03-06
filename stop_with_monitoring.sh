#!/bin/bash
set -x

docker compose --profile monitoring,mongo,hello-service down 
