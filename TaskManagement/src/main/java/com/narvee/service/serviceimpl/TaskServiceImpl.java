package com.narvee.service.serviceimpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.dto.DateSearchDTO;
import com.narvee.dto.TaskAssignDTO;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.dto.UserDTO;
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

		// System.out.println(updateTask);
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
		;
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

}
