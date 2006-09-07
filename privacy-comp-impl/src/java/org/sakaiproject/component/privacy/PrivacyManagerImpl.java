package org.sakaiproject.component.privacy;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.sakaiproject.api.privacy.PrivacyManager;
import org.sakaiproject.api.privacy.PrivacyRecord;
import org.sakaiproject.hbm.privacy.PrivacyRecordImpl;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


public class PrivacyManagerImpl extends HibernateDaoSupport implements PrivacyManager
{
	private static Log log = LogFactory.getLog(PrivacyManagerImpl.class);
	
	private static final String QUERY_BY_USERID_CONTEXTID_TYPEID = "findPrivacyByUserIdContextIdType";
	private static final String QUERY_BY_DISABLED_USERID_CONTEXTID = "findDisabledPrivacyUserIdContextIdType";
	private static final String QUERY_BY_CONTEXT_VIEWABLE_TYPE = "finalPrivacyByContextViewableType";
	private static final String QUERY_BY_CONTEXT__TYPE = "finalPrivacyByContextType";
	private static final String CONTEXT_ID = "contextId";
	private static final String USER_ID = "userId";
	private static final String RECORD_TYPE = "recordType";
	private static final String VIEWABLE = "viewable";
	
	protected boolean defaultViewable = true;
	protected Boolean overrideViewable = null;
	protected boolean userRecordHasPrecedence = true;

	public Set findViewable(String contextId, Set userIds)
	{
		if (contextId == null || userIds == null)
		{
			throw new IllegalArgumentException("Null Argument in findViewable");
		}

		if(overrideViewable!=null)
		{
			if(overrideViewable.booleanValue())
				return new HashSet();
			else
				return userIds;
		}
		
		Iterator iter = userIds.iterator();
		Set returnSet = new HashSet();
		while(iter.hasNext())
		{
			String userId = (String) iter.next();
			String returnUser = getDisabledPrivacy(contextId, userId); 
			if(returnUser != null)
				returnSet.add(returnUser);
		}
		
		return returnSet;
	}

	public Set getViewableState(String contextId, Boolean value, String recordType)
	{
  	if(contextId == null || value == null || recordType == null)
  	{
      throw new IllegalArgumentException("Null Argument in getViewableState");
  	}

		List returnedList = getViewableStateList(contextId, value, recordType);
		if(returnedList != null)
		{
			Set returnSet = new HashSet();
			for(int i=0; i<returnedList.size(); i++)
			{
				returnSet.add(((PrivacyRecord)returnedList.get(i)).getUserId());
			}
			return returnSet;
		}
		else
			return null;
	}

	public Map getViewableState(String contextId, String recordType)
	{
  	if(contextId == null || recordType == null)
  	{
      throw new IllegalArgumentException("Null Argument in getViewableState");
  	}

  	List returnedList = getPrivacyByContextAndType(contextId, recordType);
  	if(returnedList != null)
  	{
  		HashMap returnMap = new HashMap(); 
			PrivacyRecord pr;
  		for(int i=0; i<returnedList.size(); i++)
  		{
  			pr = (PrivacyRecord)returnedList.get(i);
  			returnMap.put(pr.getUserId(), new Boolean(pr.getViewable()));
  		}
  		return returnMap;
  	}
		return null;
	}

	public boolean isViewable(String contextId, String userId)
	{
		if(contextId == null || userId == null)
		{
			throw new IllegalArgumentException("Null Argument in isViewable");
		}
		
		if(overrideViewable != null)
		{
			return overrideViewable.booleanValue();
		}
		else
		{
			PrivacyRecord sysRecord = getPrivacy(contextId, userId, PrivacyManager.SYSTEM_RECORD_TYPE);
			PrivacyRecord userRecord = getPrivacy(contextId, userId, PrivacyManager.USER_RECORD_TYPE);
			
			return checkPrivacyRecord(sysRecord, userRecord);
		}
	}

	public void setViewableState(String contextId, String userId, Boolean value, String recordType)
	{
		if (contextId == null || userId == null || value == null || recordType == null)
		{
			throw new IllegalArgumentException("Null Argument in setViewableState");
		}
		
		PrivacyRecord pr = getPrivacy(contextId, userId, recordType);
		if(pr != null)
		{
			pr.setViewable(value.booleanValue());
			savePrivacyRecord(pr);
		}
		else
		{
			pr = createPrivacyRecord(userId, contextId, recordType, value.booleanValue());
		}
	}

	public void setViewableState(String contextId, Map userViewableState, String recordType)
	{
		if (contextId == null || userViewableState == null || recordType == null)
		{
			throw new IllegalArgumentException("Null Argument in setViewableState");
		}
		
		Set keySet = userViewableState.keySet();
		Iterator iter = keySet.iterator();
		while(iter.hasNext())
		{
			String userId = (String)iter.next();
			Boolean viewable = (Boolean) userViewableState.get(userId);
			setViewableState(contextId, userId, viewable, recordType);
		}
	}

	
	private PrivacyRecord getPrivacy(final String contextId, final String userId, final String recordType)
	{
		if (contextId == null || userId == null || recordType == null)
		{
			throw new IllegalArgumentException("Null Argument in getPrivacy");
		}

		HibernateCallback hcb = new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException,
			SQLException
			{
        Query q = session.getNamedQuery(QUERY_BY_USERID_CONTEXTID_TYPEID);
        q.setParameter(CONTEXT_ID, contextId, Hibernate.STRING);
        q.setParameter(USER_ID, userId, Hibernate.STRING);
        q.setParameter(RECORD_TYPE, recordType, Hibernate.STRING);
        return q.uniqueResult();
			}
		};

