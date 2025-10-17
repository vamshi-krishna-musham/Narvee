package com.narvee.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {

	//private List<TasksResponseDTO> tasks;
	private Page<TasksResponseDTO> tasks;
	private Long pid;

}
