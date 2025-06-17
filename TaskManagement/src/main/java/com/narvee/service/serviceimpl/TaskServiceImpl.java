package com.narvee.service.serviceimpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.dto.DateSearchDTO;
import com.narvee.dto.FileUploadDto;
import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.dto.TaskAssignDTO;
import com.narvee.dto.TaskResponse;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.dto.TasksResponseDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.entity.TmsAssignedUsers;
import com.narvee.entity.TmsFileUpload;
import com.narvee.entity.TmsTask;
import com.narvee.entity.TmsTicketTracker;
import com.narvee.repository.TaskRepository;
import com.narvee.repository.fileUploadRepository;
import com.narvee.service.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {
	private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
	
	
	@Value("${AppFilesDir}")
    private String UPLOAD_DIR;
	
	
	@Autowired
	private TaskRepository taskRepo;
	private static final int DIGIT_PADDING = 5;

	@Autowired
	private EmailServiceImpl emailService;
	
	@Autowired 
	private fileUploadRepository fileUploadRepository;

	@Override
	public TmsTask createTask(TmsTask task, String token) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: createTask");
		Long maxnumber = taskRepo.maxNumber();
		if (maxnumber == null) {
			maxnumber = 0L;
		}
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyMMdd");
		String formattedDateTime = now.format(inputFormatter);
		LocalDateTime dateTime = LocalDateTime.parse(formattedDateTime, inputFormatter);
		String formattedDateTime1 = dateTime.format(outputFormatter);
		String valueWithPadding = String.format("%0" + DIGIT_PADDING + "d", maxnumber + 1);
		String value = "T" + formattedDateTime1 + valueWithPadding;
		task.setTicketid(value);
		task.setMaxnum(maxnumber + 1);
		task.setStatus("To Do");
//		AssignedUsers asg = new AssignedUsers();
//		asg.setUserid(task.getAddedby());
//		List<AssignedUsers> assignedUsers = new ArrayList();
//		assignedUsers.add(asg);
//		List<AssignedUsers> addedByToAssignedUsers = task.getAssignedto();
//		addedByToAssignedUsers.addAll(assignedUsers);
		taskRepo.save(task);

		Set<TmsAssignedUsers> addedByToAssignedUsers = task.getAssignedto();
		// assignid=null, userid=28, completed=false
		List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getUserid)
				.collect(Collectors.toList());

		List<GetUsersDTO> user = taskRepo.getTaskAssinedUsersAndCreatedBy(task.getAddedby(), usersids);
		try {
			emailService.TaskAssigningEmail(task, user);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		return task;
	}

	@Override
	public boolean updateTask(UpdateTask updateTask) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: updateTask");
		TmsTask task = taskRepo.findById(updateTask.getTaskid()).get();
		List<TmsTicketTracker> listTicketTracker = task.getTrack();
		TmsTicketTracker ticketTracker = new TmsTicketTracker();

		if (task.getStartDate() == null) {
			task.setStartDate(LocalDate.now());
		}
		Set<TmsAssignedUsers> asigned = task.getAssignedto();
		for (TmsAssignedUsers assignedUsers : asigned) {
			if (updateTask.getUpdatedby() == assignedUsers.getUserid()) {
				assignedUsers.setUserstatus(updateTask.getStatus());
			}
		}
		task.setAssignedto(asigned);

		if (task != null) {
			task.setStatus(updateTask.getStatus());
			ticketTracker.setStatus(updateTask.getStatus());
			ticketTracker.setComments(updateTask.getComments());
			ticketTracker.setUpdatedby(updateTask.getUpdatedby());
			listTicketTracker.add(ticketTracker);
			task.setTrack(listTicketTracker);
			taskRepo.save(task);
			try {
				emailService.sendCommentEmail(updateTask);
			} catch (MessagingException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}

	@Override
	public TmsTask findBytaskId(Long taskid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: findBytaskId");
		TmsTask task = taskRepo.findById(taskid).get();
		for (TmsAssignedUsers aUser : task.getAssignedto()) {
			GetUsersDTO user = taskRepo.getUser(aUser.getUserid());
			aUser.setFullname(user.getFullname());
			aUser.setPseudoname(user.getPseudoname());
		}
		return task;
	}

	@Override
	public List<TmsTask> getAllTasks() {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getAllTasks");
		return taskRepo.findAll(Sort.by("taskid").descending());
	}
	
	@Override
	public List<TaskAssignDTO> taskAssignInfo(Long taskid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: taskAssignInfo");
		return taskRepo.taskAssignInfo(taskid);
	}

	@Override
	public List<TaskTrackerDTO> trackerByUser(Long userid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: trackerByUser");
		return taskRepo.trackerByUser(userid);
	}

	@Override
	public List<TaskTrackerDTO> allTasksRecords() {
		logger.info("!!! inside class: TaskServiceImpl , !! method: allTasksRecords");
		return taskRepo.allTasksRecords();
	}

	@Override
	public void deleteTask(Long id) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: deleteTask");
		taskRepo.deleteById(id);

	}

	@Override
	public List<TaskTrackerDTO> taskReports(DateSearchDTO dateSearch) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: TaskReports");
		if (dateSearch.getDepartment().equalsIgnoreCase("empty")) {
			return taskRepo.taskReports(dateSearch.getStartDate(), dateSearch.getTargetDate());
		} else {
			return taskRepo.taskReportsByDepartment(dateSearch.getStartDate(), dateSearch.getTargetDate(),
					dateSearch.getDepartment());
		}
	}

	@Override
	public Page<TaskTrackerDTO> getTaskByProjectid(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getTaskByProjectid");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		String keyword = requestresponsedto.getKeyword();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String projectid = requestresponsedto.getProjectid();
		String status = requestresponsedto.getStatus();
		if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("taskname"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		Sort.Direction sortDirection = Sort.Direction.ASC;

		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		if (keyword.equalsIgnoreCase("empty")) {
			return taskRepo.getTaskByProjectid(pageable, projectid, status);
		} else {
			return taskRepo.getTaskByProjectIdWithsearching(pageable, projectid, status, keyword);
		}
	}

	@Override
	public List<GetUsersDTO> getUsersByDepartment(String department) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getUsersByDepartment");
		return taskRepo.findDepartmentWiseUsers(department);
	}

	@Override
	public boolean updateTaskStatus(Long taskid, String status, String updatedby) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: updateTaskStatus");

		ZoneId indiaZoneId = ZoneId.of("Asia/Kolkata");
		LocalDateTime indiaDateTime = LocalDateTime.now(indiaZoneId);

		try {
			taskRepo.updateTaskStatus(taskid, status, updatedby, indiaDateTime);
			TmsTask taskInfo = taskRepo.findById(taskid).get();
			emailService.sendStatusUpdateEmail(taskInfo);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return true;

	}

	@Override
	public TaskResponse findTaskByProjectid(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getTaskByProjectid");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String projectid = requestresponsedto.getProjectid();
		String keyword = requestresponsedto.getKeyword();
		if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("taskname"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		Sort.Direction sortDirection = Sort.Direction.ASC;

		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		if (keyword.equalsIgnoreCase("empty")) {

			List<TaskTrackerDTO> res = taskRepo.findTaskByProjectid(projectid);

			logger.info("!!! inside class: TaskServiceImpl , !! method: findTaskByProjectid");
			List<TasksResponseDTO> tasksList = new ArrayList<>();

			for (TaskTrackerDTO order : res) {
				TasksResponseDTO result = new TasksResponseDTO(order);
				List<GetUsersDTO> assignUsers = taskRepo.getAssignUsers(order.getTaskid());

				List<GetUsersDTO> filteredAssignUsers = assignUsers.stream().filter(user -> user.getFullname() != null)
						.collect(Collectors.toList());
				result.setAssignUsers(filteredAssignUsers);
				tasksList.add(result);

			}

			Long pid = taskRepo.findPid(projectid);
			TaskResponse taskResp = new TaskResponse();
			//taskResp.setTasks(tasksList);
			taskResp.setPid(pid);

			return taskResp;
		} else {
			logger.info("!!! inside class: TaskServiceImpl , !! method: findTaskByProjectIdWithSearching , Filter");
			List<TaskTrackerDTO> res = taskRepo.findTaskByProjectIdWithSearching(projectid, keyword);
			List<TasksResponseDTO> tasksList = new ArrayList<>();

			for (TaskTrackerDTO order : res) {
				TasksResponseDTO result = new TasksResponseDTO(order);
				List<GetUsersDTO> assignUsers = taskRepo.getAssignUsers(order.getTaskid());
				result.setAssignUsers(assignUsers);
				tasksList.add(result);
			}

			Long pid = taskRepo.findPid(projectid);
			TaskResponse taskResp = new TaskResponse();
		//	taskResp.setTasks(tasksList);
			taskResp.setPid(pid);
			return taskResp;
		}
	}
	
	
	

	@Override
	public TmsTask findByTicketId(String taskid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: findByTicketId");
		return taskRepo.findByTicketid(taskid);
	}

	@Override
	public List<TasksResponseDTO> ticketTracker(Long taskid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: ticketTracker");
		List<TaskTrackerDTO> tracker = taskRepo.ticketTracker(taskid);
		List<TasksResponseDTO> tasksList = new ArrayList<>();

		for (TaskTrackerDTO taskTrackerDTO : tracker) {
			TasksResponseDTO track = new TasksResponseDTO(taskTrackerDTO);
			GetUsersDTO user = taskRepo.getUser(taskTrackerDTO.getUpdatedby());
			if (taskTrackerDTO.getUpdatedby() != null) {
				track.setFullname(user.getFullname());
				track.setPseudoname(user.getPseudoname());
			}
			tasksList.add(track);
		}
		return tasksList;
	}

	@Override
	public TmsTask update(TmsTask task) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: ticketTracker");
		TmsTask update = taskRepo.findById(task.getTaskid()).get();
		update.setTargetDate(task.getTargetDate());
		update.setTaskname(task.getTaskname());
		update.setDescription(task.getDescription());
		update.setAssignedto(task.getAssignedto());
		update.setUpdatedby(task.getUpdatedby());
		return taskRepo.save(update);
	}

	@Override
	public List<GetUsersDTO> getProjectUsers(String projectID) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: ticketTracker");
		return taskRepo.getProjectByTmsUsers(projectID);
	}
  
	
	
	
	
	
	
	//  ---------------  all methods replicated for the  task under thr project  for tms  -----------------------------------------------------------------------------------------------------------------------
	
	

	@Override
	public TmsTask createTmsTask(TmsTask task, String token,List<MultipartFile> files) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: createTmsTask-tms");
		Long maxnumber = taskRepo.maxNumber();
		if (maxnumber == null) {
			maxnumber = 0L;
		}
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyMMdd");
		String formattedDateTime = now.format(inputFormatter);
		LocalDateTime dateTime = LocalDateTime.parse(formattedDateTime, inputFormatter);
		String formattedDateTime1 = dateTime.format(outputFormatter);
		String valueWithPadding = String.format("%0" + DIGIT_PADDING + "d", maxnumber + 1);
		String value = "T" + formattedDateTime1 + valueWithPadding;
		task.setTicketid(value);
		task.setMaxnum(maxnumber + 1);
		task.setStatus(task.getStatus());
