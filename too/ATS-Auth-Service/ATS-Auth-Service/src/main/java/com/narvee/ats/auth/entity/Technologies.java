package com.narvee.ats.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.narvee.ats.auth.commons.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "technologies")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Technologies extends AuditModel {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
	private String technologyarea;
	
	
	@Column(name = "functionalskills", columnDefinition = "TEXT")
	private String functionalSkills;

	@Column(name = "listofkeyword", columnDefinition = "TEXT")
	private String listofkeyword;

	@Column(name = "comments", length = 250)
	private String comments;

	@Column(name = "addedby", nullable = false, updatable = false)
	private long addedby;

	@Column(name = "updatedby")
	private long updatedby;

	@Column(name = "remarks", length = 255)
	private String remarks;

	private String status;
	public Technologies(Long id, String technologyarea, String listofkeyword, String comments, String remarks) {
		this.id = id;
		this.technologyarea = technologyarea;
		this.listofkeyword = listofkeyword;
		this.comments = comments;
		this.remarks = remarks;
	}

}
