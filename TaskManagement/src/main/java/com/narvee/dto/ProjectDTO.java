package com.narvee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


public interface ProjectDTO {

	public Long getPid();

	public String getProjectname();

	public String getProjectdescription();

	public String getAddedby();
	
	public String getAddedByFullname();
	
	public String getStatus();

	public String getUpdatedby();
	
	public String getProjectid();
	
	public LocalDate getCreateddate();
	
	public LocalDate getStartDate();
	
	public LocalDate getTargetDate();
	
	 public String getAssignedTo();
	 
	
	
}
