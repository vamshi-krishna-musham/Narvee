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
import org.springframework.stereotype.Component;

import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.entity.TmsProject;
import com.narvee.entity.TmsSubTask;
import com.narvee.entity.TmsTask;
import com.narvee.feignclient.UserClient;
import com.narvee.repository.SubTaskRepository;
import com.narvee.repository.TaskRepository;

@Component
public class EmailServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	private SubTaskRepository subTaskRepository;

	@Autowired
	private TaskRepository taskRepository;

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

	public void TaskAssigningEmail(TmsTask task, List<GetUsersDTO> userdetails)
			throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: TaskAssigningEmail");

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
				users.append(userDTO.getPseudoname());

			}
			emails[i] = userDTO.getEmail();
			i++;
		}
		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		String[] uniqueEmails = emailSet.toArray(new String[0]);
		emails = uniqueEmails;

		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
//		helper.setCc(ccmail);
		// helper.setBcc(emails);

		helper.setTo(emails);

		helper.setFrom(narveemail, shortMessage);
		String subject = "Assigned Task Info ";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<!DOCTYPE html>").append("<html>").append("<head>").append("<style>")
				.append("#customers {").append("  font-family: Arial, Helvetica, sans-serif;")
				.append("  border-collapse: collapse;").append("  width: 100%;").append("}").append("")
				.append("#customers td, #customers th {").append("  border: 1px solid #ddd;").append("  padding: 8px;")
				.append("}").append("").append("#customers tr:nth-child(even){background-color: #f2f2f2;}").append("")
				.append("#customers tr:hover {background-color: #ddd;}").append("").append("#customers th {")
				.append("padding-top: 12px;").append("  padding-bottom: 12px;").append("  text-align: left;")
				.append("background-color: #04AA6D;").append("  color: white;").append("}").append(".description{")
				.append("color:blueviolet;").append("}").append("</style>").append("</head>").append("<body>")
				.append("<table id=\"customers\">").append("  <tr>").append("<th>Ticket ID</th>")
				.append("<th>Task Name</th>").append("<th>Created By</th>").append("<th>Created Date</th>")
				.append("<th>Assigned Users</th>").append("<th>Target Date</th>").append("<th>Status</th>").append("")
				.append("</tr>").append("<tr>").append("<td>" + task.getTicketid() + "</td>")
				.append("<td>" + task.getTaskname() + "</td>").append("<td>" + createdBy + "</td>")
				.append("<td>" + task.getCreateddate().format(myFormatObj) + "</td>").append("<td>" + users + "</td>")
				.append("<td>" + task.getTargetdate() + "</td>").append("<td>" + task.getStatus() + "</td>")
				.append("</tr>").append("<tr> <th colspan=\"8\" class=\"description\">Description</th> </tr>")
				.append("<tr><td colspan=\"8\">").append("<pre>").append(task.getDescription()).append("</pre>")
				.append("</td></tr>").append("</table>").append("</body>").append("</html>");

		helper.setSubject(subject);
		helper.setText(stringBuilder.toString(), true);
		mailSender.send(message);
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: End TaskAssigningEmail");
	}

	public void SubTaskAssigningEmail(TmsSubTask subTask, List<GetUsersDTO> userdetails)
			throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: SubTaskServiceImpl, !! method: SubTaskAssigningEmail");
		GetUsersDTO projectname = subTaskRepository.GetPorjectNameAndTaskName(subTask.getSubTaskId());
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
				users.append(userDTO.getPseudoname());

			}
			emails[i] = userDTO.getEmail();
			i++;
		}
		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		String[] uniqueEmails = emailSet.toArray(new String[0]);
		emails = uniqueEmails;

		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
