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

import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.ProjectDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.entity.TmsAssignedUsers;
import com.narvee.entity.TmsProject;
import com.narvee.repository.ProjectRepository;
import com.narvee.repository.TaskRepository;
import com.narvee.service.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

	@Autowired
	private ProjectRepository projectrepository;
	
	private static final int DIGIT_PADDING = 4;
	
	@Autowired
	private TaskRepository repository;

	@Override
	public TmsProject saveproject(TmsProject project) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: saveProject");
		Long pmaxnumber = projectrepository.pmaxNumber();
		if (pmaxnumber == null) {
			pmaxnumber = 0L;
		}
		String valueWithPadding = String.format("%0" + DIGIT_PADDING + "d", pmaxnumber + 1);
		String value = "PROJ" + valueWithPadding;
		project.setProjectid(value);
		projectrepository.save(project);
		project.setPmaxnum(pmaxnumber + 1);
		return project;
	}

	@Override
	public TmsProject findByprojectId(Long pid) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: findByprojectId");
		TmsProject project = projectrepository.findById(pid).get();
		for (TmsAssignedUsers aUser : project.getAssignedto()) {
			GetUsersDTO user = repository.getUser(aUser.getUserid());
			aUser.setFullname(user.getFullname());
			aUser.setPseudoname(user.getPseudoname());
		}
		return project;
		

	}

	@Override
	public void deleteProject(Long pid) {
		logger.info(
				"!!! inside class: ProjectSe                                                                                                                                                                                                    rviceImpl , !! method: deleteProject");
		projectrepository.deleteById(pid);

	}

	@Override
	public boolean updateproject(TmsProject updateproject) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: updateproject");
		Optional<TmsProject> optionalProject = projectrepository.findById(updateproject.getPId());
		if (optionalProject.isPresent()) {
			TmsProject project = optionalProject.get();
			project.setProjectName(updateproject.getProjectName());
			project.setAddedBy(updateproject.getAddedBy());
			project.setUpdatedBy(updateproject.getUpdatedBy());
			project.setDescription(updateproject.getDescription());
			project.setStatus(updateproject.getStatus());
			project.setTasks(updateproject.getTasks());
			project.setAssignedto(updateproject.getAssignedto());
			projectrepository.save(project);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Page<ProjectDTO> findAllProjects(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: findAllProjects");
		String sortorder = requestresponsedto.getSortOrder();
		String sortfield = requestresponsedto.getSortField();
		String keyword = requestresponsedto.getKeyword();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		Long userid = requestresponsedto.getUserid();
		String access=requestresponsedto.getAccess();

		if (sortfield.equalsIgnoreCase("projectid"))
			sortfield = "projectId";
		else if (sortfield.equalsIgnoreCase("projectname"))
			sortfield = "projectName";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("addedBy"))
			sortfield = "addedBy";
		else
			sortfield = "updateddate";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		if (access.equalsIgnoreCase("Super Administrator")) {
			if (keyword.equalsIgnoreCase("empty")) {
				logger.info("!!! inside class: ProjectServiceImpl , !! method: findAllProjects, Empty");
				return projectrepository.findAllProjects(pageable, keyword);
			} else {
				logger.info("!!! inside class: ProjectServiceImpl , !! method: findAllProjects, Filter");
				return projectrepository.findAllProjectWithFiltering(pageable, keyword);

			}

		} else {

			if (keyword.equalsIgnoreCase("empty")) {
				logger.info("!!! inside class: ProjectServiceImpl , !! method: getAllProjectsByUser, Empty");
				return projectrepository.getAllProjectsByUser(userid, pageable);
			} else {
				logger.info("!!! inside class: ProjectServiceImpl , !! method: getAllProjectsByUserFilter, Filter");
				return projectrepository.getAllProjectsByUserFilter(pageable, keyword, userid);

			}
		}

	}

}
