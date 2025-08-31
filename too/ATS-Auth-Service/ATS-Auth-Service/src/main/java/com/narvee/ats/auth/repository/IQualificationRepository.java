package com.narvee.ats.auth.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.narvee.ats.auth.entity.Qualification;


public interface IQualificationRepository extends JpaRepository<Qualification, Long> {
	public Optional<Qualification> findByName(String name);
	public Optional<Qualification> findByNameAndIdNot(String name, Long id);
	
	@Query(value = "select q.id,q.createddate,q.updateddate,q.name,q.addedby,q.updatedby from qualification	q Where (q.name LIKE CONCAT('%', :keyword, '%')) "
			+ "ORDER BY CASE WHEN :sortOrder = 'asc' THEN CASE WHEN :sortField = 'name' THEN q.name END END ASC, CASE WHEN :sortOrder = 'desc' THEN CASE "
			+ "WHEN :sortField = 'name' THEN q.name END END DESC",countQuery = "select count(*) from qualification",nativeQuery = true)
	 public Page<Qualification> getAllQualificationWithSortingAndFiltering(Pageable pageable, @Param("sortField") String sortfield, @Param("sortOrder") String sortorder,@Param("keyword") String keyword);
	

	@Query(value = "select q.id,q.createddate,q.updateddate,q.name,q.addedby,q.updatedby from qualification	q "
			+ "ORDER BY CASE WHEN :sortOrder = 'asc' THEN CASE WHEN :sortField = 'name' THEN q.name END END ASC, CASE WHEN :sortOrder = 'desc' THEN CASE "
			+ "WHEN :sortField = 'name' THEN q.name END END DESC",countQuery = "select count(*) from qualification",nativeQuery = true)
	 public Page<Qualification> getAllQualificationWithOnlySorting(Pageable pageable, @Param("sortField") String sortfield, @Param("sortOrder") String sortorder);
}
