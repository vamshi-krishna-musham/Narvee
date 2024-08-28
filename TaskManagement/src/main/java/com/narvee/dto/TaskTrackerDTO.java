package com.narvee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface TaskTrackerDTO {

	public Long getTrackid();

	public Long getTaskid();
	
	public Long getUpdatedby();
	
	public Long getPid();

	public String getStatus();

	public String getFullname();

	public String getTicketid();

	public String getTaskName();

	public String getDescription();
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	public LocalDateTime getCreateddate();
	
	public LocalDate getTargetdate();

	public String getTaskdescription();

	public String getPseudoname();
	
	public String getDuration();

}
