package com.narvee.ats.auth.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.narvee.ats.auth.entity.Visa;


public interface IVisaRepository extends JpaRepository<Visa, Serializable> {
	@Query(value = "select v.id, v.visa_status from visa v ", nativeQuery = true)
	public List<Object[]> getvisaidname();

	public Optional<Visa> findByVisastatus(String visa);

	public Optional<Visa> findByVisastatusAndVidNot(String visa, Long id);

	@Query(value = "select v.id, v.visa_status from visa v  where v.visa_status not in('GC', 'GC EAD', 'US CITIZEN')", nativeQuery = true)
	public List<Object[]> geth1Via();
	
	@Query(value = "SELECT v.createddate,v.updateddate,v.id,v.visa_status,v.visa_description,v.added_by,v.updatedby,v.contactnumber "
			+ "FROM visa v Where (v.visa_status LIKE CONCAT('%',:keyword, '%')) ORDER BY CASE WHEN :sortOrder = 'asc' THEN CASE "
			+ "WHEN :sortField = 'visa_status' THEN v.visa_status END END ASC, CASE WHEN :sortOrder = 'desc' THEN CASE "
			+ "WHEN :sortField = 'visa_status' THEN v.visa_status END END DESC",countQuery = "select count(*) from visa",nativeQuery = true)
	public Page<Visa> getAllVisaWithSortingAndFiltering(Pageable pageable, @Param("sortField") String sortfield, @Param("sortOrder") String sortorder,@Param("keyword") String keyword);
	
	@Query(value = "SELECT v.createddate,v.updateddate,v.id,v.visa_status,v.visa_description,v.added_by,v.updatedby,v.contactnumber "
			+ "FROM visa v ORDER BY CASE WHEN :sortOrder = 'asc' THEN CASE "
			+ "WHEN :sortField = 'visa_status' THEN v.visa_status END END ASC, CASE WHEN :sortOrder = 'desc' THEN CASE "
			+ "WHEN :sortField = 'visa_status' THEN v.visa_status END END DESC",countQuery = "select count(*) from visa",nativeQuery = true)
	public Page<Visa> getAllVisaWithSorting(Pageable pageable, @Param("sortField") String sortfield, @Param("sortOrder") String sortorder);
	
} 
