package com.narvee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface TaskTrackerDTO {

	public Long getTrackid();

	public Long getTaskid();
	
	public Long getSubtaskid();
	
	public Long getUpdatedby();
	
	public Long getPid();

	public String getStatus();

	public String getFullname();

	public String getTicketid();

	public String getTaskName();
	
	public String getSubtaskname();
	
	public String getDescription();
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public LocalDate getCreateddate();
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public LocalDate getUpdateddate();
	
	public LocalDate getTarget_date();
	
	public LocalDate getStart_date();

	public String getTaskdescription();

	public String getPseudoname();
	
	public String getDuration();
	
	public String getPriority();
	
	public String getEmail();

	public String getSubtasktokenid();

	public Long getSubtaskmaxnum();

}
