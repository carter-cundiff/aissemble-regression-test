#!/bin/bash

# Delete the Helm release
helm delete marquez

# Delete the marquez PVC
kubectl delete pvc data-marquez-postgresql-0

# Delete the marquez port forwards
# Check and kill process using port 3000
process_3000=$(lsof -t -i :3000)
if [ -n "$process_3000" ]; then
  echo "Killing process using port 3000: $process_3000"
  kill "$process_3000"
else
  echo "No process found using port 3000"
fi

# Check and kill process using port 5000
process_5000=$(lsof -t -i :5000)
if [ -n "$process_5000" ]; then
  echo "Killing process using port 5000: $process_5000"
  kill "$process_5000"
else
  echo "No process found using port 5000"
fi