package com.narvee.service.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.narvee.dto.DateSearchDTO;
import com.narvee.dto.RequestResponseDTO;
import com.narvee.dto.TaskAssignDTO;
import com.narvee.dto.TaskReportsDTO;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.dto.TicketTrackerSortDTO;
import com.narvee.dto.TrackerByUserDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.entity.Task;

public interface TaskService {
	public Task createTask(Task ticket, String token);

	public boolean updateTask(UpdateTask updateTask);

	public Task findBytaskId(Long taskid);

	public List<Task> getAllTasks();

	public List<TaskAssignDTO> taskAssignInfo(Long taskid);

	public List<TaskTrackerDTO> trackerByUser(Long userid);

	public List<TaskTrackerDTO> allTasksRecords();

	public void deleteTask(Long id);

	public List<TaskTrackerDTO> taskReports(DateSearchDTO dateSearch);
	
	public Page<TaskTrackerDTO> trackerByUserWithSortingAndPagination(TrackerByUserDTO trackerbyuserdto);
	
	public Page<TaskAssignDTO> taskAssignInfoWithSortingAndPagination(TicketTrackerSortDTO trackerbyuserdto);
	
	public Page<TaskTrackerDTO> taskReportsByDepartmentWithSortingAndPagination(TaskReportsDTO taskreportsdto);
	
	public Page<TaskTrackerDTO> allTasksRecordsWithSortingAndPagination(RequestResponseDTO requestresponsedto);
	
	public Page<Task> findAllTasks(RequestResponseDTO requestresponsedto);
	

}
