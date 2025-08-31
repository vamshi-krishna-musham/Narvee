package com.narvee.ats.auth.service;

import java.util.List;

import com.narvee.ats.auth.dto.AssociatedCompanyDetailsDTO;
import com.narvee.ats.auth.entity.AssociatedCompanys;

public interface IAssociatedCompanyService {

	public AssociatedCompanys addAssociatedCompany(AssociatedCompanys asscomCompanys);

	List<Long> findByAssociatedCompanyId(Long companyId);

	public void deleteAssociatedCompanyUsingID(Long cid) ;

	public List<AssociatedCompanyDetailsDTO> getAllAssociatedCompany() ;

	public List<AssociatedCompanys> findByAcidAndCompanyCompanyid(Long acid,Long companyid);


	
}
