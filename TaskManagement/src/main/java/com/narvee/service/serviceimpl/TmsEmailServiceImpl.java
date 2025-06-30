package com.narvee.service.serviceimpl;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.entity.TmsProject;
import com.narvee.entity.TmsSubTask;
import com.narvee.entity.TmsTask;
import com.narvee.feignclient.UserClient;
import com.narvee.repository.ProjectRepository;
import com.narvee.repository.SubTaskRepository;
import com.narvee.repository.TaskRepository;

@Service
public class TmsEmailServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	private SubTaskRepository subTaskRepository;

	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	UserClient userClient;

	@Value("${frommail}")
	private String narveemail;

	@Value("${shortMessage}")
	private String shortMessage;

	@Value("${url}")
	private String url;

	@Value("${ccmail}")
	private String[] ccmail;
	
	
	//---------Send email for project Create and update for task management ----------
	@Async
	public void sendCreateProjectEmail(TmsProject project, List<GetUsersDTO> userdetails, boolean projectUpdate)  
			throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: EmailServiceImpl, !! method: sendCreateProjectEmail");
		System.err.println("userdetails :"+userdetails.toString());

		StringBuilder assignedUsers = new StringBuilder();
		StringBuilder createdby = new StringBuilder();

		int i = 0;
		String emails[] = new String[userdetails.size()];
		
		for (GetUsersDTO userDTO : userdetails) {
			if (userDTO.getCreatedby() != null) {
				createdby.append(userDTO.getCreatedby());
			} else {
				if (i != 0) {
					assignedUsers.append(",");
				}
				assignedUsers.append(userDTO.getFullname());  
			}

			emails[i] = userDTO.getEmail();
			i++;
		}
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
		String formattedStartDate = project.getStartDate() != null
			    ? formatter.format(project.getStartDate())
			    : "Not available";
			String formattedTargetDate = project.getTargetDate() != null
			    ? formatter.format(project.getTargetDate())
			    : "Not available";

		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		String[] uniqueEmails = emailSet.toArray(new String[0]);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(uniqueEmails);
		helper.setFrom(narveemail, shortMessage);
		String subject;
		String body;

		if (projectUpdate) {
			subject = "New Project Assignment: " + project.getProjectid();
			body = "<!DOCTYPE html>"
			    + "<html><head><meta charset='UTF-8'><title>New Project Assigned</title></head>"
			    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
			    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
			    + "<tr><td align='center'>"
			    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
			    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
			    + "<h2 style='margin: 0;'>Task Management</h2>"
			    + "</td></tr>"
			    + "<tr><td style='padding: 30px; color: #333;'>"
			    + "<p style='font-size: 16px;'>Hi Team,</p>"
			    + "<p style='font-size: 14px;'>A new project has been created and assigned to you. Please find the project details below:</p>"
			    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
			    + "<tr><td style='font-weight: bold;vertical-align: top;'>Project ID:</td><td>" + project.getProjectid() + "</td></tr>"
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Project Name:</td>"
			    + "<td style='white-space: normal;'>" + project.getProjectName() + "</td></tr>"
			    + "<tr><td style='font-weight: bold; vertical-align: top;'>Description:</td>" 
			    + "<td style='width: 500px; white-space: normal; '>" + project.getDescription() + "</td></tr>" 
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Assigned Users:</td>"
			    + "<td style='white-space: normal;'>" + assignedUsers + "</td></tr>"	
			    + "<tr><td style='font-weight: bold;'>Status:</td><td>" + project.getStatus() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Created By:</td><td>" + createdby + "</td></tr>"  
			    + "<tr><td style='font-weight: bold;'>Start Date:</td><td>" + formattedStartDate + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Target Date:</td><td>" + formattedTargetDate + "</td></tr>"
			    + "</table>"
			    + "<p style='font-size: 14px; margin-top: 20px;'>Please log in to the portal to begin your work.</p>"
			    + "</td></tr>"
			    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
			    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management</p>"
			    + "</td></tr>"
     		    + "</table></td></tr></table></body></html>";

		} else {	
			subject = "Project Updated: " + project.getProjectid();
			body = "<!DOCTYPE html>"
			    + "<html><head><meta charset='UTF-8'><title>Project Updated</title></head>"
			    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
			    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
			    + "<tr><td align='center'>"
			    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
			    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
			    + "<h2 style='margin: 0;'>Task Management</h2>"
			    + "</td></tr>"
			    + "<tr><td style='padding: 30px; color: #333;'>"
			    + "<p style='font-size: 16px;'>Hi Team,</p>"
			    + "<p style='font-size: 15px;'>The project has been updated. Please find the updated details below:</p>"
			    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
			    + "<tr><td style='font-weight: bold;vertical-align: top;'>Project ID:</td><td>" + project.getProjectid() + "</td></tr>"
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Project Name:</td>" 
			    + "<td style='white-space: normal;'>" + project.getProjectName() + "</td></tr>"
			    + "<tr><td style='font-weight: bold; vertical-align: top;'>Description:</td>" 
			    + "<td style='width: 500px; white-space: normal; '>" + project.getDescription() + "</td></tr>" 
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Assigned Users:</td>"
			    + "<td style='white-space: normal;'>" + assignedUsers + "</td></tr>"	
			    + "<tr><td style='font-weight: bold;'>Status:</td><td>" + project.getStatus() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Updated By:</td><td>" + createdby + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Start Date:</td><td>" + formattedStartDate+ "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Target Date:</td><td>" + formattedTargetDate + "</td></tr>"
			    + "</table>"
			    + "<p style='font-size: 14px; margin-top: 20px;'>Please check the portal for more information.</p>"
			    + "</td></tr>"
			    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
			    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management</p>"
			    + "</td></tr>"
			    + "</table></td></tr></table></body></html>";
		}
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(message);
		logger.info("!!! inside class: EmailServiceImpl, !! method: End sendCreateProjectEmail");
	}
	
	
	//------------------Send email for task create and update for task management-----------------
	@Async
	public void TaskAssigningEmailForTMS(TmsTask task, List<GetUsersDTO> userdetails,boolean isTask)  //   task created and updated email for TMS project 
			throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: TaskAssigningEmailForTMS --- tms ");
      String projectId =  projectRepository.getProjectName(task.getTaskid());
		StringBuilder createdBy = new StringBuilder();
		StringBuilder users = new StringBuilder();
		int i = 0;
		String emails[] = new String[userdetails.size()];

		for (GetUsersDTO userDTO : userdetails) {
			if (userDTO.getCreatedby() != null) {
				createdBy.append(userDTO.getCreatedby());
			} else {
				if (i != 0) {
					users.append(",");
				}
				users.append(userDTO.getFullname());

			}
			emails[i] = userDTO.getEmail();
			i++;
		}
		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		String[] uniqueEmails = emailSet.toArray(new String[0]);
		emails = uniqueEmails;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
		String formattedStartDate = task.getStartDate() != null
			    ? formatter.format(task.getStartDate())
			    : "Not available";
			String formattedTargetDate = task.getTargetDate() != null
			    ? formatter.format(task.getTargetDate())
			    : "Not available";
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
//		helper.setCc(ccmail);
		// helper.setBcc(emails);

		helper.setTo(emails);

		helper.setFrom(narveemail, shortMessage);
		String subject = "Assigned Task Info ";
		StringBuilder stringBuilder = new StringBuilder();
		
		String body;
		
		if(isTask) {
			subject = " New Task Created " + task.getTaskname();
			
		body = "<!DOCTYPE html>"
			    + "<html><head><meta charset='UTF-8'><title>Task Created </title></head>"
			    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
			    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
			    + "<tr><td align='center'>"
			    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
			    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
			    + "<h2 style='margin: 0;'>Task Management</h2>"
			    + "</td></tr>"
			    + "<tr><td style='padding: 30px; color: #333;'>"
			    + "<p style='font-size: 16px;'>Hi Team,</p>"
			    + "<p style='font-size: 15px;'>The New Task has been Created. Please find the  details below:</p>"
			    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
			    + "<tr><td style='font-weight: bold;'>Ticket ID:</td><td>" +  task.getTicketid()+ "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Task Name:</td><td>" + task.getTaskname() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Project Id:</td><td>" + projectId + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Status:</td><td>" + task.getStatus() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Priority:</td><td>" + task.getPriority() + "</td></tr>"
			    + "<tr><td style='font-weight: bold; vertical-align: top;'>Description:</td>" 
			    + "<td style='width: 500px; white-space: normal; '>" + task.getDescription() + "</td></tr>" 
			    + "<tr><td style='font-weight: bold;'>Start Date:</td><td>" +formattedStartDate + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Target Date:</td><td>" +formattedTargetDate+ "</td></tr>"
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Assigned Users:</td>"
			    + "<td style='white-space: normal;'>" + users + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Updated By:</td><td>" + createdBy + "</td></tr>"
			    + "</table>"
			    + "<p style='font-size: 14px; margin-top: 20px;'>Please check the portal for more information.</p>"
			    + "</td></tr>"
			    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
			    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management Team</p>"
			    + "</td></tr>"
			    + "</table></td></tr></table></body></html>";
		}else {
			subject = " Task Updated " + task.getTaskname();
			
			body = "<!DOCTYPE html>"
				    + "<html><head><meta charset='UTF-8'><title>Task  Updated</title></head>"
				    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
				    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
				    + "<tr><td align='center'>"
				    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
				    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
				    + "<h2 style='margin: 0;'>Task Management</h2>"
				    + "</td></tr>"
				    + "<tr><td style='padding: 30px; color: #333;'>"
				    + "<p style='font-size: 16px;'>Hi Team,</p>"
				    + "<p style='font-size: 15px;'>The Task has been updated. Please find the updated details below:</p>"
				    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
				    + "<tr><td style='font-weight: bold;'>Ticket ID:</td><td>" +  task.getTicketid()+ "</td></tr>"
				    + "<tr><td style='font-weight: bold;'>Task Name:</td><td>" + task.getTaskname() + "</td></tr>"
				    + "<tr><td style='font-weight: bold;'>Project Id:</td><td>" + projectId + "</td></tr>"
				    + "<tr><td style='font-weight: bold;'>Status:</td><td>" + task.getStatus() + "</td></tr>"
				    + "<tr><td style='font-weight: bold;'>Priority:</td><td>" + task.getPriority()+ "</td></tr>"
				    + "<tr><td style='font-weight: bold; vertical-align: top;'>Description:</td>" 
				    + "<td style='width: 500px; white-space: normal; '>" + task.getDescription() + "</td></tr>" 
				    + "<tr><td style='font-weight: bold;'>Start Date:</td><td>" +formattedStartDate + "</td></tr>"
				    + "<tr><td style='font-weight: bold;'>Target Date:</td><td>" +formattedTargetDate+ "</td></tr>"
				    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Assigned Users:</td>"
				    + "<td style='white-space: normal;'>" + users + "</td></tr>"
				    + "<tr><td style='font-weight: bold;'>Updated By:</td><td>" + createdBy + "</td></tr>"
				    + "</table>"
				    + "<p style='font-size: 14px; margin-top: 20px;'>Please check the portal for more information.</p>"
				    + "</td></tr>"
				    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
				    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management Team</p>"
				    + "</td></tr>"
				    + "</table></td></tr></table></body></html>";
		}
		helper.setSubject(subject);
		helper.setText(body.toString(), true);
		mailSender.send(message);
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: End TaskAssigningEmail");
	}
	
	//----------------send email for create and update sub task in task management ---------------------
	@Async
	public void sendCreateSubTaskEmail(TmsSubTask subtask, List<GetUsersDTO> userdetails, boolean  SubTaskUpdate)
			throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: SubTaskServiceImpl, !! method: SubTaskAssigningEmail");
		GetUsersDTO subTaskDetails = subTaskRepository.GetPorjectNameAndTaskName(subtask.getSubTaskId());
		StringBuilder createdBy = new StringBuilder();
		StringBuilder users = new StringBuilder();
		int i = 0;
		String emails[] = new String[userdetails.size()];

		for (GetUsersDTO userDTO : userdetails) {
			if (userDTO.getCreatedby() != null) {
				createdBy.append(userDTO.getCreatedby());
			} else {
				if (i != 0) {
					users.append(",");
				}
				//users.append(userDTO.getPseudoname());
				users.append(userDTO.getFullname());   // changed for tms users 

			}
			emails[i] = userDTO.getEmail();
			i++;
		}
		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		String[] uniqueEmails = emailSet.toArray(new String[0]);
		emails = uniqueEmails;	


		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
		String formattedStartDate = subtask.getStartDate() != null
			    ? formatter.format(subtask.getStartDate())
			    : "Not available";
			String formattedTargetDate = subtask.getTargetDate() != null
			    ? formatter.format(subtask.getTargetDate())
			    : "Not available";
	

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(uniqueEmails);
		helper.setFrom(narveemail, shortMessage);
