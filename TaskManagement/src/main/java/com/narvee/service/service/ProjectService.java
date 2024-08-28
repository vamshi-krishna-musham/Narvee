package com.narvee.service.service;

import org.springframework.data.domain.Page;

import com.narvee.dto.ProjectDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.entity.Project;

public interface ProjectService {
	
	public Project saveproject(Project project);

	public Project findByprojectId(Long projectid);
	
	public void deleteProject(Long projectid);

	public boolean updateproject( Project project);
	
	public Page<ProjectDTO> findAllProjects(RequestDTO requestresponsedto);

}
