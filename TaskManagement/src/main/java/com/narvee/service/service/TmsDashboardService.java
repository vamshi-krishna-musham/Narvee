package com.narvee.service.service;

import java.util.List;

import com.narvee.dto.ProjectDropDownDTO;
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
}
