package com.narvee.dto;

import java.time.LocalDateTime;

public interface ProjectDTO {

	public Long getPid();

	public String getProjectname();

	public String getProjectdescription();

	public String getAddedby();

	public String getStatus();

	public String getUpdatedby();
	
	public String getProjectid();
	
	public LocalDateTime getCreateddate();

	
}
