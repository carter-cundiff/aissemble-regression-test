allow_k8s_contexts('local')
docker_prune_settings(num_builds=1, keep_recent=1)

aissemble_version = '1.10.0-SNAPSHOT'

build_args = { 'DOCKER_BASELINE_REPO_ID': 'ghcr.io/',
               'VERSION_AISSEMBLE': aissemble_version}

# Kafka
yaml = helm(
    'regression-test-deploy/src/main/resources/apps/kafka-cluster',
    values=['regression-test-deploy/src/main/resources/apps/kafka-cluster/values.yaml',
        'regression-test-deploy/src/main/resources/apps/kafka-cluster/values-dev.yaml']
)
k8s_yaml(yaml)

# Add deployment resources here

k8s_kind('SparkApplication', image_json_path='{.spec.image}')

# data-access
docker_build(
    ref='regression-test-data-access-docker',
    context='regression-test-docker/regression-test-data-access-docker',
    build_args=build_args,
    dockerfile='regression-test-docker/regression-test-data-access-docker/src/main/resources/docker/Dockerfile'
)


# spark-worker-image
docker_build(
    ref='regression-test-spark-worker-docker',
    context='regression-test-docker/regression-test-spark-worker-docker',
    build_args=build_args,
    extra_tag='regression-test-spark-worker-docker:latest',
    dockerfile='regression-test-docker/regression-test-spark-worker-docker/src/main/resources/docker/Dockerfile'
)


# policy-decision-point
docker_build(
    ref='regression-test-policy-decision-point-docker',
    context='regression-test-docker/regression-test-policy-decision-point-docker',
    build_args=build_args,
    dockerfile='regression-test-docker/regression-test-policy-decision-point-docker/src/main/resources/docker/Dockerfile'
)



yaml = local('helm template oci://ghcr.io/boozallen/aissemble-spark-application-chart --version %s --values regression-test-pipelines/sync-spark-pipeline/src/main/resources/apps/sync-spark-pipeline-base-values.yaml,regression-test-pipelines/sync-spark-pipeline/src/main/resources/apps/sync-spark-pipeline-dev-values.yaml' % aissemble_version)
k8s_yaml(yaml)
k8s_resource('sync-spark-pipeline', port_forwards=[port_forward(4747, 4747, 'debug')], auto_init=False, trigger_mode=TRIGGER_MODE_MANUAL)

yaml = helm(
   'regression-test-deploy/src/main/resources/apps/policy-decision-point',
   name='policy-decision-point',
   values=['regression-test-deploy/src/main/resources/apps/policy-decision-point/values.yaml',
       'regression-test-deploy/src/main/resources/apps/policy-decision-point/values-dev.yaml']
)
k8s_yaml(yaml)
yaml = helm(
   'regression-test-deploy/src/main/resources/apps/spark-operator',
   name='spark-operator',
   values=['regression-test-deploy/src/main/resources/apps/spark-operator/values.yaml',
       'regression-test-deploy/src/main/resources/apps/spark-operator/values-dev.yaml']
)
k8s_yaml(yaml)
yaml = helm(
   'regression-test-deploy/src/main/resources/apps/spark-infrastructure',
   name='spark-infrastructure',
   values=['regression-test-deploy/src/main/resources/apps/spark-infrastructure/values.yaml',
       'regression-test-deploy/src/main/resources/apps/spark-infrastructure/values-dev.yaml']
)
k8s_yaml(yaml)
yaml = helm(
   'regression-test-deploy/src/main/resources/apps/metadata',
   name='metadata',
   values=['regression-test-deploy/src/main/resources/apps/metadata/values.yaml',
       'regression-test-deploy/src/main/resources/apps/metadata/values-dev.yaml']
)
k8s_yaml(yaml)
k8s_yaml('regression-test-deploy/src/main/resources/apps/spark-worker-image/spark-worker-image.yaml')


yaml = helm(
   'regression-test-deploy/src/main/resources/apps/data-access',
   name='data-access',
   values=['regression-test-deploy/src/main/resources/apps/data-access/values.yaml',
       'regression-test-deploy/src/main/resources/apps/data-access/values-dev.yaml']
)
k8s_yaml(yaml)

yaml = helm(
   'regression-test-deploy/src/main/resources/apps/s3-local',
   name='s3-local',
   values=['regression-test-deploy/src/main/resources/apps/s3-local/values.yaml',
       'regression-test-deploy/src/main/resources/apps/s3-local/values-dev.yaml']
)
k8s_yaml(yaml)
yaml = helm(
   'regression-test-deploy/src/main/resources/apps/pipeline-invocation-service',
   name='pipeline-invocation-service',
   values=['regression-test-deploy/src/main/resources/apps/pipeline-invocation-service/values.yaml',
       'regression-test-deploy/src/main/resources/apps/pipeline-invocation-service/values-dev.yaml']
)
k8s_yaml(yaml)

yaml = helm(
   'regression-test-deploy/src/main/resources/apps/lineage-http-consumer-deploy-v2',
   name='lineage-http-consumer-deploy-v2',
   values=['regression-test-deploy/src/main/resources/apps/lineage-http-consumer-deploy-v2/values.yaml',
       'regression-test-deploy/src/main/resources/apps/lineage-http-consumer-deploy-v2/values-dev.yaml']
)
k8s_yaml(yaml)