package com.narvee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class TasksResponseDTO {

	    private Long trackid;
	    private Long taskid;
	    private Long pid;
	    private String status;
	    private String fullname;
	    private String ticketid;
	    private String taskName;
	    private String description;
	  
	    private LocalDateTime createddate;
	    private LocalDate targetdate;
	    private String taskdescription;
	    private String pseudoname;
	    private String duration;
	    List<GetUsersDTO> assignUsers;
	   
	    public TasksResponseDTO(TaskTrackerDTO dto) {
	        this.trackid = dto.getTrackid();
	        this.taskid = dto.getTaskid();
	        this.status = dto.getStatus();
	        this.fullname = dto.getFullname();
	        this.ticketid = dto.getTicketid();
	        this.taskName = dto.getTaskName();
	        this.description = dto.getDescription();
	        this.createddate = dto.getCreateddate();
	        this.targetdate = dto.getTargetdate();
	        this.taskdescription = dto.getTaskdescription();
	        this.pseudoname = dto.getPseudoname();
	        this.duration = dto.getDuration();
	        this.pid=dto.getPid();
	    }

}