//		helper.setCc(ccmail);
		String subject;
		String body;

		if (SubTaskUpdate) {
			subject = "New Sub Task Assignment ";
			body = "<!DOCTYPE html>"
			    + "<html><head><meta charset='UTF-8'><title>New Sub Task Assigned</title></head>"
			    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
			    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
			    + "<tr><td align='center'>"
			    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
			    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
			    + "<h2 style='margin: 0;'>Task Management</h2>"
			    + "</td></tr>"
			    + "<tr><td style='padding: 30px; color: #333;'>"
			    + "<p style='font-size: 16px;'>Hi Team,</p>"
			    + "<p style='font-size: 14px;'>A new Sub Task has been created and assigned to you. Please find the Sub Task details below:</p>"
			    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Ticket ID:</td>" 
			    + "<td style='white-space: normal;'>" + subTaskDetails.getTicketid() + "</td></tr>"
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Sub Task Name:</td>" 
			    + "<td style='white-space: normal;'>" + subtask.getSubTaskName() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Status:</td><td>" + subtask.getStatus() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Priority:</td><td>" + subtask.getPriority() + "</td></tr>"
				+ "<tr><td style='font-weight: bold; vertical-align: top;'>Description:</td>" 
				+ "<td style='width: 600px; white-space: normal; '>" + subtask.getSubTaskDescription() + "</td></tr>" 
				+ "<tr><td style='font-weight: bold;'>Created By:</td><td>" + createdBy + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Start Date :</td><td>" + formattedStartDate + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Target Date :</td><td>" +formattedTargetDate + "</td></tr>"
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Assigned Users:</td>"
			    + "<td style='white-space: normal;'>" + users + "</td></tr>"	   
			    + "</table>"
			    + "<p style='font-size: 14px; margin-top: 20px;'>Please log in to the portal to begin your work.</p>"
			    + "</td></tr>"
			    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
			    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management Team</p>"
			    + "</td></tr>"
     		    + "</table></td></tr></table></body></html>";

		} else {	
			subject = "Sub Task  Updated  Notification For: " + subtask.getSubTaskName();
			body = "<!DOCTYPE html>"
			    + "<html><head><meta charset='UTF-8'><title>Sub Task Updated</title></head>"
			    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
			    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
			    + "<tr><td align='center'>"
			    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
			    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
			    + "<h2 style='margin: 0;'>Narvee Technologies</h2>"
			    + "</td></tr>"
			    + "<tr><td style='padding: 30px; color: #333;'>"
			    + "<p style='font-size: 16px;'>Hi Team ,</p>"
			    + "<p style='font-size: 15px;'>The Sub Task has been updated. Please find the updated details below:</p>"
			    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
			    + "<tr><td style='font-weight: bold;'>Ticket ID:</td><td>" + subTaskDetails.getTicketid() + "</td></tr>"
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Sub Task Name:</td>" 
			    + "<td style='white-space: normal;'>" + subtask.getSubTaskName() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Status:</td><td>" + subtask.getStatus() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Priority:</td><td>" + subtask.getPriority() + "</td></tr>"
			    + "<tr><td style='font-weight: bold; vertical-align: top;'>Description:</td>" 
				+ "<td style='width: 600px; white-space: normal; '>" + subtask.getSubTaskDescription() + "</td></tr>" 
			    + "<tr><td style='font-weight: bold;'>Updated By:</td><td>" + createdBy + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Start Date :</td><td>" + formattedStartDate + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Target Date :</td><td>" +formattedTargetDate + "</td></tr>"
			    + "<tr><td style='font-weight: bold; white-space: nowrap; vertical-align: top;'>Assigned Users:</td>"
			    + "<td style='white-space: normal;'>" + users + "</td></tr>"
			    + "</table>"
			    + "<p style='font-size: 14px; margin-top: 20px;'>Please check the portal for updated information.</p>"
			    + "</td></tr>"
			    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
			    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management System Team</p>"
			    + "</td></tr>"
			    + "</table></td></tr></table></body></html>";

		}
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(message);
		logger.info("!!! inside class: EmailServiceImpl, !! method: End sendSubTaskCreationEmail");
	}
	
	
	//----------------------send comment email for task --------------------------
	@Async
	public void sendTmsCommentEmail(UpdateTask updateTask , boolean task) throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: EmailServiceImpl, !! method:  sendCommentEmail");
		List<GetUsersDTO> userdetails = null;
		String createdByDetails = null;
		 String SubTaskName = null;
		if (updateTask.getTaskid() == null) {
			userdetails = subTaskRepository.getSubtaskAssignUsersTms(updateTask.getSubTaskId());

			createdByDetails = userdetails.stream().filter(user -> user.getCemail() != null).map(GetUsersDTO::getCemail)
					.findFirst().orElse("No Email found");

			userdetails = userdetails.stream().filter(user -> user.getEmail() != null).collect(Collectors.toList());
         SubTaskName = subTaskRepository.getSubTaskName(updateTask.getSubTaskId());
			 
		} else {
			userdetails = taskRepository.getTmsAssignUsers(updateTask.getTaskid());
			createdByDetails = userdetails.stream().filter(user -> user.getCemail() != null).map(GetUsersDTO::getCemail)
					.findFirst().orElse("No Email found");
			userdetails = userdetails.stream().filter(user -> user.getEmail() != null).collect(Collectors.toList());
			 SubTaskName = taskRepository.getTaskName(updateTask.getTaskid());
		}
		GetUsersDTO getUsersDTO = taskRepository.gettmsUser(updateTask.getUpdatedby());

		StringBuilder users = new StringBuilder();
		int i = 0;
		String emails[] = new String[userdetails.size() + 1];

		for (GetUsersDTO userDTO : userdetails) {
			if (i != 0) {
				users.append(", ");
			}

			users.append(userDTO.getFullname());
			emails[i] = userDTO.getEmail();
			i++;

		}
		emails[i] = createdByDetails;

		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		String[] uniqueEmails = emailSet.toArray(new String[0]);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(uniqueEmails);
		helper.setFrom(narveemail, shortMessage);
		String subject = "Task Comment Added: " + updateTask.getTicketid();
		String body;
		if(task) {
	      body  = "<!DOCTYPE html>"
			    + "<html><head><meta charset='UTF-8'><title>New Comment</title></head>"
			    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
			    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
			    + "<tr><td align='center'>"
			    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
			    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
			    + "<h2 style='margin: 0;'>Task Management</h2>"
			    + "</td></tr>"
			    + "<tr><td style='padding: 30px; color: #333;'>"
			    + "<p style='font-size: 16px;'>Hi Team ,</p>"
			    + "<p style='font-size: 15px;'>A new comment has been added to your Task. Please see the details below: </p>"
			    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
			    + "<tr><td style='font-weight: bold;'>Ticket ID:</td><td>" +  updateTask.getTicketid()+ "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Task Name:</td><td>" + SubTaskName + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'> Task Status:</td><td>" + updateTask.getStatus() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Comment:</td><td>" + updateTask.getComments() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Commented By:</td><td>" + getUsersDTO.getFullname() + "</td></tr>"
			    + "</table>"
			    + "</td></tr>"
			    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
			    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management Team</p>"
			    + "</td></tr>"
			    + "</table></td></tr></table></body></html>";
		}else {
			 body  = "<!DOCTYPE html>"
					    + "<html><head><meta charset='UTF-8'><title>New Comment</title></head>"
					    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
					    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
					    + "<tr><td align='center'>"
					    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
					    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
					    + "<h2 style='margin: 0;'>Task Management</h2>"
					    + "</td></tr>"
					    + "<tr><td style='padding: 30px; color: #333;'>"
					    + "<p style='font-size: 16px;'>Hi Team ,</p>"
					    + "<p style='font-size: 15px;'>A new comment has been added to your Sub Task. Please see the details below: </p>"
					    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
					    + "<tr><td style='font-weight: bold;'>Ticket ID:</td><td>" +  updateTask.getTicketid()+ "</td></tr>"
					    + "<tr><td style='font-weight: bold;'>Sub Task Name:</td><td>" + SubTaskName + "</td></tr>"
					    + "<tr><td style='font-weight: bold;'> Sub Task Status:</td><td>" + updateTask.getStatus() + "</td></tr>"
					    + "<tr><td style='font-weight: bold;'>Comment:</td><td>" + updateTask.getComments() + "</td></tr>"
					    + "<tr><td style='font-weight: bold;'>Commented By:</td><td>" + getUsersDTO.getFullname() + "</td></tr>"
					    + "</table>"
					    + "</td></tr>"
					    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
					    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management Team</p>"
					    + "</td></tr>"
					    + "</table></td></tr></table></body></html>";
		}
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(message);
		logger.info("!!! inside class: EmailServiceImpl, !! method: End sendCommentEmail");
	}

	
	// ---- send email  when udated status of task in task management -------------
	@Async
	public void sendStatusUpdateEmail(TmsTask task) throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: sendStatusUpdateEmail");

		List<GetUsersDTO> userdetails = taskRepository.getTmsAssignUsers(task.getTaskid());
		GetUsersDTO getUsersDTO = taskRepository.gettmsUser(task.getUpdatedby());
		
		String createdByDetails = userdetails.stream().filter(user -> user.getCemail() != null)
				.map(GetUsersDTO::getCemail).findFirst().orElse("No Email found");
		userdetails = userdetails.stream().filter(user -> user.getEmail() != null).collect(Collectors.toList());

		StringBuilder users = new StringBuilder();
		int i = 0;
		String emails[] = new String[userdetails.size()+1];
		for (GetUsersDTO userDTO : userdetails) {
			if (i != 0) {
				users.append(", ");
			}
			if (userDTO.getEmail() != null) {
				users.append(userDTO.getFullname());
				emails[i] = userDTO.getEmail();

			}
			i++;

		}
		
		emails[i] = createdByDetails;

		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		System.err.println("emailSet "+emailSet);
		String[] uniqueEmails = emailSet.toArray(new String[0]);
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(uniqueEmails);
		System.err.println(uniqueEmails.toString());
