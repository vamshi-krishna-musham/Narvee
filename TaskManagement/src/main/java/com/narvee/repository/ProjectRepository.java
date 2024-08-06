package com.narvee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.ProjectUserDTO;
import com.narvee.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query(value = "Select p.projectid, p.projectname, p.projectdescription, u.fullname as addedby from project p, users u where u.userid = p.addedby", nativeQuery = true)
	 public Page<ProjectUserDTO> getProjectUser(Pageable pageable);
	
	
	 @Query(value = "Select p.projectid, p.projectname, p.projectdescription, u.fullname as addedby from project p, users u where u.userid = p.addedby "
	 		+ "AND (p.projectname LIKE CONCAT('%', :keyword, '%')  or p.projectdescription LIKE CONCAT('%', :keyword, '%') OR u.fullname LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	 public Page<ProjectUserDTO> getProjectUserFiltering(Pageable pageable,@Param("keyword") String keyword);
	 
	 @Query(value = "SELECT * FROM project p WHERE (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	 public Page<Project> findAllProjectWithFiltering(Pageable pageable,@Param("keyword") String keyword);
	 
	 
}
