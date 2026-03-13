#!/usr/bin/env sh

set -eu

BASE_URL="${TEST_METRICS_BASE_URL:-http://localhost:8080}"
REFRESH_URL="${BASE_URL%/}/api/test-metrics/refresh"
SUMMARY_URL="${BASE_URL%/}/api/test-metrics/summary"
HEALTH_URL="${BASE_URL%/}/actuator/health"

echo "Running mvn verify ..."
mvn verify "$@"

echo "Checking metrics service: ${HEALTH_URL}"
curl --fail --silent --show-error "${HEALTH_URL}" >/dev/null

echo "Refreshing unit test metrics: ${REFRESH_URL}"
curl --fail --silent --show-error -X POST "${REFRESH_URL}" >/dev/null

echo "Current metrics summary:"
curl --fail --silent --show-error "${SUMMARY_URL}"
echo
