package com.narvee.service.service;

import org.springframework.data.domain.Page;

import com.narvee.dto.RequestDTO;
import com.narvee.dto.SubTaskResponse;
import com.narvee.dto.SubTaskUserDTO;
import com.narvee.entity.TmsSubTask;

public interface SubTaskService {

	public TmsSubTask createSubTask(TmsSubTask subtask);

	public TmsSubTask findBySubTaskId(Long subtaskid);
	
	public SubTaskResponse findBySubTaskTicketId(String ticketId);

	public void deleteSubTask(Long subtaskid);

	public Boolean updateSubTask(TmsSubTask updatesubtask);
	
	public Page<SubTaskUserDTO> getSubTaskUser(RequestDTO requestresponsedto);
	
	public Page<TmsSubTask> getAllSubTasks(RequestDTO requestresponsedto);
	
	public boolean updateSubTaskStatus(Long subTaskId , String staus,Long updatedby);

}