//		helper.setCc(ccmail);
		// helper.setBcc(emails);

		helper.setTo(emails);

		helper.setFrom(narveemail, shortMessage);
		String subject = "Assigned SubTask Info ";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<!DOCTYPE html>").append("<html>").append("<head>").append("<style>")
				.append("#customers {").append("  font-family: Arial, Helvetica, sans-serif;")
				.append("  border-collapse: collapse;").append("  width: 100%;").append("}").append("")
				.append("#customers td, #customers th {").append("  border: 1px solid #ddd;").append("  padding: 8px;")
				.append("}").append("").append("#customers tr:nth-child(even){background-color: #f2f2f2;}").append("")
				.append("#customers tr:hover {background-color: #ddd;}").append("").append("#customers th {")
				.append("padding-top: 12px;").append("  padding-bottom: 12px;").append("  text-align: left;")
				.append("background-color: #04AA6D;").append("  color: white;").append("}").append(".description{")
				.append("color:blueviolet;").append("}").append("</style>").append("</head>").append("<body>")
				.append("<table id=\"customers\">").append("  <tr>").append("<th>Ticket ID</th>")
				.append("<th>Project Name</th>").append("<th>Task Name</th>").append("<th>SubTask Name</th>")
				.append("<th>Created By</th>").append("<th>Created Date</th>").append("<th>Assigned Users</th>")
				.append("<th>Target Date</th>").append("<th>Status</th>").append("").append("</tr>").append("<tr>")
				.append("<td>" + projectname.getTicketid() + "</td>")
				.append("<td>" + projectname.getProjectname() + "</td>")
				.append("<td>" + projectname.getTaskname() + "</td>")
				.append("<td>" + subTask.getSubTaskName() + "</td>").append("<td>" + createdBy + "</td>")
				.append("<td>" + subTask.getCreateddate().format(myFormatObj) + "</td>")
				.append("<td>" + users + "</td>").append("<td>" + subTask.getTargetDate() + "</td>")
				.append("<td>" + subTask.getStatus() + "</td>").append("</tr>")
				.append("<tr> <th colspan=\"9\" class=\"description\">Description</th> </tr>")
				.append("<tr><td colspan=\"9\">").append("<pre>").append(subTask.getSubTaskDescription())
				.append("</pre>").append("</td></tr>").append("</table>").append("</body>").append("</html>");

		helper.setSubject(subject);
		helper.setText(stringBuilder.toString(), true);
		mailSender.send(message);
		logger.info("!!! inside class: SubTaskServiceImpl, !! method: End SubTaskAssigningEmail");

	}

	public void sendStatusUpdateEmail(TmsTask task) throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: sendStatusUpdateEmail");

		List<GetUsersDTO> userdetails = taskRepository.getAssignUsers(task.getTaskid());
		GetUsersDTO getUsersDTO = taskRepository.getUser(task.getUpdatedby());
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
				users.append(userDTO.getPseudoname());
				emails[i] = userDTO.getEmail();

			}
			i++;

		}
		
		emails[i] = createdByDetails;

		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		String[] uniqueEmails = emailSet.toArray(new String[0]);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(uniqueEmails);
//		helper.setCc(ccmail);
		helper.setFrom(narveemail, shortMessage);

		String subject = "Task Status Updated: " + task.getTaskname();
		String body = "<html><body>" + "<div>Hi " + users + ",</div>" + "<div>The status of the task <strong>"
				+ task.getTaskname() + "</strong> has been updated to: <strong>" + task.getStatus()
				+ "</strong> by <strong>" + getUsersDTO.getFullname() + "</strong></div>" + "<div>  Task ID: <strong>"
				+ task.getTicketid() + " </strong> </div>" + "<div>Task Name: <strong>" + task.getTaskname()
				+ "</strong></div> <br>" + "<div>Best Regards,</div>" + "<div> Narvee Technologies </div>"
				+ "</body></html>";
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(message);
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: End sendStatusUpdateEmail");
	}

	public void sendSubtaskEmail(TmsSubTask subTask) throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: EmailServiceIml, !! method: End sendSubtaskEmail");
		List<GetUsersDTO> userdetails = taskRepository.getSubtaskAssignUsers(subTask.getSubTaskId());
		userdetails = userdetails.stream().filter(user -> user.getEmail() != null).collect(Collectors.toList());

		GetUsersDTO getUsersDTO = taskRepository.getUser(subTask.getUpdatedBy());

		StringBuilder users = new StringBuilder();
		int i = 0;
		String emails[] = new String[userdetails.size()];

		for (GetUsersDTO userDTO : userdetails) {
			if (i != 0) {
				users.append(", ");
			}
			users.append(userDTO.getPseudoname());
			emails[i] = userDTO.getEmail();
			i++;

		}

		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));

		String[] uniqueEmails = emailSet.toArray(new String[0]);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(narveemail, shortMessage);
		helper.setTo(uniqueEmails);
