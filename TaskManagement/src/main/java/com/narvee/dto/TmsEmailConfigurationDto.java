package com.narvee.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmsEmailConfigurationDto {

	private Long id;
	private String emailNotificationType;
	private  Boolean isEnabled;
	private List<String> bccMails;
	private List<String> ccMails;
	private String subject;
	private Long adminId;
	
}