//		helper.setCc(ccmail);
		helper.setFrom(narveemail, shortMessage);

		String subject = "Task Status Updated: " + task.getTaskname();
		
		String	body = "<!DOCTYPE html>"
			    + "<html><head><meta charset='UTF-8'><title>New Comment</title></head>"
			    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
			    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
			    + "<tr><td align='center'>"
			    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
			    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
			    + "<h2 style='margin: 0;'>Task Management</h2>"
			    + "</td></tr>"
			    + "<tr><td style='padding: 30px; color: #333;'>"
			    + "<p style='font-size: 16px;'>Hi Team ,</p>"
			    + "<p style='font-size: 15px;'>The status of the task has been updated: </p>"
			    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
			    + "<tr><td style='font-weight: bold;'>Ticket ID:</td><td>" +  task.getTicketid()+ "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Status:</td><td>" + task.getStatus() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Task Name:</td><td>" + task.getTaskname() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Updated By:</td><td>" + getUsersDTO.getFullname() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Updated date And Time:</td><td>" + task.getUpdateddate() .format(dateTimeFormatter)+ "</td></tr>"
			    + "</table>"
			    + "</td></tr>"
			    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
			    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management Team</p>"
			    + "</td></tr>"
			    + "</table></td></tr></table></body></html>";
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(message);
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: End sendStatusUpdateEmail");
	}
	
	
	// ---------------------- send sub task Status Update email------------
	@Async
	public void sendSubtaskStatusUpdateEmailTms(TmsSubTask subTask) throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: EmailServiceIml, !! method: End sendSubtaskEmail");
		List<GetUsersDTO> userdetails = subTaskRepository.getSubtaskAssignUsersTms(subTask.getSubTaskId());
		userdetails = userdetails.stream().filter(user -> user.getEmail() != null).collect(Collectors.toList());

		GetUsersDTO getUsersDTO = taskRepository.gettmsUser(subTask.getUpdatedBy());

		StringBuilder users = new StringBuilder();
		int i = 0;
		String emails[] = new String[userdetails.size()];

		for (GetUsersDTO userDTO : userdetails) {
			if (i != 0) {
				users.append(", ");
			}
			//users.append(userDTO.getPseudoname());
			users.append(userDTO.getFullname());
			emails[i] = userDTO.getEmail();
			i++;

		}

		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));

		String[] uniqueEmails = emailSet.toArray(new String[0]);

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");
		
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(narveemail, shortMessage);
		helper.setTo(uniqueEmails);
