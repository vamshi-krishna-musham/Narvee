package com.narvee.service.serviceimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narvee.controller.ProjectController;
import com.narvee.dto.TmsTaskCountData;
import com.narvee.repository.TmsDashboardRepository;
import com.narvee.service.service.TmsDashboardService;

import io.swagger.v3.oas.annotations.servers.Server;

@Service
public class TmsDashboardServiceImpl implements TmsDashboardService {
	
	private static final Logger logger = LoggerFactory.getLogger(TmsDashboardServiceImpl.class);
	
	@Autowired
	private TmsDashboardRepository dashboardRepository;

	@Override
	public List<TmsTaskCountData> getAllTaskCount() {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getAllTaskCount");
		return dashboardRepository.getAllCount();
	 
	}

	@Override
	public List<TmsTaskCountData> getTaskCountByProjectId(Long pid) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectId");
		return	dashboardRepository.getTaskCountByProjectId(pid);
	}

	
	@Override
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserId(Long pid, Long userId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndUserId");
		return dashboardRepository.getTaskCountByPidAndUserId(pid, userId);
	}

	@Override
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndUserId");
		return dashboardRepository.getTaskCountByPidAndUserIdAndTime(pid, userId,IntervelTime);
	}

	@Override
	public List<TmsTaskCountData> getTaskStatusCountByMonth(String status) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskStatusCountByMonth");	
		return dashboardRepository.getTaskCountByMonth(status);
	}
	
	

}
