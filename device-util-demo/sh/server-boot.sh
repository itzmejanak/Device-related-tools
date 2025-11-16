#! /bin/sh
# Configure Environment
PROFILE=test
# Module
MODULES=(communication)
# Module Name
MODULE_NAMES=(Communication Service Module)
# JAR File Array
JARS=(brezze-communication-1.0.jar)
# JDK Path (Java 8)
JDK_PATH='/usr/local/jdk1.8.0_371'
# Project Path
BASE_PATH='/usr/project/share/test'
# JAR File Path
JAR_PATH=$BASE_PATH'/jars'
# GC Path
GC_PATH=$BASE_PATH'/logs/gc'

function outModels() {
  local one=
  echo ".................................server models name.............................................."
  for((i=0;i<${#MODULES[@]};i++))
  do
    one=${MODULES[$i]}
    echo "$one"
  done
  echo ".................................server models name.............................................."
  echo ""
  echo ""
}

function start() {
  local MODULE=
  local MODULE_NAME=
  local JAR_NAME=
  local command="$1"
  local commandOk=0
  local count=0
  local okCount=0
  echo ".................................Start service.............................................."
  for((i=0;i<${#MODULES[@]};i++))
  do
    MODULE=${MODULES[$i]}
    MODULE_NAME=${MODULE_NAMES[$i]}
    JAR_NAME=${JARS[$i]}
    if [ "$command" == "all" ] || [ "$command" == "$MODULE" ];then
      commandOk=1
      count=0
      PID=`$JDK_PATH/bin/jps -l | grep $JAR_PATH/$JAR_NAME | grep -v grep | awk '{print $1}'`
      if [ -n "$PID" ];then
        echo "$MODULE---$MODULE_NAME:Already running,PID=$PID"
      else
        if [ ! -d "$GC_PATH" ]; then
          mkdir -p $GC_PATH
        fi
        nohup $JDK_PATH/bin/java -server -Xms128m -Xmx128m -Xmn56m \
          -Xlog:gc*,gc+heap=info:file=$GC_PATH/gc_"$MODULE"_$(date +"%Y-%m-%d").log:time,uptime,level,tags \
          -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$GC_PATH/oom_"$MODULE"_$(date +"%Y-%m-%d").hprof \
          -jar $JAR_PATH/$JAR_NAME --spring.profiles.active=$PROFILE > /dev/null 2>&1 &
        echo "nohup $JDK_PATH/bin/java ... -jar $JAR_PATH/$JAR_NAME --spring.profiles.active=$PROFILE &"

        PID=`$JDK_PATH/bin/jps -l | grep $JAR_PATH/$JAR_NAME | grep -v grep | awk '{print $1}'`
        while [ -z "$PID" ]
        do
          if (($count == 30));then
            echo "$MODULE---$MODULE_NAME:Not started within $(expr $count \* 10) seconds, please check!"
            break
          fi
          count=$(($count+1))
          echo "$MODULE_NAME starting.................."
          sleep 10s
          PID=`$JDK_PATH/bin/jps -l | grep $JAR_PATH/$JAR_NAME | grep -v grep | awk '{print $1}'`
        done
        okCount=$(($okCount+1))
        echo "$MODULE---$MODULE_NAME:Started successfully,PID=$PID"
      fi
    fi
  done
  if(($commandOk == 0));then
    echo "Second parameter input error"
  else
    echo ".................................Total started: $okCount services................................."
  fi
}

function stop() {
  local MODULE=
  local MODULE_NAME=
  local JAR_NAME=
  local command="$1"
  local commandOk=0
  local okCount=0
  echo ".................................Stop service.............................................."
  for((i=0;i<${#MODULES[@]};i++))
  do
    MODULE=${MODULES[$i]}
    MODULE_NAME=${MODULE_NAMES[$i]}
    JAR_NAME=${JARS[$i]}
    if [ "$command" = "all" ] || [ "$command" = "$MODULE" ];then
      commandOk=1
      PID=`$JDK_PATH/bin/jps -l | grep $JAR_PATH/$JAR_NAME | grep -v grep | awk '{print $1}'`
      if [ -n "$PID" ];then
        echo "$MODULE---$MODULE_NAME:Preparing to stop,PID=$PID"
        kill -9 $PID
        PID=`$JDK_PATH/bin/jps -l | grep $JAR_PATH/$JAR_NAME | grep -v grep | awk '{print $1}'`
        while [ -n "$PID" ]
        do
          sleep 3s
          PID=`$JDK_PATH/bin/jps -l | grep $JAR_PATH/$JAR_NAME | grep -v grep | awk '{print $1}'`
        done
        echo "$MODULE---$MODULE_NAME:Stopped successfully"
        okCount=$(($okCount+1))
      else
        echo "$MODULE---$MODULE_NAME:Not running"
      fi
    fi
  done
  if (($commandOk == 0));then
    echo "Second parameter input error"
  else
    echo ".................................Total stopped: $okCount services................................."
  fi
}

outModels;

case "$1" in
  start)
    start "$2"
  ;;
  stop)
    stop "$2"
  ;;
  restart)
    stop "$2"
    sleep 3s
    start "$2"
  ;;
  *)
    echo "First parameter please enter: start|stop|restart"
    exit 1
  ;;
esac