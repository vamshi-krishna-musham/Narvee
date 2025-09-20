package com.narvee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;


public interface ProjectDTO {

	public Long getPid();

	public String getProjectname();

	public String getProjectdescription();

	public String getAddedby();
	
	public String getaddedByFullname();
	
	public String getStatus();

	public String getUpdatedby();
	
	public String getUpdatedByFullname();
	
	public String getProjectid();
	
	public LocalDateTime getCreateddate();
	
	public LocalDate getstartDate();
	
	public LocalDate gettargetDate();  
	
	public LocalDate getUpdateddate();
	
	
}
