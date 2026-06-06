#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if [[ -x "./mvnw" ]]; then
  ./mvnw spring-boot:run
else
  mvn spring-boot:run
fi
