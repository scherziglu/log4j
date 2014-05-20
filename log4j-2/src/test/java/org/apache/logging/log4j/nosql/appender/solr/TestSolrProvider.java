package org.apache.logging.log4j.nosql.appender.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.logging.log4j.nosql.appender.solr.SolrProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for SolrProvider. test createNoSQLProvider by reviewing the
 * description
 */
public class TestSolrProvider {
    // provider to test
    private SolrProvider solrProvider;

    @Before
    public void setUp() {
        this.solrProvider = null;
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testHttpSolrServer() {
        // pre test
        assertNull(this.solrProvider);

        // create provider
        this.solrProvider = SolrProvider.createNoSQLProvider(null, null,
                "http://localhost:8983/solr", null, null, null);

        // test provider
        assertNotNull(this.solrProvider);
        assertEquals("solr{ HttpSolrServer(\"http://localhost:8983/solr\") }",
                this.solrProvider.toString());
    }

    @Test
    public void testHttpSolrServerWithCore() {
        // pre test
        assertNull(this.solrProvider);

        // create provider
        this.solrProvider = SolrProvider.createNoSQLProvider("myCore", null,
                "http://localhost:8983/solr", null, null, null);

        // test provider
        assertNotNull(this.solrProvider);
        assertEquals(
                "solr{ HttpSolrServer(\"http://localhost:8983/solr/myCore\") }",
                this.solrProvider.toString());
    }

    @Test
    public void testLBHttpSolrServer() {
        // pre test
        assertNull(this.solrProvider);

        // create provider
        this.solrProvider = SolrProvider.createNoSQLProvider(null, null, null,
                "http://localhost:8983/solr,http://localhost:8984/solr", null,
                null);

        // test provider
        assertNotNull(this.solrProvider);
        assertEquals(
                "solr{ LBHttpSolrServer(\"http://localhost:8983/solr,http://localhost:8984/solr\") }",
                this.solrProvider.toString());
    }

    @Test
    public void testLBHttpSolrServerWithCore() {
        // pre test
        assertNull(this.solrProvider);

        // create provider
        this.solrProvider = SolrProvider.createNoSQLProvider("myCore", null,
                null, "http://localhost:8983/solr,http://localhost:8984/solr",
                null, null);

        // test provider
        assertNotNull(this.solrProvider);
        assertEquals(
                "solr{ LBHttpSolrServer(\"http://localhost:8983/solr/myCore,http://localhost:8984/solr/myCore\") }",
                this.solrProvider.toString());
    }

    @Test
    public void testCloudSolrServer() {
        // pre test
        assertNull(this.solrProvider);

        // create provider
        this.solrProvider = SolrProvider.createNoSQLProvider(null, null, null,
                null, "localhost:2181", null);

        // test provider
        assertNotNull(this.solrProvider);
        assertEquals(
                "solr{ CloudSolrServer(\"localhost:2181\").setDefaultCollection(\"collection1\") }",
                this.solrProvider.toString());
    }

    @Test
    public void testCloudSolrServerWithCore() {
        // pre test
        assertNull(this.solrProvider);

        // create provider
        this.solrProvider = SolrProvider.createNoSQLProvider("myCore", null,
                null, null, "localhost:2181", null);

        // test provider
        assertNotNull(this.solrProvider);
        assertEquals(
                "solr{ CloudSolrServer(\"localhost:2181\").setDefaultCollection(\"myCore\") }",
                this.solrProvider.toString());
    }

    @Test
    public void testEmbeddedSolrServer() {
        // pre test
        assertNull(this.solrProvider);

        // create provider
        String solrHome = TestSolrProvider.class
                .getResource(
                        "./solr_home")
                .getPath();
        this.solrProvider = SolrProvider.createNoSQLProvider("myCore", null,
                null, null, null, solrHome);

        // test provider
        assertNotNull(this.solrProvider);
        assertEquals("solr{ EmbeddedSolrServer(\"" + solrHome
                + "\", \"myCore\") }", this.solrProvider.toString());
    }

    @Test
    public void testNoValidConfiguration() {
        // pre test
        assertNull(this.solrProvider);

        // create provider
        this.solrProvider = SolrProvider.createNoSQLProvider(null, null, null,
                null, null, null);
        assertNull(this.solrProvider);

        // empty coreName
        this.solrProvider = SolrProvider.createNoSQLProvider(null, null, "",
                null, null, null);
        assertNull(this.solrProvider);

        // empty url
        this.solrProvider = SolrProvider.createNoSQLProvider("", null, null,
                null, null, null);
        assertNull(this.solrProvider);

        // empty list of Solr server
        this.solrProvider = SolrProvider.createNoSQLProvider(null, null, null,
                "", null, null);
        assertNull(this.solrProvider);

        // empty zkHost
        this.solrProvider = SolrProvider.createNoSQLProvider(null, null, null,
                null, "", null);
        assertNull(this.solrProvider);

        // empty solrHome
        this.solrProvider = SolrProvider.createNoSQLProvider(null, null, null,
                null, null, "");
        assertNull(this.solrProvider);
    }
}
