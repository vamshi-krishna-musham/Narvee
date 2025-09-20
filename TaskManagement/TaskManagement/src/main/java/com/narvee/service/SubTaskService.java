package com.narvee.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.dto.RequestDTO;
import com.narvee.dto.SubTaskResponse;
import com.narvee.dto.SubTaskUserDTO;
import com.narvee.dto.TasksResponseDTO;
import com.narvee.dto.UpdateTask;
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
	
	public List<TasksResponseDTO> ticketTrackerBySubTaskId(Long subtaskid);

	public boolean updateSubTaskTrack(UpdateTask updateTask);
	
	//-----------------------all replicated methods for tms by keerthi -------------------
	
	public TmsSubTask createTmsSubTask(TmsSubTask subtask, List<MultipartFile> files);
	
	public TmsSubTask updateTmsSubTask(TmsSubTask updatesubtask,List<MultipartFile> files);

	public Page<TmsSubTask> getAllSubTasksTms(RequestDTO requestresponsedto);
	
	public void deleteSubTaskTms(Long subtaskid);
	
	public TmsSubTask findBySubTaskIdTms(Long subtaskid);
	
	public boolean updateSubTaskStatusTms(Long subTaskId , String staus,Long updatedby);
	
	public SubTaskResponse findTmsSubTaskByTicketId(RequestDTO requestresponsedto);
	
	public boolean updateTmsSubTaskTrack(UpdateTask updateTask);
	
	public List<TasksResponseDTO> ticketTrackerByTmsSubTaskId(Long subtaskid);
}
