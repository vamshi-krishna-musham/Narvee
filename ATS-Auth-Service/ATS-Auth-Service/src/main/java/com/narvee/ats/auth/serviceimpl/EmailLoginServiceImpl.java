package com.narvee.ats.auth.serviceimpl;

import java.io.UnsupportedEncodingException;

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

import com.narvee.ats.auth.dto.EmailVerificationDTO;
import com.narvee.ats.auth.dto.ResetPassword;
import com.narvee.ats.auth.service.EmailLoginService;



@Service
public class EmailLoginServiceImpl implements EmailLoginService {
	
	private static final Logger logger = LoggerFactory.getLogger(EmailLoginServiceImpl.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${frommail}")
	private String narveemail;
	
	@Value("${TmsLoginUrl}")
	private String TmsLoginUrl;
	
	

	@Override
	public void sendAtsLoginOtp(EmailVerificationDTO emailVerificationDTO) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom(narveemail, "Login Verification Code");
			helper.setTo(emailVerificationDTO.getEmail());

			String subject = "Login Verification Code";
			StringBuilder sb = new StringBuilder();
			
			sb.append("<!DOCTYPE html>");
			sb.append("<html>");
			sb.append("<head><meta charset='UTF-8'></head>");
			sb.append("<body style='margin:0; padding:0; font-family:Arial, sans-serif; background-color:#f4f4f4;'>");

			sb.append("<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f4; padding:20px;'>");
			sb.append("<tr><td align='center'>");

			sb.append("<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:8px; overflow:hidden;'>");

			// Header
			sb.append("<tr><td style='background-color:#00466a; padding:20px; text-align:center;'>");
			sb.append("<h1 style='color:#ffffff; font-size:24px; margin:0;'>Verify Your Login</h1>");
			sb.append("</td></tr>");

			// Company Name
			sb.append("<tr><td style='padding:20px 40px 10px 40px; color:#00466a; font-size:18px;'>");
			sb.append("<p style='margin:0;'><strong>Singular Analysts Inc</strong></p>");
			sb.append("</td></tr>");

			// Greeting & Message
			sb.append("<tr><td style='padding:0 40px 20px 40px; color:#333333; font-size:15px;'>");
			sb.append("<p style='margin:0;'>Hello " + emailVerificationDTO.getUserName() + "</p>");
			sb.append("<p style='margin:10px 0 0 0;'>Thank you for choosing Singular. Use the following OTP to complete your Sign-In</p>");
			sb.append("</td></tr>");

			// OTP Box
			sb.append("<tr><td style='padding:10px 40px;'>");
			sb.append("<table width='100%' cellpadding='0' cellspacing='0'>");
			sb.append("<tr>");
			sb.append("<td align='center' style='background-color:#f1f1f1; padding:15px; font-size:24px; font-weight:bold; color:#5e3bea; border-radius:5px;'>");
			sb.append(emailVerificationDTO.getOtp());
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</td></tr>");

			// Note
			sb.append("<tr><td style='padding:20px 40px; color:#333333; font-size:13px;'>");
			sb.append("<p style='margin:0;'>This OTP is valid for <strong>2 minutes</strong>. Please do not share this code with anyone.</p>");
			sb.append("</td></tr>");
			
			sb.append("<tr><td style='padding:10px 40px 20px 40px; color:#333333; font-size:14px;'>");
			sb.append("<p style='margin:0;'>Best Regards,</p>");
			sb.append("<p style='margin:0;'>Singular Analysts Inc</p>");
			sb.append("</td></tr>");

			// Footer
			sb.append("<tr><td style='background-color:#f4f4f4; text-align:center; padding:20px; font-size:12px; color:#888888;'>");
			sb.append("<p style='margin:0;'>Singular Analysts Inc</p>");
			sb.append("<p style='margin:0;'>17440 Dallas Pkwy #250,</p>");
			sb.append("<p style='margin:0;'>Dallas, TX 75287 USA</p>");
			sb.append("</td></tr>");

			sb.append("</table>"); // End inner table
			sb.append("</td></tr>");
			sb.append("</table>"); // End outer table

			sb.append("</body>");
			sb.append("</html>");

			helper.setSubject(subject);
			helper.setCc(new String[] {}); // No admin CC in this case
			helper.setText(sb.toString(), true); // true means HTML content

		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}

