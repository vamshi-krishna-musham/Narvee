package com.narvee.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.narvee.commons.AuditModel;
import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.TmsUsersInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data@AllArgsConstructor
@NoArgsConstructor
@Table
@EqualsAndHashCode(callSuper = true)
public class TmsProject extends AuditModel {
	
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
	private Long adminId;
	@Column(name ="updatedby")
	private Long updatedBy;
	private Long pmaxnum;
	private String status="To Do";
	@Column(name = "projectdescription", columnDefinition = "MEDIUMTEXT")
	private String description;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate targetDate;
	

	private String department;
	
	@JsonManagedReference
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)	
	@JoinColumn(name = "pid")
    private List<TmsTask> tasks = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "pid")
    private Set<TmsAssignedUsers> assignedto;
	
	 @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	 @JsonManagedReference(value = "project-file")
     @ToString.Exclude
	    private List<TmsFileUpload> files = new ArrayList<>();


	public List<GetUsersDTO> getAssignedUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	

	
}
