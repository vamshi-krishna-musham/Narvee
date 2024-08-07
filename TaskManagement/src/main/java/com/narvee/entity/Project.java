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
	@Column(name ="pid" )
	private Long pId;
	@Column(name ="projectid",nullable = false)
	private String projectid;
	@Column(name = "projectname")
	private String projectName;
	@Column(name ="addedby" )
	private Long addedBy;
	@Column(name ="updatedby")
	private Long updatedBy;
	private Long pmaxnum;
	private String status="toDo";
	@Column(name = "projectdescription", columnDefinition = "MEDIUMTEXT")
	private String description;
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "pid")
    private List<Task> tasks = new ArrayList<>();
}
