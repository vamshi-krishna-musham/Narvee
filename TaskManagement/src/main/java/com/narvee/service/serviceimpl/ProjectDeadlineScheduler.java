package com.narvee.service.serviceimpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.narvee.entity.TmsAssignedUsers;
import com.narvee.entity.TmsProject;
import com.narvee.entity.TmsSubTask;
import com.narvee.entity.TmsTask;
import com.narvee.repository.ProjectRepository;
import com.narvee.repository.SubTaskRepository;
import com.narvee.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ProjectDeadlineScheduler {
	private static final Logger logger = LoggerFactory.getLogger(ProjectDeadlineScheduler.class);

	@Autowired
	private EmailServiceImpl emailService;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private SubTaskRepository subTaskRepository;

	// @Scheduled(cron = "0 * * * * *") // every 1 minute
//@Scheduled(cron = "0 0 9 * * ?") // every day at 9 AM
	@Transactional
//@Scheduled(cron = "0 * * * * *") // every 1 minute
	public void taskDailyDeadlineCheck() throws UnsupportedEncodingException, MessagingException {
		logger.info("!!! inside class: ProjectDeadlineScheduler, !! method: taskDailyDeadlineCheck");
		List<TmsTask> allTasks = taskRepository.getAllTaskDeatils();

		LocalDate today = LocalDate.now();

		// filter expired tasks
		List<TmsTask> expiredTasks = allTasks.stream()
				.filter(t -> t.getTargetDate() != null && t.getTargetDate().isBefore(today)).toList();

		// filter tasks due tomorrow
		List<TmsTask> dueTomorrowTasks = allTasks.stream()
				.filter(t -> t.getTargetDate() != null && t.getTargetDate().isEqual(today.plusDays(1))).toList();

		// process both cases

		logger.info("!!! inside class: ProjectDeadlineScheduler, !! method: processTasks");
		processTasks(expiredTasks, "expiredTask");
		processTasks(dueTomorrowTasks, "oneDayBeforeTask");
	}

	private void processTasks(List<TmsTask> tasks, String emailType)
			throws UnsupportedEncodingException, MessagingException {
		logger.info("!!! inside class: ProjectDeadlineScheduler, !! method: processTasks");
		for (TmsTask task : tasks) {
			if (task.getAssignedto() == null || task.getAssignedto().isEmpty()) {
				continue; // skip tasks with no assigned users
			}
			StringBuilder assignedUsers = new StringBuilder();
			List<String> emails = new ArrayList<>();

			for (TmsAssignedUsers assigned : task.getAssignedto()) {
				List<Object[]> resultList = taskRepository.findFullNameByUserId(assigned.getTmsUserId());
				if (resultList != null && !resultList.isEmpty()) {
					Object[] row = resultList.get(0);
					if (row != null && row.length >= 2) {
						String fullName = (String) row[0];
						String email = (String) row[1];

						if (fullName != null) {
							if (assignedUsers.length() > 0) {
								assignedUsers.append(", ");
							}
							assignedUsers.append(fullName);
						}
						if (email != null) {
							emails.add(email);
						}
					}
				}
			}

			if (!emails.isEmpty()) {
				emailService.sendTaskDeadlineAlerts(task, assignedUsers.toString(), emails, emailType);
			}
		}
	}

	@Transactional
//@Scheduled(cron = "0 * * * * *")
	public void dailyDeadlineCheck() throws UnsupportedEncodingException, MessagingException {
		logger.info("!!! inside class: ProjectDeadlineScheduler, !! method: dailyDeadlineCheck");

		List<TmsProject> projects = projectRepository.findAll(); // get all projects
		LocalDate today = LocalDate.now();

		List<TmsProject> expiredProjects = projects.stream()
				.filter(p -> p.getTargetDate() != null && p.getTargetDate().isBefore(today))
				.collect(Collectors.toList());

		List<TmsProject> oneDayBeforeProjects = projects.stream()
				.filter(p -> p.getTargetDate() != null && p.getTargetDate().isEqual(today.plusDays(1)))
				.collect(Collectors.toList());

		for (TmsProject tmsProject : expiredProjects) {
			StringBuilder assignUsernames = new StringBuilder();
			List<String> emails = new ArrayList<>();

			Set<TmsAssignedUsers> tmsAssignedUsers = tmsProject.getAssignedto();
			for (TmsAssignedUsers tmsUser : tmsAssignedUsers) {
				List<Object[]> resultList = projectRepository.findFullNameByUserId(tmsUser.getTmsUserId());
				if (resultList != null && !resultList.isEmpty()) {
					Object[] row = resultList.get(0); // Get the first row

					if (row != null && row.length >= 2) {
						String fullName = (String) row[0]; // CONCAT(first_name, last_name)
						String email = (String) row[1]; // email

						assignUsernames.append(fullName);
						assignUsernames.append(", ");

						emails.add(email);
					}

				}
			}

			emailService.sendProjectDeadlineAlerts(tmsProject, assignUsernames.toString(), emails, "expiredProjects");

		}

		for (TmsProject tmsProject : oneDayBeforeProjects) {
			StringBuilder assignUsernames = new StringBuilder();
			List<String> emails = new ArrayList<>();
			Set<TmsAssignedUsers> tmsAssignedUsers = tmsProject.getAssignedto();
			for (TmsAssignedUsers tmsUser : tmsAssignedUsers) {
				List<Object[]> resultList = projectRepository.findFullNameByUserId(tmsUser.getTmsUserId());
				if (resultList != null && !resultList.isEmpty()) {
					Object[] row = resultList.get(0); // Get the first row

					if (row != null && row.length >= 2) {
						String fullName = (String) row[0]; // CONCAT(first_name, last_name)
						String email = (String) row[1]; // email
						assignUsernames.append(fullName);
						assignUsernames.append(", ");
						emails.add(email);
					}

				}
			}

			emailService.sendProjectDeadlineAlerts(tmsProject, assignUsernames.toString(), emails,
					"oneDayBeforeProjects");

		}
	}

//	 @Scheduled(cron = "0 * * * * *") // every 1 minute
	public void subtaskDailyDeadlineCheck() throws UnsupportedEncodingException, MessagingException {
		logger.info("!!! inside class: ProjectDeadlineScheduler, !! method: taskDailyDeadlineCheck");
		List<TmsSubTask> allTasks = subTaskRepository.getAllSubTaskDeatils();

		LocalDate today = LocalDate.now();

		// filter expired tasks
		List<TmsSubTask> expiredTasks = allTasks.stream()
				.filter(t -> t.getTargetDate() != null && t.getTargetDate().isBefore(today)).toList();

		// filter tasks due tomorrow
		List<TmsSubTask> dueTomorrowTasks = allTasks.stream()
				.filter(t -> t.getTargetDate() != null && t.getTargetDate().isEqual(today.plusDays(1))).toList();

		// process both cases

		logger.info("!!! inside class: ProjectDeadlineScheduler, !! method: processTasks");
		processsubTasks(expiredTasks, "expiredSubTask");
		processsubTasks(dueTomorrowTasks, "oneDayBeforeSubTask");
	}

	private void processsubTasks(List<TmsSubTask> Subtask, String emailType)
			throws UnsupportedEncodingException, MessagingException {
		logger.info("!!! inside class: ProjectDeadlineScheduler, !! method: processTasks");
		for (TmsSubTask Subtasks : Subtask) {
			if (Subtasks.getAssignedto() == null || Subtasks.getAssignedto().isEmpty()) {
				continue; // skip tasks with no assigned users
			}
			StringBuilder assignedUsers = new StringBuilder();
			List<String> emails = new ArrayList<>();

			for (TmsAssignedUsers assigned : Subtasks.getAssignedto()) {
				List<Object[]> resultList = taskRepository.findFullNameByUserId(assigned.getTmsUserId());
				if (resultList != null && !resultList.isEmpty()) {
					Object[] row = resultList.get(0);
					if (row != null && row.length >= 2) {
						String fullName = (String) row[0];
						String email = (String) row[1];

						if (fullName != null) {
							if (assignedUsers.length() > 0) {
								assignedUsers.append(", ");
							}
							assignedUsers.append(fullName);
						}
						if (email != null) {
							emails.add(email);
						}
					}
				}
			}

			if (!emails.isEmpty()) {
				emailService.sendSubTaskDeadlineAlerts(Subtasks, assignedUsers.toString(), emails, emailType);
			}
		}
	}

}
