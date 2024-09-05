package com.narvee.dto;

import java.util.List;

import com.narvee.entity.TmsSubTask;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskResponse {
	
	private List<TmsSubTask> subtasks;
	private Long taskId;

}
