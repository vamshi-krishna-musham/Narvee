package com.narvee.dto;

import java.time.LocalDate;

public interface SubTaskUserDTO {
	
	public Long getSubTaskID();
	public String getSubTaskName();
	public String getSubTaskDescription();
	public String getStatus();
	public LocalDate getTargetdate();
	public String getaddedby();
	public String getsubtasktokenid();
	public Long getsubtaskmaxnum();
	
}
