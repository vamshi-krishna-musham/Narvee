package com.narvee.ats.auth.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.narvee.ats.auth.dto.TechAndSupportDTO;
import com.narvee.ats.auth.entity.TechAndSupport;

public interface ITechSupportRepository extends JpaRepository<TechAndSupport, Serializable> {
	public TechAndSupport findByEmailAndIdNot(String email, long id);

	public TechAndSupport findByEmail(String email);

	@Query(value = "select t.name, t.experience,s.technologyarea as technology,t.skills, t.email, t.mobile, t.second_mobile,t.id from techsupport t, technologies s\r\n"
			+ "where t.techid = s.id order by t.name", nativeQuery = true)
	public List<TechAndSupportDTO> getAll();

	@Modifying
	@Query("UPDATE TechAndSupport c SET c.remarks = :rem  WHERE c.id =:id")
	public int toggleStatus(@Param("id") Long id, @Param("rem") String rem);
	
	
	@Query(value = "SELECT t.id ,t.name, t.experience, s.technologyarea AS technologyarea, t.skills, t.email, t.mobile "
	        + "FROM techsupport t JOIN technologies s ON t.techid = s.id ", nativeQuery = true)
	public Page<TechAndSupportDTO> getAllWithSorting(Pageable pageable);
	
	
	
//	@Query(value = "SELECT t.id, t.name, t.experience, s.technologyarea AS technology, t.skills, t.email, t.mobile FROM techsupport t JOIN technologies s ON t.id = s.id AND"
//			+ " (t.name  LIKE CONCAT('%',:keyword, '%') OR t.experience  LIKE CONCAT('%',:keyword, '%') OR s.technologyarea  LIKE CONCAT('%',:keyword, '%') OR "
//			+ "t.skills  LIKE CONCAT('%',:keyword, '%')  OR t.email  LIKE CONCAT('%',:keyword, '%')   OR t.mobile  LIKE CONCAT('%',:keyword, '%')  )", nativeQuery = true)
	@Query(value = "SELECT t.id, t.name, t.experience, s.technologyarea AS technologyarea, t.skills, t.email, t.mobile " +
            "FROM techsupport t " +
            "JOIN technologies s ON t.techid = s.id " +
            "WHERE (t.name LIKE CONCAT('%', :keyword, '%') " +
            "OR t.experience LIKE CONCAT('%', :keyword, '%') " +
            "OR s.technologyarea LIKE CONCAT('%', :keyword, '%') " +
            "OR t.skills LIKE CONCAT('%', :keyword, '%') " +
            "OR t.email LIKE CONCAT('%', :keyword, '%') " +
            "OR t.mobile LIKE CONCAT('%', :keyword, '%'))",
    nativeQuery = true)
	public Page<TechAndSupportDTO> getAllWithSortingAndFiltering(Pageable pageable, @Param("keyword") String keyword);


}
