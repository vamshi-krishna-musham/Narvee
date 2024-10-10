package com.narvee.service.serviceimpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.narvee.dto.TaskTrackerDTO;
import com.narvee.repository.SubTaskRepository;
import com.narvee.repository.TaskRepository;
import com.narvee.service.service.ScheduleTasksService;

@Service
public class ScheduleTasksServiceImpl implements ScheduleTasksService {
	private static final Logger logger = LoggerFactory.getLogger(ScheduleTasksServiceImpl.class);

	@Autowired
	private SubTaskRepository subTaskRepository;

	@Autowired
	private TaskRepository repository;

	@Autowired
	private EmailServiceImpl emailServiceImpl;

	@Override
	@Scheduled(cron = "0 30 09 * * ?", zone = "Asia/Kolkata")
	public void subtaskDeadlineExceededEmail() throws UnsupportedEncodingException, MessagingException {
		logger.info("!!! inside class: ScheduleTasksServiceImpl, !! method: subtaskDeadlineExceededEmail");
		ZoneId india = ZoneId.of("Asia/Kolkata");
		LocalDate date = LocalDate.now(india);
		List<TaskTrackerDTO> getExceededTargetDateSubTasks = subTaskRepository.getExceededTargetDateSubTasks(date);
		for (TaskTrackerDTO taskTrackerDTO : getExceededTargetDateSubTasks) {
			emailServiceImpl.targetExceededEmail(taskTrackerDTO,"subTask");

		}

	}

	@Override
	@Scheduled(cron = "0 30 09 * * ?", zone = "Asia/Kolkata")
	public void TaskdeadlineExceededEmail() throws UnsupportedEncodingException, MessagingException {
		logger.info("!!! inside class: ScheduleTasksServiceImpl, !! method: TaskdeadlineExceededEmail");
		ZoneId india = ZoneId.of("Asia/Kolkata");
		LocalDate date = LocalDate.now(india);
		List<TaskTrackerDTO> getExceededTargetDateSubTasks = repository.getExceededTargetDateSubTasks(date);
		for (TaskTrackerDTO taskTrackerDTO : getExceededTargetDateSubTasks) {
			emailServiceImpl.targetExceededEmail(taskTrackerDTO,"task");

		}

	}

}
