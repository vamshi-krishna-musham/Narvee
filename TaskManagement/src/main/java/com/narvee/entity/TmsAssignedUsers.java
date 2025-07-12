package com.narvee.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TmsAssignedUsers {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long assignid;
	private Long userid;
	private Long tmsUserId;
	private boolean completed ;
	private String userstatus = "open";
	
	@Transient
	private String fullname;
	
	@Transient
	private String pseudoname;
	
}
