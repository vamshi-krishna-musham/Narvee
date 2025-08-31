package com.narvee.ats.auth.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.narvee.ats.auth.entity.Technologies;

public interface TechnologyRepository extends JpaRepository<Technologies, Serializable> {

	@Query(value = "select s.id, s.technologyarea,s.listofkeyword from technologies s  order by s.technologyarea", nativeQuery = true)
	List<Object[]> gettechnologies();

	@Query(value = "select listofkeyword from technologies s where s.id = :id ", nativeQuery = true)
	public String gettechnologySkillById(long id);

	public Optional<Technologies> findByTechnologyarea(String technology);

	Page<Technologies> findByTechnologyareaContaining(String name, Pageable pageable);

	@Query("select new com.narvee.ats.auth.entity.Technologies(t.id,t.technologyarea,t.listofkeyword,t.comments,t.remarks)   from Technologies t")
	Page<List<Technologies>> getalltech(Pageable pageable);

	@Modifying
	@Query("UPDATE Technologies c SET c.remarks = :rem  WHERE c.id = :id")
	public int toggleStatus(@Param("id") long id, @Param("rem") String rem);

	@Query(value = "select id from rec_requirements  where techid= :id ", nativeQuery = true)
	public List<Long> findrequirementsByTechId(@Param("id") Long id);

	@Query(value = "select consultantid from consultant_info s where techid= :id ", nativeQuery = true)
	public List<Long> findConsultantByTechId(@Param("id") Long id);

	@Query(value = "SELECT t.functionalSkills, t.id,t.technologyarea,t.addedby,t.comments,t.createddate,t.remarks,t.updatedby,t.updateddate,t.status, t.listofkeyword\r\n"
			+ "FROM technologies t\r\n" + "ORDER BY CASE WHEN :sortOrder = 'asc' THEN CASE "
			+ "WHEN :sortField = 'technologyarea' THEN t.technologyarea "
			+ "WHEN :sortField = 'functionalSkills' THEN t.functionalSkills "
			+ "WHEN :sortField = 'listofkeyword' THEN t.listofkeyword END END ASC,"
			+ "CASE WHEN :sortOrder = 'desc' THEN CASE " + "WHEN :sortField = 'technologyarea' THEN t.technologyarea "
			+ "WHEN :sortField = 'functionalSkills' THEN t.functionalSkills "
			+ "WHEN :sortField = 'listofkeyword' THEN t.listofkeyword END END desc", countQuery = "SELECT count(*) FROM technologies ", nativeQuery = true)
	public Page<Technologies> getAllTechnologies(Pageable pageable, @Param("sortField") String sortField,
			@Param("sortOrder") String sortOrder);

	@Query(value = "SELECT t.functionalSkills, t.id,t.technologyarea,t.addedby,t.comments,t.createddate,t.remarks,t.updatedby,t.updateddate,t.status, t.listofkeyword "
			+ "FROM technologies t " + "WHERE t.technologyarea LIKE CONCAT('%',:keyword, '%') "
			+ "OR t.listofkeyword LIKE CONCAT('%', :keyword, '%') "
			+ "OR t.functionalSkills LIKE CONCAT('%',:keyword, '%') "
			+ "ORDER BY CASE WHEN :sortOrder = 'asc' THEN CASE "
			+ "WHEN :sortField = 'technologyarea' THEN t.technologyarea "
			+ "WHEN :sortField = 'functionalSkills' THEN t.functionalSkills "
			+ "WHEN :sortField = 'listofkeyword' THEN t.listofkeyword END END ASC,"
			+ "CASE WHEN :sortOrder = 'desc' THEN CASE " + "WHEN :sortField = 'technologyarea' THEN t.technologyarea "
			+ "WHEN :sortField = 'functionalSkills' THEN t.functionalSkills "
			+ "WHEN :sortField = 'listofkeyword' THEN t.listofkeyword END END desc", countQuery = "SELECT count(*) FROM technologies", nativeQuery = true)
	public Page<Technologies> getAllTechnologieswithFilter(String keyword, Pageable pageable,
			@Param("sortField") String sortField, @Param("sortOrder") String sortOrder);
	
	 boolean existsByTechnologyareaIgnoreCase(String technologyarea);
}
