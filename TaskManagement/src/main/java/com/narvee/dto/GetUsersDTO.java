package com.narvee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface GetUsersDTO {
	
	public Long getUserid();
	public String getFullname();
	public String getPseudoname();
	public String getEmail();
	public LocalDate getCreatedby();
	public String getTaskid();
	public String getProjectname();
	public String getTaskname();
	public String getTicketid();
	public String getUpdatedby();
	public String getCemail();
	public String getStart_date();
	public String getTarget_date();
	public byte[] getProfile();
	public byte[] getpCprofile();
 	
	
}
