package com.narvee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.ProjectDTO;
import com.narvee.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query(value = "select max(pmaxnum) as max from project", nativeQuery = true)
	public Long pmaxNumber();

	@Query(value = "SELECT p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid FROM project p WHERE (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> findAllProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword);

	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid FROM project p ", nativeQuery = true)
	public Page<ProjectDTO> findAllProjects(Pageable pageable, @Param("keyword") String keyword);

	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid FROM project p where p.pid= :pid ", nativeQuery = true)
	public ProjectDTO getByProjectId(Long pid);

}
