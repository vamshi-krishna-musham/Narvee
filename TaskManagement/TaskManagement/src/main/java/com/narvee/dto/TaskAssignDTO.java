package com.narvee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface TaskAssignDTO {

	public String getTicketid();

	public String getCreatedby();

	public String getPseudoname();

	public String getStatus();
	
	public String getAustatus();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	public LocalDateTime getCreateddate();

	public LocalDate getTargetdate();
}
