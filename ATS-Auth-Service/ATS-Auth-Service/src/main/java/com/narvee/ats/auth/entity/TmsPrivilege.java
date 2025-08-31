package com.narvee.ats.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.narvee.ats.auth.commons.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@Table
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
public class TmsPrivilege extends AuditModel {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	private String type;

	@Column(name = "description")
	private String description;

	@Column(name = "createdby", insertable = true, updatable = false)
	private Long createdBy;

	@Column(name = "updatedby", insertable = true)
	private Long updatedBy;

	
		@Column(name = "cardType")
		private String cardType ="one";  //one/many..
	
	private boolean selected;

	public enum privilegeType {
		 TEAM_MEMBERS,TASKS,SUB_TASKS,PROJECTS
	}

}
