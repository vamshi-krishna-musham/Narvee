package com.narvee.ats.auth.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.entity.Qualification;
import com.narvee.ats.auth.repository.IQualificationRepository;
import com.narvee.ats.auth.service.IQualificationService;

@Service
public class QualificationServiceImpl implements IQualificationService {

	private static final Logger logger = LoggerFactory.getLogger(QualificationServiceImpl.class);

	@Autowired
	private IQualificationRepository qualificationRepo;

	@Override
	public boolean saveQualification(Qualification qualification) {
		logger.info("!!! inside class: QualificationServiceImpl, !! method: saveQualification");
		Optional<Qualification> entity = null;
		if (qualification.getId() == null) {
			entity = qualificationRepo.findByName(qualification.getName());
		} else {
			entity = qualificationRepo.findByNameAndIdNot(qualification.getName(), qualification.getId());
		}
		if (!entity.isPresent()) {
			qualificationRepo.save(qualification);
			return true;
		}
		return false;
	}

	@Override
	public Qualification getQualificationById(Long id) {
		logger.info("!!! inside class: QualificationServiceImpl, !! method: getQualificationById");
		Optional<Qualification> qualification = qualificationRepo.findById(id);
		return qualification.get();
	}

	@Override
	public List<Qualification> getAllQualifications() {
		logger.info("!!! inside class: QualificationServiceImpl, !! method: getAllQualification");
		List<Qualification> qualificationList = qualificationRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
		return qualificationList;
	}

	@Override
	public void deleteQualificationById(Long id) {
		logger.info("!!! inside class: QualificationServiceImpl, !! method: deleteQualificationByID");
		qualificationRepo.deleteById(id);
	}

	@Override
	public boolean update(Qualification qualification) {
		logger.info("!!! inside class: QualificationServiceImpl, !! method: Update");
		Optional<Qualification> qualificatio = qualificationRepo.findByNameAndIdNot(qualification.getName(),
				qualification.getId());
		if (!qualificatio.isPresent()) {
			logger.info("Role saved after checking duplicate records available or not");
			qualificationRepo.save(qualification);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Page<Qualification> getAllQualificationWithSortingAndFiltering(SortingRequestDTO qualificationsortandfilterdto) {
		Pageable pageable = PageRequest.of(qualificationsortandfilterdto.getPageNumber()-1, qualificationsortandfilterdto.getPageSize());
		
		String sortedField=qualificationsortandfilterdto.getSortField();
		String sortedOrder=qualificationsortandfilterdto.getSortOrder();
		
		if (sortedField.equalsIgnoreCase("name"))
			sortedField = "name";
		
		
		
		if (qualificationsortandfilterdto.getKeyword().equalsIgnoreCase("empty")) {
			System.out.println("Hi I am in IF Block");
			return qualificationRepo.getAllQualificationWithOnlySorting(pageable,qualificationsortandfilterdto.getSortField(), qualificationsortandfilterdto.getSortOrder());
		}else {
			 System.out.println("Hi I am in ELSE Block");
			return qualificationRepo.getAllQualificationWithSortingAndFiltering(pageable, qualificationsortandfilterdto.getSortField(), qualificationsortandfilterdto.getSortOrder(), qualificationsortandfilterdto.getKeyword());
		}
		
	
	}
	
	
	

}
