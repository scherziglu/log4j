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

import java.net.MalformedURLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLProvider;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.core.CoreContainer;

/**
 * The Apache Solr implementation of {@link NoSQLProvider}.
 * @author Markus Klose
 */
@Plugin(name = "Solr", category = "Core", printObject = true)
public final class SolrProvider implements NoSQLProvider<SolrConnection> {
    // status logger
	private static final Logger LOGGER = StatusLogger.getLogger();
    
	// solr server instance to log to
    private final SolrServer solrServer;
    
    // amount of time before commit is done
    private int commitWithinMs;
    
    // description of the solr provider
    private final String description;
    
    // default coreName
    private static String DEFAULT_CORENAME = "collection1";
    
    /**
     * constructor of the SolrProvider.
     * 
     * @param solrServer
     * 	solr server instance to log to
     * @param commitWithinMs
     * 	amount of time before commit is done
     * @param description
     * 	description of the solr provider
     */
    private SolrProvider(final SolrServer solrServer, int commitWithinMs, final String description) {
        this.solrServer = solrServer;
        this.description = "solr{ " + description + " }";
        this.commitWithinMs = commitWithinMs;
    }

    @Override
    public SolrConnection getConnection() {
        return new SolrConnection(this.solrServer, this.commitWithinMs);
    }

    @Override
    public String toString() {
        return this.description;
    }
 
    /**
     * Factory method for creating an Apache Solr provider within the plugin manager.
     *
     * @param coreName
     * 	name of the core/collection. used by all solr server implementation ({@link HttpSolrServer}, {@link LBHttpSolrServer}, {@link CloudSolrServer}, {@link EmbeddedSolrServer}). NOT optional for {@link EmbeddedSolrServer}
     * @param commitWithinMs
     * 	amount of time before a commit is executed. used by all solr server implementation ({@link HttpSolrServer}, {@link LBHttpSolrServer}, {@link CloudSolrServer}, {@link EmbeddedSolrServer}).
     * @param url 
     * 	solr URL used by {@link HttpSolrServer}
     * @param solrServerUrls
     * 	comma (",") separated list of solr urls. used by {@link LBHttpSolrServer}
     * @param zkHost
     * 	list of zooKeeper instances to connect to. used by {@link CloudSolrServer}
     * @param solrHome
     * 	path to the solr home directory. used by {@link EmbeddedSolrServer}
     * @return 
     * 	new Apache Solr provider.
     */
    @PluginFactory
    public static SolrProvider createNoSQLProvider (
    		@PluginAttribute("coreName") final String coreName,
    		@PluginAttribute("commitWithinMs") final String commitWithinMs,
    		@PluginAttribute("url") final String url,
            @PluginAttribute("solrServerUrls") final String solrServerUrls,
            @PluginAttribute("zkHost") final String zkHost,
            @PluginAttribute("solrHome") final String solrHome) {
    	
    	// init commitWithin 
    	int commitWithin;
    	if(commitWithinMs == null) {
    		// default -1 -> not used by insertObject()
    		commitWithin = -1;
    	} else {
    		commitWithin = Integer.parseInt(commitWithinMs);
    	}
    	
    	// create the correct solr server
    	SolrProvider solrProvider =  null;
    	
    	if (url != null && url.length() > 0)  {
    		// create SolrProvider with HttpSolrServer
    		solrProvider = getHttpSolrServer(url, coreName, commitWithin);    		
    	} else if(solrServerUrls != null && solrServerUrls.length() > 0) {
    		// create SolrProvider with LBHttpSolrServer
    		solrProvider = getLBHttpSolrServer(solrServerUrls, coreName, commitWithin);
    	} else if(zkHost != null && zkHost.length() > 0) {
    		// create SolrProvider with CloudSolrServer
    		solrProvider = getCloudSolrServer(zkHost, coreName, commitWithin);
    	} else if((solrHome != null && solrHome.length() > 0) 
    			&& (coreName != null && coreName.length() > 0)) {
    		// create SolrProvider with EmbeddedSolrServer
    		solrProvider = getEmbeddedSolrServer(solrHome, coreName, commitWithin);
    	} else {
    		// no valid configuration found
    		LOGGER.error("No valid SolrProvider configuration found!");
    	}
    	
        return solrProvider;
    }
    
    
    // helper
    /**
	 * create SolrProvider with HttpSolrServer.
	 */ 
    private static SolrProvider getHttpSolrServer(String url, String coreName, int commitWithinMs) {
		String solrUrl = url;

		// handle coreName
    	if (coreName != null && coreName.length() > 0)  {
    		solrUrl = url + "/" + coreName;
    	}
		
		return new SolrProvider(new HttpSolrServer(solrUrl), commitWithinMs, "HttpSolrServer(\"" + solrUrl + "\")");
    }
    
    /**
	 * create SolrProvider with LBHttpSolrServer.
	 */ 
    private static SolrProvider getLBHttpSolrServer(String solrServerUrls, String coreName, int commitWithinMs) {
    	String[] solrURLs = solrServerUrls.split(",");	
    	String description = solrServerUrls;
    	
		// handle coreName
    	if (coreName != null && coreName.length() > 0)  {
    		description = "";
    		for (int i = 0; i< solrURLs.length; i++) {
    			solrURLs[i] = solrURLs[i] + "/" + coreName;
    			description += "," + solrURLs[i];
    		}
    		description = description.replaceFirst(",", "");
    	}
    	
		try {
			return new SolrProvider(new LBHttpSolrServer(solrURLs), commitWithinMs, "LBHttpSolrServer(\"" + description + "\")");
		} catch (MalformedURLException murlEx) {
			// LBHttpSolrServer does not throw this exception
    		return null;
		}
    }
    
    /**
	 * create SolrProvider with CloudSolrServer.
	 */ 
    private static SolrProvider getCloudSolrServer(String zkHost, String coreName, int commitWithinMs) {
    	SolrServer solrServer = new CloudSolrServer(zkHost);
		String description = "CloudSolrServer(\"" + zkHost + "\")";
		
		// handle coreName
		if (coreName == null || coreName.length() == 0)  {
			coreName = DEFAULT_CORENAME;
    	}
		((CloudSolrServer)solrServer).setDefaultCollection(coreName);
		description += ".setDefaultCollection(\"" + coreName + "\")";
		
		
		return new SolrProvider(solrServer, commitWithinMs, description);
    }
    
    /**
 	 * create SolrProvider with EmbeddedSolrServer.
 	 */ 
     private static SolrProvider getEmbeddedSolrServer(String solrHome, String coreName, int commitWithinMs) {    	 
    	 CoreContainer coreContainer = new CoreContainer(solrHome);
    	 coreContainer.load();
    	 String description = "EmbeddedSolrServer(\"" + solrHome + "\", \"" + coreName + "\")";
 		
    	 return new SolrProvider(new EmbeddedSolrServer(coreContainer, coreName), commitWithinMs, description);
     }
}
