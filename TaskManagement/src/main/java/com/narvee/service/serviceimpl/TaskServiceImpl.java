package com.narvee.service.serviceimpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.dto.DateSearchDTO;
import com.narvee.dto.GetAssignUsers;
import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.RequestResponseDTO;
import com.narvee.dto.TaskAssignDTO;
import com.narvee.dto.TaskReportsDTO;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.dto.TicketTrackerSortDTO;
import com.narvee.dto.TrackerByUserDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.dto.UserDTO;
import com.narvee.entity.AssignedUsers;
import com.narvee.entity.Task;
import com.narvee.entity.TicketTracker;
import com.narvee.feignclient.UserClient;
import com.narvee.repository.TaskRepository;
import com.narvee.service.service.TaskService;
import com.narvee.util.DateFormat;

@Service
public class TaskServiceImpl implements TaskService {
	private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
	@Autowired
	private TaskRepository taskRepo;
	private static final int DIGIT_PADDING = 5;

	@Autowired
	private EmailServiceIml emailService;

	@Autowired
	private UserClient userClient;

	@Override
	public Task createTask(Task task, String token) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: createTask");
		Long maxnumber = taskRepo.maxNumber();
		if (maxnumber == null) {
			maxnumber = 0L;
		}
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyMMdd");
		String formattedDateTime = now.format(inputFormatter);
		LocalDateTime dateTime = LocalDateTime.parse(formattedDateTime, inputFormatter);
		String formattedDateTime1 = dateTime.format(outputFormatter);
		String valueWithPadding = String.format("%0" + DIGIT_PADDING + "d", maxnumber + 1);
		String value = "T" + formattedDateTime1 + valueWithPadding;
		task.setTicketid(value);
		task.setMaxnum(maxnumber + 1);
		task.setStatus("Assigned");
//		AssignedUsers asg = new AssignedUsers();
//		asg.setUserid(task.getAddedby());
//		List<AssignedUsers> assignedUsers = new ArrayList();
//		assignedUsers.add(asg);
//		List<AssignedUsers> addedByToAssignedUsers = task.getAssignedto();
//		addedByToAssignedUsers.addAll(assignedUsers);
		taskRepo.save(task);
		List<AssignedUsers> addedByToAssignedUsers = task.getAssignedto();
		// assignid=null, userid=28, completed=false
		List<Long> usersids = addedByToAssignedUsers.stream().map(AssignedUsers::getUserid)
				.collect(Collectors.toList());
		List<UserDTO> user = userClient.getTaskAssinedUsersAndCreatedBy(token, task.getAddedby(), usersids);
		try {
			emailService.TaskAssigningEmail(task, user);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		return task;
	}

	@Override
	public boolean updateTask(UpdateTask updateTask) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: updateTask  ");
		List<TicketTracker> listTicketTracker = new ArrayList<>();
		TicketTracker ticketTracker = new TicketTracker();
		Task task = taskRepo.findById(updateTask.getTaskid()).get();

		List<AssignedUsers> asigned = task.getAssignedto();
		for (AssignedUsers assignedUsers : asigned) {
			if (updateTask.getUpdatedby() == assignedUsers.getUserid()) {
				assignedUsers.setUserstatus(updateTask.getStatus());
			}
		}
		task.setAssignedto(asigned);

		if (task != null) {
			task.setStatus(updateTask.getStatus());
			ticketTracker.setStatus(updateTask.getStatus());
			ticketTracker.setDescription(updateTask.getDescription());
			ticketTracker.setUpdatedby(updateTask.getUpdatedby());
			ticketTracker.setFromdate(updateTask.getFromdate());
			ticketTracker.setTodate(updateTask.getTodate());
			ticketTracker.setFtime(updateTask.getFtime());
			ticketTracker.setTtime(updateTask.getTtime());
			ticketTracker.setDuration(updateTask.getDuration());
			listTicketTracker.add(ticketTracker);
			task.setTrack(listTicketTracker);
			taskRepo.save(task);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Task findBytaskId(Long taskid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: findBytaskId");
		return taskRepo.findById(taskid).get();
	}

	@Override
	public List<Task> getAllTasks() {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getAllTasks");
		return taskRepo.findAll(Sort.by("taskid").descending());
	}

	@Override
	public List<TaskAssignDTO> taskAssignInfo(Long taskid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: taskAssignInfo");
		return taskRepo.taskAssignInfo(taskid);
	}

	@Override
	public List<TaskTrackerDTO> trackerByUser(Long userid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: trackerByUser");
		return taskRepo.trackerByUser(userid);
	}

	@Override
	public List<TaskTrackerDTO> allTasksRecords() {
		logger.info("!!! inside class: TaskServiceImpl , !! method: allTasksRecords");
		return taskRepo.allTasksRecords();
	}

	@Override
	public void deleteTask(Long id) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: deleteTask");
		taskRepo.deleteById(id);

	}

