package org.apache.logging.log4j.core.appender.db.nosql.solr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationFactory.ConfigurationSource;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test to validate log4j.xml configuration test by reviewing the
 * description of the appender
 */
public class TestSolrAppender {

    private static LoggerContext context;

    @BeforeClass
    public static void init() {
        try {
            // read log4j configuration from xml
            InputStream in = new FileInputStream(
                    new File(TestSolrAppender.class.getResource("/log4j2.xml")
                            .getPath()));
            ConfigurationFactory factory = new XMLConfigurationFactory();
            Configuration configuration = factory
                    .getConfiguration(new ConfigurationSource(in));

            // init context
            context = (LoggerContext) LogManager.getContext();
            context.start(configuration);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testHttpSolrServer() {
        String appenderDescription = context.getLogger("HttpSolrServer")
                .getAppenders().get("HttpSolrServerAppender").toString();

        assertNotNull(context.getLogger("HttpSolrServer"));
        assertTrue(appenderDescription
                .contains("provider=solr{ HttpSolrServer(\"http://localhost:8983/solr\") }"));
    }

    @Test
    public void testHttpSolrServerWithCore() {
        String appenderDescription = context
                .getLogger("HttpSolrServerWithCore").getAppenders()
                .get("HttpSolrServerWithCoreAppender").toString();

        assertNotNull(context.getLogger("HttpSolrServerWithCore"));
        assertTrue(appenderDescription
                .contains("provider=solr{ HttpSolrServer(\"http://localhost:8983/solr/collection1\") }"));
    }

    @Test
    public void testLBHttpSolrServer() {
        String appenderDescription = context.getLogger("LBHttpSolrServer")
                .getAppenders().get("LBHttpSolrServerAppender").toString();

        assertNotNull(context.getLogger("LBHttpSolrServer"));
        assertTrue(appenderDescription
                .contains("provider=solr{ LBHttpSolrServer(\"http://localhost:8983/solr,http://localhost:8984/solr\") }"));
    }

    @Test
    public void testLBHttpSolrServerWithCore() {
        String appenderDescription = context
                .getLogger("LBHttpSolrServerWithCore").getAppenders()
                .get("LBHttpSolrServerWithCoreAppender").toString();

        assertNotNull(context.getLogger("LBHttpSolrServerWithCore"));
        assertTrue(appenderDescription
                .contains("provider=solr{ LBHttpSolrServer(\"http://localhost:8983/solr/collection1,http://localhost:8984/solr/collection1\") }"));
    }

    @Test
    public void testCloudSolrServer() {
        String appenderDescription = context.getLogger("CloudSolrServer")
                .getAppenders().get("CloudSolrServerAppender").toString();

        assertNotNull(context.getLogger("CloudSolrServer"));
        assertTrue(appenderDescription
                .contains("provider=solr{ CloudSolrServer(\"localhost:2181\").setDefaultCollection(\"collection1\") }"));
    }

    @Test
    public void testCloudSolrServerWithCore() {
        String appenderDescription = context
                .getLogger("CloudSolrServerWithCore").getAppenders()
                .get("CloudSolrServerWithCoreAppender").toString();

        assertNotNull(context.getLogger("CloudSolrServerWithCore"));
        assertTrue(appenderDescription
                .contains("provider=solr{ CloudSolrServer(\"localhost:2181\").setDefaultCollection(\"collection1\") }"));
    }

    @Test
    public void testEmbeddedSolrServer() {
        String appenderDescription = context.getLogger("EmbeddedSolrServer")
                .getAppenders().get("EmbeddedSolrServerAppender").toString();

        assertNotNull(context.getLogger("EmbeddedSolrServer"));
        assertTrue(appenderDescription
                .contains("provider=solr{ EmbeddedSolrServer(\"./target/test-classes/solr_home\", \"collection1\") }"));
    }

    // @Test
    public void testSendLogEvents() {
        // this is not a real test ... it is just for validating the output in solr
        // to run this test unzip "solrCloud.zip" and start startZooKeeper.bat and startSolr.bat

        // send log event to HttpSolrServer
        context.getLogger("HttpSolrServer").log(Level.DEBUG, "--- HttpSolrServer: This is a logging message.");
        context.getLogger("HttpSolrServer").log(Level.DEBUG, "--- HttpSolrServer: This is a second logging message.");
        // send log event to HttpSolrServerWithCore
        context.getLogger("HttpSolrServerWithCore").log(Level.DEBUG, "--- HttpSolrServerWithCore: This is a logging message.");
        context.getLogger("HttpSolrServerWithCore").log(Level.DEBUG, "--- HttpSolrServerWithCore: This is a second logging message.");
        // send log event to LBHttpSolrServer
        context.getLogger("LBHttpSolrServer").log(Level.DEBUG, "--- LBHttpSolrServer: This is a logging message.");
        context.getLogger("LBHttpSolrServer").log(Level.DEBUG, "--- LBHttpSolrServer: This is a second logging message.");
        // send log event to LBHttpSolrServerWithCore
        context.getLogger("LBHttpSolrServerWithCore").log(Level.DEBUG, "--- LBHttpSolrServerWithCore: This is a logging message.");
        context.getLogger("LBHttpSolrServerWithCore").log(Level.DEBUG, "--- LBHttpSolrServerWithCore: This is a second logging message.");
        // send log event to CloudSolrServer
        context.getLogger("CloudSolrServer").log(Level.DEBUG, "--- CloudSolrServer: This is a logging message.");
        context.getLogger("CloudSolrServer").log(Level.DEBUG, "--- CloudSolrServer: This is a second logging message.");
        // send log event to CloudSolrServerWithCore
        context.getLogger("CloudSolrServerWithCore").log(Level.DEBUG, "--- CloudSolrServerWithCore: This is a logging message.");
        context.getLogger("CloudSolrServerWithCore").log(Level.DEBUG, "--- CloudSolrServerWithCore: This is a second logging message.");
        // send log event to EmbeddedSolrServer
        context.getLogger("EmbeddedSolrServer").log(Level.DEBUG, "--- EmbeddedSolrServer: This is a logging message.");
        context.getLogger("EmbeddedSolrServer").log(Level.DEBUG, "--- EmbeddedSolrServer: This is a second logging message.");
    }
}
