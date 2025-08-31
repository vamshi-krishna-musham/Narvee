package com.narvee.ats.auth.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.narvee.ats.auth.entity.Privilege;

public interface IPrivilegeRepository extends JpaRepository<Privilege, Serializable> {

//	    @Query(
//	        value = "SELECT privilege_id, company_id, card_id FROM privilege_selected_companies WHERE company_id = :companyId",
//	        nativeQuery = true
//	    )
//	    List<PrivilegeCompanyCardProjection> findPrivilegeIdsByCompanyId(@Param("companyId") Long companyId);
//
//	    @Query(
//	    		value = "SELECT * FROM privilege_selected_companies", nativeQuery = true
//	    		)
//	    List<PrivilegeCompanyCardProjection> findAllSelectedCompanies();

	    @Query(value = "SELECT roleid_id,company_id,priv_id FROM privilege_selected_companiess where roleid_id!=':roleId' and company_id!=':compnayId'", nativeQuery = true)
	    List<Object[]> findOtherThanThisRoleAndCompany(Long roleId, Long compnayId);

}
