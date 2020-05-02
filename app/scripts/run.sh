#!/bin/sh

export NAMESPACE=dev-giovannysissa
export GRPC_IMAGE=${CONTAINER_REGISTRY}"ref-arch-grpc":$(sbt version | tail -1 | awk '{print $2}')
cat app/grpc-deployment.yml | envsubst | kubectl -n $NAMESPACE apply -f -
