package org.sakaiproject.component.privacy;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

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
	private static final String CONTEXT_ID = "contextId";
	private static final String USER_ID = "userId";
	private static final String RECORD_TYPE = "recordType";

	public Set findViewable(String contextId, Set userIds)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Set getViewableState(String contextId, Boolean value, String recordType)
	{
		// TODO Auto-generated method stub
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
			createPrivacyRecord(userId, contextId, recordType, value.booleanValue());
		}
		else
		{
			savePrivacyRecord(pr);
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
  
  private void savePrivacyRecord(PrivacyRecord privacy)
  {
  	getHibernateTemplate().saveOrUpdate(privacy);
  }

  private void removePrivacyObject(PrivacyRecord o)
  {
    getHibernateTemplate().delete(o);
  }

}
