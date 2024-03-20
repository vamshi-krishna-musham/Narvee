package com.narvee.service.serviceimpl;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.narvee.dto.UserDTO;
import com.narvee.entity.Task;
import com.narvee.feignclient.UserClient;

@Component
public class EmailServiceIml {
	private static final Logger logger = LoggerFactory.getLogger(EmailServiceIml.class);

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

	public void TaskAssigningEmail(Task task, List<UserDTO> userdetails)
			throws MessagingException, UnsupportedEncodingException {
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: TaskAssigningEmail");
		StringBuilder createdBy = new StringBuilder();
		StringBuilder users = new StringBuilder();
		int i = 0;
		String emails[] = new String[userdetails.size()];
		
		for (UserDTO userDTO : userdetails) {
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
		/*
		 * for (UserDTO userDTO : userdetails) { if (userDTO.getCreatedby() != null) {
		 * createdBy.append(userDTO.getCreatedby()); } else { if (i != 0) {
		 * users.append(","); } users.append(userDTO.getPseudoname()); } emails[i++] =
		 * userDTO.getEmail(); System.out.println(i); }
		 */
		for (String s : emails) {
			System.out.println(Arrays.toString(emails));
		}
		String q = url;
		String rooturl = "<a href='" + q + "'>Click Here to Go Task List</a>";

		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		// helper.setCc(ccmail);
		// helper.setBcc(emails);

		helper.setCc(emails);

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
				.append("</td></tr>").append("</table>").append(url).append("</body>").append("</html>");

		helper.setSubject(subject);
		helper.setText(stringBuilder.toString(), true);
		mailSender.send(message);
		logger.info("!!! inside class: TaskEmailServiceIml, !! method: End TaskAssigningEmail");
	}

}
