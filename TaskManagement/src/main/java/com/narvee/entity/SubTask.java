package com.narvee.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.narvee.commons.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubTask extends AuditModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subtaskid")
	private Long subTaskId;
	@Column(name = "subtaskname")
	private String subTaskName;
	@Column(name = "subtaskdescription")
	private String subTaskDescription;
	@Column(name = "addedby")
	private Long addedBy;
	@Column(name = "updatedby")
	private Long updatedBy;
	@Column(name = "status")
	private String status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(name = "targetdate")
	private LocalDate targetDate;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "taskid")
	private Task task;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "subtaskid")
	private List<AssignedUsers> assignedto;

	@Transient
	private Long taskId;
}
