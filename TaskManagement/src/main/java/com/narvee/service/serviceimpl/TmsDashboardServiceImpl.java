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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.controller.ProjectController;
import com.narvee.dto.CompletedStatusCountResponse;
import com.narvee.dto.DashBoardRequestDto;
import com.narvee.dto.FileUploadDto;
import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.ProjectDropDownDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.dto.TaskResponse;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.dto.TasksResponseDTO;
import com.narvee.dto.TmsTaskCountData;
import com.narvee.entity.TmsFileUpload;
import com.narvee.repository.TaskRepository;
import com.narvee.repository.TmsDashboardRepository;
import com.narvee.service.service.TmsDashboardService;



@Service
public class TmsDashboardServiceImpl implements TmsDashboardService {
	
	private static final Logger logger = LoggerFactory.getLogger(TmsDashboardServiceImpl.class);
	
	@Autowired
	private TmsDashboardRepository dashboardRepository;
	
	@Autowired
	private TaskRepository taskRepo;

	@Override
	public List<TmsTaskCountData> getAllTaskCount() {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getAllTaskCount");
		return dashboardRepository.getAllCount();
	 
	}

	@Override
	public List<TmsTaskCountData> getTaskCountByAdminId(Long userid) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByAdminId");
	  String userRole = 	dashboardRepository.roleName(userid);
	  
