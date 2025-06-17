package com.narvee.service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.dto.ProjectDTO;
import com.narvee.dto.ProjectResponseDto;
import com.narvee.dto.RequestDTO;
import com.narvee.entity.TmsProject;


public interface ProjectService {
	
	public TmsProject saveproject(TmsProject project);
	
	public TmsProject findByprojectId(Long projectid);
	
	public void deleteProject(Long projectid);  // for both Ats and tms users 

	public boolean updateproject( TmsProject project);
	
	public Page<ProjectDTO> findAllProjects(RequestDTO requestresponsedto);
	
	
	 //------------------------ All tms code replicate for tms users  Added by keerthi-------------------
	
	public String saveTmsproject(TmsProject project,List<MultipartFile> files) ; 
	
	public TmsProject findByprojectIdTms(Long projectid); 
	
	public TmsProject updateprojectTms(TmsProject updateproject,List<MultipartFile> files) ;
	
	public Page<ProjectResponseDto> findTmsAllProjects(RequestDTO requestresponsedto);
	
}