		mailSender.send(message);
	}
		
	
	@Override
	@Async
	public void sendTmsLoginOtp(EmailVerificationDTO emailVerificationDTO) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom(narveemail, "Login Verification Code");
			helper.setTo(emailVerificationDTO.getEmail());

			String subject = "Login Verification Code";
			StringBuilder sb = new StringBuilder();
			
			sb.append("<!DOCTYPE html>");
			sb.append("<html>");
			sb.append("<head><meta charset='UTF-8'></head>");
			sb.append("<body style='margin:0; padding:0; font-family:Arial, sans-serif; background-color:#f4f4f4;'>");

			sb.append("<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f4; padding:20px;'>");
			sb.append("<tr><td align='center'>");

			sb.append("<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:8px; overflow:hidden;'>");

			// Header
			sb.append("<tr><td style='background-color:#00466a; padding:20px; text-align:center;'>");
			sb.append("<h1 style='color:#ffffff; font-size:24px; margin:0;'>Verify Your Login</h1>");
			sb.append("</td></tr>");

			// Company Name
			sb.append("<tr><td style='padding:20px 40px 10px 40px; color:#00466a; font-size:18px;'>");
			sb.append("<p style='margin:0;'><strong> Task Management System </strong></p>");
			sb.append("</td></tr>");

			// Greeting & Message
			sb.append("<tr><td style='padding:0 40px 20px 40px; color:#333333; font-size:15px;'>");
			sb.append("<p style='margin:0;'>Hello " + emailVerificationDTO.getUserName() + "</p>");
			sb.append("<p style='margin:10px 0 0 0;'>Thank you for choosing Task Management System,Use the following OTP to complete your Sign-In</p>");
			sb.append("</td></tr>");

			// OTP Box
			sb.append("<tr><td style='padding:10px 40px;'>");
			sb.append("<table width='100%' cellpadding='0' cellspacing='0'>");
			sb.append("<tr>");
			sb.append("<td align='center' style='background-color:#f1f1f1; padding:15px; font-size:24px; font-weight:bold; color:#5e3bea; border-radius:5px;'>");
			sb.append(emailVerificationDTO.getOtp());
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</td></tr>");

			// Note
			sb.append("<tr><td style='padding:20px 40px; color:#333333; font-size:13px;'>");
			sb.append("<p style='margin:0;'>This OTP is valid for <strong>2 minutes</strong>. Please do not share this code with anyone.</p>");
			sb.append("</td></tr>");
			
			sb.append("<tr><td style='padding:10px 40px 20px 40px; color:#333333; font-size:14px;'>");
			sb.append("<p style='margin:0;'>Best Regards,</p>");
			sb.append("<p style='margin:0;'>Task Management System Team! </p>");
			sb.append("</td></tr>");

			// Footer
			sb.append("<tr><td style='background-color:#f4f4f4; text-align:center; padding:20px; font-size:12px; color:#888888;'>");
			sb.append("<p style='margin:0;'>Task Management System </p>");
			sb.append("</td></tr>");

			sb.append("</table>"); // End inner table
			sb.append("</td></tr>");
			sb.append("</table>"); // End outer table

			sb.append("</body>");
			sb.append("</html>");

			helper.setSubject(subject);
			helper.setCc(new String[] {}); // No admin CC in this case
			helper.setText(sb.toString(), true); // true means HTML content

		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}

		mailSender.send(message);
	}


	@Override
	public void sendAtsForgotPassword(ResetPassword resetPassword) {
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom(narveemail, "Reset Password");
			helper.setTo(resetPassword.getEmail());
//			helper.setCc(adminccmail); //
			String subject = "One Time Password ";
			StringBuilder sb = new StringBuilder();
			
			sb.append("<!DOCTYPE html>");
			sb.append("<html>");
			sb.append("<head><meta charset='UTF-8'></head>");
			sb.append("<body style='margin:0; padding:0; font-family:Arial, sans-serif; background-color:#f4f4f4;'>");

			sb.append("<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f4; padding:20px;'>");
			sb.append("<tr><td align='center'>");

			sb.append("<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:8px; overflow:hidden;'>");

			// Header
			sb.append("<tr><td style='background-color:#00466a; padding:20px; text-align:center;'>");
			sb.append("<h1 style='color:#ffffff; font-size:24px; margin:0;'>Reset Password</h1>");
			sb.append("</td></tr>");

			// Company Name
			sb.append("<tr><td style='padding:20px 40px 10px 40px; color:#00466a; font-size:18px;'>");
			sb.append("<p style='margin:0;'><strong>Singular Analysts Inc </strong></p>");
			sb.append("</td></tr>");

			// Greeting & Message
			sb.append("<tr><td style='padding:0 40px 20px 40px; color:#333333; font-size:15px;'>");
			sb.append("<p style='margin:0;'>Hello " + resetPassword.getUsername()+ "</p>");
			sb.append("<p style='margin:10px 0 0 0;'>Use the following OTP to complete your password reset</p>");
			sb.append("</td></tr>");

			// OTP Box
			sb.append("<tr><td style='padding:10px 40px;'>");
			sb.append("<table width='100%' cellpadding='0' cellspacing='0'>");
			sb.append("<tr>");
			sb.append("<td align='center' style='background-color:#f1f1f1; padding:15px; font-size:24px; font-weight:bold; color:#5e3bea; border-radius:5px;'>");
			sb.append(resetPassword.getOtp());
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</td></tr>");

			// Note
			sb.append("<tr><td style='padding:20px 40px; color:#333333; font-size:13px;'>");
			sb.append("<p style='margin:0;'>This OTP is valid for <strong>2 minutes</strong>. Please do not share this code with anyone.</p>");
			sb.append("</td></tr>");
			
			sb.append("<tr><td style='padding:10px 40px 20px 40px; color:#333333; font-size:14px;'>");
			sb.append("<p style='margin:0;'>Best Regards,</p>");
			sb.append("<p style='margin:0;'>Singular Analysts Inc </p>");
			sb.append("</td></tr>");

			// Footer
			sb.append("<tr><td style='background-color:#f4f4f4; text-align:center; padding:20px; font-size:12px; color:#888888;'>");
			sb.append("<p style='margin:0;'>Singular Analysts Inc</p>");
			sb.append("<p style='margin:0;'>17440 Dallas Pkwy #250,</p>");
			sb.append("<p style='margin:0;'>Dallas, TX 75287 USA</p>");
			sb.append("</td></tr>");

			sb.append("</table>"); // End inner table
			sb.append("</td></tr>");
			sb.append("</table>"); // End outer table

			sb.append("</body>");
			sb.append("</html>");

			helper.setSubject(subject);
			helper.setBcc(new String[] { "shanpasha@narveetech.com" });
			helper.setText(sb.toString(), true);

		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}

		mailSender.send(message);
	}


	@Override
	public void sendTmsForgotPassword(ResetPassword resetPassword) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom(narveemail, "Reset Password");
			helper.setTo(resetPassword.getEmail());
