package com.narvee.controller;

import java.util.List;

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
	
	@GetMapping("/getTaskCountByAdminId/{adminId}")
	 public ResponseEntity<RestAPIResponse> getTaskCountByPid( @PathVariable Long adminId){
		logger.info("!!! inside class: TmsDashboardController , !! method: getTaskCountByPid");
		dashboardService.getTaskCountByAdminId(adminId);
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Task count By project Id fetched  successfully", dashboardService.getTaskCountByAdminId(adminId)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getTaskCountByPidAndUserId/{pid}/{userId}")
	 public ResponseEntity<RestAPIResponse> getTaskCountByPidAndUserId( @PathVariable Long pid,@PathVariable Long userId){
		logger.info("!!! inside class: TmsDashboardController , !! method: getTaskCountByPidAndUserId");
		dashboardService.getTaskCountByProjectIdAndUserId(pid,userId);
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Task count By project Id AND User Id  fetched  successfully", dashboardService.getTaskCountByProjectIdAndUserId(pid,userId)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getTaskCountByPidAndUserIdAndTime/{pid}/{userId}/{time}")
	 public ResponseEntity<RestAPIResponse> getTaskCountByPidAndUserIdAndTime( @PathVariable Long pid,@PathVariable Long userId,@PathVariable String time){
		logger.info("!!! inside class: TmsDashboardController , !! method: getTaskCountByPidAndUserIdAndTime");
		dashboardService.getTaskCountByProjectIdAndUserIdAndTime(pid,userId,time);
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Task count By project Id AND User Id  and Time fetched  successfully", dashboardService.getTaskCountByProjectIdAndUserIdAndTime(pid,userId,time)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getTaskCountByMonth/{status}")
	 public ResponseEntity<RestAPIResponse> getTaskStatusCountByMonth( @PathVariable String status){
		logger.info("!!! inside class: TmsDashboardController , !! method: getTaskStatusCountByMonth"); 
		dashboardService.getTaskStatusCountByMonth(status);
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Task count By Month fetched  successfully", dashboardService.getTaskStatusCountByMonth(status)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getUserTrackerByAdmin/{adminId}/{projectId}/{timeIntervel}")
	 public ResponseEntity<RestAPIResponse> getUserTrackerByAdmin( @PathVariable Long  adminId ,@PathVariable  Long projectId, @PathVariable String timeIntervel ){
		logger.info("!!! inside class: TmsDashboardController , !! method: getUserTrackerByAdmin"); 
	//	List<TmsTaskCountData>   data =	  dashboardService.getUserTracker(adminId,projectId,timeIntervel);
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " User Tracker Report fetched Successfully ",dashboardService.getUserTracker(adminId,projectId,timeIntervel)),       
				HttpStatus.OK); 
	 }
	

	@GetMapping("/dropDown/{userId}")
	public ResponseEntity<RestAPIResponse> projectDropDownWithOutAdmin(@PathVariable Long userId){
		logger.info("!!! inside class: ProjectController , !! method: projectDropDownWithOutAdmin");
	 return new ResponseEntity<RestAPIResponse>(new RestAPIResponse("success", "Fetched projectDropDownWithOutAdmin successfully",
			 dashboardService.projectDropDownWithOutAdmin(userId)), HttpStatus.OK);
		
	}
}
