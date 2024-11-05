# Script for creating base Data Access image
#
# GENERATED DockerFile - please ***DO*** modify.
#
# Generated from: templates/general-docker/data.access.docker.file.vm

ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE

FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-quarkus:${VERSION_AISSEMBLE}

COPY target/quarkus-app/ /deployments/
