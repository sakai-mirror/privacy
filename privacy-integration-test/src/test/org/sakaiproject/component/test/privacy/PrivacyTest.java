package org.sakaiproject.component.test.privacy;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.api.privacy.PrivacyManager;
import org.sakaiproject.api.privacy.PrivacyRecord;
import org.sakaiproject.test.SakaiTestBase;

public class PrivacyTest extends SakaiTestBase
{	
	private static Log log = LogFactory.getLog(PrivacyTest.class);
	private PrivacyManager privacyManager;
	
	public static Test suite() 
	{
		TestSetup setup = new TestSetup(new TestSuite(PrivacyTest.class)) 
		{
			protected void setUp() throws Exception 
			{
				log.info("starting setup -- PrivacyTest");
				oneTimeSetup();
				log.info("finished setup -- PrivacyTest");
			}
			protected void tearDown() throws Exception 
			{
				oneTimeTearDown();
			}
		};
		return setup;
	}

	public void setUp() throws Exception 
	{
		log.info("Setting up an AuthzIntegrationTest test");

		privacyManager = (PrivacyManager)getService(PrivacyManager.class.getName());
	}
	
	public void tearDown() throws Exception 
	{
		log.info("Tearing down an PrivacyTest test");
		
		privacyManager = null;
	}
	
	public void testSetViewableState() throws Exception 
	{
//		privacyManager.setViewableState("main_page", "a", new Boolean(false), org.sakaiproject.api.privacy.PrivacyManager.USER_RECORD_TYPE);
//		privacyManager.setViewableState("main_page", "a", new Boolean(true), org.sakaiproject.api.privacy.PrivacyManager.SYSTEM_RECORD_TYPE);
//		
//		privacyManager.setViewableState("main_page1", "a", new Boolean(false), org.sakaiproject.api.privacy.PrivacyManager.USER_RECORD_TYPE);
//		privacyManager.setViewableState("main_page1", "a", new Boolean(true), org.sakaiproject.api.privacy.PrivacyManager.SYSTEM_RECORD_TYPE);
//
//		privacyManager.setViewableState("main_page", "b", new Boolean(false), org.sakaiproject.api.privacy.PrivacyManager.USER_RECORD_TYPE);		
//		privacyManager.setViewableState("main_page", "b", new Boolean(true), org.sakaiproject.api.privacy.PrivacyManager.SYSTEM_RECORD_TYPE);
//
//		privacyManager.setViewableState("main_page1", "b", new Boolean(true), org.sakaiproject.api.privacy.PrivacyManager.USER_RECORD_TYPE);		
//		privacyManager.setViewableState("main_page1", "b", new Boolean(true), org.sakaiproject.api.privacy.PrivacyManager.SYSTEM_RECORD_TYPE);
	}
	
	public void testFindViewable() throws Exception
	{
		Set inputSet = new TreeSet();
		inputSet.add("a");
		inputSet.add("b");
		inputSet.add("c");
		Set resultSet = privacyManager.findViewable("main_page", inputSet);
		Iterator iter = resultSet.iterator();
		while(iter.hasNext())
		{
			PrivacyRecord pr = (PrivacyRecord)iter.next();
			log.info("PrivacyRecord -- " + pr.getContextId() + ":" + pr.getUserId() + ":" + pr.getRecordType() + ":" + pr.getViewable());
		}
	}
	
	public void testGetViewableState() throws Exception
	{
		Set resultSet = privacyManager.getViewableState("main_page1", new Boolean(true), PrivacyManager.SYSTEM_RECORD_TYPE);
		Iterator iter = resultSet.iterator();
		while(iter.hasNext())
		{
			PrivacyRecord pr = (PrivacyRecord)iter.next();
			log.info("PrivacyRecord -- " + pr.getContextId() + ":" + pr.getUserId() + ":" + pr.getRecordType() + ":" + pr.getViewable());
		}
		
	}
}
