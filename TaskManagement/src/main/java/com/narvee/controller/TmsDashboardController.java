package com.narvee.controller;

import java.util.List;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.commons.RestAPIResponse;
import com.narvee.dto.DashBoardRequestDto;
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
	 public ResponseEntity<RestAPIResponse> getTaskCountByAdminId( @PathVariable Long adminId){
		logger.info("!!! inside class: TmsDashboardController , !! method: getTaskCountByAdminId");	
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All TMS status count by admin id  fetched  successfully",
						dashboardService.getTaskCountByAdminId(adminId)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getTaskCountByPidAndUserId/{pid}/{userId}")
	 public ResponseEntity<RestAPIResponse> getTaskCountByPidAndUserId( @PathVariable Long pid,@PathVariable Long userId){
		logger.info("!!! inside class: TmsDashboardController , !! method: getTaskCountByPidAndUserId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Status count By project id AND admin id  User Id  fetched  successfully",
						dashboardService.getTaskCountByProjectIdAndUserId(pid,userId)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getTaskCountByPidAndUserIdAndTime")
	 public ResponseEntity<RestAPIResponse> getTaskCountByPidAndUserIdAndTime( @RequestParam(required = false) Long pid,@RequestParam Long userId,@RequestParam String time){
		logger.info("!!! inside class: TmsDashboardController , !! method: getTaskCountByPidAndUserIdAndTime");	
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Status count By project Id AND User Id  and Time fetched  successfully",
						dashboardService.getTaskCountByProjectIdAndUserIdAndTime(pid,userId,time)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getPriorityCountByAdminId/{adminId}")
	 public ResponseEntity<RestAPIResponse> getPriorityCountByAdminId( @PathVariable Long adminId){
		logger.info("!!! inside class: TmsDashboardController , !! method: getPriorityCountByAdminId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Priority count By Admin Id fetched  successfully",
						dashboardService.getPriorityCountByAdminId(adminId)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getPriorityCountByPidAndUserId/{pid}/{userId}")
	 public ResponseEntity<RestAPIResponse> getPriorityCountByPidAndUserId( @PathVariable Long pid,@PathVariable Long userId){
		logger.info("!!! inside class: TmsDashboardController , !! method: getPriorityCountByPidAndUserId");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Priority count By project Id AND User Id  fetched  successfully",
						dashboardService.getPriorityCountByProjectIdAndUserId(pid,userId)),       
				HttpStatus.OK); 
	 }
	
	@GetMapping("/getPriorityCountByPidAndUserIdAndTime")
	 public ResponseEntity<RestAPIResponse> getPriorityCountByPidAndUserIdAndTime( @RequestParam(required = false) Long pid,@RequestParam Long userId,@RequestParam String time){
		logger.info("!!! inside class: TmsDashboardController , !! method: getPriorityCountByPidAndUserIdAndTime");
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Priority count By project Id AND User Id  and Time fetched  successfully",
						dashboardService.getPriorityCountByProjectIdAndUserIdAndTime(pid,userId,time)),       
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
	
	@GetMapping("/getUserTrackerByAdmin")
	 public ResponseEntity<RestAPIResponse> getUserTrackerByAdmin( @RequestParam Long  adminId ,@RequestParam  Long projectId, @RequestParam String timeIntervel ){
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
	
	
	
	@GetMapping("/get-completed-count")
	 public ResponseEntity<RestAPIResponse> getTaskStatusCountByMonth( @RequestBody DashBoardRequestDto dashBoardRequestDto){
		logger.info("!!! inside class: TmsDashboardController , !! method: getCompletedStatusCount"); 
		
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("success", " All tms Task count By Month fetched  successfully",dashboardService.getCompleteStatusCount(dashBoardRequestDto)),       
				HttpStatus.OK); 
	 }
	
}
