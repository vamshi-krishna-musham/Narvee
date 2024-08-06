package com.narvee.service.service;

import org.springframework.data.domain.Page;

import com.narvee.dto.RequestResponseDTO;
import com.narvee.dto.SubTaskUserDTO;
import com.narvee.entity.SubTask;

public interface SubTaskService {

	public SubTask createSubTask(SubTask subtask);

	public SubTask findBySubTaskId(Long subtaskid);

	public void deleteSubTask(Long subtaskid);

	public Boolean updateSubTask(SubTask updatesubtask);
	
	public Page<SubTaskUserDTO> getSubTaskUser(RequestResponseDTO requestresponsedto);
	
	public Page<SubTask> getAllSubTasks(RequestResponseDTO requestresponsedto);

}
