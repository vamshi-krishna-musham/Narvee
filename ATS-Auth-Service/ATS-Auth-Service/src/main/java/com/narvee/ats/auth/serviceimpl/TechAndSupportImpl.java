package com.narvee.ats.auth.serviceimpl;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.clientdto.FilterTechAndSupportDTO;
import com.narvee.ats.auth.dto.TechAndSupportDTO;
import com.narvee.ats.auth.entity.TechAndSupport;
import com.narvee.ats.auth.repository.ITechSupportRepository;
import com.narvee.ats.auth.service.ITechSupportService;

@Transactional
@Service

public class TechAndSupportImpl implements ITechSupportService {

	public static final Logger logger = LoggerFactory.getLogger(TechAndSupportImpl.class);

	@Autowired
	private ITechSupportRepository repository;

	@Override
	public boolean saveTechSupp(TechAndSupport entity) {
		logger.info("TechAndSupportImpl.saveTechSupp()");
		TechAndSupport dupcheck = repository.findByEmail(entity.getEmail());
		if (dupcheck == null) {
			repository.save(entity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean updateTechSupp(TechAndSupport entity) {
		logger.info("TechAndSupportImpl.updateTechSupp()");
		TechAndSupport dupcheck = repository.findByEmailAndIdNot(entity.getEmail(), entity.getId());
		if (dupcheck == null) {
			repository.save(entity);
			return true;
		} else {
			return false;
		}
	}

//	@Override
//	public List<TechAndSupportDTO> getAll(String search) {
//		logger.info("TechAndSupportImpl.getAll() with search");
//		List<TechAndSupportDTO> findAlln = new ArrayList();
//		return findAlln;
//	}

	@Override
	public List<TechAndSupportDTO> all() {
		logger.info("TechAndSupportImpl.all()");
		return repository.getAll();
	}

	@Override
	public TechAndSupport getTechSupp(Long id) {
		logger.info("TechAndSupportImpl.getTechSuppByid()");
		return repository.findById(id).get();
	}

	@Override
	public boolean deleteSupp(Long id) {
		logger.info("TechAndSupportImpl.deleteSupp()");
		repository.deleteById(id);
		return true;
	}

	@Override
	public int changeStatus(String status, Long id, String remarks) {
		logger.info("TechAndSupportImpl.changeStatus()");
		return repository.toggleStatus(id, remarks);
	}

	@Override
	public Page<TechAndSupportDTO> allWithSortingWithFiltering(FilterTechAndSupportDTO filtertechandsupportdto) {

		Pageable pageable = PageRequest.of(filtertechandsupportdto.getPageNumber() - 1,
				filtertechandsupportdto.getPageSize());
		String keyword = filtertechandsupportdto.getKeyword();
		String sortField = filtertechandsupportdto.getSortField();
		String sortOrder = filtertechandsupportdto.getSortOrder();
		int pageNo = filtertechandsupportdto.getPageNumber();
		int pageSize = filtertechandsupportdto.getPageSize();

		if (sortField.equalsIgnoreCase("Name"))
			sortField = "name";
		else if (sortField.equalsIgnoreCase("Experience"))
			sortField = "experience";
		else if (sortField.equalsIgnoreCase("Technology"))
			sortField = "technologyarea";
		else if (sortField.equalsIgnoreCase("Skills"))
			sortField = "skills";
		else if (sortField.equalsIgnoreCase("Email"))
			sortField = "email";
		else if (sortField.equalsIgnoreCase("ContactNumber"))
			sortField = "mobile";
		else
			sortField = "id";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortField);
		pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		if (filtertechandsupportdto.getKeyword().equalsIgnoreCase("empty")) {
			return repository.getAllWithSorting(pageable);
		} else {
			return repository.getAllWithSortingAndFiltering(pageable, keyword);
		}

	}

}
