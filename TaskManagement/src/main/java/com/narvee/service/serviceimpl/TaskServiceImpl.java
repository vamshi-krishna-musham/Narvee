package com.narvee.service.serviceimpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.dto.DateSearchDTO;
import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.dto.TaskAssignDTO;
import com.narvee.dto.TaskResponse;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.dto.TasksResponseDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.entity.AssignedUsers;
import com.narvee.entity.Task;
import com.narvee.entity.TicketTracker;
import com.narvee.feignclient.UserClient;
import com.narvee.repository.TaskRepository;
import com.narvee.service.service.TaskService;

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
		task.setStatus("To Do");
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

		List<GetUsersDTO> user = taskRepo.getTaskAssinedUsersAndCreatedBy(task.getAddedby(), usersids);
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

		if (task.getStartDate() == null) {
			task.setStartDate(LocalDate.now());
		}

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
			ticketTracker.setComments(updateTask.getComments());
			ticketTracker.setUpdatedby(updateTask.getUpdatedby());
			listTicketTracker.add(ticketTracker);
			task.setTrack(listTicketTracker);
			taskRepo.save(task);
			return true;
		}
		return false;
	}

	@Override
	public Task findBytaskId(Long taskid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: findBytaskId");
		Task task = taskRepo.findById(taskid).get();
		for (AssignedUsers aUser : task.getAssignedto()) {
			GetUsersDTO user = taskRepo.getUser(aUser.getUserid());
			aUser.setFullname(user.getFullname());
			aUser.setPseudoname(user.getPseudoname());
		}
		return task;
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

	

	@Override
	public Page<TaskTrackerDTO> getTaskByProjectid(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getTaskByProjectid");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		String keyword = requestresponsedto.getKeyword();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String projectid = requestresponsedto.getProjectid();
		String status = requestresponsedto.getStatus();
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
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
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
		return true;

	}

	@Override
	public TaskResponse findTaskByProjectid(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getTaskByProjectid");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String projectid = requestresponsedto.getProjectid();
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
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		if (keyword.equalsIgnoreCase("empty")) {

			List<TaskTrackerDTO> res = taskRepo.findTaskByProjectid(projectid);

			logger.info("!!! inside class: TaskServiceImpl , !! method: findTaskByProjectid");
			List<TasksResponseDTO> tasksList = new ArrayList<>();

			for (TaskTrackerDTO order : res) {
				TasksResponseDTO result = new TasksResponseDTO(order);
				List<GetUsersDTO> assignUsers = taskRepo.getAssignUsers(order.getTaskid());
				result.setAssignUsers(assignUsers);
				tasksList.add(result);
			}

			Long pid = taskRepo.findPid(projectid);
			TaskResponse taskResp = new TaskResponse();
			taskResp.setTasks(tasksList);
			taskResp.setPid(pid);

			return taskResp;
		} else {
			logger.info("!!! inside class: TaskServiceImpl , !! method: findTaskByProjectIdWithSearching , Filter");
			List<TaskTrackerDTO> res = taskRepo.findTaskByProjectIdWithSearching(projectid, keyword);
			List<TasksResponseDTO> tasksList = new ArrayList<>();

			for (TaskTrackerDTO order : res) {
				TasksResponseDTO result = new TasksResponseDTO(order);
				List<GetUsersDTO> assignUsers = taskRepo.getAssignUsers(order.getTaskid());
				result.setAssignUsers(assignUsers);
				tasksList.add(result);
			}

			Long pid = taskRepo.findPid(projectid);
			TaskResponse taskResp = new TaskResponse();
			taskResp.setTasks(tasksList);
			taskResp.setPid(pid);
			return taskResp;
		}
	}

	@Override
	public Task findByTicketId(String taskid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: findByTicketId");
		return taskRepo.findByTicketid(taskid);
	}

	@Override
	public List<TasksResponseDTO> ticketTracker(Long taskid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: ticketTracker");
		List<TaskTrackerDTO> tracker = taskRepo.ticketTracker(taskid);
		List<TasksResponseDTO> tasksList = new ArrayList<>();

		for (TaskTrackerDTO taskTrackerDTO : tracker) {
			TasksResponseDTO track = new TasksResponseDTO(taskTrackerDTO);
			GetUsersDTO user = taskRepo.getUser(taskTrackerDTO.getUpdatedby());
			if (taskTrackerDTO.getUpdatedby() != null) {
				track.setFullname(user.getFullname());
				track.setPseudoname(user.getPseudoname());
			}
			tasksList.add(track);
		}
		return tasksList;
	}

	@Override
	public Task update(Task task) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: ticketTracker");
		Task update = taskRepo.findById(task.getTaskid()).get();
		update.setTargetdate(task.getTargetdate());
		update.setTaskname(task.getTaskname());
		update.setDescription(task.getDescription());
		update.setAssignedto(task.getAssignedto());
		update.setUpdatedby(task.getUpdatedby());
		return taskRepo.save(update);
	}



}