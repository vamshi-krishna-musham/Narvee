package com.narvee.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssignedUsers {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long assignid;
	private Long userid;
	private boolean completed ;
	private String userstatus ="Assigned";
}
