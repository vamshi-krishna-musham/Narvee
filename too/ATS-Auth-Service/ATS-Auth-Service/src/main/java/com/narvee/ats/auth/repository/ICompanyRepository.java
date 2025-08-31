package com.narvee.ats.auth.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.narvee.ats.auth.dto.CompanyDTO;
import com.narvee.ats.auth.entity.Company;

@Repository
public interface ICompanyRepository extends JpaRepository<Company, Serializable> {
	public Optional<Company> findByCompanyname(String companyname);

	public Optional<Company> findByCompanynameAndCompanyidNot(String companyname, Long companyid);

	@Query(value = "SELECT cid,companyname FROM company where domain IS NOT NULL AND code IS NOT NULL AND cid in (:companies) ", nativeQuery = true)
	public List<Object[]> getcompanies(List<Long> companies);
	
	@Query(value = "SELECT cid,companyname FROM company", nativeQuery = true)
	public List<Object[]> getCompaniesDropdown();

	@Query(value = "select c.cid as companyid, c.domain , c.code , c.companyname, c.createddate, c.description, c.updatedby, c.addedby, c.updateddate , u.pseudoname as pseudoname from company c , users u where u.userid=c.addedby  ", nativeQuery = true)
	public Page<CompanyDTO> getAllCompaniesnWithOnlySorting(Pageable pageable);

	@Query(value = "select c.cid as companyid, c.domain , c.code , c.companyname, c.createddate, c.description, c.updatedby, c.addedby, c.updateddate , u.pseudoname as pseudoname from company c , users u where u.userid=c.addedby AND"
			+ " (c.companyname LIKE CONCAT('%',:keyword, '%') OR c.domain LIKE CONCAT('%',:keyword, '%') OR u.pseudoname LIKE CONCAT('%',:keyword, '%') OR  c.code LIKE CONCAT('%',:keyword, '%'))", nativeQuery = true)
	public Page<CompanyDTO> getAllCompaniesnWithSortingAndFiltering(Pageable pageable, String keyword);

	public Company findByCode(String code);

	public Company findByDomain(String domain);
	
	
	@Query(value = "SELECT \r\n"
			+ "  CASE \r\n"
			+ "    WHEN EXISTS (\r\n"
			+ "      SELECT 1 FROM consultant_info ci WHERE ci.company_id = :cid\r\n"
			+ "      UNION ALL\r\n"
			+ "      SELECT 1 FROM rec_requirements r WHERE r.company_id = :cid\r\n"
			+ "      UNION ALL\r\n"
			+ "      SELECT 1 FROM users u WHERE u.companyid = :cid\r\n"
			+ "    )\r\n"
			+ "    THEN 1\r\n"
			+ "    ELSE 0\r\n"
			+ "  END AS result",nativeQuery = true)
	public Integer checkCompanyDataExists(Long cid );
	

}