	  if ( "Admin".equalsIgnoreCase(userRole) ||"Project Manager".equalsIgnoreCase(userRole) || "Super Admin".equalsIgnoreCase(userRole)) {
			 Long adminid = ("Super Admin".equalsIgnoreCase(userRole))
		                ? userid
		                : dashboardRepository.AdminId(userid);		 
			logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByAdminId");
		return	dashboardRepository.getTaskCountByadminId(adminid);
		}else 
			logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByUserId");
		return  dashboardRepository.getTaskCountByUserId(userid);
	}

	
	@Override
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserId(Long pid, Long userId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndUserId");
		  String userRole = 	dashboardRepository.roleName(userId);
		  if ( "Admin".equalsIgnoreCase(userRole) ||"Project Manager".equalsIgnoreCase(userRole) || "Super Admin".equalsIgnoreCase(userRole)) {
				 Long adminid = ("Super Admin".equalsIgnoreCase(userRole))
			                ? userId
			                : dashboardRepository.AdminId(userId);		
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByProjectIdAndAdminId");
		  return dashboardRepository.getTaskCountByPidAndAdminId(pid, adminid);		  
	     } else 
	    	 logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByProjectIdAndUserId");
		 return dashboardRepository.getTaskCountByPidAndUserId(pid, userId);
	  }
	
	@Override
	public List<TmsTaskCountData> getTaskCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getTaskCountByProjectIdAndUserIdAndTime");
		 String userRole = 	dashboardRepository.roleName(userId);
		  if ( "Admin".equalsIgnoreCase(userRole) ||"Project Manager".equalsIgnoreCase(userRole) || "Super Admin".equalsIgnoreCase(userRole)) {
				 Long adminid = ("Super Admin".equalsIgnoreCase(userRole))
			                ? userId
			                : dashboardRepository.AdminId(userId);	 
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByProjectIdAndAdminIdAndTime");
		  return dashboardRepository.getTaskCountByPidAndAdminIdAndTime(pid, adminid,IntervelTime);
	      }else
	    	  logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getTaskCountByProjectIdAndUserIdAndTime");
		  return dashboardRepository.getTaskCountByPidAndUserIdAndTime(pid, userId,IntervelTime);
	     }
	
	@Override
	public List<TmsTaskCountData> getPriorityCountByAdminId(Long userId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByAdminId");
	  String userRole = 	dashboardRepository.roleName(userId);
	  if ( "Admin".equalsIgnoreCase(userRole) ||"Project Manager".equalsIgnoreCase(userRole) || "Super Admin".equalsIgnoreCase(userRole)) {
			 Long adminid = ("Super Admin".equalsIgnoreCase(userRole))
		                ? userId
		                : dashboardRepository.AdminId(userId);	 
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getPriorityCountByAdminId");
		return	dashboardRepository.getPriorityByAdminId(adminid);
		}else 
			logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getPriorityCountByUserId");
		return  dashboardRepository.getPriorityByUserId(userId);
	}

	
	@Override
	public List<TmsTaskCountData> getPriorityCountByProjectIdAndUserId(Long pid, Long userId) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndUserId");
		  String userRole = 	dashboardRepository.roleName(userId);
		  if ( "Admin".equalsIgnoreCase(userRole) ||"Project Manager".equalsIgnoreCase(userRole) || "Super Admin".equalsIgnoreCase(userRole)) {
				 Long adminid = ("Super Admin".equalsIgnoreCase(userRole))
			                ? userId
			                : dashboardRepository.AdminId(userId);	
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getPriorityCountByProjectIdAndAdminId");
		  return dashboardRepository.getPriorityByAdminIdAndPid(adminid, pid);
	     } else
	    	 logger.info("!!! inside class: TmsDashboardServiceImpl , !! Condition: getPriorityCountByProjectIdAndUserId");
		 return dashboardRepository.getPriorityByUserIdAndpid(userId, pid);
	  }
	
	@Override
	public List<TmsTaskCountData> getPriorityCountByProjectIdAndUserIdAndTime(Long pid, Long userId,String IntervelTime) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndUserIdAndTime");
		 String userRole = 	dashboardRepository.roleName(userId);
		 if ( "Admin".equalsIgnoreCase(userRole) ||"Project Manager".equalsIgnoreCase(userRole) || "Super Admin".equalsIgnoreCase(userRole)) {
			 Long adminid = ("Super Admin".equalsIgnoreCase(userRole))
		                ? userId
		                : dashboardRepository.AdminId(userId);	
			  logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getPriorityCountByProjectIdAndAdminIdAndTimeIntervel");
		  return dashboardRepository.getPriorityByAdminIdAndPidAndTime(adminid,pid,IntervelTime);
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
	public List<TmsTaskCountData> getUserTracker(Long userid, Long projectId, String timeIntervel) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getUserTracker");	
		 String userRole = 	dashboardRepository.roleName(userid);
		  if ( "Admin".equalsIgnoreCase(userRole) ||"Project Manager".equalsIgnoreCase(userRole) || "Super Admin".equalsIgnoreCase(userRole)) {
				 Long adminid = ("Super Admin".equalsIgnoreCase(userRole))
			                ? userid
			                : dashboardRepository.AdminId(userid);	
				 System.err.println("adminid "+adminid);
			return	dashboardRepository.getProjectUsersTaskStats(adminid,projectId,timeIntervel);
		  }else {
			return dashboardRepository.getTeamMemberTaskStats(userid,projectId,timeIntervel);
		
		}
		
	}
	
	
	
	
