package org.sakaiproject.component.privacy;

import java.util.Map;
import java.util.Set;

import org.sakaiproject.api.privacy.PrivacyManager;

public class PrivacyManagerImpl implements PrivacyManager
{

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
		// TODO Auto-generated method stub	
	}

	public void setViewableState(String contextId, Map userViewableState, String recordType)
	{
		// TODO Auto-generated method stub
	}

}
