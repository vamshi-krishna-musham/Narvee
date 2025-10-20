package com.narvee.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTrackerDTOs {
	 private  String taskId ;
	    private  String taskName;
	    private  String description;
	    private  String assignedUsernames; // comma separated names
	    private  String createdBy;
	    private  LocalDate startDate;
	    private  LocalDate targetDate;
}