//			helper.setCc(adminccmail); //
			String subject = "One Time Password ";
			StringBuilder sb = new StringBuilder();
			
			sb.append("<!DOCTYPE html>");
			sb.append("<html>");
			sb.append("<head><meta charset='UTF-8'></head>");
			sb.append("<body style='margin:0; padding:0; font-family:Arial, sans-serif; background-color:#f4f4f4;'>");

			sb.append("<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f4; padding:20px;'>");
			sb.append("<tr><td align='center'>");

			sb.append("<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:8px; overflow:hidden;'>");

			// Header
			sb.append("<tr><td style='background-color:#00466a; padding:20px; text-align:center;'>");
			sb.append("<h1 style='color:#ffffff; font-size:24px; margin:0;'>Reset Password</h1>");
			sb.append("</td></tr>");

			// Company Name
			sb.append("<tr><td style='padding:20px 40px 10px 40px; color:#00466a; font-size:18px;'>");
			sb.append("<p style='margin:0;'><strong>Task Management System </strong></p>");
			sb.append("</td></tr>");

			// Greeting & Message
			sb.append("<tr><td style='padding:0 40px 20px 40px; color:#333333; font-size:15px;'>");
			sb.append("<p style='margin:0;'>Hello " + resetPassword.getUsername()+  "</p>");
			sb.append("<p style='margin:10px 0 0 0;'>Use the following OTP to complete your password reset</p>");
			sb.append("</td></tr>");

			// OTP Box
			sb.append("<tr><td style='padding:10px 40px;'>");
			sb.append("<table width='100%' cellpadding='0' cellspacing='0'>");
			sb.append("<tr>");
			sb.append("<td align='center' style='background-color:#f1f1f1; padding:15px; font-size:24px; font-weight:bold; color:#5e3bea; border-radius:5px;'>");
			sb.append(resetPassword.getOtp());
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</td></tr>");

			// Note
			sb.append("<tr><td style='padding:20px 40px; color:#333333; font-size:13px;'>");
			sb.append("<p style='margin:0;'>This OTP is valid for <strong>2 minutes</strong>. Please do not share this code with anyone.</p>");
			sb.append("</td></tr>");
			
			sb.append("<tr><td style='padding:10px 40px 20px 40px; color:#333333; font-size:14px;'>");
			sb.append("<p style='margin:0;'>Best Regards,</p>");
			sb.append("<p style='margin:0;'>Task Management System Team</p>");
			sb.append("</td></tr>");

			// Footer
			sb.append("<tr><td style='background-color:#f4f4f4; text-align:center; padding:20px; font-size:12px; color:#888888;'>");
			sb.append("<p style='margin:0;'>Task Management System</p>");
		
			sb.append("</td></tr>");

			sb.append("</table>"); // End inner table
			sb.append("</td></tr>");
			sb.append("</table>"); // End outer table

			sb.append("</body>");
			sb.append("</html>");

			helper.setSubject(subject);
			helper.setBcc(new String[] { "shanpasha@narveetech.com" });
			helper.setText(sb.toString(), true);

		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}

		mailSender.send(message);
	}


	@Override
	public void sendAtsChangePasswordEmail(ResetPassword passwordDTO) {
		
		logger.info("!!! inside class: EmailLoginServiceImpl, !! method: sendAtsChangePasswordEmail ");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom(narveemail, "Change Password");
			helper.setTo(passwordDTO.getEmail());
//			helper.setCc(adminccmail); //
			String subject = " Change  Password ";
			StringBuilder content = new StringBuilder();
			content.append(
				    "<html>" +
				    "<head>" +
				    "<title>Reset Password Email</title>" +
				    "</head>" +
				    "<body style=\"margin:0; padding:0; font-family: Helvetica, Arial, sans-serif; background-color: #f4f4f4;\">" +
				    "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color: #f4f4f4; padding: 30px 0;\">" +
				    "  <tr>" +
				    "    <td align=\"center\">" +
				    "      <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">" +
				    "        <tr>" +
				    "          <td align=\"center\" style=\"padding: 20px 0; font-size: 24px; font-weight: bold; color: #0468b4;\">" +
				    "            Singular Analysts Inc" +
				    "          </td>" +
				    "        </tr>" +
				    "        <tr>" +
				    "          <td style=\"padding: 20px; font-size: 16px; color: #333333;\">" +
				    "            <p>Hi " + passwordDTO.getUsername() + ",</p>" +
				    "            <p>Your password has been changed successfully.</p>" +
				    "            <p style=\"margin: 20px 0;  color: #000000; display: inline-block; padding: 10px 20px; font-size: 16px; border-radius: 4px;\">" +
				    "            You can now log in to your portal and continue your work seamlessly." +
				    "            </p>" +
				    "            <p>Regards,<br/>Singular Analysts Inc</p>" +
				    "          </td>" +
				    "        </tr>" +
				    "        <tr>" +
				    "          <td style=\"padding: 10px; font-size: 12px; color: #999999; text-align: center;\">" +
				    "            <hr style=\"border:none; border-top:1px solid #eeeeee;\">" +
				    "            This is an automated message. Please do not reply." +
				    "          </td>" +
				    "        </tr>" +
				    "      </table>" +
				    "    </td>" +
				    "  </tr>" +
				    "</table>" +
				    "</body>" +
				    "</html>"
				);

			helper.setSubject(subject);
//			helper.setCc(new String[] { "shanpasha@narveetech.com"});
			helper.setText(content.toString(), true);

		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}

		mailSender.send(message);
		
	}


	@Override
	public void sendTmsChangePassword(ResetPassword passwordDTO) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom(narveemail, "Change Password");
			helper.setTo(passwordDTO.getEmail());
