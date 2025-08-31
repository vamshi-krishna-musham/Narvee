package com.narvee.ats.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.narvee.ats.auth.commons.AuditModel;

import lombok.Data;

@Entity
@Data
@Table(name = "visa")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Visa  extends AuditModel{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Visa")
	@SequenceGenerator(name = "Visa", sequenceName = "Visa_seq")
	
//	@Id
//	@Column(name = "id")
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@SequenceGenerator(name = "Visa", sequenceName = "Visa_seq")
	private Long vid;

	@Column(name = "visa_status")
	private String visastatus;

	@Column(name = "visa_description")
	private String description;

	@Column(name = "added_by", nullable = false, updatable = false)
	private long addedby;

	private long updatedby;
	
	public String contactnumber;



}
