package com.narvee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.ProjectDTO;
import com.narvee.entity.TmsProject;

@Repository
public interface ProjectRepository extends JpaRepository<TmsProject, Long> {

	@Query(value = "select max(pmaxnum) as max from tms_project", nativeQuery = true)
	public Long pmaxNumber();

	@Query(value = "SELECT p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department "
			+ "FROM tms_tms_project WHERE (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> findAllProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword);
	
	
	@Query(value = "SELECT p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department "
			+ "FROM tms_tms_project WHERE p.addedby = :addedby  AND  (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or "
			+ "p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword,  @Param("addedby") Long addedby);


	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid  , p.createddate , p.department FROM tms_project p ", nativeQuery = true)
	public Page<ProjectDTO> findAllProjects(Pageable pageable, @Param("keyword") String keyword);
    
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid  , p.createddate , p.department FROM tms_project p  where p.addedby = :addedby", nativeQuery = true)
	public Page<ProjectDTO> findAllTmsProjects(Pageable pageable, Long addedby);

	
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department  FROM tms_project p where p.pid= :pid ", nativeQuery = true)
	public ProjectDTO getByProjectId(Long pid);
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.department ,p.createddate FROM   tms_project p , tms_assigned_users au where au.pid= p.pid AND au.userid=:userid", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByUser(Long userid,Pageable pageable );
	
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.department ,p.createddate FROM   tms_project p , tms_assigned_users au where au.pid= p.pid AND au.tms_user_id=:userid", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUser(Long userid,Pageable pageable );  // --- query for get all projects for tms users --- 
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department FROM tms_project p  , tms_assigned_users au where au.pid= p.pid AND"
			+ " au.userid=:userid AND (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByUserFilter(Pageable pageable, @Param("keyword") String keyword , Long userid);
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department FROM tms_project p  , tms_assigned_users au where au.pid= p.pid AND"
			+ " au.userid=:userid AND (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable, @Param("keyword") String keyword , Long userid); // --- QUERY FOR GET ALL PROJECT BY USER ID FOR TMS USERS ADDED BY KEERTHI


}