//			helper.setCc(adminccmail); //
			String subject = " Change  Password ";
			StringBuilder content = new StringBuilder();
			content.append(
				    "<html>" +
				    "<head>" +
				    "<title>change Password Email-TMS</title>" +
				    "</head>" +
				    "<body style=\"margin:0; padding:0; font-family: Helvetica, Arial, sans-serif; background-color: #f4f4f4;\">" +
				    "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color: #f4f4f4; padding: 30px 0;\">" +
				    "  <tr>" +
				    "    <td align=\"center\">" +
				    "      <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">" +
				    "        <tr>" +
				    "          <td align=\"center\" style=\"padding: 20px 0; font-size: 24px; font-weight: bold; color: #0468b4;\">" +
				    "            Task Management " +
				    "          </td>" +
				    "        </tr>" +
				    "        <tr>" +
				    "          <td style=\"padding: 20px; font-size: 16px; color: #333333;\">" +
				    "            <p>Hi " + passwordDTO.getUsername() + ",</p>" +
				    "            <p>Your password has been changed successfully.</p>" +
				    "            <p style=\"margin: 20px 0;  color: #000000; display: inline-block; padding: 10px 20px; font-size: 16px; border-radius: 4px;\">" +
				    "              You can now log in to your portal and continue your work seamlessly." +
				    "            </p>" +
				    "            <p>Regards,<br/>Task Management Team </p>" +
				    "          </td>" +
				    "        </tr>" +
				    "        <tr>" +
				    "          <td style=\"padding: 10px; font-size: 12px; color: #999999; text-align: center;\">" +
				    "            <hr style=\"border:none; border-top:1px solid #eeeeee;\">" +
				    "            This is an automated message. Please do not reply." +
				    "          </td>" +
				    "        </tr>" +
				    "      </table>" +
				    "    </td>" +
				    "  </tr>" +
				    "</table>" +
				    "</body>" +
				    "</html>"
				);

			helper.setSubject(subject);
