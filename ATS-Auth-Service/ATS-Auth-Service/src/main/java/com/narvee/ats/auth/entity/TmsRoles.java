package com.narvee.ats.auth.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
import com.narvee.ats.auth.commons.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@Table
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TmsRoles extends AuditModel {
	private static final long serialVersionUID = -5864729843497670210L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roleid;

	@Column(length = 100)
	private String rolename;

	@Column(length = 255)
	private String description;

	@Column(length = 30)
	private String status = "Active";

	@Column(name = "addedby", nullable = false, updatable = false)
	private long addedby;

	@Column(name = "updatedby")
	private long updatedby;
	
	@Column(name = "admin_id")
	private long adminId;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinTable(name = "Tms_role_privilege", joinColumns = {
	@JoinColumn(name = "role_id", nullable = false, updatable = false) }, inverseJoinColumns = {
	@JoinColumn(name = "privilege_id", nullable = false, updatable = false) })
	private Set<TmsPrivilege> privileges = new HashSet<TmsPrivilege>();

}
