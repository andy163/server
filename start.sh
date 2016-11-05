#!/bin/bash
rm -rf /opt/common_api/nohup.out

if [ -e "/var/run/common_api.pid" ]; then
  pid=$(cat /var/run/common_api.pid)
  kill $pid
fi

cd /opt/common_api

ulimit -n 10240
nohup java -Xmx1g -DSERVER_ADDRESS="weixin.jh920.com" -DDEBUG="false" -jar /opt/common_api/common_api-1.0.jar &
echo $! > /var/run/common_api.pid

