package org.apache.logging.log4j.core.appender.db.nosql.solr;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for SolrObject.
 * @author Markus Klose
 */
public class TestSolrObject {
	private SolrObject solrObject;
	
	@Before
	public void setUp() {
		this.solrObject = new SolrObject();
	}

	@After
	public void tearDown() {
	}
	    
	@Test
	public void testConstructor() {
		SolrObject solrObject = new SolrObject();
		assertNotNull(solrObject.unwrap());
	}
	
	@Test
	public void testSetSingleValuedField() {
		//pre test
		assertNull(this.solrObject.unwrap().getFieldValue("a"));
		assertNull(this.solrObject.unwrap().getFieldValue("b"));
		
		// set
		this.solrObject.set("a", "solr field");
		this.solrObject.set("b", "another solr field");
		
		// test set
		assertNotNull(this.solrObject.unwrap().getFieldValue("a"));
		assertEquals("solr field", this.solrObject.unwrap().getFieldValue("a"));
		assertEquals(1, this.solrObject.unwrap().getFieldValues("a").size());
		assertNotNull(this.solrObject.unwrap().getFieldValue("b"));
		assertEquals("another solr field", this.solrObject.unwrap().getFieldValue("b"));
		assertEquals(1, this.solrObject.unwrap().getFieldValues("b").size());
	}

	@Test
	public void testSetMultiValuedField() {
		//pre test
		assertNull(this.solrObject.unwrap().getFieldValue("a"));
		assertNull(this.solrObject.unwrap().getFieldValue("b"));
		
		// set
		String[] array1 = {"solr", "field"};
		this.solrObject.set("a", array1);
		String[] array2 = {"another", "solr", "field"};
		this.solrObject.set("b", array2);
				
		// test set
		assertNotNull(this.solrObject.unwrap().getFieldValue("a"));
		assertArrayEquals(array1, this.solrObject.unwrap().getFieldValues("a").toArray());
		assertEquals(2, this.solrObject.unwrap().getFieldValues("a").size());
		assertNotNull(this.solrObject.unwrap().getFieldValues("b"));
		assertArrayEquals(array2, this.solrObject.unwrap().getFieldValues("b").toArray());
		assertEquals(3, this.solrObject.unwrap().getFieldValues("b").size());
	}
	
	@Test
	public void testSetSingleNestedDoc() {
		//pre test
		assertNull(this.solrObject.unwrap().getFieldValue("a"));
		assertNull(this.solrObject.unwrap().getFieldValue("b"));
		
		// set
		this.solrObject.set("a", new SolrObject());
		this.solrObject.set("b", new SolrObject());
				
		// test set
		assertNotNull(this.solrObject.unwrap().getChildDocuments());
		assertEquals(2, this.solrObject.unwrap().getChildDocuments().size());
		assertNull(this.solrObject.unwrap().getFieldValue("a"));
		assertNull(this.solrObject.unwrap().getFieldValue("b"));
	}
	
	@Test
	public void testSetMultipleNestedDoc() {
		//pre test
		assertNull(this.solrObject.unwrap().getFieldValue("a"));
		assertNull(this.solrObject.unwrap().getFieldValue("b"));
						
		// set
		SolrObject[] array1 = {new SolrObject(), new SolrObject()};
		this.solrObject.set("a", array1);
		SolrObject[] array2 = {new SolrObject(), new SolrObject(), new SolrObject()};
		this.solrObject.set("b", array2);
						
		// test set
		assertNotNull(this.solrObject.unwrap().getChildDocuments());
		assertEquals(5, this.solrObject.unwrap().getChildDocuments().size());
		assertNull(this.solrObject.unwrap().getFieldValue("a"));
		assertNull(this.solrObject.unwrap().getFieldValue("b"));
	}
	
	@Test
	public void testSetComplex() {
		//pre test
		assertNull(this.solrObject.unwrap().getFieldValue("a"));
		assertNull(this.solrObject.unwrap().getFieldValue("b"));
		assertNull(this.solrObject.unwrap().getFieldValue("c"));
		assertNull(this.solrObject.unwrap().getFieldValue("d"));
						
		// set
		this.solrObject.set("a", "solr field");
		String[] array1 = {"another", "solr", "field"};
		this.solrObject.set("b", array1);	
		this.solrObject.set("c", new SolrObject());
		SolrObject[] array2 = {new SolrObject(), new SolrObject(), new SolrObject()};
		this.solrObject.set("d", array2);
		
		// test set
		assertNotNull(this.solrObject.unwrap().getFieldValue("a"));
		assertEquals("solr field", this.solrObject.unwrap().getFieldValue("a"));
		assertEquals(1, this.solrObject.unwrap().getFieldValues("a").size());
		
		assertNotNull(this.solrObject.unwrap().getFieldValues("b"));
		assertArrayEquals(array1, this.solrObject.unwrap().getFieldValues("b").toArray());
		assertEquals(3, this.solrObject.unwrap().getFieldValues("b").size());
		
		assertNotNull(this.solrObject.unwrap().getChildDocuments());
		assertEquals(4, this.solrObject.unwrap().getChildDocuments().size());
		assertNull(this.solrObject.unwrap().getFieldValue("c"));
		assertNull(this.solrObject.unwrap().getFieldValue("d"));
	}
}
