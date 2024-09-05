package com.narvee.service.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.narvee.dto.DateSearchDTO;
import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.dto.TaskAssignDTO;
import com.narvee.dto.TaskResponse;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.dto.TasksResponseDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.entity.TmsTask;

public interface TaskService {
	public TmsTask createTask(TmsTask ticket, String token);
	
	public TmsTask update(TmsTask ticket);

	public boolean updateTask(UpdateTask updateTask);

	public TmsTask findBytaskId(Long taskid);
	
	public TmsTask findByTicketId(String taskid);

	public List<TmsTask> getAllTasks();

	public List<TaskAssignDTO> taskAssignInfo(Long taskid);

	public List<TaskTrackerDTO> trackerByUser(Long userid);

	public List<TaskTrackerDTO> allTasksRecords();

	public void deleteTask(Long id);

	public List<TaskTrackerDTO> taskReports(DateSearchDTO dateSearch);
	
	public Page<TaskTrackerDTO> getTaskByProjectid(RequestDTO requestresponsedto);
	
	public List<GetUsersDTO> getUsersByDepartment(String department);
	
	public List<GetUsersDTO> getProjectUsers(String projectID);

	public boolean updateTaskStatus(Long taskid, String status,String updatedby);
	
	public TaskResponse findTaskByProjectid(RequestDTO requestresponsedto);
	
	public List<TasksResponseDTO> ticketTracker(Long taskid);
	
}
