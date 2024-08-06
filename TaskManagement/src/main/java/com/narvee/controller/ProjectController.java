package com.narvee.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.narvee.commons.RestAPIResponse;
import com.narvee.dto.RequestResponseDTO;
import com.narvee.entity.Project;
import com.narvee.service.service.ProjectService;

@RestController
@RequestMapping("/project")
@CrossOrigin(origins="*")
public class ProjectController {

	@Autowired
	private ProjectService projectservice;

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@PostMapping("/save")
	public ResponseEntity<RestAPIResponse> createProject(@RequestBody Project project) {
		logger.info("!!! inside class: ProjectController , !! method: createproject");
		this.projectservice.saveproject(project);
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " project created successfully", projectservice.saveproject(project)),
				HttpStatus.CREATED);
	}

	@GetMapping("/getbyProjectId/{projectid}")
	public ResponseEntity<RestAPIResponse> findByProjectId(@PathVariable Long projectid) {
		logger.info("!!! inside class: ProjectController , !! method: findByprojectid");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched  Project successfully",
				projectservice.findByprojectId(projectid)), HttpStatus.OK);
	}

	@DeleteMapping("/delete/{projectid}")
	public ResponseEntity<RestAPIResponse> deleteProjectById(@PathVariable Long projectid) {
		logger.info("!!! inside class: ProjectController , !! method: deleteProjectById");
		projectservice.deleteProject(projectid);
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Deleted successfully"),
				HttpStatus.OK);
	}

	@PutMapping("/update")
	public ResponseEntity<RestAPIResponse> updateProject(@RequestBody Project project) {
		logger.info("!!! inside class: ProjectController , !! method: updateProject");
		boolean flag = projectservice.updateproject(project);
		if (flag == true) {
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated successfully"),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("fail", "Project not found"), HttpStatus.OK);
		}

	}

	@PostMapping("/getProjectUser")
	public ResponseEntity<RestAPIResponse> getProjectUser(@RequestBody RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: ProjectController , !! method: getProjectUser");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched all projects successfully",
				projectservice.getProjectUser(requestresponsedto)), HttpStatus.OK);
	}
	
	@PostMapping("/findAllProjects")
	public ResponseEntity<RestAPIResponse> getAllProjects(
			@RequestBody RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: ProjectController , !! method: getAllProjects");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched all projects successfully",
				projectservice.findAllProjects(requestresponsedto)), HttpStatus.OK);
	}

}
