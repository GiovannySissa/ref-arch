#!/bin/sh

export NAMESPACE=dev-giovannysissa
export GRPC_IMAGE=${REGISTRY_ADDRESS}"/ref-arch-grpc":$(sbt version | tail -1 | awk '{print $2}')
cat app/grpc-deployment.yml | envsubst | kubectl -n $NAMESPACE apply -f -
