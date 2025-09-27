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
import com.narvee.repository.ProjectRepository;



@Component
public class ProjectDeadlineScheduler {

	@Autowired
	private EmailServiceImpl emailService;

	@Autowired
	private ProjectRepository projectRepository;

//@Scheduled(cron = "0 0 9 * * ?") // every day at 9 AM
	 @Transactional 
//@Scheduled(cron = "0 * * * * *")
	public void dailyDeadlineCheck() throws UnsupportedEncodingException, MessagingException {

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
}