//	@Override
//	public List<TmsTaskCountData> getUserTracker(Long adminId, Long projectId, String timeIntervel) {
//		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getUserTracker");	
//		  String role     = dashboardRepository.roleName(adminId);
//		    boolean isAdmin = "Admin".equalsIgnoreCase(role);
//		    boolean isAddedby = "Project Manager".equalsIgnoreCase(role);
//		    
//		if(isAdmin) {
//			return	dashboardRepository.getUserTrackerByAdmin(adminId,projectId,timeIntervel)	;
//		}else if (isAddedby) {
//			return null;
//			//return dashboardRepository.getUserTrackerByAdminAndPid(adminId, projectId);
//		}else {
//			return null;
//		//	return dashboardRepository.getUserTrackerByAdminAndPidAndTimeInterval(adminId, projectId, timeIntervel);
//		}
//		
//	}
	
	
	@Override
	public List<ProjectDropDownDTO> projectDropDownWithOutAdmin(Long userId) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: projectDropDownWithOutAdmin");
		 String role     = dashboardRepository.roleName(userId);	 
		 if ( "Admin".equalsIgnoreCase(role) ||"Project Manager".equalsIgnoreCase(role) || "Super Admin".equalsIgnoreCase(role)) {
			 Long adminId = ("Super Admin".equalsIgnoreCase(role))
		                ? userId
		                : dashboardRepository.AdminId(userId);
			 
	            return dashboardRepository.projectDropDownWithAdmin(adminId);
	        } else {
           return dashboardRepository.projectDropDownWithOutAdmin(userId);
	      }
	}
	
	


	@Override
	public List<CompletedStatusCountResponse> getCompleteStatusCount(DashBoardRequestDto request) {	
		logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount");
	    String role     = dashboardRepository.roleName(request.getUserId());
	    
	    boolean isAdmin = ( "Admin".equalsIgnoreCase(role) ||"Project Manager".equalsIgnoreCase(role) || "Super Admin".equalsIgnoreCase(role));
	    Long adminId = ("Super Admin".equalsIgnoreCase(role))
                ? request.getUserId()
                : dashboardRepository.AdminId(request.getUserId());

	    List<CompletedStatusCountResponse> result;

	    String interval = request.getTimeIntervel().toLowerCase();
	    switch (interval) {
	        case "daily":
	            if (isAdmin) {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Daily ,, !! condition : For Admin");
	                result = dashboardRepository.getDailyTaskStatsAdmin( request.getFromDate(), request.getToDate(), adminId, request.getPid() );
	            } else {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Daily ,, !! condition : For User");
	                result = dashboardRepository.getDailyTaskStatussUserId(request.getFromDate(), request.getToDate(), request.getUserId(), request.getPid());
	            }
	            break;
	         
	        case "weekly":
	            if (isAdmin) {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : weekly ,, !! condition : For Admin");
	                result = dashboardRepository.getWeeklyTaskStatsAdmin( request.getFromDate(), request.getToDate(),adminId, request.getPid() );
	            } else {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : weekly ,, !! condition : For User");
	                result = dashboardRepository.getWeeklyTaskStatsUser(request.getFromDate(), request.getToDate(), request.getUserId(), request.getPid());
	            }
	            break;

	        case "monthly":
	            if (request.getYear() == null) {
	                throw new IllegalArgumentException("Year is required for monthly interval");
	            }
	            if (isAdmin) {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Monthly ,, !! condition : For Admin");
	                result = dashboardRepository.getMonthlyTaskStatsAdmin(request.getYear(), adminId, request.getPid());
	            } else {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Monthly ,, !! condition : For User");
	                result = dashboardRepository.getMonthlyTaskStatusUserId(request.getYear(), request.getUserId(), request.getPid());
	            }
	            break;

	        case "yearly":
	            if (isAdmin) {
	            	logger.info("!!! inside class: ProjectServiceImpl , !! method: getCompleteStatusCount , !! Case : Yearly ,, !! condition : For Admin");
	            	result = dashboardRepository.getYearlyTaskStatusAdmin(adminId, request.getPid());
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

        // Get current year dynamically
        int currentYear = LocalDate.now().getYear();

        // Start from the first Sunday on or before Jan 1st of current year
        LocalDate startDate = LocalDate.of(currentYear, 1, 1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        // End at the last Saturday of current year
        LocalDate yearEnd = LocalDate.of(currentYear, 12, 31)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        while (!startDate.isAfter(yearEnd)) {
            // Each block covers 8 weeks (Sunday to Saturday)
            LocalDate endDate = startDate.plusWeeks(8).minusDays(1);

            // Clamp the end date to yearEnd if it overshoots
            if (endDate.isAfter(yearEnd)) {
                endDate = yearEnd;
            }

            // Build the block label
            String label = startDate.format(formatter) + " to " + endDate.format(formatter);
            Map<String, String> blockMap = new HashMap<>();
            blockMap.put("label", label);
            blocks.add(blockMap);

            // Move startDate to next block (next Sunday)
            startDate = endDate.plusDays(1)
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
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
    
    

	@Override
	public TaskResponse getAllTaskByPidAndTimeIntervel(RequestDTO requestDTO) {
		logger.info("!!! inside class: TmsDashboardServiceImpl , !! method: getAllTaskByPidAndTimeIntervel");
	
		    String sortfield = requestDTO.getSortField();
		    String sortorder = requestDTO.getSortOrder();
		    String projectid = requestDTO.getProjectid();
		    String keyword = requestDTO.getKeyword();
		    int pageNo = requestDTO.getPageNumber();
		    int pageSize = requestDTO.getPageSize();
		    String Time  = requestDTO.getTimeIntervel();

		    // Map frontend sort fields to DB columns
		    if (sortfield.equalsIgnoreCase("ticketid"))
		        sortfield = "ticketid";
		    else if (sortfield.equalsIgnoreCase("TaskName"))
		        sortfield = "taskname";
		    else if (sortfield.equalsIgnoreCase("TaskDescription"))
		        sortfield = "description";
		    else if (sortfield.equalsIgnoreCase("DueDate"))
		        sortfield = "target_date";
		    else if (sortfield.equalsIgnoreCase("StartDate"))
		        sortfield = "start_date";
		    else if (sortfield.equalsIgnoreCase("status"))
		        sortfield = "status";
		    else if (sortfield.equalsIgnoreCase("Priority"))
		        sortfield = "priority";
		    

		    Sort.Direction sortDirection = Sort.Direction.ASC;
		    if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
		        sortDirection = Sort.Direction.DESC;
		    }

		    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortDirection, sortfield));

		


		    if (keyword.equalsIgnoreCase("empty")) {
				logger.info("!!! inside class: TaskServiceImpl , !! method: findTaskByProjectid -- tms with empty ");
				Page<TaskTrackerDTO> res = dashboardRepository.findTaskListByTmsProjectid(pageable,projectid,Time);

				List<TasksResponseDTO> tasksList = new ArrayList<>();

				for (TaskTrackerDTO order : res) {
					logger.info("!!! inside class:  for loop");
					TasksResponseDTO result = new TasksResponseDTO(order);
              System.err.println(result.toString());
					List<GetUsersDTO> assignUsers = taskRepo.getTmsAssignUsers(order.getTaskid());

					List<GetUsersDTO> filteredAssignUsers = assignUsers.stream().filter(user -> user.getFullname() != null)
							.collect(Collectors.toList());
					result.setAssignUsers(filteredAssignUsers);
					tasksList.add(result);

				}
				Page<TasksResponseDTO> tasksPage = new PageImpl<>(tasksList, pageable, res.getTotalElements());
				Long pid = taskRepo.findPid(projectid);
				TaskResponse taskResp = new TaskResponse();
				taskResp.setTasks(tasksPage);
				taskResp.setPid(pid);
				System.err.println(taskResp.toString());
				return taskResp;
				
				
			} else {
				logger.info("!!! inside class: TaskServiceImpl , !! method: findTaskByProjectIdWithSearching , Filter-tms");
				Page<TaskTrackerDTO> res = dashboardRepository.findTaskListByTmsProjectIdWithSearching(pageable,projectid,keyword,Time);
				List<TasksResponseDTO> tasksList = new ArrayList<>();
				for (TaskTrackerDTO order : res) {
					TasksResponseDTO result = new TasksResponseDTO(order);
					List<GetUsersDTO> assignUsers = taskRepo.getTmsAssignUsers(order.getTaskid());
					List<GetUsersDTO> filteredAssignUsers = assignUsers.stream().filter(user -> user.getFullname() != null)
							.collect(Collectors.toList());
					result.setAssignUsers(filteredAssignUsers);
					tasksList.add(result);

				}
				Page<TasksResponseDTO> tasksPage = new PageImpl<>(tasksList, pageable, res.getTotalElements());

				Long pid = taskRepo.findPid(projectid);
				TaskResponse taskResp = new TaskResponse();
				taskResp.setTasks(tasksPage);
				taskResp.setPid(pid);
				return taskResp;
			}
	}
}



