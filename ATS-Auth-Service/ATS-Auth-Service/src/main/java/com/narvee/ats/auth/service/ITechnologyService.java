package com.narvee.ats.auth.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.springframework.data.domain.Page;

import com.narvee.ats.auth.entity.Technologies;


public interface ITechnologyService {

	public boolean saveTechnologies(Technologies technologies);

	public List<Technologies> getAllTechnologies();

	public List<Object[]> gettechnologies();

	public Technologies getTechnologyByID(long id);

	public boolean deleteTechnologiesById(long id) throws SQLIntegrityConstraintViolationException;

	public int changeStatus(String status, long id, String remarks);

	public String getTechnologySkillsByID(long id);
	
	public Page<Technologies> getAllTechnologies(int pageNo, int pageSize, String field ,String sortField ,String sortOrder);
	
	public boolean isTechnologyAreaAvailable(String technologyarea);
}
