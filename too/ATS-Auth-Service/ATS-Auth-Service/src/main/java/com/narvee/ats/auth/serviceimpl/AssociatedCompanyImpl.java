package com.narvee.ats.auth.serviceimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.dto.AssociatedCompanyDetailsDTO;
import com.narvee.ats.auth.entity.AssociatedCompanys;
import com.narvee.ats.auth.repository.IAssociatedCompanyRepository;
import com.narvee.ats.auth.service.IAssociatedCompanyService;

@Service
public class AssociatedCompanyImpl implements IAssociatedCompanyService{

	public static final Logger logger = LoggerFactory.getLogger(AssociatedCompanyImpl.class);
	
	@Autowired
	private IAssociatedCompanyRepository assCmpRepo;
	
	@Override
	public AssociatedCompanys addAssociatedCompany(AssociatedCompanys asscomCompanys) {
		
		logger.info("!!! inside class: AssociatedCompanyImpl, !! method: addAssociatedCompany ");
		
		return assCmpRepo.save(asscomCompanys);
	}

	@Override
	public List<AssociatedCompanyDetailsDTO> getAllAssociatedCompany() {
		logger.info("!!! inside class: AssociatedCompanyImpl, !! method: getAllAssociatedCompany ");
		return assCmpRepo.findAssociatedCompanyId();
	}

	@Override
	public List<Long> findByAssociatedCompanyId(Long companyId) {
		logger.info("!!! inside class: AssociatedCompanyImpl, !! method: findByCompanyId ");
		
		return assCmpRepo.findAssociatedCompanyId(companyId);
	}

	@Override
	public void deleteAssociatedCompanyUsingID(Long cid) {
		logger.info("!!! inside class: AssociatedCompanyImpl, !! method: deleteByAssociatedId ");
		
		assCmpRepo.deleteById(cid);
	}


	@Override
	public List<AssociatedCompanys> findByAcidAndCompanyCompanyid(Long acid, Long companyid) {
		logger.info("!!! inside class: AssociatedCompanyImpl, !! method: deleteByAssociatedId ");
		return assCmpRepo.findByAcidAndCompanyCompanyid(acid, companyid);
	}

}
