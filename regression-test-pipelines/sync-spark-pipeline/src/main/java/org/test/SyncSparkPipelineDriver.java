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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import simple.test.record.Person;
import simple.test.record.PersonSchema;


import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.CDI;

import org.test.pipeline.PipelineBase;

/**
 * Configures the steps needed to run a Spark-based implementation for SyncSparkPipeline.
 *
 * This pipeline serves the following purpose: ${pipeline.description}.
 *
 * Please **DO** modify with your customizations, as appropriate.
 *
 * Originally generated from: templates/pipeline.driver.impl.java.vm 
 */
public class SyncSparkPipelineDriver extends SyncSparkPipelineBaseDriver {

  private static final Logger logger = LoggerFactory.getLogger(SyncSparkPipelineDriver.class);
  
  public static void main(String[] args) {
    logger.info("STARTED: {} driver", "SyncSparkPipeline");
    SyncSparkPipelineBaseDriver.main(args);

    PipelineBase.getInstance().recordPipelineLineageStartEvent();


    final Ingest ingest = CDI.current().select(Ingest.class, new Any.Literal()).get();
    Set<Person> ingestResult = ingest.executeStep(null);
    PipelineBase.getInstance().recordPipelineLineageCompleteEvent();
  }
}
