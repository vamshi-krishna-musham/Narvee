package com.narvee.ats.auth.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.serviceimpl.ResumeParseServiceImpl;

@RequestMapping("/resume")
@RestController
public class ResumeParseController {
	private static final Logger logger = LoggerFactory.getLogger(ResumeParseController.class);

	@Autowired
	private ResumeParseServiceImpl resumeParseImpl;

	@PostMapping("/parse")
	public ResponseEntity<RestAPIResponse> resumeParsing(@RequestParam("resume") MultipartFile resume) throws IOException {
		logger.info("!!! inside class: ResumeParseController, !! method: resumeParsing");
		try {
			String skills = resumeParseImpl.convertDocumentToString(resume);
			return new ResponseEntity<>(new RestAPIResponse("success", "Skills parsed Successfuly", skills),
					HttpStatus.OK);

		} catch (Exception e) {

			return new ResponseEntity<>(new RestAPIResponse("fail", "check resume document"), HttpStatus.BAD_REQUEST);
		}

	}

}
