package com.narvee.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.narvee.entity.TmsSubTask;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskResponse {
	
	//private List<SubTaskResponseDTO> subtasks;
	Page<SubTaskResponseDTO> subtasks;
	private Long taskId;

}
