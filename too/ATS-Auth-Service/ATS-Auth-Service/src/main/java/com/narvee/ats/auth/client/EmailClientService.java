package com.narvee.ats.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


import com.narvee.ats.auth.dto.EmailVerificationDTO;
import com.narvee.ats.auth.dto.ResetPassword;

import com.narvee.ats.auth.entity.Users;




@FeignClient("EMAIL-SERVICE")
public interface EmailClientService {

	@GetMapping("/mail/userRegistrationMail/{email}/{username}")
    public ResponseEntity<String> employeeRegistarionMail(@PathVariable String email ,@PathVariable String username, @RequestHeader("AUTHORIZATION") String token);

	@PostMapping("/mail/resetPasswordEmailLink")
	public void resetlinkmail(@RequestBody ResetPassword resetPassword);
	
	@PostMapping("/mail/changePassword")
	public void changeUserPassword(@RequestBody ResetPassword resetPassword);

	@PostMapping("/mail/emailVerification")
	public void emailVerification(@RequestBody ResetPassword resetPassword);
	
	@PostMapping("/mail/consultantRegistrationMail")
    public ResponseEntity<String> consultantRegistarion(Users users);
	
	 @PostMapping("/mail/login")
	 public ResponseEntity<String> login(@RequestBody EmailVerificationDTO emailVerificationDTO);
	 
	 @PostMapping("/mail/ChangePasswordTmsUser/{userName}/{Password}")
	 public ResponseEntity<String> ChangePasswordForTmsUser(@PathVariable String userName,@PathVariable String Password );


}
