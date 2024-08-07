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

import com.narvee.dto.ProjectUserDTO;
import com.narvee.dto.RequestResponseDTO;
import com.narvee.entity.Project;
import com.narvee.repository.ProjectRepository;
import com.narvee.service.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

	@Autowired
	private ProjectRepository projectrepository;
	  private static final int DIGIT_PADDING =4; 
	    
	    
	@Override
	public Project saveproject(Project project) {
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
	public Project findByprojectId(Long pid) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: findByprojectId");
		Optional<Project> optional = projectrepository.findById(pid);
		if (optional.isPresent()) {
			return optional.get();
		} else
			return null;

	}

	@Override
	public void deleteProject(Long pid) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: deleteProject");
		projectrepository.deleteById(pid);

	}

	@Override
	public boolean updateproject(Project updateproject) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: updateproject");
		Optional<Project> optionalProject = projectrepository.findById(updateproject.getPId());
		if (optionalProject.isPresent()) {
			Project project = optionalProject.get();
			project.setProjectName(updateproject.getProjectName());
			project.setAddedBy(updateproject.getAddedBy());
			project.setUpdatedBy(updateproject.getUpdatedBy());
			project.setDescription(updateproject.getDescription());
			project.setTasks(updateproject.getTasks());
			projectrepository.save(project);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Page<ProjectUserDTO> getProjectUser(RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: getProjectUser");

		String sortfield = requestresponsedto.getSortField();
		String sortorder = requestresponsedto.getSortOrder();

		if (sortfield.equalsIgnoreCase("projectid"))
			sortfield = "projectid";
		else if (sortfield.equalsIgnoreCase("projectname"))
			sortfield = "projectname";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "projectdescription";
		else if (sortfield.equalsIgnoreCase("addedby"))
			sortfield = "fullname";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(requestresponsedto.getPageNumber() - 1, requestresponsedto.getPageSize(),
				sort);
		if (requestresponsedto.getKeyword().equals("empty")) {
			return projectrepository.getProjectUser(pageable);
		} else {
			
			return projectrepository.getProjectUserFiltering(pageable, requestresponsedto.getKeyword());

		}
	}

	@Override
	public Page<Project> findAllProjects(RequestResponseDTO requestresponsedto) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: findAllProjects");
		String sortorder = requestresponsedto.getSortOrder();
		String sortfield = requestresponsedto.getSortField();
		String keyword = requestresponsedto.getKeyword();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();

		if (sortfield.equalsIgnoreCase("projectid"))
			sortfield = "projectId";
		else if (sortfield.equalsIgnoreCase("projectname"))
			sortfield = "projectName";
		else if (sortfield.equalsIgnoreCase("description"))
			sortfield = "description";
		else if (sortfield.equalsIgnoreCase("addedBy"))
			sortfield = "addedBy";
		else sortfield = "updateddate";
		
		System.out.println(sortfield);

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(requestresponsedto.getPageNumber() - 1, requestresponsedto.getPageSize(),
				sort);

		if (keyword.equalsIgnoreCase("empty")) {
			return projectrepository.findAll(pageable);
		} else {
		   return projectrepository.findAllProjectWithFiltering(pageable, keyword);
			
		}

	}

}