	@Override
	public List<TaskTrackerDTO> taskReports(DateSearchDTO dateSearch) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: TaskReports");
		if (dateSearch.getDepartment().equalsIgnoreCase("empty")) {
			return taskRepo.taskReports(dateSearch.getStartDate(), dateSearch.getTargetDate());
		} else {
			return taskRepo.taskReportsByDepartment(dateSearch.getStartDate(), dateSearch.getTargetDate(),
					dateSearch.getDepartment());
		}
	}

	//completedtesting
	@Override
	public Page<Task> findAllTasks(RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: findAllTasks");
		String sortorder = requestresponsedto.getSortOrder();
		String sortfield = requestresponsedto.getSortField();
		String keyword = requestresponsedto.getKeyword();
		Integer pageSize = requestresponsedto.getPageNumber();
		Integer pageNo = requestresponsedto.getPageSize();
	
		if (sortfield.equalsIgnoreCase("taskid"))
			sortfield = "taskid";
		else if (sortfield.equalsIgnoreCase("taskname"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("maxnum"))
			sortfield = "maxnum";
		else if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("department"))
			sortfield = "department";
		else if (sortfield.equalsIgnoreCase("createddate"))
			sortfield = "createddate";
		else 
			sortfield = "updateddate";
		
		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(requestresponsedto.getPageNumber() - 1, requestresponsedto.getPageSize(),
				sort);

		if (keyword.equalsIgnoreCase("empty")) {
			Page<Task> page = taskRepo.findAll(pageable);
			return page;
		} else {
			logger.info("!!! inside class: TaskServiceImpl , !! method: findAllTasks, !! Filtering");
			List<Task> listTask = taskRepo.findAll();

			Pattern pattern = Pattern.compile("(?i).*" + keyword + ".*");
			List<Task> filteredContent = listTask.stream().filter(applicant -> {
				String concatenatedFields = String.format("%s%s%s%s%s%s%s%s", applicant.getTaskname(),
						applicant.getStatus(), applicant.getDepartment(), applicant.getDescription(),
						DateFormat.formatDate(applicant.getCreateddate().toString()), DateFormat.formatDate(applicant.getUpdateddate().toString()), applicant.getTicketid(),
						applicant.getMaxnum());
				Matcher matcher = pattern.matcher(concatenatedFields);
				return matcher.find();
			}).collect(Collectors.toList());
			pageSize = pageable.getPageSize();
			pageNo = pageable.getPageNumber();
			int start = pageNo * pageSize;
			int end = Math.min(start + pageSize, filteredContent.size());

			Page<Task> pageResult = new PageImpl<>(filteredContent.subList(start, end), pageable,
					filteredContent.size());
			return pageResult;

		}

	}

	@Override
	public Page<TaskTrackerDTO> trackerByUserWithSortingAndPagination(TrackerByUserDTO trackerbyuserdto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: trackerByUserWithSortingAndPagination");
		String sortorder = trackerbyuserdto.getSortOrder();
		String sortfield = trackerbyuserdto.getSortField();
		String keyword = trackerbyuserdto.getKeyword();
		Integer pageNo = trackerbyuserdto.getPageNumber();
		Integer pageSize = trackerbyuserdto.getPageSize();

		if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("duration"))
			sortfield = "duration";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("taskname"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("fullname"))
			sortfield = "fullname";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(trackerbyuserdto.getPageNumber() - 1, trackerbyuserdto.getPageSize(), sort);
		if (keyword.equalsIgnoreCase("empty")) {
			return taskRepo.trackerByUserWithSortingAndPagination(pageable, trackerbyuserdto.getUserid());
			
		} else {
			logger.info("!!! inside class: TaskServiceImpl , !! method: trackerByUserWithSortingAndPagination, !! Filtering");
		    List<TaskTrackerDTO> listTaskTrackerDTO = taskRepo.trackerByUserWithSortingAndPagination(trackerbyuserdto.getUserid());
			Pattern pattern = Pattern.compile("(?i).*" + keyword + ".*");
			List<TaskTrackerDTO> filteredContent = listTaskTrackerDTO.stream().filter(applicant -> {
				String concatenatedFields = String.format("%s%s%s%s%s%s%s%s%s%s%s%s", applicant.getTrackid(),
						applicant.getTaskid(), applicant.getStatus(), applicant.getFullname(), applicant.getTicketid(),
						applicant.getTaskName(), applicant.getDescription(), applicant.getFtime(), applicant.getTtime(), applicant.getTaskdescription(), applicant.getPseudoname(),
						applicant.getDuration());
				Matcher matcher = pattern.matcher(concatenatedFields);
				return matcher.find();
			}).collect(Collectors.toList());
			pageSize = pageable.getPageSize();
			pageNo = pageable.getPageNumber();
			int start = pageNo * pageSize;
			int end = Math.min(start + pageSize, filteredContent.size());
			Page<TaskTrackerDTO> pageResult = new PageImpl<>(filteredContent.subList(start, end), pageable,
					filteredContent.size());
			return pageResult;

		}

	}

	@Override
	public Page<TaskAssignDTO> taskAssignInfoWithSortingAndPagination(TicketTrackerSortDTO tickettrackersortdto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: taskAssignInfoWithSortingAndPagination");
		String sortfield = tickettrackersortdto.getSortField();
		String sortorder = tickettrackersortdto.getSortOrder();
		String keyword = tickettrackersortdto.getKeyword();
		Integer pageNo = tickettrackersortdto.getPageNumber();
		Integer pageSize = tickettrackersortdto.getPageSize();

		if (sortfield.equalsIgnoreCase("pseudoname"))
			sortfield = "u.pseudoname";
		else if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("createddate"))
			sortfield = "createddate";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("userstatus"))
			sortfield = "au.userstatus";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(tickettrackersortdto.getPageNumber() - 1, tickettrackersortdto.getPageSize(),
				sort);

		if (keyword.equalsIgnoreCase("empty")) {
			return taskRepo.taskAssignInfoWithSortingAndFiltering(pageable, tickettrackersortdto.getTaskid());
		} else {
			logger.info("!!! inside class: TaskServiceImpl , !! method: taskAssignInfoWithSortingAndPagination, !! Filtering");
			List<TaskAssignDTO> listTaskAssignDTO = taskRepo.taskAssignInfoWithSortingAndFiltering(tickettrackersortdto.getTaskid());
			Pattern pattern = Pattern.compile("(?i).*" + keyword + ".*");
			List<TaskAssignDTO> filteredContent = listTaskAssignDTO.stream().filter(applicant -> {
				String concatenatedFields = String.format("%s%s%s%s%s%s%s", applicant.getTicketid(),
						applicant.getCreatedby(), applicant.getPseudoname(), applicant.getStatus(),
						applicant.getAustatus(),DateFormat.formatDate(applicant.getCreateddate().toString()),
						DateFormat.formatDate(applicant.getTargetdate().toString())
				);
				Matcher matcher = pattern.matcher(concatenatedFields);
				return matcher.find();
			}).collect(Collectors.toList());
			pageSize = pageable.getPageSize();
			pageNo = pageable.getPageNumber();
			int start = pageNo * pageSize;
			int end = Math.min(start + pageSize, filteredContent.size());
			Page<TaskAssignDTO> pageResult = new PageImpl<>(filteredContent.subList(start, end), pageable,
					filteredContent.size());
			return pageResult;

		}

	}

	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public Page<TaskTrackerDTO> taskReportsByDepartmentWithSortingAndPagination(TaskReportsDTO taskreportsdto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: taskReportsByDepartmentWithSortingAndPagination");
		String sortfield = taskreportsdto.getSortField();
		String sortorder = taskreportsdto.getSortOrder();
		String keyword = taskreportsdto.getKeyword();
		Integer pageNo = taskreportsdto.getPageNumber();
		Integer pageSize = taskreportsdto.getPageSize();

		if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("taskname"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("createddate"))
			sortfield = "createddate";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "taskdescription";
		else if (sortfield.equalsIgnoreCase("pseudoname"))
			sortfield = "u.pseudoname";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield); 
		Pageable pageable = PageRequest.of(taskreportsdto.getPageNumber() - 1, taskreportsdto.getPageSize(), sort);

		if (taskreportsdto.getDepartment().equalsIgnoreCase("empty")) {
			if (keyword.equalsIgnoreCase("empty")) {
			return taskRepo.taskReportsWithSortingAndPagination(pageable, taskreportsdto.getFromDate(),taskreportsdto.getToDate());
				
			} else {
				logger.info("!!! inside class: TaskServiceImpl, !! method: taskReportsByDepartmentWithSortingAndPagination,  Filter");
				List<TaskTrackerDTO> listTaskTrackerDTO = taskRepo.taskReportsWithSortingAndPagination(taskreportsdto.getFromDate(), taskreportsdto.getToDate());

				Pattern pattern = Pattern.compile("(?i).*" + keyword + ".*");
				List<TaskTrackerDTO> filteredContent = listTaskTrackerDTO.stream().filter(applicant -> {
					String concatenatedFields = String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
							applicant.getTrackid(), applicant.getTaskid(), applicant.getStatus(),
							applicant.getDescription(), applicant.getFullname(),applicant.getTicketid(), applicant.getTaskName(), applicant.getFtime(), applicant.getTtime(),
							applicant.getTaskdescription(), applicant.getPseudoname(), applicant.getDuration(),
							DateFormat.formatDate(applicant.getCreateddate().toString()),applicant.getFromdate(), 
							applicant.getTodate());
					Matcher matcher = pattern.matcher(concatenatedFields);
					return matcher.find();
				}).collect(Collectors.toList());
				pageSize = pageable.getPageSize();
				pageNo = pageable.getPageNumber();
				int start = pageNo * pageSize;
				int end = Math.min(start + pageSize, filteredContent.size());

				Page<TaskTrackerDTO> pageResult = new PageImpl<>(filteredContent.subList(start, end), pageable,
						filteredContent.size());
				return pageResult;

			}

		} else {

			if (keyword.equalsIgnoreCase("empty")) {
				return taskRepo.taskReportsByDepartmentWithSortingAndPagination(pageable, taskreportsdto.getFromDate(),
						taskreportsdto.getToDate(), taskreportsdto.getDepartment());
			} else {
				
		      List<TaskTrackerDTO> listTaskTrackerDTO = taskRepo.taskReportsByDepartmentWithSortingAndPagination(taskreportsdto.getFromDate(), taskreportsdto.getToDate(), taskreportsdto.getDepartment());
		      Pattern pattern = Pattern.compile("(?i).*" + keyword + ".*");
				List<TaskTrackerDTO> filteredContent = listTaskTrackerDTO.stream().filter(applicant -> {
					String concatenatedFields = String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
							applicant.getTrackid(), applicant.getTaskid(), applicant.getStatus(),
							applicant.getDescription(),  applicant.getFullname(), applicant.getTicketid(), applicant.getTaskName(), 
						   applicant.getFtime(), applicant.getTtime(),applicant.getTaskdescription(), applicant.getPseudoname(), applicant.getDuration(),
							DateFormat.formatDate(applicant.getCreateddate().toString()),applicant.getFromdate(), 
							applicant.getTodate());
					Matcher matcher = pattern.matcher(concatenatedFields);
					return matcher.find();
				}).collect(Collectors.toList());
				pageSize = pageable.getPageSize();
				pageNo = pageable.getPageNumber();
				int start = pageNo * pageSize;
				int end = Math.min(start + pageSize, filteredContent.size());

				Page<TaskTrackerDTO> pageResult = new PageImpl<>(filteredContent.subList(start, end), pageable,
						filteredContent.size());
				return pageResult;

			}
		}
	}

	
	
	
	
	
	
	
 //Description 
	@Override
	public Page<TaskTrackerDTO> allTasksRecordsWithSortingAndPagination(RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: allTasksRecordsWithSortingAndPagination");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		String keyword = requestresponsedto.getKeyword();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();

		if (sortfield.equalsIgnoreCase("description"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("taskname"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("fullname"))
			sortfield = "u.fullname";
		else if (sortfield.equalsIgnoreCase("createddate"))
			sortfield = "createddate";
		

		Sort.Direction sortDirection = Sort.Direction.ASC;

		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(requestresponsedto.getPageNumber() - 1, requestresponsedto.getPageSize(),
				sort);
		if (keyword.equalsIgnoreCase("empty")) {
			return taskRepo.allTasksRecordsWithSortingAndPagination(pageable);

		} else {
			logger.info("!!! inside class: TaskServiceImpl, !! method: allTasksRecordsWithSortingAndPagination,  Filter");
			List<TaskTrackerDTO> listTaskTrackerDTO = taskRepo.allTasksRecordsWithSortingAndPagination();
			Pattern pattern = Pattern.compile("(?i).*" + keyword + ".*");
			List<TaskTrackerDTO> filteredContent = listTaskTrackerDTO.stream().filter(applicant -> {
				String concatenatedFields = String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s", applicant.getTrackid(),
						applicant.getTaskid(), applicant.getStatus(), applicant.getDescription(),
						 applicant.getFullname(), applicant.getTicketid(),
						applicant.getTaskName(), applicant.getFtime(),
						applicant.getTtime(), applicant.getTaskdescription(), applicant.getPseudoname(),
						applicant.getDuration(), DateFormat.formatDate(applicant.getCreateddate().toString()),
						applicant.getFromdate(),applicant.getTodate());
				Matcher matcher = pattern.matcher(concatenatedFields);
				return matcher.find();
			}).collect(Collectors.toList());
			pageSize = pageable.getPageSize();
			pageNo = pageable.getPageNumber();
			int start = pageNo * pageSize;
			int end = Math.min(start + pageSize, filteredContent.size());
			Page<TaskTrackerDTO> pageResult = new PageImpl<>(filteredContent.subList(start, end), pageable,
					filteredContent.size());
			return pageResult;

		}

	}

	@Override
	public Page<TaskTrackerDTO> getTaskByProjectid(RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getTaskByProjectid");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		String keyword = requestresponsedto.getKeyword();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String projectid=requestresponsedto.getProjectid();
		String status=requestresponsedto.getStatus();
	 if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("taskname"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		Sort.Direction sortDirection = Sort.Direction.ASC;

		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(requestresponsedto.getPageNumber() - 1, requestresponsedto.getPageSize(),
				sort);
		if (keyword.equalsIgnoreCase("empty")) {
			return taskRepo.getTaskByProjectid(pageable, projectid, status);
		} else {
			return taskRepo.getTaskByProjectIdWithsearching(pageable, projectid, status, keyword);
		}
	}

	@Override
	public List<GetUsersDTO> getUsersByDepartment(String department) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getUsersByDepartment");
		return taskRepo.findDepartmentWiseUsers(department);
	}

	@Override
	public boolean updateTaskStatus(Long taskid, String status) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: updateTaskStatus");
		taskRepo.updateTaskStatus(taskid, status);
		return false;
		
	}

	@Override
	public Page<TaskTrackerDTO> findTaskByProjectid(RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getTaskByProjectid");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String projectid=requestresponsedto.getProjectid();
		String keyword = requestresponsedto.getKeyword();
	 if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("taskname"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		Sort.Direction sortDirection = Sort.Direction.ASC;

		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(requestresponsedto.getPageNumber() - 1, requestresponsedto.getPageSize(),
				sort);
		if (keyword.equalsIgnoreCase("empty")) {
			return taskRepo.findTaskByProjectid(pageable, projectid);
		} else {
			return taskRepo.findTaskByProjectIdWithSearching(pageable, projectid, keyword);
		}
}
}