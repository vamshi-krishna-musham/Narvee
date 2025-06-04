package com.narvee.service.service;

import java.util.List;

import com.narvee.dto.TmsTaskCountData;

public interface TmsDashboardService {
  
	public List<TmsTaskCountData> getAllTaskCount();
	public List<TmsTaskCountData> getTaskCountByProjectId(Long pid);
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserId(Long pid , Long userId);	
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime);
}
