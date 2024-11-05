package org.test;

/*-
 * #%L
 * regressionTestProject::Pipelines::Sync Spark Pipeline
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * All Rights Reserved. You may not copy, reproduce, distribute, publish, display,
 * execute, modify, create derivative works of, transmit, sell or offer for resale,
 * or in any way exploit any part of this solution without Booz Allen Hamilton's
 * express written permission.
 * #L%
 */

 import java.util.Set;
 import simple.test.record.Person;
 import simple.test.record.PersonSchema;
 
 import jakarta.enterprise.context.ApplicationScoped;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import java.time.Instant;
 import java.util.Map;
 
 import com.boozallen.aissemble.core.metadata.MetadataModel;
 
 import java.util.List;
 import java.util.Arrays;
 import java.util.stream.Collectors;
 import java.util.stream.Stream;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.io.IOException;
 
 import org.apache.spark.sql.Dataset;
 import org.apache.spark.sql.Row;

/**
 * Performs the business logic for Ingest.
 *
 * Because this class is {@link ApplicationScoped}, exactly one managed singleton instance will exist
 * in any deployment.
 *
 * GENERATED STUB CODE - PLEASE ***DO*** MODIFY
 *
 * Originally generated from: templates/data-delivery-spark/synchronous.processor.impl.java.vm
 */
@ApplicationScoped
public class Ingest extends IngestBase {

    private static final Logger logger = LoggerFactory.getLogger(Ingest.class);

    public Ingest(){
        super("synchronous",getDataActionDescriptiveLabel());
    }

    /**
    * Provides a descriptive label for the action that can be used for logging (e.g., provenance details).
    *
    * @return descriptive label
    */
    private static String getDataActionDescriptiveLabel(){
        // TODO: replace with descriptive label
        return"Ingest";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<Person> executeStepImpl(Set<Person> inbound) {
        // TODO: Add your business logic here for this step!
        logger.info("Validating People");

        PersonSchema personSchema = new PersonSchema();
        List<Row> rows = inbound.stream().map(PersonSchema::asRow).collect(Collectors.toList());
        Dataset<Row> dataset = sparkSession.createDataFrame(rows, personSchema.getStructType());

        logger.info("=============== before validation ===================");
        dataset.show(false);

        logger.info("=============== after validation ===================");
        dataset = personSchema.validateDataFrame(dataset);
        dataset.show(false);

        logger.info("Validated People");

        logger.info("Saving People");
        saveDataset(dataset, "People");
        logger.info("Completed saving People");

        try {
            logger.info("Pushing file to S3 Local");
            pushFileToS3();
            logger.info("Fetching file from S3 Local");
            fetchFileFromS3Local();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to push file to S3 Local");
        }

        return inbound;
    }

    protected void pushFileToS3() {
        String bucket = "test-filestore-bucket";
        String fileToUpload = "push_testFileStore.txt";
        Path localPathToFile = Paths.get("/opt/spark/work-dir/" + fileToUpload);
        try {
            Files.writeString(localPathToFile, "test txt file"); // Create the test file to push
            this.s3TestFileStore.store(bucket, fileToUpload, localPathToFile); // Push file to FileStore (LocalStack S3)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void fetchFileFromS3Local() throws IOException  {
        String bucket = "test-filestore-bucket";
        String fileToFetch = "push_testFileStore.txt";
        Path localPathToSaveFile = Paths.get("/opt/spark/work-dir/fetch_TestFileStore.txt");
        this.s3TestFileStore.fetch(bucket, fileToFetch, localPathToSaveFile); // Fetches file from FileStore (LocalStack S3)
        logger.info("Fetched file with contents: {}", Files.readAllLines(localPathToSaveFile).get(0));
    }

    @Override
    protected Set<Person> checkAndApplyEncryptionPolicy(Set<Person> inbound) {
        return inbound;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected MetadataModel createProvenanceMetadata(String resource,String subject,String action){
        // TODO: Add any additional provenance-related metadata here
        return new MetadataModel(resource,subject,action,Instant.now());
    }
}
