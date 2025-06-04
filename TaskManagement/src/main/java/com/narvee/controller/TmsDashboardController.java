package com.narvee.controller;

import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.commons.RestAPIResponse;
import com.narvee.service.service.TmsDashboardService;

@RestController
@RequestMapping("/Dashboard")
public class TmsDashboardController {
    
	
	private static final Logger logger = LoggerFactory.getLogger(TmsDashboardController.class);
	
	@Autowired
	private TmsDashboardService dashboardService;
	
	@GetMapping("/getAllTaskCount")
	 public ResponseEntity<RestAPIResponse> getAllTaskCount(){
		logger.info("!!! inside class: TmsDashboardController , !! method: getAllTaskCount");
		dashboardService.getAllTaskCount();
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Task count fetched  successfully", dashboardService.getAllTaskCount()),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getTaskCountByProjectId/{pid}")
	 public ResponseEntity<RestAPIResponse> getTaskCountByPid( @PathVariable Long pid){
		logger.info("!!! inside class: TmsDashboardController , !! method: getAllTaskCount");
		dashboardService.getAllTaskCount();
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Task count By project Id fetched  successfully", dashboardService.getTaskCountByProjectId(pid)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getTaskCountByPidAndUserId/{pid}/{userId}")
	 public ResponseEntity<RestAPIResponse> getTaskCountByPidAndUserId( @PathVariable Long pid,@PathVariable Long userId){
		logger.info("!!! inside class: TmsDashboardController , !! method: getAllTaskCount");
		dashboardService.getAllTaskCount();
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Task count By project Id AND User Id  fetched  successfully", dashboardService.getTaskCountByProjectIdAndUserId(pid,userId)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getTaskCountByPidAndUserIdAndTime/{pid}/{userId}/{time}")
	 public ResponseEntity<RestAPIResponse> getTaskCountByPidAndUserIdAndTime( @PathVariable Long pid,@PathVariable Long userId,@PathVariable String time){
		logger.info("!!! inside class: TmsDashboardController , !! method: getAllTaskCount");
		dashboardService.getAllTaskCount();
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Task count By project Id AND User Id  and Time fetched  successfully", dashboardService.getTaskCountByProjectIdAndUserIdAndTime(pid,userId,time)),       
				HttpStatus.OK); 
	 }
}
