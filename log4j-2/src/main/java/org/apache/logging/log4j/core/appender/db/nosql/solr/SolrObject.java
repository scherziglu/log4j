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

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject;
import org.apache.solr.common.SolrInputDocument;

/**
 * The Apache Solr implementation of {@link NoSQLObject}.
 * its a wrapper class for the SolrInputDocument.
 * @author Markus Klose
 */
public final class SolrObject implements NoSQLObject<SolrInputDocument> {
    // solr document
	private final SolrInputDocument solrDoc;

    /**
     * default constructor.
     */
    public SolrObject() {
        this.solrDoc = new SolrInputDocument();
    }

    @Override
    public void set(final String field, final Object value) {
    	// add a single valued field
        this.solrDoc.addField(field, value);
    }

    @Override
    public void set(final String field, final NoSQLObject<SolrInputDocument> value) {
    	// add a single nested document
    	this.solrDoc.addChildDocument(value.unwrap());
    	
    	//TODO what is with field name?
    }

    @Override
    public void set(final String field, final Object[] values) {
    	// add a multi valued field
        this.solrDoc.addField(field, Arrays.asList(values));
    }

    @Override
    public void set(final String field, final NoSQLObject<SolrInputDocument>[] values) {
    	// add a a list of nested documents
        final ArrayList<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
        for (final NoSQLObject<SolrInputDocument> value : values) {
            list.add(value.unwrap());
        }
        this.solrDoc.addChildDocuments(list);
        
      //TODO what is with field name?
    }

    @Override
    public SolrInputDocument unwrap() {
        return this.solrDoc;
    }
}
