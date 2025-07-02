package com.narvee.service.serviceimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narvee.controller.ProjectController;
import com.narvee.dto.ProjectDropDownDTO;
import com.narvee.dto.TmsTaskCountData;

import com.narvee.repository.TmsDashboardRepository;
import com.narvee.service.service.TmsDashboardService;



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
	public List<TmsTaskCountData> getTaskCountByAdminId(Long adminId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectId");
	  String userRole = 	dashboardRepository.roleName(adminId);
		if(userRole .equalsIgnoreCase("Admin")) {
		return	dashboardRepository.getTaskCountByadminId(adminId);
		}else 
		return  dashboardRepository.getTaskCountByUserId(adminId);
	}

	
	@Override
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserId(Long pid, Long userId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndUserId");
		  String userRole = 	dashboardRepository.roleName(userId);
		  if(userRole .equalsIgnoreCase("Admin")) {
		  return dashboardRepository.getTaskCountByPidAndAdminId(pid, userId);
	     } else 
		 return dashboardRepository.getTaskCountByPidAndUserId(pid, userId);
	  }
	
	@Override
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndUser");
		 String userRole = 	dashboardRepository.roleName(userId);
		  if(userRole .equalsIgnoreCase("Admin")) {
			  System.err.println("userRole "+userRole  +"userId  " +userId );
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndAdminId");
		  return dashboardRepository.getTaskCountByPidAndAdminIdAndTime(pid, userId,IntervelTime);
	      }else
	    	  logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndUserId");
		  return dashboardRepository.getTaskCountByPidAndUserIdAndTime(pid, userId,IntervelTime);
	     }
	
	@Override
	public List<TmsTaskCountData> getPriorityCountByAdminId(Long adminId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByAdminId");
	  String userRole = 	dashboardRepository.roleName(adminId);
		if(userRole .equalsIgnoreCase("Admin")) {
		return	dashboardRepository.getPriorityByAdminId(adminId);
		}else 
		return  dashboardRepository.getPriorityByUserId(adminId);
	}

	
	@Override
	public List<TmsTaskCountData> getPriorityCountByProjectIdAndUserId(Long pid, Long userId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndUserId");
		  String userRole = 	dashboardRepository.roleName(userId);
		  if(userRole .equalsIgnoreCase("Admin")) {
		  return dashboardRepository.getPriorityByAdminIdAndPid(userId, pid);
	     } else 
		 return dashboardRepository.getPriorityByUserIdAndpid(userId, pid);
	  }
	
	@Override
	public List<TmsTaskCountData> getPriorityCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndUserIdAndTime");
		 String userRole = 	dashboardRepository.roleName(userId);
		  if(userRole .equalsIgnoreCase("Admin")) {
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndAdminId");
		  return dashboardRepository.getPriorityByAdminIdAndPidAndTime(userId,pid,IntervelTime);
	      }else
	    	  logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndUserId");
		  return dashboardRepository.getPriorityByuserIdAndPidAndTime(userId, pid,IntervelTime);
	     }

	@Override
	public List<TmsTaskCountData> getTaskStatusCountByMonth(String status) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskStatusCountByMonth");	
		return dashboardRepository.getTaskCountByMonth(status);
	}
	@Override
	public List<TmsTaskCountData> getUserTracker(Long adminId, Long projectId, String timeIntervel) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getUserTracker");	
		
		if(projectId == null && timeIntervel == null) {
			return	dashboardRepository.getUserTrackerByAdmin(adminId)	;
		}else if (timeIntervel == null) {
			return dashboardRepository.getUserTrackerByAdminAndPid(adminId, projectId);
		}else {
			return dashboardRepository.getUserTrackerByAdminAndPidAndTimeInterval(adminId, projectId, timeIntervel);
		}
		
	}
	@Override
	public List<ProjectDropDownDTO> projectDropDownWithOutAdmin(Long userId) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: projectDropDownWithOutAdmin");
		List<Long> addedBtId = dashboardRepository.getAddedBy();
		for (Long userIds : addedBtId) {
		 if (userId == userIds) {
	            return dashboardRepository.projectDropDownWithAdmin(userId);
	        } else {
	            return dashboardRepository.projectDropDownWithOutAdmin(userId);
	        }
	    }
		return null;
	}
	
	

}