//		helper.setCc(ccmail);

		String subject = "SubTask Status Updated: " + subTask.getSubTaskName();
		String body = "<html><body>" + "<div>Hi " + users + ",</div>" + "<div>The status of the task <strong>"
				+ subTask.getSubTaskName() + "</strong> has been updated to: <strong>" + subTask.getStatus()
				+ "</strong> by <strong>" + getUsersDTO.getFullname() + "</strong></div>" + "<div> Task ID: <strong>"
				+ subTask.getTask().getTicketid() + " </strong> </div>" + "<div>Sub-Task Name: <strong>"
				+ subTask.getSubTaskName() + "</strong></div><br>" + "<div>Best Regards,</div>"
				+ "<div> Narvee Technologies </div>" + "</body></html>";
		helper.setSubject(subject);
		helper.setText(body, true);

		mailSender.send(message);
		logger.info("!!! inside class: EmailServiceIml, !! method: End sendSubtaskEmail");
	}

	public void sendCommentEmail(UpdateTask updateTask) throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: EmailServiceImpl, !! method:  sendCommentEmail");
		List<GetUsersDTO> userdetails = null;
		String createdByDetails = null;
		if (updateTask.getTaskid() == null) {
			userdetails = taskRepository.getSubtaskAssignUsers(updateTask.getSubTaskId());

			createdByDetails = userdetails.stream().filter(user -> user.getCemail() != null).map(GetUsersDTO::getCemail)
					.findFirst().orElse("No Email found");

			userdetails = userdetails.stream().filter(user -> user.getEmail() != null).collect(Collectors.toList());

		} else {
			userdetails = taskRepository.getAssignUsers(updateTask.getTaskid());
			createdByDetails = userdetails.stream().filter(user -> user.getCemail() != null).map(GetUsersDTO::getCemail)
					.findFirst().orElse("No Email found");
			userdetails = userdetails.stream().filter(user -> user.getEmail() != null).collect(Collectors.toList());

		}
		GetUsersDTO getUsersDTO = taskRepository.getUser(updateTask.getUpdatedby());

		StringBuilder users = new StringBuilder();
		int i = 0;
		String emails[] = new String[userdetails.size() + 1];

		for (GetUsersDTO userDTO : userdetails) {
			if (i != 0) {
				users.append(", ");
			}

			users.append(userDTO.getPseudoname());
			emails[i] = userDTO.getEmail();
			i++;

		}
		emails[i] = createdByDetails;

		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		String[] uniqueEmails = emailSet.toArray(new String[0]);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(uniqueEmails);
//		helper.setCc(ccmail);
		helper.setFrom(narveemail, shortMessage);
		String subject = "Task Comment Added: " + updateTask.getTicketid();
		String body = "<html><body>" + "<div>Hi " + users + ",</div> <br>" + "<div>The Ticket  Id : <strong>"
				+ updateTask.getTicketid() + "</strong> has a new comment:</div>" + "<div><strong>Comment: </strong> "
				+ updateTask.getComments() + " <strong>Commented by: </strong> " + getUsersDTO.getFullname() + "</div>"
				+ "<div><strong>Status:</strong> " + updateTask.getStatus() + "</div>" + "<div>Ticket ID: <strong>"
				+ updateTask.getTicketid() + "</strong></div> <br>" + "<div>Best Regards,</div>"
				+ "<div>Narvee Technologies</div>" + "</body></html>";
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(message);
		logger.info("!!! inside class: EmailServiceImpl, !! method: End sendCommentEmail");
	}

	public void sendCreateProjectEmail(TmsProject project, List<GetUsersDTO> userdetails, boolean projectUpdate)
			throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: EmailServiceImpl, !! method: sendCreateProjectEmail");

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
				//assignedUsers.append(userDTO.getPseudoname());
				assignedUsers.append(userDTO.getPseudoname());  //- chnaged for full name for tms users
			}

			emails[i] = userDTO.getEmail();
			i++;
		}
		Set<String> emailSet = new HashSet<>(Arrays.asList(emails));
		String[] uniqueEmails = emailSet.toArray(new String[0]);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(uniqueEmails);
		helper.setFrom(narveemail, shortMessage);
