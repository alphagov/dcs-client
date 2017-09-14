#!/usr/bin/env bash

set -e

service_name='dcs-client'

. deploy/functions.sh

teardown_apps

# change accordingly to need
#export DCS_URL=http://localhost:52110/checks/dva-driving-licence
#export KEY_STORE_PASSWORD=password
#export TRUST_STORE_PASSWORD=password

export KEY_STORE_PATH=../doc-checking/deploy/keys/acceptance-test-client-mutual-auth_20170907113445.jks
export TRUST_STORE_PATH=../doc-checking/deploy/keys/non-prod-tls-truststore.jks
export CLIENT_SIGNING_KEY=../doc-checking/deploy/keys/dcs_staging_acceptance_test_signing_20170410145452.pk8
export CLIENT_SIGNING_CERT=../doc-checking/deploy/keys/dcs_staging_acceptance_test_signing_20170410145452.crt
export CLIENT_ENCRYPTION_KEY=../doc-checking/deploy/keys/dcs_staging_acceptance_test_encryption_20170410145440.pk8
export SSL_REQUEST_HEADER="/CN=ssl one"
export DCS_ENCRYPTION_CERT=../doc-checking/deploy/keys/encryption_dcs.crt

echo -n "Starting $service_name..."

./gradlew run > logs/dcs-client_console.log 2>&1 &

get_service_pid $service_name

while [ -z "$pid" ]; do
 sleep 1
 get_service_pid $service_name
done

printf "\rStarted %-25s (pid: %s)\n" $service_name $pid

