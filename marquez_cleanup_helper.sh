#!/bin/bash

IFS=$'\n'

helm delete marquez

processes=$(ps -A | grep svc/marquez)
for line in $processes
do
  if [[ $line =~ 'grep' ]]
  then
    continue
  else
    echo $line
    pid=$(echo $line | cut -d " " -f1)
    echo "Killing process $pid"
    kill $pid
  fi
done