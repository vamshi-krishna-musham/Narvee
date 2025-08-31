package com.narvee.ats.auth.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.narvee.ats.auth.commons.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Entity
@Data
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Roles  extends AuditModel {
	private static final long serialVersionUID = 1L;
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roleid;

	@Column(length = 100)
	private String rolename;

	@Column(length = 255)
	private String description;

	@Column(length = 10)
	private long roleno;

	@Column(length = 255)
	private String remarks;

	@Column(length = 30)
	private String status = "Active";

//	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name = "addedby", nullable = false, updatable = false)
	private long addedby;

//	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name = "updatedby")
	private long updatedby;
	

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "role_privilege", joinColumns = {
			@JoinColumn(name = "role_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "privilege_id", nullable = false, updatable = false) })
	private Set<Privilege> privileges = new HashSet<Privilege>();
	


	@Column(length = 250)
	private Long companyid;
	
}
