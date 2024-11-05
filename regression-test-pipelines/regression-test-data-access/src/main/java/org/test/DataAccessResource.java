package org.test;

/*-
 * #%L
 * regressionTestProject::Pipelines::Data Access
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * All Rights Reserved. You may not copy, reproduce, distribute, publish, display,
 * execute, modify, create derivative works of, transmit, sell or offer for resale,
 * or in any way exploit any part of this solution without Booz Allen Hamilton's
 * express written permission.
 * #L%
 */

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.graphql.GraphQLApi;

import io.quarkus.runtime.StartupEvent;

/**
 * GraphQL resource that exposes the /graphql REST endpoint for Data Access requests.
 *
 * GENERATED STUB CODE - Please **DO** modify with your customizations, as appropriate.
 *
 * Originally generated from: templates/data-access/data.access.resource.impl.java.vm
 */
@GraphQLApi
@ApplicationScoped
public class DataAccessResource extends DataAccessResourceBase {

}
