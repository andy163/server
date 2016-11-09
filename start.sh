#!/bin/bash
rm -rf /opt/server/nohup.out

if [ -e "/var/run/server_api.pid" ]; then
  pid=$(cat /var/run/server_api.pid)
  kill $pid
fi

cd /opt/server/

ulimit -n 10240
nohup java -Xmx1g -DSERVER_ADDRESS="pangbohao.com" -DDEBUG="false" -jar /opt/common_api/common_api-1.0.jar &
echo $! > /var/run/common_api.pid

