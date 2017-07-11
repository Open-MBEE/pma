#!/bin/bash
#
# pma     This shell script takes care of starting and stopping PMA
#
# Tommy Hang 7/11/2017

SHUTDOWN_WAIT=20

pma_pid() {
  echo `ps aux | grep "java -jar ./pma" | grep -v grep | awk '{ print $2 }'`
}

start() {
  pid=$(pma_pid)
  if [ -n "$pid" ]
  then
    echo "PMA is already running (pid: $pid)"
  else
    # Start PMA
    echo "Starting PMA"

    cd /opt/local/pma

    # Reading in SSL certificate credentials from credentials.txt
    # PMA will start in http if credentials.txt doesn't exist or is incorrect
    # Alias is the second word of the first line
    # Password is the second word of the second line

    line=$(sed -n '1p' credentials.txt)
    if [-n "$line"]
    then
      echo "Found ssl alias"
      export ssl_key_alias=$(echo $line | awk '{print $2}')
    fi

    line=$(sed -n '2p' credentials.txt)
    if [-n "$line"]
    then
      echo "Found ssl password"
      export ssl_key_password=$(echo $line | awk '{print $2}')
    fi

    pmaJarName=$(find . -name 'pma*jar') # Looking for the pma jar file
    java -jar $pmaJarName & # running the pma jar
  fi

  return 0
}

stop() {
  pid=$(pma_pid)
  if [ -n "$pid" ]
  then
    echo "Stoping PMA"
    kill $pid

    let kwait=$SHUTDOWN_WAIT
    count=0;
    until [ `ps -p $pid | grep -c $pid` = '0' ] || [ $count -gt $kwait ]
    do
      echo -n -e "\nwaiting for processes to exit\n";
      sleep 1
      let count=$count+1;
    done

    if [ $count -gt $kwait ]; then
      echo -n -e "\nkilling processes which didn't stop after $SHUTDOWN_WAIT seconds"
      kill -9 $pid
    fi
  else
    echo "PMA is not running"
  fi

  return 0
}

case $1 in
start)
  start
;;
stop)
  stop
;;
restart)
  stop
  start
;;
status)
  pid=$(pma_pid)
  if [ -n "$pid" ]
  then
    echo "PMA is running with pid: $pid"
  else
    echo "PMA is not running"
  fi
;;
esac
exit 0
