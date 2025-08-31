package com.narvee.ats.auth.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.narvee.ats.auth.dto.AssociatedCompanyDetailsDTO;
import com.narvee.ats.auth.entity.AssociatedCompanys;

public interface IAssociatedCompanyRepository extends JpaRepository<AssociatedCompanys, Serializable> {

	@Query(value = "select ac.id, c.companyname as cmpname,ac.a_cid,cmp.companyname as associtedname from company c join associated_companys ac on c.cid=ac.company_id join company cmp on cmp.cid=ac.a_cid Order by ac.updateddate desc", nativeQuery = true)
	List<AssociatedCompanyDetailsDTO> findAssociatedCompanyId();

	@Query(value = "select ac.a_cid from associated_companys ac where ac.company_id=:compnayId", nativeQuery = true)
	List<Long> findAssociatedCompanyId(Long compnayId);
	
	List<AssociatedCompanys> findByAcidAndCompanyCompanyid(Long acid,Long companyid);
	

}
