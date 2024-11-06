**Start the pipeline:**
```
curl --location 'http://localhost:8085/invoke-pipeline/start-spark-operator-job' \
--header 'Content-Type: application/json' \
--data '{
    "applicationName": "sync-spark-pipeline",
    "profile": "dev",
    "overrideValues": {}
}'
```

**Verify Data Validation:**
View the logs for the pipeline driver:
```
kubectl logs sync-spark-pipeline-driver | grep -e SyncSparkPipelineDriver
```
Verify you see something similar the following:
```
INFO SyncSparkPipelineDriver: Person: John Smith {NUMBER}; error: {ERROR DETAILS}
... (Repeat x10)
INFO SyncSparkPipelineDriver: Returned 11 people
```

View the logs for the ingest step:
```
kubectl logs sync-spark-pipeline-driver | grep -e 'before validation' -A 30
```
Verify you see something similar the following:
 - Note: Values in the table may vary as they are randomly generated
 - Note: The after validation table may contain more than one person
```
INFO Ingest: =============== before validation ===================
INFO CodeGenerator: Code generated in 551.994143 ms
INFO CodeGenerator: Code generated in 81.873753 ms
+-------------+-------------+---+----------+-----------------+--------------+-------------+-------------+------------+-----------+
|name         |ssn          |age|employment|stringLength5To12|long2000To3000|short100To200|decimal10To20|double20To30|float30To40|
+-------------+-------------+---+----------+-----------------+--------------+-------------+-------------+------------+-----------+
|John Smith 2 |111-222-0002 |1  |Student2  |IYR6XSYglS5M     |3126          |170          |19.39        |27.727      |35.269     |
|John Smith 4 |111-222-0004 |2  |Student4  |3wfVkf           |2662          |170          |10.13        |25.421      |38.112     |
|John Smith 8 |111-222-0008 |4  |Student8  |REmym62loAdb     |3092          |170          |18.60        |24.306      |39.039     |
|John Smith 7 |111-222-0007 |9  |Student7  |Okx04H           |2587          |170          |17.76        |29.039      |30.381     |
|John Smith 6 |111-222-0006 |3  |Student6  |                 |2607          |170          |15.55        |28.986      |38.231     |
|John Smith 1 |111-222-0001 |6  |Student1  |whOpEV           |2578          |170          |12.15        |29.647      |37.005     |
|John Smith 9 |111-222-0009 |10 |Student9  |                 |3276          |170          |20.00        |22.182      |34.118     |
|John Smith   |111-222-0000 |10 |Student   |abc123xyz        |2436          |170          |19.60        |23.256      |37.933     |
|John Smith 5 |111-222-0005 |8  |Student5  |tJHDxh9f1w2I     |2546          |170          |15.19        |30.034      |40.957     |
|John Smith 10|111-222-00010|5  |Student10 |VViGgw           |3037          |170          |10.38        |28.331      |34.223     |
|John Smith 3 |111-222-0003 |7  |Student3  |                 |3126          |170          |11.06        |22.139      |35.983     |
+-------------+-------------+---+----------+-----------------+--------------+-------------+-------------+------------+-----------+

INFO CodeGenerator: Code generated in 83.224187 ms
INFO CodeGenerator: Code generated in 91.909298 ms
INFO Ingest: =============== after validation ===================
INFO CodeGenerator: Code generated in 89.908356 ms
+------------+------------+---+----------+-----------------+--------------+-------------+-------------+------------+-----------+
|name        |ssn         |age|employment|stringLength5To12|long2000To3000|short100To200|decimal10To20|double20To30|float30To40|
+------------+------------+---+----------+-----------------+--------------+-------------+-------------+------------+-----------+
|John Smith  |111-222-0000|10 |Student   |abc123xyz        |2436          |170          |19.60        |23.256      |37.933     |
+------------+------------+---+----------+-----------------+--------------+-------------+-------------+------------+-----------+
INFO Ingest: Validated People
```

**Verify Hive:**
```
kubectl logs sync-spark-pipeline-driver | grep -e 'Ingest to Hive'
```
Verify you see something similar the following:
```
INFO IngestBase: Saving Ingest to Hive...
INFO IngestBase: Saved Ingest to Hive
```

