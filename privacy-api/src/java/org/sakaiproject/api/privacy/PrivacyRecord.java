package org.sakaiproject.api.privacy;

public interface PrivacyRecord
{
  public Integer getLockId();
  
  public void setLockId(Integer lockId);
  
  public String getContextId();
  
  public void setContextId(String contextId);
  
  public String getUserId();
  
  public void setUserId(String userId);
  
  public boolean getViewable();
  
  public void setViewable(boolean viewable);
  
  public String getRecordType();
  
  public void setRecordType(String recordType);

  public Long getSurrogateKey();

	public void setSurrogateKey(Long surrogateKey);
}
