package com.narvee.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubTaskResponseDTO {
	private Long trackid;
	private Long taskid;
	private Long pid;
	private String status;
	private String fullname;
	private String ticketid;
	private String taskName;
	private String subTaskDescription;
	private LocalDate startDate;
	private Long subTaskId;
	private String subtaskName;
	private LocalDate createddate;
	private LocalDate targetdate;
	private String taskdescription;
	private String pseudoname;
	private String duration;
	private String priority;
	List<GetUsersDTO> assignUsers;

	List<FileUploadDto> files;

	public SubTaskResponseDTO(TaskTrackerDTO dto) {
		this.trackid = dto.getTrackid();
		this.taskid = dto.getTaskid();
		this.status = dto.getStatus();
		this.fullname = dto.getFullname();
		this.ticketid = dto.getTicketid();
		this.taskName = dto.getTaskName();
		this.startDate = dto.getStart_date();
		this.subTaskDescription = dto.getDescription();
		this.createddate = dto.getCreateddate();
		this.targetdate = dto.getTarget_date();
		this.taskdescription = dto.getTaskdescription();
		this.pseudoname = dto.getPseudoname();
		this.duration = dto.getDuration();
		this.subTaskId = dto.getSubtaskid();
		this.subtaskName = dto.getSubtaskname();
		this.priority = dto.getPriority();
		this.pid = dto.getPid();
	}

}
