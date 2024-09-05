package com.narvee.service.serviceimpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.dto.SubTaskResponse;
import com.narvee.dto.SubTaskUserDTO;
import com.narvee.entity.TmsAssignedUsers;
import com.narvee.entity.TmsSubTask;

import com.narvee.entity.TmsTask;

import com.narvee.repository.SubTaskRepository;
import com.narvee.repository.TaskRepository;
import com.narvee.service.service.SubTaskService;

@Service
public class SubTaskServiceImpl implements SubTaskService {

	private static final Logger logger = LoggerFactory.getLogger(SubTaskServiceImpl.class);

	@Autowired
	private SubTaskRepository subtaskrepository;

	@Autowired
	private EmailServiceIml emailService;

	@Autowired
	private TaskRepository repository;

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
				GetUsersDTO user = repository.getUser(assignUsers.getUserid());
				assignUsers.setFullname(user.getFullname());
				assignUsers.setPseudoname(user.getPseudoname());
			}
		}
		Long taskId = subtaskrepository.findTaskId(ticketId);
		response.setSubtasks(subTasksList);
		response.setTaskId(taskId);

		return response;
	}

	@Override
	public boolean updateSubTaskStatus(Long subTaskId, String staus, Long updatedby) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateSubTaskStatus");
		TmsSubTask subtasks = subtaskrepository.findById(subTaskId).get();

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
}