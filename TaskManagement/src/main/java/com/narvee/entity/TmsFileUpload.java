package com.narvee.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tms_file_upload")
public class TmsFileUpload {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fileName;
    private String filePath;
    private String fileType;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pid")
    @JsonBackReference(value = "project-file")
    @ToString.Exclude
    private TmsProject project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskId")
    @JsonBackReference(value = "task-file")
    private TmsTask task;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subTaskId")
    @JsonBackReference(value = "subTask-file")
    private TmsSubTask subtask;
    

}
