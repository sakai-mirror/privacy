package org.sakaiproject.component.privacy;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

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
	private static final String QUERY_BY_USERID_CONTEXTID_TYPEID = "findPrivacyByUserIdContextIdType";
	private static final String QUERY_BY_DISABLED_USERID_CONTEXTID = "findDisabledPrivacyUserIdContextIdType";
	private static final String QUERY_BY_CONTEXT_VIEWABLE_TYPE = "finalPrivacyByContextViewableType";
	private static final String CONTEXT_ID = "contextId";
	private static final String USER_ID = "userId";
	private static final String RECORD_TYPE = "recordType";
	private static final String VIEWABLE = "viewable";

	public Set findViewable(String contextId, Set userIds)
	{
		if (contextId == null || userIds == null)
		{
			throw new IllegalArgumentException("Null Argument in findViewable");
		}

		Iterator iter = userIds.iterator();
		Set returnSet = new HashSet();
		while(iter.hasNext())
		{
			String userId = (String) iter.next();
			PrivacyRecord pr = getDisabledPrivacy(contextId, userId); 
			if(pr != null)
				returnSet.add(pr);
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
				returnSet.add((PrivacyRecord)returnedList.get(i));
			}
			return returnSet;
		}
		else
			return null;
	}

	public Map getViewableState(String contextId, String recordType)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isViewable(String contextId, String userId)
	{
		// TODO Auto-generated method stub
		return true;
	}

	public void setViewableState(String contextId, String userId, Boolean value, String recordType)
	{
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
		// TODO Auto-generated method stub
	}

	
	private PrivacyRecord getPrivacy(final String contextId, final String userId, final String recordType)
	{
		if (contextId == null)
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

	
	private PrivacyRecord getDisabledPrivacy(final String contextId, final String userId)
	{
		if (contextId == null || userId == null)
		{
			throw new IllegalArgumentException("Null Argument in getDisabledPrivacy");
		}

		return findPrivacyWithFullArg(contextId, userId, PrivacyManager.USER_RECORD_TYPE, new Boolean(false));

	}
	
	private PrivacyRecord findPrivacyWithFullArg(final String contextId, final String userId, final String recordType, final Boolean viewable)
	{
		if (contextId == null || userId == null || recordType == null || viewable == null)
		{
			throw new IllegalArgumentException("Null Argument in findPrivacyWithFullArg");
		}
		HibernateCallback hcb = new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException,
			SQLException
			{
        Query q = session.getNamedQuery(QUERY_BY_DISABLED_USERID_CONTEXTID);
        q.setParameter(USER_ID, userId, Hibernate.STRING);
        q.setParameter(CONTEXT_ID, contextId, Hibernate.STRING);
        q.setParameter(RECORD_TYPE, recordType, Hibernate.STRING);
        q.setParameter(VIEWABLE, viewable, Hibernate.BOOLEAN);
        return q.uniqueResult();
			}
		};

		return (PrivacyRecord) getHibernateTemplate().execute(hcb);
	}

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
  
  private void savePrivacyRecord(PrivacyRecord privacy)
  {
  	getHibernateTemplate().saveOrUpdate(privacy);
  }

  private void removePrivacyObject(PrivacyRecord o)
  {
    getHibernateTemplate().delete(o);
  }

}
