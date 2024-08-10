package com.narvee.service.serviceimpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.dto.RequestResponseDTO;
import com.narvee.dto.SubTaskUserDTO;
import com.narvee.entity.SubTask;
import com.narvee.repository.SubTaskRepository;
import com.narvee.service.service.SubTaskService;

@Service
public class SubTaskServiceImpl implements SubTaskService {

	private static final Logger logger = LoggerFactory.getLogger(SubTaskServiceImpl.class);

	@Autowired
	private SubTaskRepository subtaskrepository;

	@Override
	public SubTask createSubTask(SubTask subtask) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: createSubTask");
		return subtaskrepository.save(subtask);

	}

	@Override
	public SubTask findBySubTaskId(Long subtaskid) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: findBySubTaskId");
		Optional<SubTask> optional = subtaskrepository.findById(subtaskid);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void deleteSubTask(Long subtaskid) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: deleteSubTask");
		subtaskrepository.deleteById(subtaskid);

	}

	@Override
	public Boolean updateSubTask(SubTask updatesubtask) {
		logger.info("!!! inside class: SubTaskServiceImpl , !! method: updateSubTask");
		Optional<SubTask> optional = subtaskrepository.findById(updatesubtask.getSubTaskId());
		if (optional.isPresent()) {
			SubTask subtask = optional.get();
			subtask.setSubTaskName(updatesubtask.getSubTaskName());
			subtask.setSubTaskDescription(updatesubtask.getSubTaskDescription());
			subtask.setAddedBy(updatesubtask.getAddedBy());
			subtask.setUpdatedBy(updatesubtask.getUpdatedBy());
			subtask.setStatus(updatesubtask.getStatus());
			subtask.setTargetDate(updatesubtask.getTargetDate());
			subtaskrepository.save(subtask);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Page<SubTaskUserDTO> getSubTaskUser(RequestResponseDTO requestresponsedto) {
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
		else sortfield = "updateddate";

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
	public Page<SubTask> getAllSubTasks(RequestResponseDTO requestresponsedto) {
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
		else sortfield = "updateddate";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize,
				sort);

		if (keyword.equalsIgnoreCase("empty")) {
			Page<SubTask> page = subtaskrepository.findAll(pageable);
			return page;
		} else {
			logger.info("!!! inside class: SubTaskServiceImpl , !! method: getAllSubTasks Inside Filters");
			return subtaskrepository.getAllSubTasksSortingAndFiltering(pageable, keyword);

		}
	}
}