		return (PrivacyRecord) getHibernateTemplate().execute(hcb);

	}

	
	private String getDisabledPrivacy(final String contextId, final String userId)
	{
		if (contextId == null || userId == null)
		{
			throw new IllegalArgumentException("Null Argument in getDisabledPrivacy");
		}
		PrivacyRecord sysRecord = getPrivacy(contextId, userId, PrivacyManager.SYSTEM_RECORD_TYPE);
		PrivacyRecord userRecord = getPrivacy(contextId, userId, PrivacyManager.USER_RECORD_TYPE);
		if(!checkPrivacyRecord(sysRecord, userRecord))
			return userId;
		else
			return null;
	}
	
//	private PrivacyRecord findPrivacyWithFullArg(final String contextId, final String userId, final String recordType, final Boolean viewable)
//	{
//		if (contextId == null || userId == null || recordType == null || viewable == null)
//		{
//			throw new IllegalArgumentException("Null Argument in findPrivacyWithFullArg");
//		}
//		HibernateCallback hcb = new HibernateCallback()
//		{
//			public Object doInHibernate(Session session) throws HibernateException,
//			SQLException
//			{
//        Query q = session.getNamedQuery(QUERY_BY_DISABLED_USERID_CONTEXTID);
//        q.setParameter(USER_ID, userId, Hibernate.STRING);
//        q.setParameter(CONTEXT_ID, contextId, Hibernate.STRING);
//        q.setParameter(RECORD_TYPE, recordType, Hibernate.STRING);
//        q.setParameter(VIEWABLE, viewable, Hibernate.BOOLEAN);
//        return q.uniqueResult();
//			}
//		};
//
//		return (PrivacyRecord) getHibernateTemplate().execute(hcb);
//	}

  private PrivacyRecord createPrivacyRecord(final String userId, 
  		final String contextId, final String recordType, final boolean viewable)
  {
    if (userId == null || contextId == null || recordType == null )
    {
      throw new IllegalArgumentException("Null Argument in createPrivacyRecord");
    }
    else
    {
      PrivacyRecord privacy = new PrivacyRecordImpl(userId, contextId, recordType, viewable);      
      savePrivacyRecord(privacy);
      return privacy;
    }
  }
  
  private List 	getViewableStateList(final String contextId, final Boolean viewable, final String recordType)
  {
  	if(contextId == null || viewable == null || recordType == null)
  	{
      throw new IllegalArgumentException("Null Argument in getViewableStateList");
  	}
  	
    HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(QUERY_BY_CONTEXT_VIEWABLE_TYPE);
        q.setParameter("contextId", contextId, Hibernate.STRING);
        q.setParameter("viewable", viewable, Hibernate.BOOLEAN);
        q.setParameter("recordType", recordType, Hibernate.STRING);
        return q.list();
      }
    };

    return (List) getHibernateTemplate().execute(hcb); 
  }
  
  private List getPrivacyByContextAndType(final String contextId, final String recordType)
  {
  	if(contextId == null || recordType == null)
  	{
  		throw new IllegalArgumentException("Null Argument in getPrivacyByContextAndType");
  	}
  	
  	HibernateCallback hcb = new HibernateCallback()
  	{
  		public Object doInHibernate(Session session) throws HibernateException,
  		    SQLException
  		{
        Query q = session.getNamedQuery(QUERY_BY_CONTEXT__TYPE);
        q.setParameter("contextId", contextId, Hibernate.STRING);
        q.setParameter("recordType", recordType, Hibernate.STRING);
        return q.list();
  		}
  	};
  	
  	return (List) getHibernateTemplate().executeFind(hcb);
  }
  
  private void savePrivacyRecord(PrivacyRecord privacy)
  {
  	getHibernateTemplate().saveOrUpdate(privacy);
  }

  private void removePrivacyObject(PrivacyRecord o)
  {
    getHibernateTemplate().delete(o);
  }
  
  private boolean checkPrivacyRecord(PrivacyRecord sysRecord, PrivacyRecord userRecord)
  {
		if(sysRecord != null && userRecord != null)
		{
			if(userRecordHasPrecedence)
			{
				return userRecord.getViewable();
			}
			else
				return sysRecord.getViewable();
		}
		else if(sysRecord == null && userRecord == null)
		{
			if(defaultViewable)
				return true;
			else
				return false;
		}
		else if(sysRecord != null)
		{
			return sysRecord.getViewable();
		}
		else
		{
			return userRecord.getViewable();
		}
  }
  
	/**
	 * A 'true' value will set privacy enabled for a user whose privacy settings
	 * are not known. A 'false' value will set privacy disabled for a user whose
	 * privacy settings are not known (i.e. no data found).
	 * 
	 * The default behavior will be to show users or make them viewable.
	 * 
	 * @param defaultViewable
	 *          the defaultViewable to set
	 */
	public void setDefaultViewable(boolean defaultViewable)
	{
		this.defaultViewable = defaultViewable;
	}

	/**
	 * A 'true' value will make all users viewable in the system. A 'false' value
	 * will make all users hidden in the system.
	 * 
	 * Do not set this value for normal operation (non overridden behavior; i.e. null).
	 * 
	 * @param overrideViewable
	 *          the overrideViewable to set
	 */
	public void setOverrideViewable(Boolean overrideViewable)
	{
		this.overrideViewable = overrideViewable;
	}

	/**
	 * A 'true' value indicates that a user record has precedence over a system
	 * record. A 'false' value indicates that a system record has precedence over
	 * a user record
	 * 
	 * @param userRecordHasPrecedence
	 *          the userRecordHasPrecedence to set
	 */
	public void setUserRecordHasPrecedence(boolean userRecordHasPrecedence)
	{
		this.userRecordHasPrecedence = userRecordHasPrecedence;
	}
}
