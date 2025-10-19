package com.narvee.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.commons.RestAPIResponse;
import com.narvee.dto.FileUploadDto;
import com.narvee.service.service.FileUploadService;

@RestController
@RequestMapping("/fileUpload")
public class FileUploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
	
	@Autowired
	private FileUploadService fileUploadService;
	
	
	@PostMapping("/save")
	public ResponseEntity<RestAPIResponse> createProject (@RequestParam("files") MultipartFile[] files,
                                                          @RequestParam(value = "projectId", required = false) Long projectId,
                                                          @RequestParam(value = "taskId", required = false) Long taskId,
                                                          @RequestParam(value = "subTaskId", required = false) Long subTaskId ) {
		logger.info("!!! inside class: FileUploadController , !! method: save");
		try {				
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " File Uploded successfully", fileUploadService.uploadFile(files,projectId,taskId,subTaskId)),
				HttpStatus.CREATED);
		}catch(RuntimeException exception) {
			return new ResponseEntity<RestAPIResponse>(
					new RestAPIResponse("failed", " failed to  upload file", exception.getMessage()),
					HttpStatus.OK);
		}
	}
	
	@GetMapping("/get-files")
	public ResponseEntity<RestAPIResponse> getFiles(
	        @RequestParam(value = "projectId", required = false) Long projectId,
	        @RequestParam(value = "taskId", required = false) Long taskId,
	        @RequestParam(value = "subTaskId", required = false) Long subTaskId) {
	    
	    logger.info("Inside FileUploadController :: getFiles()");
	    try {
	        List<FileUploadDto> files = fileUploadService.getAllFiles(projectId, taskId, subTaskId);
	        return new ResponseEntity<>(
	                new RestAPIResponse("success", "Fetched files successfully", files),
	                HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(
	                new RestAPIResponse("failed", e.getMessage(), null),
	                HttpStatus.OK);
	    }
	}
	
	@DeleteMapping("/delete-files/{fileId}")
	public ResponseEntity<RestAPIResponse> DeleteFiles(
	        @PathVariable(value = "fileId") Long fileId) {	    
	    logger.info("Inside FileUploadController :: DeleteFiles()");
	    try {    
	    	fileUploadService.DeleteFile(fileId);
	      return new ResponseEntity<>(
	                new RestAPIResponse("success", "Deleted files successfully"),
	                HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(
	                new RestAPIResponse("failed", e.getMessage(), null),
	                HttpStatus.OK);
	    }
	}
	
	@GetMapping("/get-file/{fileId}")
	public ResponseEntity<RestAPIResponse> getFile(
	        @PathVariable(value = "fileId") Long fileId) {	    
	    logger.info("Inside FileUploadController :: getFile()");
	    try {      
      return new ResponseEntity<>(
	                new RestAPIResponse("success", " files Fetched successfully",	fileUploadService.getFile(fileId)),
	                HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(
	                new RestAPIResponse("failed", e.getMessage(), null),
	                HttpStatus.OK);
	    }
	}
	
	@PutMapping("/UpdateFile/{id}")
	public ResponseEntity<RestAPIResponse> replaceFileById(
	        @PathVariable Long id,
	        @RequestParam("file") MultipartFile file) {
	    try {
	        FileUploadDto dto = fileUploadService.replaceFile(id, file);
	        return new ResponseEntity<>(
	                new RestAPIResponse("success", " files Updated successfully",dto),
	                HttpStatus.OK);
	    } catch (RuntimeException e) {
	    	return new ResponseEntity<>(
	                new RestAPIResponse("failed", " Failed to update file ",e.getMessage()),
	                HttpStatus.OK);
	    }
	}

	
}