//		helper.setCc(ccmail);

		String subject = "SubTask Status Updated: " + subTask.getSubTaskName();

		String	body = "<!DOCTYPE html>"
			    + "<html><head><meta charset='UTF-8'><title>New Comment</title></head>"
			    + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>"
			    + "<table width='100%' cellpadding='0' cellspacing='0' style='padding: 30px 0;'>"
			    + "<tr><td align='center'>"
			    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 6px;'>"
			    + "<tr><td style='background-color: #0468b4; padding: 20px; color: #ffffff; text-align: center; border-top-left-radius: 6px; border-top-right-radius: 6px;'>"
			    + "<h2 style='margin: 0;'>Task Management</h2>"
			    + "</td></tr>"
			    + "<tr><td style='padding: 30px; color: #333;'>"
			    + "<p style='font-size: 16px;'>Hi Team ,</p>"
			    + "<p style='font-size: 15px;'>The status of the  Sub Task has been updated: </p>"
			    + "<table cellpadding='6' cellspacing='0' style='font-size: 14px;'>"
			    + "<tr><td style='font-weight: bold;'>Sub Task Name:</td><td>" + subTask.getSubTaskName() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Status:</td><td>" + subTask.getStatus() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Updated By:</td><td>" + getUsersDTO.getFullname() + "</td></tr>"
			    + "<tr><td style='font-weight: bold;'>Updated date And Time:</td><td>" + subTask.getUpdateddate().format(dateTimeFormatter)+ "</td></tr>"
			    + "</table>"
			    + "</td></tr>"
			    + "<tr><td style='background-color: #f0f0f0; padding: 20px; text-align: center; color: #555; border-bottom-left-radius: 6px; border-bottom-right-radius: 6px;'>"
			    + "<p style='margin: 0; font-size: 13px;'>Best Regards,<br/>Task Management Team</p>"
			    + "</td></tr>"
			    + "</table></td></tr></table></body></html>";
		helper.setSubject(subject);
		helper.setText(body.toString(), true);
		mailSender.send(message);

		logger.info("!!! inside class: EmailServiceImpl, !! method: End sendStatusUpdateSubtaskEmail");
	}
	
}
