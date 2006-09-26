package org.sakaiproject.hbm.privacy;

public class PrivacyRecordImpl
{
  private Long surrogateKey;
	private Integer lockId;
	private String contextId;
	private String recordType;
	private String userId;
	private boolean viewable;
	
	public PrivacyRecordImpl()
	{
		
	}

	public PrivacyRecordImpl(String userId, String contextId, String recordType, boolean viewable)
	{
		this.userId = userId;
		this.contextId = contextId;
		this.recordType = recordType;
		this.viewable = viewable;
	}
	
	public String getContextId()
	{
		return contextId;
	}

	public Integer getLockId()
	{
		return lockId;
	}

	public String getRecordType()
	{
		return recordType;
	}

	public String getUserId()
	{
		return userId;
	}

	public boolean getViewable()
	{
		return viewable;
	}

	public void setContextId(String contextId)
	{
		this.contextId = contextId;
	}

	public void setLockId(Integer lockId)
	{
		this.lockId = lockId;
	}

	public void setRecordType(String recordType)
	{
		this.recordType = recordType;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public void setViewable(boolean viewable)
	{
		this.viewable = viewable;
	}

	public Long getSurrogateKey()
	{
		return surrogateKey;
	}

	public void setSurrogateKey(Long surrogateKey)
	{
		this.surrogateKey = surrogateKey;
	}

}
