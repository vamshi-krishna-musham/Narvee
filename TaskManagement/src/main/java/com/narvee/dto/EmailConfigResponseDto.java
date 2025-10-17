package com.narvee.dto;

public interface EmailConfigResponseDto {

	public String getSubject();
	public String getBccMails();
	public String getCcMails();
	public String getNotificationType();
	public Boolean getIsEnabled();
	
}
