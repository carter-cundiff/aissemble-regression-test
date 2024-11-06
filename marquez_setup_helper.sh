#!/bin/bash

echo "Setting up marquez deployment..."

cd regression-test-deploy/src/main/resources/apps/marquez
helm install marquez . --dependency-update
while : ; do
  status=$(kubectl get pods | grep -E 'marquez-[[:digit:]].+')
  if [[ $status =~ "1/1" ]] && [[ $status =~ "Running" ]]
  then
    break
  else
    echo "Waiting for marquez deployment to be ready..."
    sleep 5
  fi
done

echo "Preparing port forwards..."

kubectl port-forward svc/marquez 5000:80 &
kubectl port-forward svc/marquez-web 3000:80 &

cd -
sleep 3
echo "You may now visit the Marquez web UI at http://localhost:3000"