//		helper.setCc(ccmail);
		String subject;
		String body;

		if (projectUpdate) {

			subject = "New Project Assignment:" + project.getProjectid();
			body = "<html><body>" + "<div>Hi " + assignedUsers + ",</div>" + "<br>"
					+ "<div>A new project has been created and assigned to: <strong>" + assignedUsers
					+ "</strong></div>" + "<div><strong>Project Id:</strong> " + project.getProjectid() + "</div>"
					+ "<div><strong>Project Name:</strong> " + project.getProjectName() + "</div>"
					+ "<div><strong>Description:</strong> " + project.getDescription() + "</div>"
					+ "<div><strong>Created By:</strong> " + createdby + "</div>" + "<br>" + "<div>Best Regards,</div>"
					+ "<div>Narvee Technologies.</div>" + "</body></html>";
		} else {
			subject = "Project Updated: " + project.getProjectid();
			body = "<html><body>" + "<div>Hi " + assignedUsers + ",</div>" + "<br>"
					+ "<div>The project has been updated:</div>" + "<div><strong>Project Id:</strong> "
					+ project.getProjectid() + "</div>" + "<div><strong>Project Name:</strong> "
					+ project.getProjectName() + "</div>" + "<div><strong>Description:</strong> "
					+ project.getDescription() + "</div>" + "<div><strong>Updated By: </strong> " + createdby + "</div>"
					+ "<br>" + "<div>Best Regards,</div>" + "<div>Narvee Technologies.</div>" + "</body></html>";

		}
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(message);
		logger.info("!!! inside class: EmailServiceImpl, !! method: End sendCreateProjectEmail");

	}

	public void targetExceededEmail(TaskTrackerDTO task, String taskType)
			throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: targetExceededEmail");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(task.getEmail());
//		helper.setCc(ccmail);
		helper.setFrom(narveemail, shortMessage);
		String subject = null;

		StringBuilder body = new StringBuilder();

		body.append("<html><body>" + "<div>Hi " + task.getPseudoname() + ",</div> <br>"
				+ "<div>I hope this email finds you well.</div> <br>"
				+ "<div>I wanted to bring to your attention that your assigned task target has been exceeded. "
				+ "Please review the progress and ensure that any necessary adjustments or actions are taken to bring things back on track.</div> <br>"
				+ "</strong></div>" + "<div><strong>Ticket Id:</strong> " + task.getTicketid() + "</div>");
		if (taskType.equalsIgnoreCase("subtask")) {
			subject = "Task Target Exceeded " + task.getSubtaskname();
			body.append("<div><strong>Sub-Task Name:</strong> " + task.getSubtaskname() + "</div>");
		} else {
			subject = "Task Target Exceeded " + task.getTaskName();
			body.append("<div><strong>Task Name:</strong> " + task.getTaskName() + "</div>");
		}
		body.append("<div><strong style='color: red;'>Target Date:</strong> " + task.getTargetdate() + "</div>");

		body.append(
				"If there are any challenges or issues preventing completion within the specified time frame, feel free to reach out so we can discuss potential solutions.<br>"
						+ "Thank you for your attention to this matter. <br>" + "</strong></div> <br>"
						+ "<div>Best Regards,</div>" + "<div> Narvee Technologies </div>" + "</body></html>");
		helper.setSubject(subject);
		helper.setText(body.toString(), true);
		mailSender.send(message);
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: End targetExceededEmail end");
	}

}