**Verify Filestore:**
```
kubectl logs sync-spark-pipeline-driver | grep -e 'S3 Local'
```
Verify you see something similar the following:
```
INFO Ingest: Pushing file to S3 Local with contents: test file text
INFO Ingest: Fetching file from S3 Local
INFO Ingest: Fetched file from S3 Local with contents: test file text
```

Verify there is a file in the S3 Local bucket:
```
awslocal --endpoint-url=http://localhost:4566 s3 ls s3://test-filestore-bucket/
```
Verify you see something similar the following:
```
14 push_testFileStore.txt
```

**Verify Provenance:**
```
curl -X GET http://localhost:8082/metadata/healthcheck
```
Verify you see something similar the following:
```
Metadata service is running...
```

```
curl -X GET http://localhost:8082/metadata
```
Verify you see something similar the following:
```
[{"resource":"Unspecified Ingest resource","subject":"synchronous","action":"Ingest","timestamp":"2024-11-06T22:12:56.755302Z","additionalValues":{}}]
```

**Verify Data Access:**
```
curl --location 'http://localhost:8081/graphql' \
--header 'Content-Type: application/json' \
--data '{"query":"{Person(table: \"People\") {name}}"}'
```
Verify you see something similar the following:
  - Note: The results may contain more than one person
```
{"data":{"Person":[{"name":"John Smith"}]}}
```

**Verify Data Lineage:**
In a new terminal, exec into the kafka pod: `kubectl exec -it kafka-cluster-0 -- sh`. Then run the following command:
```
/opt/bitnami/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic lineage-event-out --from-beginning
```
Verify you see something similar the following:
```
{"eventTime":"2024-11-06T22:12:32.354Z","producer":"http://github.com/producer","schemaURL":"https://openlineage.io/spec/2-0-2/OpenLineage.json#/$defs/RunEvent","eventType":"START","run":{"runId":"0a2ac90d-8bcb-4355-9df4-ce47ee82895c","facets":{}},"job":{"namespace":"SyncSparkPipeline","name":"SyncSparkPipeline","facets":{}}}
{"eventTime":"2024-11-06T22:12:37.446Z","producer":"http://github.com/producer","schemaURL":"https://openlineage.io/spec/2-0-2/OpenLineage.json#/$defs/RunEvent","eventType":"START","run":{"runId":"7f89d73e-f9a0-4978-8510-e4ba00aa7214","facets":{"parent":{"_producer":"http://github.com/producer","_schemaURL":"https://openlineage.io/spec/facets/1-0-1/ParentRunFacet.json#/$defs/ParentRunFacet","run":{"runId":"0a2ac90d-8bcb-4355-9df4-ce47ee82895c"},"job":{"namespace":"SyncSparkPipeline","name":"SyncSparkPipeline"}}}},"job":{"namespace":"SyncSparkPipeline","name":"SyncSparkPipeline.Ingest","facets":{}}}
{"eventTime":"2024-11-06T22:12:56.756Z","producer":"http://github.com/producer","schemaURL":"https://openlineage.io/spec/2-0-2/OpenLineage.json#/$defs/RunEvent","eventType":"COMPLETE","run":{"runId":"7f89d73e-f9a0-4978-8510-e4ba00aa7214","facets":{"parent":{"_producer":"http://github.com/producer","_schemaURL":"https://openlineage.io/spec/facets/1-0-1/ParentRunFacet.json#/$defs/ParentRunFacet","run":{"runId":"0a2ac90d-8bcb-4355-9df4-ce47ee82895c"},"job":{"namespace":"SyncSparkPipeline","name":"SyncSparkPipeline"}}}},"job":{"namespace":"SyncSparkPipeline","name":"SyncSparkPipeline.Ingest","facets":{}}}
{"eventTime":"2024-11-06T22:12:56.758Z","producer":"http://github.com/producer","schemaURL":"https://openlineage.io/spec/2-0-2/OpenLineage.json#/$defs/RunEvent","eventType":"COMPLETE","run":{"runId":"0a2ac90d-8bcb-4355-9df4-ce47ee82895c","facets":{}},"job":{"namespace":"SyncSparkPipeline","name":"SyncSparkPipeline","facets":{}}}
```

**Clean Up:**
Delete the marquez and kafka PVCs:
```
kubectl delete pvc data-kafka-cluster-0
kubectl delete pvc data-marquez-postgresql-0
```