package com.narvee.entity;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tms_email_configuration")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmsEmailConfiguration {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String emailNotificationType;
	private  Boolean isEnabled;
	private String ccMails;
	private String bccMails;
	private String subject;
	private Long adminId;

}
