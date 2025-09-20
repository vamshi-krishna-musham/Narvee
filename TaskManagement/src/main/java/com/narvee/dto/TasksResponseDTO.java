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
	  private LocalDate startdate;
	    private LocalDate createddate;
	    private LocalDate updateddate;
	    private LocalDate targetdate;
	    private String taskdescription;
	    private String pseudoname;
	    private String duration;
	    private String priority;
	    List<GetUsersDTO> assignUsers;
	    
	    List<FileUploadDto> files;
	   
	    public TasksResponseDTO(TaskTrackerDTO dto) {
	        this.trackid = dto.getTrackid();
	        this.taskid = dto.getTaskid();
	        this.status = dto.getStatus();
	        this.startdate = dto.getStart_date();
	        this.fullname = dto.getFullname();
	        this.ticketid = dto.getTicketid();
	        this.taskName = dto.getTaskName();
	        this.description = dto.getDescription();
	        this.createddate = dto.getCreateddate();
	        this.targetdate = dto.getTarget_date();
	        this.taskdescription = dto.getTaskdescription();
	        this.pseudoname = dto.getPseudoname();
	        this.duration = dto.getDuration();
	        this.priority = dto.getPriority();
	        this.pid=dto.getPid();
	        this.updateddate=dto.getUpdateddate();
	    }

}
