package com.narvee.service.serviceimpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public List<CompletedStatusCountResponse> getCompleteStatusCount(DashBoardRequestDto request) {	
		logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount");
	    String role     = dashboardRepository.roleName(request.getUserId());
	    boolean isAdmin = "Admin".equalsIgnoreCase(role);

	    List<CompletedStatusCountResponse> result;

	    String interval = request.getTimeIntervel().toLowerCase();
	    switch (interval) {
	        case "daily":
	            if (isAdmin) {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Daily ,, !! condition : For Admin");
	                result = dashboardRepository.getDailyTaskStatsAdmin( request.getFromDate(), request.getToDate(), request.getUserId(), request.getPid() );
	            } else {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Daily ,, !! condition : For User");
	                result = dashboardRepository.getDailyTaskStatussUserId(request.getFromDate(), request.getToDate(), request.getUserId(), request.getPid());
	            }
	            break;
	         
	        case "weekly":
	            if (isAdmin) {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Daily ,, !! condition : For Admin");
	                result = dashboardRepository.getWeeklyTaskStatsAdmin( request.getFromDate(), request.getToDate(), request.getUserId(), request.getPid() );
	            } else {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Daily ,, !! condition : For User");
	                result = dashboardRepository.getWeeklyTaskStatsUser(request.getFromDate(), request.getToDate(), request.getUserId(), request.getPid());
	            }
	            break;

	        case "monthly":
	            if (request.getYear() == null) {
	                throw new IllegalArgumentException("Year is required for monthly interval");
	            }
	            if (isAdmin) {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Monthly ,, !! condition : For Admin");
	                result = dashboardRepository.getMonthlyTaskStatsAdmin(request.getYear(), request.getUserId(), request.getPid());
	            } else {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Monthly ,, !! condition : For User");
	                result = dashboardRepository.getMonthlyTaskStatusUserId(request.getYear(), request.getUserId(), request.getPid());
	            }
	            break;

	        case "yearly":
	            if (isAdmin) {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Yearly ,, !! condition : For Admin");
	            	result = dashboardRepository.getYearlyTaskStatusAdmin(request.getUserId(), request.getPid());
	            } else {
	              	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Yearly ,, !! condition : For User");
	                result = dashboardRepository.getYearlyTaskStatusByUser(request.getUserId(), request.getPid());
	            }
	            break;

	        default:
	            throw new IllegalArgumentException("Invalid interval: " + request.getTimeIntervel());
	    }

	    return result;
	}

	@Override
    public List<Map<String, String>> getDropDownForDailyCount(String type) {
        switch (type.toLowerCase()) {
            case "daily":
                return getDailyIntervals();
            case "weekly":
                return getTwoMonthBlockIntervals();
            case "monthly":
                return getMonthlyIntervals();
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    private List<Map<String, String>> getDailyIntervals () {
		List<Map<String, String>> weekLabels = new ArrayList<>();
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(today.getYear(), today.getMonth());

        LocalDate start = currentMonth.atDay(1);
        while (start.getDayOfWeek() != DayOfWeek.SUNDAY) {
            start = start.minusDays(1);
        }
        LocalDate end = currentMonth.atEndOfMonth();
        while (end.getDayOfWeek() != DayOfWeek.SATURDAY) {
            end = end.plusDays(1);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate weekStart = start;
        while (!weekStart.isAfter(end)) {
            LocalDate weekEnd = weekStart.plusDays(6);

            String label = weekStart.format(formatter) + " to " + weekEnd.format(formatter);

            Map<String, String> weekMap = new HashMap<>();
            weekMap.put("label", label); 
            weekLabels.add(weekMap);

            weekStart = weekStart.plusWeeks(1);
        }

        return weekLabels;
    }
    private List<Map<String, String>> getTwoMonthBlockIntervals() {
        List<Map<String, String>> blocks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Start from first Monday before or on Jan 1st
        LocalDate startDate = LocalDate.of(2025, 1, 1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Stop at last Sunday of December
        LocalDate yearEnd = LocalDate.of(2025, 12, 31)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        while (!startDate.isAfter(yearEnd)) {
            // Tentative 2-month end date
            LocalDate tentativeEnd = startDate.plusMonths(2).minusDays(1);

            // Adjust to last Sunday in the 2-month period
            LocalDate endDate = tentativeEnd.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            // Clamp to year end
            if (endDate.isAfter(yearEnd)) {
                endDate = yearEnd;
            }

            // Build block label
            String label = startDate.format(formatter) + " to " + endDate.format(formatter);
            Map<String, String> blockMap = new HashMap<>();
            blockMap.put("label", label);
            blocks.add(blockMap);

            // Next block starts on Monday after endDate
            startDate = endDate.plusDays(1)
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        }

        return blocks;
    }



    private List<Map<String, String>> getMonthlyIntervals() {
            List<Map<String, String>> yearBlockList = new ArrayList<>();
            LocalDate today = LocalDate.now();
            int currentYear = today.getYear();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            for (int year = currentYear - 2; year <= currentYear + 2; year++) {
                LocalDate yearStart = LocalDate.of(year, 1, 1);
                LocalDate yearEnd = LocalDate.of(year, 12, 31);

                String label = yearStart.format(formatter) + " to " + yearEnd.format(formatter);
                Map<String, String> yearMap = new HashMap<>();
                yearMap.put("label", label);
                yearBlockList.add(yearMap);
            }

            return yearBlockList;
        }
	
}



