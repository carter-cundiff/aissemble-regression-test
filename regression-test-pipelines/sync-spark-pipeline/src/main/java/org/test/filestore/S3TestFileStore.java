package org.test.filestore;

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

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Utility for accessing the s3TestFile file storage. To customize
 * the file store connection modify the environment variables as per the documentation at
 * https://boozallen.github.io/aissemble/aissemble/current/pipeline-metamodel.html
 * Please **DO** modify with your customizations, as appropriate.
 *
 * Originally generated from: templates/file-store/file.store.impl.java.vm
 */
@ApplicationScoped
public class S3TestFileStore extends S3TestFileStoreBase {
	public S3TestFileStore() {
		super("s3TestFile");
	}
}
