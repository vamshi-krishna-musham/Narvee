package com.narvee.service.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.narvee.dto.CompletedStatusCountResponse;
import com.narvee.dto.DashBoardRequestDto;
import com.narvee.dto.ProjectDropDownDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.dto.TaskResponse;
import com.narvee.dto.TasksResponseDTO;
import com.narvee.dto.TmsTaskCountData;

public interface TmsDashboardService {
  
	public List<TmsTaskCountData> getAllTaskCount();
	public List<TmsTaskCountData> getTaskCountByAdminId(Long pid);
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserId(Long pid , Long userId);	
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime);
	
	public List<TmsTaskCountData> getPriorityCountByAdminId(Long pid);
	public List<TmsTaskCountData> getPriorityCountByProjectIdAndUserId(Long pid , Long userId);	
	public List<TmsTaskCountData> getPriorityCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime);
	
	
	public List<TmsTaskCountData> getUserTracker(Long adminId,Long projectId,String timeIntervel);
	
	public List<TmsTaskCountData> getTaskStatusCountByMonth(String status);
	public List<ProjectDropDownDTO> projectDropDownWithOutAdmin(Long userId);
	
	 public List<CompletedStatusCountResponse> getCompleteStatusCount(DashBoardRequestDto request);
	 
	 public List<Map<String, String>>  getDropDownForDailyCount(String intervel);
	 
	 public TaskResponse  getAllTaskByPidAndTimeIntervel(RequestDTO  requestDTO);
}
