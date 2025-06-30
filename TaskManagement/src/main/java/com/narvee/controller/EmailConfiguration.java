package com.narvee.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.commons.RestAPIResponse;
import com.narvee.dto.TmsEmailConfigurationDto;
import com.narvee.entity.TmsEmailConfiguration;
import com.narvee.service.service.EmailConfigurationService;

@RestController
@RequestMapping("/EmailConfiguration")
public class EmailConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(EmailConfiguration.class);
	
	@Autowired
	private EmailConfigurationService configurationService;
	
	
     
	@PostMapping("/saveEmailNotifications")
	   public ResponseEntity<RestAPIResponse> saveEmailConfiguration(@RequestBody List<TmsEmailConfigurationDto> configurationDto){
		   logger.info("!!! inside class: EmailConfiguration , !! method: saveEmailConfiguration");	   
		   Map<String, Object> resultMap = configurationService.saveEmailConfiguration(configurationDto);
		    String message = "Email configuration saved successfully.";
		    List<String> duplicates = (List<String>) resultMap.get("duplicates");
		    if (!duplicates.isEmpty()) {
		        message += " Skipped duplicates: " + String.join("; ", duplicates);
		        return new ResponseEntity<>( new RestAPIResponse("success", message, resultMap),
				        HttpStatus.CREATED );
		    }else {
		    	return new ResponseEntity<>( new RestAPIResponse("failed", "Failed to add the Email configuration"),
				        HttpStatus.CREATED );
		    }	    
	   }
	
	@PutMapping("/updateEmailNotification")
	public ResponseEntity<RestAPIResponse> updateEmailNotification( @RequestBody TmsEmailConfigurationDto dto) {
	   logger.info("!!! inside class: EmailConfiguration , !! method: updateEmailNotification");
	    TmsEmailConfiguration updated = configurationService.updateEmailConfiguration(dto);
	    return new ResponseEntity<>(
	        new RestAPIResponse("success", "Email notification configuration updated.", updated),
	        HttpStatus.OK
	    );
	}

	
	@GetMapping("/getAllEmailNotifications/{adminId}")
	public ResponseEntity<RestAPIResponse> getAllEmailNotifications(@PathVariable Long adminId) {
	    logger.info("!!! inside class: EmailConfiguration , !! method: getAllEmailNotifications");

	    List<TmsEmailConfiguration> configList = configurationService.getEmailConfiguration(adminId);

	    return new ResponseEntity<>(
	        new RestAPIResponse("success", "All email configurations fetched successfully.", configList),
	        HttpStatus.OK
	    );
	}

	
	
}