//		AssignedUsers asg = new AssignedUsers();
//		asg.setUserid(task.getAddedby());
//		List<AssignedUsers> assignedUsers = new ArrayList();
//		assignedUsers.add(asg);
//		List<AssignedUsers> addedByToAssignedUsers = task.getAssignedto();
//		addedByToAssignedUsers.addAll(assignedUsers);
		TmsTask savedtask =	taskRepo.save(task);

		
		  if (files != null && !files.isEmpty()) {
		        List<TmsFileUpload> taskFiles = new ArrayList<>();     

		        for (MultipartFile file : files) {
		        	
		            try {
		            	
		                String originalFileName = file.getOriginalFilename();
		                if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
			                continue;
			            }
		                String nameWithoutExt = originalFileName;
		                String ext = "";

						int dotIndex = originalFileName.lastIndexOf('.');
						if (dotIndex != -1) {
							nameWithoutExt = originalFileName.substring(0, dotIndex);
							ext = originalFileName.substring(dotIndex);
						}
		                String newFileName = nameWithoutExt + "-" + value + ext;
		                Path path = Paths.get(UPLOAD_DIR + newFileName);
		                Files.createDirectories(path.getParent());
		                Files.write(path, file.getBytes());

		                TmsFileUpload taskFile = new TmsFileUpload();
		                taskFile.setFileName(newFileName);
		                taskFile.setFilePath(path.toAbsolutePath().toString());
		                taskFile.setFileType(file.getContentType());
		                taskFile.setTask(task);

		                taskFiles.add(taskFile);
		            } catch (IOException e) {
		                logger.error("Failed to save file: " + file.getOriginalFilename(), e);
		            }
		        }
		        savedtask.getFiles().addAll(taskFiles);
		        fileUploadRepository.saveAll(taskFiles);
		    }
		  
		Set<TmsAssignedUsers> addedByToAssignedUsers = task.getAssignedto();
		// assignid=null, userid=28, completed=false
		List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getTmsUserId)
				.collect(Collectors.toList());

		List<GetUsersDTO> user = taskRepo.getTaskAssinedTmsUsersAndCreatedBy(task.getAddedby(), usersids);
		try {
			emailService.TaskAssigningEmailForTMS(task, user,true);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		return task;
	}
	
	
	@Override
	public boolean updateTmsTask(UpdateTask updateTask) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: updateTask-tms");
		TmsTask task = taskRepo.findById(updateTask.getTaskid()).get();
		List<TmsTicketTracker> listTicketTracker = task.getTrack();
		TmsTicketTracker ticketTracker = new TmsTicketTracker();

		if (task.getStartDate() == null) {
			task.setStartDate(LocalDate.now());
		}
		Set<TmsAssignedUsers> asigned = task.getAssignedto();
		for (TmsAssignedUsers assignedUsers : asigned) {
			if (updateTask.getUpdatedby() == assignedUsers.getTmsUserId()) {
				assignedUsers.setUserstatus(updateTask.getStatus());
			}
		}
		task.setAssignedto(asigned);

		if (task != null) {
		//	task.setStatus(updateTask.getStatus());
			ticketTracker.setStatus(updateTask.getStatus());
			ticketTracker.setComments(updateTask.getComments());
			ticketTracker.setUpdatedby(updateTask.getUpdatedby());
			listTicketTracker.add(ticketTracker);
			task.setTrack(listTicketTracker);
			taskRepo.save(task);
			try {
				emailService.sendTmsCommentEmail(updateTask);
			} catch (MessagingException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}
	
	
	@Override
	public TmsTask Tmsupdate(TmsTask task , List<MultipartFile> files) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: Tmsupdate-tms");
		TmsTask update = taskRepo.findById(task.getTaskid()).get();
		
		update.setTargetDate(task.getTargetDate());
		update.setStartDate(task.getStartDate());
		update.setTaskname(task.getTaskname());
		update.setPriority(task.getPriority());
		update.setDescription(task.getDescription());
		update.setAssignedto(task.getAssignedto());
		update.setUpdatedby(task.getUpdatedby());
		update.setStatus(task.getStatus());
		update.setDuration(task.getDuration());

		
		   //Path path = Paths.get(UPLOAD_DIR + getOriginalFilename);
		if (files != null && !files.isEmpty()) {
		    List<TmsFileUpload> uploadedFiles = files.stream().filter(file -> file != null && !file.isEmpty()).map(file -> {
		        String ext = Optional.ofNullable(file.getOriginalFilename())
		                             .filter(f -> f.contains("."))
		                             .map(f -> f.substring(f.lastIndexOf(".")))
		                             .orElse("");
		        String baseName = file.getOriginalFilename().replace(ext, "");
		        String fileName = baseName + "-" + update.getTicketid() + ext;
		        String fullPath = UPLOAD_DIR + fileName;

		        try {
		            Files.write(Paths.get(fullPath), file.getBytes());
		        } catch (IOException e) {
		            throw new RuntimeException("Failed to save file: " + fileName, e);
		        }


	            TmsFileUpload existing = fileUploadRepository
	                .findByFileNameAndTask(fileName, task);
	            if (existing != null) {
	                
	                existing.setFileType(file.getContentType());
	                existing.setFilePath(fullPath);
	                return existing;
	            } else {
	                
	                TmsFileUpload f = new TmsFileUpload();
	                f.setFileName(fileName);
	                f.setFilePath(fullPath);
	                f.setFileType(file.getContentType());
	                f.setTask(task);
	                return f;
	            }
		    }).collect(Collectors.toList());

		    update.getFiles().addAll(uploadedFiles);
		}
		
		Set<TmsAssignedUsers> addedByToAssignedUsers = task.getAssignedto();
		List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getTmsUserId)
				.collect(Collectors.toList());
		
		
		List<GetUsersDTO> user = taskRepo.getTaskAssinedTmsUsersAndCreatedBy(task.getAddedby(), usersids);
		try {
			emailService.TaskAssigningEmailForTMS(update,user,false);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		return taskRepo.save(update);
	}

	
	@Override
	public List<GetUsersDTO> getProjectByTmsUsers(String projectID) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: ticketTracker-tms");
		return taskRepo.getProjectByTmsUsers(projectID);
	}
	

	@Override
	public TmsTask findByTmstaskId(Long taskid) {
		
			logger.info("!!! inside class: TaskServiceImpl , !! method: findByTmstaskId-tms");
			TmsTask task = taskRepo.findById(taskid).get();
			for (TmsAssignedUsers aUser : task.getAssignedto()) {
				GetUsersDTO user = taskRepo.gettmsUser(aUser.getTmsUserId());
				aUser.setFullname(user.getFullname());
			}
			
			if (task.getFiles() != null) {
		        for (TmsFileUpload file : task.getFiles()) {
		            file.getFileName(); // trigger loading
		            file.getFilePath();
		            file.getFileType();
		        }
			}
			return task;
	}
	
	@Override
	public TaskResponse findTmsTaskByProjectid(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: findTmsTaskByProjectid-tms");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String projectid = requestresponsedto.getProjectid();
		String keyword = requestresponsedto.getKeyword();
		if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("TaskName"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("TaskDescription"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("DueDate"))
			sortfield = "target_date";
		else if (sortfield.equalsIgnoreCase("StartDate"))
			sortfield = "start_date";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("Priority"))
			sortfield = "priority";
		Sort.Direction sortDirection = Sort.Direction.ASC;

		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		
		if (keyword.equalsIgnoreCase("empty")) {
			logger.info("!!! inside class: TaskServiceImpl , !! method: findTaskByProjectid -- tms with empty ");
			Page<TaskTrackerDTO> res = taskRepo.findTaskByTmsProjectid(projectid,pageable);
			
			List<TasksResponseDTO> tasksList = new ArrayList<>();

			for (TaskTrackerDTO order : res) {
				TasksResponseDTO result = new TasksResponseDTO(order);

				List<GetUsersDTO> assignUsers = taskRepo.getTmsAssignUsers(order.getTaskid());

				List<GetUsersDTO> filteredAssignUsers = assignUsers.stream().filter(user -> user.getFullname() != null)
						.collect(Collectors.toList());
				result.setAssignUsers(filteredAssignUsers);
				
				  List<TmsFileUpload> fileEntities = fileUploadRepository.getTaskFiles(order.getTaskid());
				  // Implement this
				    List<FileUploadDto> fileDtos = fileEntities.stream().map(file -> {
				        FileUploadDto dto = new FileUploadDto();
				        dto.setId(file.getId());
				        dto.setFileName(file.getFileName());
				        dto.setFilePath(file.getFilePath());
				        dto.setFileType(file.getFileType());
				        return dto;
				    }).collect(Collectors.toList());
				    result.setFiles(fileDtos);

				tasksList.add(result);

				
			}
			  Page<TasksResponseDTO> tasksPage = new PageImpl<>(tasksList, pageable, res.getTotalElements());
			Long pid = taskRepo.findPid(projectid);
			TaskResponse taskResp = new TaskResponse();
			taskResp.setTasks(tasksPage);
			taskResp.setPid(pid);

			return taskResp;
		} else {
			logger.info("!!! inside class: TaskServiceImpl , !! method: findTaskByProjectIdWithSearching , Filter-tms");
			Page<TaskTrackerDTO> res = taskRepo.findTaskByTmsProjectIdWithSearching(projectid, keyword,pageable);
			List<TasksResponseDTO> tasksList = new ArrayList<>();
			for (TaskTrackerDTO order : res) {
				TasksResponseDTO result = new TasksResponseDTO(order);
				List<GetUsersDTO> assignUsers = taskRepo.getTmsAssignUsers(order.getTaskid());
				result.setAssignUsers(assignUsers);
				
				  List<TmsFileUpload> fileEntities = fileUploadRepository.getTaskFiles(order.getTaskid()); // Implement this
				    List<FileUploadDto> fileDtos = fileEntities.stream().map(file -> {
				        FileUploadDto dto = new FileUploadDto();
				        dto.setId(file.getId());
				        dto.setFileName(file.getFileName());
				        dto.setFilePath(file.getFilePath());
				        dto.setFileType(file.getFileType());
				        return dto;
				    }).collect(Collectors.toList());
				    result.setFiles(fileDtos);

				   
				tasksList.add(result);
				
			}
			 Page<TasksResponseDTO> tasksPage = new PageImpl<>(tasksList, pageable, res.getTotalElements()); 

			Long pid = taskRepo.findPid(projectid);
			TaskResponse taskResp = new TaskResponse();
			taskResp.setTasks(tasksPage);
			taskResp.setPid(pid);
			return taskResp;
		}
	}

	@Override
	public Page<TaskTrackerDTO> getTmsTaskByProjectid(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getTmsTaskByProjectid-tms");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		String keyword = requestresponsedto.getKeyword();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String projectid = requestresponsedto.getProjectid();
		String status = requestresponsedto.getStatus();
		if (sortfield.equalsIgnoreCase("ticketid"))
			sortfield = "ticketid";
		else if (sortfield.equalsIgnoreCase("taskname"))
			sortfield = "taskname";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("targetdate"))
			sortfield = "targetdate";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		Sort.Direction sortDirection = Sort.Direction.ASC;

		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		
		
		if (keyword.equalsIgnoreCase("empty")) {
			return taskRepo.getTmsTaskByProjectid(pageable, projectid, status);
		} else {
			return taskRepo.getTmsTaskByProjectIdWithsearching(pageable, projectid, status, keyword);
		}	
}

	

	@Override
	public void deleteTmsTask(Long id) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: deleteTmsTask-tms");
		taskRepo.deleteById(id);

	}
	
	@Override
	public void deleteTmsTaskFileIpload(Long id) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: deleteTmsTask-tms");
		fileUploadRepository.deleteById(id);

	}

	@Override
	public List<TasksResponseDTO> ticketTmsTracker(Long taskid) {
		
			logger.info("!!! inside class: TaskServiceImpl , !! method: ticketTmsTracker-tms");
			List<TaskTrackerDTO> tracker = taskRepo.ticketTracker(taskid);
			List<TasksResponseDTO> tasksList = new ArrayList<>();

			for (TaskTrackerDTO taskTrackerDTO : tracker) {
				TasksResponseDTO track = new TasksResponseDTO(taskTrackerDTO);
				GetUsersDTO user = taskRepo.gettmsUser(taskTrackerDTO.getUpdatedby());
				if (taskTrackerDTO.getUpdatedby() != null) {
					track.setFullname(user.getFullname());
					//track.setPseudoname(user.getPseudoname());
				}
				tasksList.add(track);
			}
			return tasksList;
		}

	@Override
	public List<TaskAssignDTO> taskTmsAssignInfo(Long taskid) {
			logger.info("!!! inside class: TaskServiceImpl , !! method: taskTmsAssignInfo-tms");
			return taskRepo.taskTmsAssignInfo(taskid);
		}

	@Override
	public List<TmsTask> getAllTmsTasks() {
			logger.info("!!! inside class: TaskServiceImpl , !! method: getAllTmsTasks-tms");
			return taskRepo.findAll(Sort.by("taskid").descending());
		}

	
	
	@Override
	public Map<String, Long> getTaskCountByStatus(Long pid,Long userid) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: getAllTasksCount");
		List<Object[]> result;
		String UserRole = taskRepo.getUserRole(userid);
		
		if("SUPER_ADMIN".equalsIgnoreCase(UserRole)) {
		  if(pid != null) {
			result = taskRepo.countTasksByStatusAndPid(pid);
		}else {
	     result = taskRepo.countTasksByStatus();
		  }
		
		}else  {
			
			if(pid != null) {
				result = taskRepo.getTaskCountByUserAndPid(pid,userid);
			}else {
				result = taskRepo.countTasksByStatusAndUser(userid);
			   }
		}
	    Map<String, Long> statusCount = new HashMap<>();

	    for (Object[] row : result) {
	        String status = (String) row[0];
	        Long count = ((Number) row[1]).longValue();
	        statusCount.put(status, count);
	    }
	    return statusCount;
	}
   
	
	@Override
	public boolean updateTmsTaskStatus(Long taskid, String status, Long updatedby) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: updateTaskStatus");

		ZoneId indiaZoneId = ZoneId.of("Asia/Kolkata");
		LocalDateTime indiaDateTime = LocalDateTime.now(indiaZoneId);

		try {
			taskRepo.updateTmsTaskStatus(taskid, status, updatedby, indiaDateTime);
			TmsTask taskInfo = taskRepo.findById(taskid).get();
			emailService.sendStatusUpdateEmail(taskInfo);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return true;

	}
	
}