//			helper.setCc(new String[] { "shanpasha@narveetech.com"});
			helper.setText(content.toString(), true);

		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}

		mailSender.send(message);
		
	}
		
	@Override
	public void ChangePasswordforTmsLogin(String Username) { 
		  
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message);
				
				try {
					helper.setFrom(narveemail, "Reset Password");
					helper.setTo(Username);
				String subject = "Your TMS Account is Ready to Use" ;
				String loginUrl = TmsLoginUrl;
				StringBuilder sb = new StringBuilder();			
				 
				sb.append("<!DOCTYPE html>");
				sb.append("<html><head><meta charset='UTF-8'></head>");
				sb.append("<body style='margin:0; padding:0; background-color:#f4f6f8;'>");

				sb.append("<table role='presentation' width='100%' cellpadding='0' cellspacing='0' border='0' style='background-color:#f4f6f8;'>");
				sb.append("<tr><td align='center' style='padding: 40px 0;'>");

				sb.append("<table role='presentation' width='700' cellpadding='0' cellspacing='0' border='0' style='background-color:#e3f2fd; border-radius:10px; padding:30px;'>");
				sb.append("<tr><td align='center' style='font-family: Arial, sans-serif;'>");

				sb.append("<h2 style='font-size:22px; color:#333333;'>Welcome to the Task Management System</h2>");
				sb.append("<p style='font-size:16px; color:#555;'>We’re excited to let you know that your account has been successfully created in TMS (Task Management System)</p>");
				sb.append("<p style='font-size:16px; color:#555;'>You can now log in and start managing your projects and tasks seamlessly</p>");

				sb.append("<p style='font-size:15px; color:#555;'><strong>Username:</strong> ").append(Username).append("</p>");

				sb.append("<table role='presentation' border='0' cellspacing='0' cellpadding='0' style='margin: 15px auto;'>");
				sb.append("<tr><td align='center' bgcolor='#3498db' style='border-radius: 4px;'>");
				sb.append("<a href='").append(loginUrl)
				  .append("'target='_blank' style='font-size:16px; font-family: Arial, sans-serif; color: #ffffff; text-decoration: none;  border-radius: 4px;'>Login to TMS</a>");
				sb.append("</td></tr></table>");
				sb.append("<p style='font-size:12px; color:#999999; margin-top:30px;'>If you did not expect this account creation, please contact our support team</p>");
				sb.append("<p style='color:#888;'>Best regards,<br><strong>Task Management Team</strong></p>");

				sb.append("</td></tr></table>");
				sb.append("</td></tr></table>");

				sb.append("</body></html>");


			    		    
				 helper.setSubject(subject);
					helper.setText(sb.toString(), true); // Enable HTML
					helper.setText(sb.toString(), true); // true means HTML content
	   }catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}

		mailSender.send(message);
	
	}
		
	

}
