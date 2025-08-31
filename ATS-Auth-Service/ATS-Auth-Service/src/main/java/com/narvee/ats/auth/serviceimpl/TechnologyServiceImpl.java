package com.narvee.ats.auth.serviceimpl;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.entity.Technologies;
import com.narvee.ats.auth.repository.TechnologyRepository;
import com.narvee.ats.auth.service.ITechnologyService;

@Service
@Transactional
public class TechnologyServiceImpl implements ITechnologyService {
	public static final Logger logger = LoggerFactory.getLogger(TechnologyServiceImpl.class);
	@Autowired
	public TechnologyRepository repository;

	@Override
	public boolean saveTechnologies(Technologies technologies) {
		logger.info("TechnologyServiceImpl.saveTechnologies()");
		Technologies tech = repository.save(technologies);
		if (tech != null)
			return true;
		else
			return false;
	}

	@Override
	public List<Technologies> getAllTechnologies() {
		logger.info("TechnologyServiceImpl.getAllTechnologies()");
		return repository.findAll();
	}

	@Override
	public Page<Technologies> getAllTechnologies(int pageNo, int pageSize, String field, String sortField,
			String sortOrder) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

		if (sortField.equalsIgnoreCase("Technology"))
			sortField = "technologyarea";
		else if(sortField.equalsIgnoreCase("Skills"))
			sortField = "listofkeyword";
		else if(sortField.equalsIgnoreCase("FunctionalSkills"))
			sortField = "functionalSkills";
		else
			sortField = "id";
		
		if (field.equalsIgnoreCase("empty")) {
			return repository.getAllTechnologies(pageable, sortField, sortOrder);
		}
		return repository.getAllTechnologieswithFilter(field, pageable, sortField, sortOrder);
	}

	@Override
	public Technologies getTechnologyByID(long id) {
		logger.info("TechnologyServiceImpl.getTechnologyByID()");
		return repository.findById(id).get();
	}

	@Override
	public boolean deleteTechnologiesById(long id) throws SQLIntegrityConstraintViolationException {
		logger.info("TechnologyServiceImpl.deleteTechnologiesById()");
		// Technologies eid = repository.findById(id).get();
		List<Long> reqs = repository.findrequirementsByTechId(id);
		List<Long> cons = repository.findConsultantByTechId(id);
		if (reqs.size() == 0 && cons.size() == 0) {
			repository.deleteById(id);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Object[]> gettechnologies() {
		logger.info("TechnologyServiceImpl.gettechnologies()");
		return repository.gettechnologies();
	}

	@Override
	public int changeStatus(String status, long id, String remarks) {
		logger.info("TechnologyServiceImpl.changeStatus()");
		return repository.toggleStatus(id, remarks);
	}

	@Override
	public String getTechnologySkillsByID(long id) {
		return repository.gettechnologySkillById(id);
	}

	 @Override
	    public boolean isTechnologyAreaAvailable(String technologyarea) {
		 boolean isDuplicate = repository.existsByTechnologyareaIgnoreCase(technologyarea);
		    return !isDuplicate;

}}
