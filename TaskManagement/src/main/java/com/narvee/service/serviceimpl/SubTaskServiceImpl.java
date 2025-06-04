package com.narvee.service.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
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

import com.narvee.dto.FileUploadDto;
import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.dto.SubTaskResponse;
import com.narvee.dto.SubTaskResponseDTO;
import com.narvee.dto.SubTaskUserDTO;
import com.narvee.dto.TaskResponse;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.dto.TasksResponseDTO;
import com.narvee.dto.UpdateTask;
import com.narvee.entity.TmsAssignedUsers;
import com.narvee.entity.TmsFileUpload;
import com.narvee.entity.TmsSubTask;
import com.narvee.entity.TmsTicketTracker;
import com.narvee.repository.SubTaskRepository;
import com.narvee.repository.TaskRepository;
import com.narvee.repository.fileUploadRepository;
import com.narvee.service.service.SubTaskService;

@Service
public class SubTaskServiceImpl implements SubTaskService {

	private static final Logger logger = LoggerFactory.getLogger(SubTaskServiceImpl.class);

	@Autowired
	private SubTaskRepository subtaskrepository;

	@Autowired
	private EmailServiceImpl emailService;

	@Autowired
	private TaskRepository repository;
	
	@Autowired
    private fileUploadRepository fileUploadRepository;
	
	
	@Value("${AppFilesDir}")
    private String UPLOAD_DIR;
	
	
	@Override
	public TmsSubTask createSubTask(TmsSubTask subtask) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: createSubTask");
		Set<TmsAssignedUsers> addedByToAssignedUsers = subtask.getAssignedto();
		List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getUserid)
				.collect(Collectors.toList());
		List<GetUsersDTO> user = repository.getTaskAssinedUsersAndCreatedBy(subtask.getAddedby(), usersids);
		TmsSubTask subtasks = subtaskrepository.save(subtask);

		try {
			emailService.SubTaskAssigningEmail(subtasks, user);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		return subtaskrepository.save(subtasks);
	}

	@Override
	public TmsSubTask findBySubTaskId(Long subtaskid) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: findBySubTaskId");
		TmsSubTask subtask = subtaskrepository.findById(subtaskid).get();
		for (TmsAssignedUsers aUser : subtask.getAssignedto()) {
			GetUsersDTO user = repository.getUser(aUser.getUserid());
			aUser.setFullname(user.getFullname());
			aUser.setPseudoname(user.getPseudoname());
		}
		return subtask;
	}

	@Override
	public void deleteSubTask(Long subtaskid) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: deleteSubTask");
		subtaskrepository.deleteById(subtaskid);

	}

	@Override
	public Boolean updateSubTask(TmsSubTask updatesubtask) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateSubTask");
		Optional<TmsSubTask> optional = subtaskrepository.findById(updatesubtask.getSubTaskId());
		if (optional.isPresent()) {
			TmsSubTask subtask = optional.get();
			subtask.setSubTaskName(updatesubtask.getSubTaskName());
			subtask.setSubTaskDescription(updatesubtask.getSubTaskDescription());
			subtask.setAddedby(updatesubtask.getAddedby());
			subtask.setUpdatedBy(updatesubtask.getUpdatedBy());
			subtask.setStatus(updatesubtask.getStatus());
			subtask.setTargetDate(updatesubtask.getTargetDate());
			subtask.setAssignedto(updatesubtask.getAssignedto());

			subtaskrepository.save(subtask);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Page<SubTaskUserDTO> getSubTaskUser(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: getSubTaskUser");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();

		if (sortfield.equalsIgnoreCase("subTaskId"))
			sortfield = "subTaskId";
		else if (sortfield.equalsIgnoreCase("subTaskName"))
			sortfield = "subTaskName";
		else if (sortfield.equalsIgnoreCase("subTaskDescription"))
			sortfield = "subTaskDescription";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("targetDate"))
			sortfield = "targetDate";
		else if (sortfield.equalsIgnoreCase("addedby"))
			sortfield = "addedby";
		else
			sortfield = "updateddate";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(requestresponsedto.getPageNumber() - 1, requestresponsedto.getPageSize(),
				sort);
		return subtaskrepository.getSubTaskUser(pageable);

	}

	@Override
	public Page<TmsSubTask> getAllSubTasks(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: getAllSubTasks");
		String sortorder = requestresponsedto.getSortOrder();
		String sortfield = requestresponsedto.getSortField();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String keyword = requestresponsedto.getKeyword();

		if (sortfield.equalsIgnoreCase("subTaskId"))
			sortfield = "subTaskId";
		else if (sortfield.equalsIgnoreCase("subTaskName"))
			sortfield = "subTaskName";
		else if (sortfield.equalsIgnoreCase("subTaskDescription"))
			sortfield = "subTaskDescription";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("targetDate"))
			sortfield = "targetDate";
		else
			sortfield = "updateddate";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		if (keyword.equalsIgnoreCase("empty")) {
			Page<TmsSubTask> page = subtaskrepository.findAll(pageable);
			return page;
		} else {
			logger.info("!!! inside class: SubTaskServiceImpl , !! method: getAllSubTasks Inside Filters");
			return subtaskrepository.getAllSubTasksSortingAndFiltering(pageable, keyword);

		}
	}

	
	@Override
	public SubTaskResponse findBySubTaskTicketId(String ticketId) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: findBySubTaskTicketId");
		List<TmsSubTask> subTasksList = subtaskrepository.findByTaskTicketid(ticketId);
		SubTaskResponse response = new SubTaskResponse();
		for (TmsSubTask order : subTasksList) {
			order.setTaskId(order.getTask().getTaskid());
			for (TmsAssignedUsers assignUsers : order.getAssignedto()) {
				GetUsersDTO user = repository.gettmsUser(assignUsers.getTmsUserId());
				assignUsers.setFullname(user.getFullname());
			//	assignUsers.setPseudoname(user.getPseudoname());
			}
		}
		Long taskId = subtaskrepository.findTaskId(ticketId);
	//	response.setSubtasks(subTasksList);
		response.setTaskId(taskId);

		return response;
	}

	@Override
	public boolean updateSubTaskStatus(Long subTaskId, String staus, Long updatedby) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateSubTaskStatus");
		ZoneId indiaZoneId = ZoneId.of("Asia/Kolkata");
		LocalDateTime indiaDateTime = LocalDateTime.now(indiaZoneId);

		try {
			subtaskrepository.updateTaskStatus(subTaskId, staus, updatedby, indiaDateTime);
			TmsSubTask subtasks = subtaskrepository.findById(subTaskId).get();
			emailService.sendSubtaskEmail(subtasks);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			subtaskrepository.updateTaskStatus(subTaskId, staus, updatedby, indiaDateTime);
		}

		return true;

	}

	@Override
	public List<TasksResponseDTO> ticketTrackerBySubTaskId(Long subtaskid) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateSubTaskStatus");
		List<TaskTrackerDTO> tracker = subtaskrepository.ticketTrackerBySubTaskId(subtaskid);
		List<TasksResponseDTO> tasksList = new ArrayList<>();

		for (TaskTrackerDTO taskTrackerDTO : tracker) {
			TasksResponseDTO track = new TasksResponseDTO(taskTrackerDTO);
			GetUsersDTO user = repository.getUser(taskTrackerDTO.getUpdatedby());
			if (taskTrackerDTO.getUpdatedby() != null) {
				track.setFullname(user.getFullname());
				track.setPseudoname(user.getPseudoname());
			}
			tasksList.add(track);
		}

		return tasksList;
	}

	@Override
	public boolean updateSubTaskTrack(UpdateTask updateTask) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateSubTaskTrack");
		
		TmsSubTask subTask = subtaskrepository.findById(updateTask.getSubTaskId()).get();
		List<TmsTicketTracker> listTicketTracker = subTask.getTrack();
		TmsTicketTracker ticketTracker = new TmsTicketTracker();

		if (subTask != null) {
			subTask.setStatus(updateTask.getStatus());
			ticketTracker.setStatus(updateTask.getStatus());
			ticketTracker.setComments(updateTask.getComments());
			ticketTracker.setUpdatedby(updateTask.getUpdatedby());
			listTicketTracker.add(ticketTracker);
			subTask.setTrack(listTicketTracker);
			subtaskrepository.save(subTask);
			
			
			try {
				emailService.sendCommentEmail(updateTask);
			} catch (MessagingException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			return true;
		}
		return false;

	}

	
	
	//----------------------------all methods replicated for tms by keerthi ----------------------------------
	
	
	@Override
	public TmsSubTask createTmsSubTask(TmsSubTask subtask, List<MultipartFile> files) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: createSubTask--tms");
		Set<TmsAssignedUsers> addedByToAssignedUsers = subtask.getAssignedto();
		List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getTmsUserId)
				.collect(Collectors.toList());
		List<GetUsersDTO> user = repository.getTaskAssinedTmsUsersAndCreatedBy(subtask.getAddedby(), usersids);
		TmsSubTask subtasks = subtaskrepository.save(subtask);
 
	 

		  if (files != null && !files.isEmpty()) {
		        List<TmsFileUpload> taskFiles = new ArrayList<>();
		        

		        for (MultipartFile file : files) {
		            try {
		                String originalFileName = file.getOriginalFilename();
		                
		                if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
			                continue;
			            }
		                String nameWithoutExt = originalFileName;
		                String ext = "";

						int dotIndex = originalFileName.lastIndexOf('.');
						if (dotIndex != -1) {
							nameWithoutExt = originalFileName.substring(0, dotIndex);
							ext = originalFileName.substring(dotIndex);
						}
		                String newFileName = nameWithoutExt + " SUB-TASK-" + subtasks.getSubTaskId() +  ext;
		                Path path = Paths.get(UPLOAD_DIR + newFileName);
		                Files.createDirectories(path.getParent());
		                Files.write(path, file.getBytes());

		                TmsFileUpload taskFile = new TmsFileUpload();
		                taskFile.setFileName(newFileName);
		                taskFile.setFilePath(path.toAbsolutePath().toString());
		                taskFile.setFileType(file.getContentType());
		                taskFile.setSubtask(subtasks);

		                taskFiles.add(taskFile);
		            } catch (IOException e) {
		                logger.error("Failed to save file: " + file.getOriginalFilename(), e);
		            }
		        }
		        subtasks.getFiles().addAll(taskFiles);
		        fileUploadRepository.saveAll(taskFiles);
		    }
		try {
			emailService.sendCreateSubTaskEmail(subtasks, user,true);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		return subtaskrepository.save(subtasks);
	}

	@Override
	public TmsSubTask updateTmsSubTask(TmsSubTask updatesubtask,List<MultipartFile> files) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateTmsSubTask --Tms");
		Optional<TmsSubTask> optional = subtaskrepository.findById(updatesubtask.getSubTaskId());
		 if (!optional.isPresent()) {
		        throw new RuntimeException("Subtask not found with ID: " + updatesubtask.getSubTaskId());
		    }

		    TmsSubTask subtask = optional.get();

		    subtask.setSubTaskName(updatesubtask.getSubTaskName());
		    subtask.setSubTaskDescription(updatesubtask.getSubTaskDescription());
		    subtask.setAddedby(updatesubtask.getAddedby());
		    subtask.setUpdatedBy(updatesubtask.getUpdatedBy());
		    subtask.setStatus(updatesubtask.getStatus());
		    subtask.setTargetDate(updatesubtask.getTargetDate());
		    subtask.setStartDate(updatesubtask.getStartDate());
		    subtask.setAssignedto(updatesubtask.getAssignedto());
		    subtask.setSubTaskDescription(updatesubtask.getSubTaskDescription());

			if (files != null && !files.isEmpty()) {
			    List<TmsFileUpload> uploadedFiles = files.stream().filter(file -> file != null && !file.isEmpty()).map(file -> {
			        String ext = Optional.ofNullable(file.getOriginalFilename())
			                             .filter(f -> f.contains("."))
			                             .map(f -> f.substring(f.lastIndexOf(".")))
			                             .orElse("");
			        String baseName = file.getOriginalFilename().replace(ext, "");
			        String fileName = baseName + "-" + subtask.getSubTaskId() + ext;
			        String fullPath = UPLOAD_DIR + fileName;

			        try {
			            Files.write(Paths.get(fullPath), file.getBytes());
			        } catch (IOException e) {
			            throw new RuntimeException("Failed to save file: " + fileName, e);
			        }
			        
			      
		            TmsFileUpload existing = fileUploadRepository
		                .findByFileNameAndSubtask(fileName, updatesubtask);
		            if (existing != null) {
		                
		                existing.setFileType(file.getContentType());
		                existing.setFilePath(fullPath);
		                return existing;
		            } else {
		                
		                TmsFileUpload f = new TmsFileUpload();
		                f.setFileName(fileName);
		                f.setFilePath(fullPath);
		                f.setFileType(file.getContentType());
		                f.setSubtask(updatesubtask);
		                return f;
		            }
			    }).collect(Collectors.toList());

			    subtask.getFiles().addAll(uploadedFiles);
			    
			}
		
			Set<TmsAssignedUsers> addedByToAssignedUsers = subtask.getAssignedto();
			List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getTmsUserId)
					.collect(Collectors.toList());
			List<GetUsersDTO> user = repository.getTaskAssinedTmsUsersAndCreatedBy(subtask.getAddedby(), usersids);
			TmsSubTask subtasks = subtaskrepository.save(subtask);
	 
			try {
				emailService.sendCreateSubTaskEmail(subtasks, user,false);
			} catch (UnsupportedEncodingException | MessagingException e) {
				e.printStackTrace();
			}
			
		
			return subtasks ;
		}
		
	

	@Override
	public Page<TmsSubTask> getAllSubTasksTms(RequestDTO requestresponsedto) {
		
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: getAllSubTasksTms--tms");
		String sortorder = requestresponsedto.getSortOrder();
		String sortfield = requestresponsedto.getSortField();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String keyword = requestresponsedto.getKeyword();

		if (sortfield.equalsIgnoreCase("subTaskId"))
			sortfield = "subTaskId";
		else if (sortfield.equalsIgnoreCase("subTaskName"))
			sortfield = "subTaskName";
		else if (sortfield.equalsIgnoreCase("subTaskDescription"))
			sortfield = "subTaskDescription";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("targetDate"))
			sortfield = "targetDate";
		else if (sortfield.equalsIgnoreCase("startDate"))
			sortfield = "startDate";
		else
			sortfield = "updateddate";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		if (keyword.equalsIgnoreCase("empty")) {
			Page<TmsSubTask> page = subtaskrepository.findAll(pageable);
			return page;
		} else {
			logger.info("!!! inside class: SubTaskServiceImpl , !! method: getAllSubTasks Inside Filters");
			return subtaskrepository.getAllSubTasksSortingAndFiltering(pageable, keyword);

		}
	}

	@Override
	public void deleteSubTaskTms(Long subtaskid) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: deleteSubTask--tms");
		subtaskrepository.deleteById(subtaskid);
	}

	@Override
	public TmsSubTask findBySubTaskIdTms(Long subtaskid) {
	
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: findBySubTaskIdTms--tms");
		TmsSubTask subtask = subtaskrepository.findById(subtaskid).get();
		for (TmsAssignedUsers aUser : subtask.getAssignedto()) {
			GetUsersDTO user = repository.gettmsUser(aUser.getTmsUserId());
			aUser.setFullname(user.getFullname());
		//	aUser.setPseudoname(user.getPseudoname());
		}
		
		if (subtask.getFiles() != null) {
	        for (TmsFileUpload file : subtask.getFiles()) {
	            file.getFileName(); // trigger loading
	            file.getFilePath();
	            file.getFileType();
	        }
		}
		return subtask;
	}

	@Override
	public boolean updateSubTaskStatusTms(Long subTaskId, String staus, Long updatedby) {
		
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateSubTaskStatusTms");
		ZoneId indiaZoneId = ZoneId.of("Asia/Kolkata");
		LocalDateTime indiaDateTime = LocalDateTime.now(indiaZoneId);

		try {
			subtaskrepository.updateTaskStatus(subTaskId, staus, updatedby, indiaDateTime);
			TmsSubTask subtasks = subtaskrepository.findById(subTaskId).get();
			emailService.sendSubtaskEmailTms(subtasks);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			subtaskrepository.updateTaskStatus(subTaskId, staus, updatedby, indiaDateTime);
		}

		return true;
	}
	
	
	@Override
	public SubTaskResponse findTmsSubTaskByTicketId(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: TaskServiceImpl , !! method: findTmsTaskByProjectid-tms");
		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		String projectid = requestresponsedto.getProjectid();
		String keyword = requestresponsedto.getKeyword();
		String ticketId = requestresponsedto.getTicketId();
		
		if (sortfield.equalsIgnoreCase("subTaskId"))
			sortfield = "subTaskId";
		else if (sortfield.equalsIgnoreCase("subTaskName"))
			sortfield = "subTaskName";
		else if (sortfield.equalsIgnoreCase("subTaskDescription"))
			sortfield = "subTaskDescription";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("targetDate"))
			sortfield = "targetDate";
		else if (sortfield.equalsIgnoreCase("startDate"))
			sortfield = "startDate";
		else
			sortfield = "updateddate";
		
		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		if (keyword.equalsIgnoreCase("empty")) {

			Page<TaskTrackerDTO> res = subtaskrepository.findSubTaskByTicketid(ticketId,pageable);
			
			logger.info("!!! inside class: TaskServiceImpl , !! method: findSubTaskByTicketid");
			List<SubTaskResponseDTO> tasksList = new ArrayList<>();

			for (TaskTrackerDTO order : res) {
				SubTaskResponseDTO result = new SubTaskResponseDTO(order);

				List<GetUsersDTO> assignUsers = subtaskrepository.getSubtaskAssignUsersTms(order.getSubtaskid());

				List<GetUsersDTO> filteredAssignUsers = assignUsers.stream().filter(user -> user.getFullname() != null)
						.collect(Collectors.toList());
				result.setAssignUsers(filteredAssignUsers);
				
				  List<TmsFileUpload> fileEntities = fileUploadRepository.getFilesBySubTaskId(order.getSubtaskid()); // Implement this
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
			Page<SubTaskResponseDTO> tasksPage = new PageImpl<>(tasksList, pageable, res.getTotalElements()); 
			
			Long TicketId = repository.findTicketId(ticketId);
			SubTaskResponse taskResp = new SubTaskResponse();
			taskResp.setSubtasks(tasksPage);
			taskResp.setTaskId(TicketId);

			return taskResp;
		} else {
			logger.info("!!! inside class: TaskServiceImpl , !! method: findTaskByProjectIdWithSearching , Filter-tms");
			Page<TaskTrackerDTO> res = subtaskrepository.findSubTaskByTicketIdWithSearching(ticketId, keyword,pageable);
			List<SubTaskResponseDTO> tasksList = new ArrayList<>();

			for (TaskTrackerDTO order : res) {
				SubTaskResponseDTO result = new SubTaskResponseDTO(order);
				List<GetUsersDTO> assignUsers = subtaskrepository.getSubtaskAssignUsersTms(order.getSubtaskid());
				result.setAssignUsers(assignUsers);
				
				  List<TmsFileUpload> fileEntities = fileUploadRepository.getFilesBySubTaskId(order.getSubtaskid()); // Implement this
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
			Page<SubTaskResponseDTO> tasksPage = new PageImpl<>(tasksList, pageable, res.getTotalElements());
			Long TicketId = repository.findTicketId(ticketId);
			SubTaskResponse taskResp = new SubTaskResponse();
			taskResp.setSubtasks(tasksPage);
			taskResp.setTaskId(TicketId);
			return taskResp;
		}
	}

	@Override
	public boolean updateTmsSubTaskTrack(UpdateTask updateTask) {
logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateSubTaskTrack");
		
		TmsSubTask subTask = subtaskrepository.findById(updateTask.getSubTaskId()).get();
		List<TmsTicketTracker> listTicketTracker = subTask.getTrack();
		TmsTicketTracker ticketTracker = new TmsTicketTracker();

		if (subTask != null) {
			//subTask.setStatus(updateTask.getStatus());
			ticketTracker.setStatus(updateTask.getStatus());
			ticketTracker.setComments(updateTask.getComments());
			ticketTracker.setUpdatedby(updateTask.getUpdatedby());
			listTicketTracker.add(ticketTracker);
			subTask.setTrack(listTicketTracker);
			subtaskrepository.save(subTask);
			
			
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
	public List<TasksResponseDTO> ticketTrackerByTmsSubTaskId(Long subtaskid) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateSubTaskStatus");
		List<TaskTrackerDTO> tracker = subtaskrepository.ticketTrackerBySubTaskId(subtaskid);
		List<TasksResponseDTO> tasksList = new ArrayList<>();

		for (TaskTrackerDTO taskTrackerDTO : tracker) {
			TasksResponseDTO track = new TasksResponseDTO(taskTrackerDTO);
			GetUsersDTO user = repository.gettmsUser(taskTrackerDTO.getUpdatedby());
			if (taskTrackerDTO.getUpdatedby() != null) {
				track.setFullname(user.getFullname());
			//	track.setPseudoname(user.getPseudoname());
			}
			tasksList.add(track);
		}

		return tasksList;
	}

}