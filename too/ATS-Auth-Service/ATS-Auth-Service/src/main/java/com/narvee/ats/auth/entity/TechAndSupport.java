package com.narvee.ats.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.narvee.ats.auth.commons.AuditModel;

import lombok.Data;

@Entity
@Data
@Table(name = "techsupport")

public class TechAndSupport extends AuditModel {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(name = "pseudo_name")
	private String pseudoname;

	private String mobile;

	@Column(name = "second_mobile")
	private String secmobile;

	private String email;

	@Column(name = "pre_company")
	private String precompany;

	private int experience;

	private String location;

	@Column(name = "techid")
	private Long technology;

	@Column(name = "skills", columnDefinition = "TEXT")
	private String skills;

	private String status = "Active";

	@Column(name = "updatedby")
	private long updatedby;

	@Column(name = "added_by", nullable = false, updatable = false)
	private long addedby = 1;

	@Column(name = "remarks", length = 250)
	private String remarks;

	@Transient
	private String tech;

	private TechAndSupport() {

	}

	public TechAndSupport(Long id, String name, int experience, String skills, String email, String mobile,
			String tech) {
		this.id = id;
		this.name = name;
		this.experience = experience;
		this.skills = skills;
		this.email = email;
		this.mobile = mobile;
		this.tech = tech;
	}

}
