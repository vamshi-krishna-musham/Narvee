package com.narvee.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.narvee.commons.RestAPIResponse;
import com.narvee.dto.RequestDTO;
import com.narvee.entity.TmsFileUpload;
import com.narvee.entity.TmsProject;
import com.narvee.repository.fileUploadRepository;
import com.narvee.service.service.ProjectService;


@RestController
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	private ProjectService projectservice;
	
	@Autowired
	private ObjectMapper mapper;

	
	@Autowired
	private fileUploadRepository fileUploadRepository;
	
	@Value("${AppFilesDir}")
    private String UPLOAD_DIR;
	
	 //private final Path fileStorageLocation = Paths.get("project-files").toAbsolutePath().normalize();
	 
	 
	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@PostMapping("/save")
	public ResponseEntity<RestAPIResponse> createProject(@RequestBody TmsProject project) {
		logger.info("!!! inside class: ProjectController , !! method: createproject");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " project created successfully", projectservice.saveproject(project)),
				HttpStatus.CREATED);
	}

	
	@GetMapping("/getbyProjectId/{pid}")
	public ResponseEntity<RestAPIResponse> findByProjectId(@PathVariable Long pid) {
		logger.info("!!! inside class: ProjectController , !! method: findByprojectid");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched  Project successfully",
				projectservice.findByprojectId(pid)), HttpStatus.OK);
	}
	
	
	@DeleteMapping("/delete/{pid}")
	public ResponseEntity<RestAPIResponse> deleteProjectById(@PathVariable Long pid) {
		logger.info("!!! inside class: ProjectController , !! method: deleteProjectById");
		try {
		projectservice.deleteProject(pid);
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Deleted successfully"),
			HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("Failed", "Cannot delete this Project because it has associated Task"),
					HttpStatus.OK);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<RestAPIResponse> updateProject(@RequestBody TmsProject project) {
		logger.info("!!! inside class: ProjectController , !! method: updateProject");
		boolean flag = projectservice.updateproject(project);
		if (flag == true) {
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated successfully"),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("fail", "Project not found"), HttpStatus.OK);
		}

	}

	@PostMapping("/findAllProjects")
	public ResponseEntity<RestAPIResponse> getAllProjects(@RequestBody RequestDTO requestresponsedto) {
		logger.info("!!! inside class: ProjectController , !! method: getAllProjects");
		return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched all projects successfully",
				projectservice.findAllProjects(requestresponsedto)), HttpStatus.OK);
	}
	
	
	
	
//	--- -------------------    all methods replicated for tms projects    --------------------------
	
	@PostMapping(value = "/save_tms", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<RestAPIResponse> TmscreateProject(@RequestPart("project") String tmsProject,
		                                                    @RequestPart(value = "files",required =  false) List<MultipartFile> projectFile) throws IOException {  
		
		    TmsProject project = mapper.readValue(tmsProject, TmsProject.class);

		// save tms project 
		logger.info("!!! inside class: ProjectController , !! method: TmscreateProject , !! for tms project ");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " project created successfully", projectservice.saveTmsproject(project,projectFile)),
				HttpStatus.CREATED);
	}

		@GetMapping("/getbyProjectIdTms/{pid}")
		public ResponseEntity<RestAPIResponse> findByProjectIdTms(@PathVariable Long pid) {
			logger.info("!!! inside class: ProjectController , !! method: findByProjectIdTms, !! for tms project ");
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched  Project successfully",
					projectservice.findByprojectIdTms(pid)), HttpStatus.OK);
	}
		
		@PutMapping(value = "/update-tms",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
		public ResponseEntity<RestAPIResponse> updateProjectTms(@RequestPart("project") String tmsProject,
				  @RequestPart(value = "files",required = false) List<MultipartFile> projectFile) throws IOException {
			logger.info("!!! inside class: ProjectController , !! method: updateProjectTms , !! For Tms Users ");
			    TmsProject project = mapper.readValue(tmsProject, TmsProject.class);
			   
			 try {
				return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Updated successfully",projectservice.updateprojectTms(project,projectFile)),
						HttpStatus.OK);
			} catch(Exception e) {
				return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("fail", "Project not found----",e), HttpStatus.OK);
			}

		}
		
		@PostMapping("/findAllProjects-tms")
		public ResponseEntity<RestAPIResponse> getAllProjectsTms(@RequestBody RequestDTO requestresponsedto) {
			logger.info("!!! inside class: ProjectController , !! method: getAllProjectsTms , !! for Tms Users ");
			return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched all projects successfully",
					projectservice.findTmsAllProjects(requestresponsedto)), HttpStatus.OK);
		}
		
		
		
		@GetMapping("/download-file/{id}")
        public ResponseEntity<Resource> downloadFileFromDisk(@PathVariable Long id) {
			
                        try {
                                        TmsFileUpload doc = fileUploadRepository.findById(id)
                                                                        .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + id));
                                        String filename = doc.getFileName();
                                        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
                                        Resource resource = new UrlResource(filePath.toUri());
                                        if (!resource.exists() || !resource.isReadable()) {
                                                        throw new FileNotFoundException("Could not read file: " + filename);
                                        }
                                        return ResponseEntity.ok().contentType(MediaType.parseMediaType(doc.getFileType()))
                                                                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                                                                        .body(resource);
                        } catch (Exception e) {
                                        e.printStackTrace();
                                        return ResponseEntity.status(HttpStatus.OK).body(null);
                        }
        }


		@GetMapping("/dropDown/{userId}/{isAdmin}")
		public ResponseEntity<RestAPIResponse> projectDropDownWithOutAdmin(@PathVariable Long userId,@PathVariable String isAdmin){
			logger.info("!!! inside class: ProjectController , !! method: projectDropDownWithOutAdmin");
		 return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched projectDropDownWithOutAdmin successfully",
					projectservice.projectDropDownWithOutAdmin(userId, isAdmin)), HttpStatus.OK);
			
		}
		

}
