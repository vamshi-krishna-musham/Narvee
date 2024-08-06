package com.narvee.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.narvee.commons.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@EqualsAndHashCode(callSuper = true)
public class Project extends AuditModel {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="projectid" )
	private Long projectId;
	@Column(name = "projectname")
	private String projectName;
	@Column(name ="addedby" )
	private Long addedBy;
	@Column(name ="updatedby")
	private Long updatedBy;
	@Column(name = "projectdescription", columnDefinition = "MEDIUMTEXT")
	private String description;
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
    private List<Task> tasks = new ArrayList<>();

}
