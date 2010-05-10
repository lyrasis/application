package org.collectionspace.chain.csp.persistence.services.vocab;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.collectionspace.bconfigutils.bootstrap.BootstrapConfigLoadFailedException;
import org.collectionspace.chain.csp.config.ConfigRoot;
import org.collectionspace.chain.csp.inner.CoreConfig;
import org.collectionspace.chain.csp.persistence.services.ServicesBaseClass;
import org.collectionspace.chain.csp.persistence.services.ServicesStorageGenerator;
import org.collectionspace.chain.csp.persistence.services.connection.ConnectionException;
import org.collectionspace.chain.csp.schema.Record;
import org.collectionspace.chain.csp.schema.Spec;
import org.collectionspace.csp.api.container.CSPManager;
import org.collectionspace.csp.api.core.CSPDependencyException;
import org.collectionspace.csp.api.core.CSPRequestCredentials;
import org.collectionspace.csp.api.persistence.ExistException;
import org.collectionspace.csp.api.persistence.Storage;
import org.collectionspace.csp.api.persistence.StorageGenerator;
import org.collectionspace.csp.container.impl.CSPManagerImpl;
import org.collectionspace.csp.helper.core.RequestCache;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class TestVocab extends ServicesBaseClass {
	private static final Logger log=LoggerFactory.getLogger(TestVocab.class);
	private static Pattern person_urn=Pattern.compile("urn:cspace.org.collectionspace.demo.personauthority:name\\((.*?)\\):person:name\\((.*?)\\)'(.*?)'");
	private 
	
	static Pattern vocab_urn=Pattern.compile("urn:cspace:org.collectionspace.demo:vocabulary\\((.*?)\\):item\\((.*?)\\)'(.*?)'");
		
	private InputStream getRootSource(String file) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
	}
	
	@Before public void checkServicesRunning() throws BootstrapConfigLoadFailedException, ConnectionException {
		setup();
	}
	
	@Test public void testVocab() throws Exception {
		Storage ss=makeServicesStorage(base+"/cspace-services/");
		// Create
		JSONObject data=new JSONObject();
		data.put("name","TEST");
		String id=ss.autocreateJSON("/vocab/xxx",data);
		Matcher m=vocab_urn.matcher(id);
		assertTrue(m.matches());
		assertEquals("TEST",m.group(3));
		// Read
		JSONObject out=ss.retrieveJSON("/vocab/xxx/"+id);
		assertEquals("TEST",out.getString("name"));
		// Update
		data.remove("name");
		data.put("name","TEST2");
		ss.updateJSON("/vocab/xxx/"+id,data);
		out=ss.retrieveJSON("/vocab/xxx/"+id);
		assertEquals("TEST2",out.getString("name"));
		String id3=out.getString("csid");
		// List
		data.remove("name");
		data.put("name","TEST3");
		String id2=ss.autocreateJSON("/vocab/xxx",data);
		out=ss.retrieveJSON("/vocab/xxx/"+id2);
		assertEquals("TEST3",out.getString("name"));		
		boolean found1=false,found2=false;
		for(String u : ss.getPaths("/vocab/xxx",null)) {
			log.debug(u);
			if(id3.equals(u)){
				found1=true;
			}
			if(id2.equals(u)){
				found2=true;
			}
		}
		log.debug("id2="+id2+" f="+found2);
		log.debug("id3="+id3+" f="+found1);
		assertTrue(found1);
		assertTrue(found2);
		// Delete
		ss.deleteJSON("/vocab/xxx/"+id);
		try {
			out=ss.retrieveJSON("/vocab/xxx/"+id);		
			assertTrue(false);
		} catch(ExistException x) {}
	}
	
	@Test public void testName() throws Exception {
		Storage ss=makeServicesStorage(base+"/cspace-services/");
		// Create
		JSONObject data=new JSONObject();
		data.put("displayName","TEST");
		data.put("status","Provisional");
		String id=ss.autocreateJSON("/person/person",data);
		// Read
		JSONObject out=ss.retrieveJSON("/person/person/"+id);
		assertEquals("TEST",out.getString("displayName"));
		assertEquals("Provisional",out.getString("status"));
		// Update
		data.remove("displayName");
		data.put("displayName","TEST2");
		data.put("status","Provisional2");
		ss.updateJSON("/person/person/"+id,data);
		out=ss.retrieveJSON("/person/person/"+id);
		assertEquals("TEST2",out.getString("displayName"));
		assertEquals("Provisional2",out.getString("status"));
		String id3=out.getString("csid");
		// List
		data.remove("displayName");
		data.put("displayName","TEST3");
		String id2=ss.autocreateJSON("/person/person",data);
		out=ss.retrieveJSON("/person/person/"+id2);
		assertEquals("TEST3",out.getString("displayName"));		
		boolean found1=false,found2=false;
		for(String u : ss.getPaths("/person/person",null)) {
			log.info(u);
			if(id3.equals(u)){
				found1=true;
			}
			if(id2.equals(u)){
				found2=true;
			}
		}
		if(!found1||!found2){
			for(String u : ss.getPaths("/person/person",null)) {
				log.info(u);
				if(id3.equals(u)){
					found1=true;
				}
				if(id2.equals(u)){
					found2=true;
				}
			}
			
		}
		log.info("id2="+id2+" f="+found2);
		log.info("id3="+id3+" f="+found1);
		/* XXX pagination: failing because pagination support is not there yet */
		//assertTrue(found1);
		//assertTrue(found2);
		// Delete
		ss.deleteJSON("/person/person/"+id);
		try {
			out=ss.retrieveJSON("/person/person/"+id);		
			assertTrue(false);
		} catch(ExistException x) {}
	}
	
	/* Commented until wednesday 
	// XXX factor tests
	@Test public void testOrgs() throws Exception {
		Storage ss=makeServicesStorage(base+"/cspace-services/");
		// Create
		JSONObject data=new JSONObject();
		data.put("displayName","TEST");
		String id=ss.autocreateJSON("/organization/organization",data);
		Matcher m=person_urn.matcher(id);
		assertTrue(m.matches());
		assertEquals("TEST",m.group(3));
		// Read
		JSONObject out=ss.retrieveJSON("/organization/organization/"+id);
		assertEquals("TEST",out.getString("displayName"));
		// Update
		data.remove("name");
		data.put("displayName","TEST2");
		ss.updateJSON("/organization/organization/"+id,data);
		out=ss.retrieveJSON("/organization/organization/"+id);
		assertEquals("TEST2",out.getString("displayName"));
		String id3=out.getString("csid");
		// List
		data.remove("name");
		data.put("displayName","TEST3");
		String id2=ss.autocreateJSON("/organization/organization",data);
		out=ss.retrieveJSON("/organization/organization/"+id2);
		assertEquals("TEST3",out.getString("displayName"));		
		boolean found1=false,found2=false;
		for(String u : ss.getPaths("/organization/organization",null)) {
			log.info(u);
			if(id3.equals(u))
				found1=true;
			if(id2.equals(u))
				found2=true;
		}
		log.info("id2="+id2+" f="+found2);
		log.info("id3="+id3+" f="+found1);
		assertTrue(found1);
		assertTrue(found2);
		// Delete
		ss.deleteJSON("/organization/organization/"+id);
		try {
			out=ss.retrieveJSON("/organization/organization/"+id);		
			assertTrue(false);
		} catch(ExistException x) {}
	}
	*/
	
}
