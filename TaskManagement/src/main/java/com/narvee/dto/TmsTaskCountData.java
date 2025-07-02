package com.narvee.dto;

public interface TmsTaskCountData {

	public String getType();
	public String getStatus();
	public String getPriority();
	public Long getCount();
	public String getMonth();
	public Long getClosedTaskCount();
	public Long getInProgressTaskCount();
	public Long getOverDueTaskCount();
	public Long getBlockedTaskCount();
	public Long getOnHoldTaskCount();
	public Long getOpenTaskCount();
	public Long getToBeTestedTaskCount();
	public Long getInReviewCount();
	public String getFirstName();
	public Long getTotalAssignedTasks();
	public String getPosition();
	
	
}
