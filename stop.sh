#!/bin/bash
set -x

docker compose --profile mongo,hello-service down
