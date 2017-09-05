function service_pid {
  local service=$1
  pids=`ps aux | grep java | grep ${service}.yml | awk '{print $2}'`
}

function teardown_service {
  service=$1
  service_pid $service
  for pid in $pids; do
    if [[ -n $pid ]]; then
      echo "Killing process ${pid}: ${service}"
      kill -9 $pid 2> /dev/null
    fi
  done
}

function teardown_apps {
    echo "Tearing down previous deploys..."
    teardown_service dcs-client
}