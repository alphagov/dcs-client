#!/usr/bin/env bash

set -e

service_name='dcs-client'

. deploy/functions.sh

teardown_apps

echo -n "Starting $service_name..."

./gradlew run > logs/dcs-client_console.log 2>&1 &


service_pid $service_name

while [ -z "$pids" ]; do
 sleep 1
 service_pid $service_name
done

printf "\rStarted %-25s (pid: %s)\n" $service_name $pids

