package com.narvee.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.narvee.entity.AssignedUsers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
	
	private int Count;
	private List<AssignedUsers> assignedUsers;
	private Page<TaskTrackerDTO> taskTrackerDTO;
	
	

}
