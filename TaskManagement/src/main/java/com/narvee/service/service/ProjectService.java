package com.narvee.service.service;

import org.springframework.data.domain.Page;

import com.narvee.dto.ProjectDTO;
import com.narvee.dto.RequestDTO;
import com.narvee.entity.TmsProject;

public interface ProjectService {
	
	public TmsProject saveproject(TmsProject project);
	
	public TmsProject findByprojectId(Long projectid);
	
	public void deleteProject(Long projectid);  // for both Ats and tms users 

	public boolean updateproject( TmsProject project);
	
	public Page<ProjectDTO> findAllProjects(RequestDTO requestresponsedto);
	
	
	 //------------------------ All tms code replicate for tms users  Added by keerthi-------------------
	
	public TmsProject saveTmsproject(TmsProject project); 
	
	public TmsProject findByprojectIdTms(Long projectid); 
	
	public boolean updateprojectTms(TmsProject updateproject) ;
	
}
