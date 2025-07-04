package com.narvee.service.serviceimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narvee.controller.ProjectController;
import com.narvee.dto.CompletedStatusCountResponse;
import com.narvee.dto.DashBoardRequestDto;
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
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByAdminId");
	  String userRole = 	dashboardRepository.roleName(adminId);
		if(userRole .equalsIgnoreCase("Admin")) {
			logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByAdminId");
		return	dashboardRepository.getTaskCountByadminId(adminId);
		}else 
			logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByUserId");
		return  dashboardRepository.getTaskCountByUserId(adminId);
	}

	
	@Override
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserId(Long pid, Long userId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndUserId");
		  String userRole = 	dashboardRepository.roleName(userId);
		  if(userRole .equalsIgnoreCase("Admin")) {
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByProjectIdAndAdminId");
		  return dashboardRepository.getTaskCountByPidAndAdminId(pid, userId);		  
	     } else 
	    	 logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByProjectIdAndUserId");
		 return dashboardRepository.getTaskCountByPidAndUserId(pid, userId);
	  }
	
	@Override
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndUserIdAndTime");
		 String userRole = 	dashboardRepository.roleName(userId);
		  if(userRole .equalsIgnoreCase("Admin")) {  
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByProjectIdAndAdminIdAndTime");
		  return dashboardRepository.getTaskCountByPidAndAdminIdAndTime(pid, userId,IntervelTime);
	      }else
	    	  logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByProjectIdAndUserIdAndTime");
		  return dashboardRepository.getTaskCountByPidAndUserIdAndTime(pid, userId,IntervelTime);
	     }
	
	@Override
	public List<TmsTaskCountData> getPriorityCountByAdminId(Long adminId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByAdminId");
	  String userRole = 	dashboardRepository.roleName(adminId);
		if(userRole .equalsIgnoreCase("Admin")) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getPriorityCountByAdminId");
		return	dashboardRepository.getPriorityByAdminId(adminId);
		}else 
			logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getPriorityCountByUserId");
		return  dashboardRepository.getPriorityByUserId(adminId);
	}

	
	@Override
	public List<TmsTaskCountData> getPriorityCountByProjectIdAndUserId(Long pid, Long userId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndUserId");
		  String userRole = 	dashboardRepository.roleName(userId);
		  if(userRole .equalsIgnoreCase("Admin")) {
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getPriorityCountByProjectIdAndAdminId");
		  return dashboardRepository.getPriorityByAdminIdAndPid(userId, pid);
	     } else
	    	 logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getPriorityCountByProjectIdAndUserId");
		 return dashboardRepository.getPriorityByUserIdAndpid(userId, pid);
	  }
	
	@Override
	public List<TmsTaskCountData> getPriorityCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndUserIdAndTime");
		 String userRole = 	dashboardRepository.roleName(userId);
		  if(userRole .equalsIgnoreCase("Admin")) {
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndAdminIdAndTimeIntervel");
		  return dashboardRepository.getPriorityByAdminIdAndPidAndTime(userId,pid,IntervelTime);
	      }else
	    	  logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndUserIdAndTimeIntervel");
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

	@Override
	 public List<CompletedStatusCountResponse> getCompleteStatusCount(DashBoardRequestDto request ) {
	        List<Object[]> results = null;
         
	        String userRole = dashboardRepository.roleName(request.getUserId());
	        boolean isAdmin = "Admin".equalsIgnoreCase(userRole);
	        
	        switch (request.getTimeIntervel().toLowerCase()) {
	            case "daily":
	                results = isAdmin
	                        ? dashboardRepository.getDailyTaskStatsAdmin(request.getFromDate(), request.getToDate(),request.getUserId(),request.getPid())
	                        : dashboardRepository.getDailyTaskStatsUserId(request.getFromDate(), request.getToDate(), request.getUserId(),request.getPid());
	                break;

	            case "weekly":
	              //  results = isAdmin
	                     //   ? dashboardRepository.getWeeklyTaskStatsAdmin(request.getFromDate(), request.getToDate())
	                     //   : dashboardRepository.getWeeklyTaskStatsUser(request.getFromDate(), request.getToDate(), request.getUserId());
	                break;

	            case "monthly":
	            	 if (request.getYear() == null) {
	                     throw new IllegalArgumentException("Year is required for monthly interval");
	                 }
                    results = isAdmin
                    
	                        ? dashboardRepository.getMonthlyTaskStatsAdmin(request.getYear(),request.getUserId(),request.getPid())
	                        : dashboardRepository.getMonthlyTaskStats(request.getYear(), request.getUserId(),request.getPid());
	                break;

	            case "yearly":
	              
//	                results = isAdmin
//	                        ? dashboardRepository.getYearlyTaskStatsAdmin(request.getUserId(),request.getPid())
//	                        : dashboardRepository.getYearlyTaskStatsUser(request.getUserId(),request.getPid());
//	                break;

	            default:
	                throw new IllegalArgumentException("Invalid interval: " + request.getTimeIntervel());
	        }

	        return results.stream()
	            .map(row -> new CompletedStatusCountResponse(
	                (String) row[0],
	                (String) row[1],
	                (String) row[2],
	                ((Number) row[3]).longValue()
	            ))
	            .toList();
	    }

	
}



