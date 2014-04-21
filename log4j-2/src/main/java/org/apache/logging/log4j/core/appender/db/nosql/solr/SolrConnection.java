/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.appender.db.nosql.solr;

import java.io.IOException;

import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLConnection;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 * The Apache Solr implementation of {@link NoSQLConnection}.
 * @author Markus Klose
 */
public final class SolrConnection implements NoSQLConnection<SolrInputDocument, SolrObject> {
	// solr server instance to log to
	private final SolrServer solrServer;
	//  amount of time before commit is done
	private int commitWithinMs;
	
    /**
     * default constructor.
     * 
     * @param solrServer
     * 	solr server instance to log to
     * @param commitWithinMs
     * 	amount of time before commit is done
     */
    public SolrConnection(final SolrServer solrServer, int commitWithinMs) {
        this.solrServer = solrServer;
        this.commitWithinMs = commitWithinMs;
    }

    @Override
    public SolrObject createObject() {
        return new SolrObject();
    }

    @Override
    public SolrObject[] createList(final int length) {
        return new SolrObject[length];
    }

    @Override
    public void insertObject(final NoSQLObject<SolrInputDocument> object) {
    	try {
    		UpdateResponse response;
    		// if commitWithinMs was specified ... use it
    		if (commitWithinMs < 0) {
    			response = this.solrServer.add(object.unwrap());
    		} else {
    			response = this.solrServer.add(object.unwrap(), commitWithinMs);
    		}
    		
    		// check if solr response shows error
			if (response.getStatus() !=  0) {
				throw new AppenderLoggingException("Failed to write log event to Solr. Request Status is: " + response.getStatus());
			}
		} catch (SolrServerException | IOException e) {
			throw new AppenderLoggingException("Failed to write log event to Solr due to error: " + e.getMessage(), e);
		}
    }

    @Override
    public synchronized void close() {
    	// there is nothing to do 
    }

    @Override
    public synchronized boolean isClosed() {
    	return false;
    }
}
