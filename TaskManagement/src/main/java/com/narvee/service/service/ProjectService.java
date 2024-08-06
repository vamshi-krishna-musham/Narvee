package com.narvee.service.service;

import org.springframework.data.domain.Page;

import com.narvee.dto.ProjectUserDTO;
import com.narvee.dto.RequestResponseDTO;
import com.narvee.entity.Project;

public interface ProjectService {
	
	public Project saveproject(Project project);

	public Project findByprojectId(Long projectid);
	
	public void deleteProject(Long projectid);

	public boolean updateproject( Project project);
	
	public Page<ProjectUserDTO> getProjectUser(RequestResponseDTO requestresponsedto);
	
	public Page<Project> findAllProjects(RequestResponseDTO requestresponsedto);

}
