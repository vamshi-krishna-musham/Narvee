package com.narvee.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.EmailConfigResponseDto;
import com.narvee.dto.ProjectDTO;

import com.narvee.entity.TmsProject;

@Repository
public interface ProjectRepository extends JpaRepository<TmsProject, Long> {

	@Query(value = "select max(pmaxnum) as max from tms_project", nativeQuery = true)
	public Long pmaxNumber();

	@Query(value = "SELECT p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department "
			+ "FROM tms_project WHERE (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> findAllProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword);
	
	
	@Query(value = "SELECT p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department "
			+ "FROM tms_project p WHERE p.addedby = :addedby  AND  (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or "
			+ "p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> findAllProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword,  @Param("addedby") Long addedby);


	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid  , p.createddate , p.department FROM tms_project p ", nativeQuery = true)
	public Page<ProjectDTO> findAllProjects(Pageable pageable, @Param("keyword") String keyword);
    
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid  , p.createddate ,  FROM tms_project p  where p.addedby = :addedby", nativeQuery = true)
	public Page<ProjectDTO> findAllProjects(Pageable pageable, Long addedby);

	
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department  FROM tms_project p where p.pid= :pid ", nativeQuery = true)
	public ProjectDTO getByProjectId(Long pid);
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.department ,p.createddate FROM   tms_project p , tms_assigned_users au where au.pid= p.pid AND au.userid=:userid", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByUser(Long userid,Pageable pageable );

	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department FROM tms_project p  , tms_assigned_users au where au.pid= p.pid AND"
			+ " au.userid=:userid AND (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByUserFilter(Pageable pageable, @Param("keyword") String keyword , Long userid);
	
	@Query(value = "select  p.projectid  from tms_project p join tms_task t where t.pid = p.pid and taskid =:taskid",nativeQuery = true)
	public String getProjectName(Long taskid);

  /////-----------------Replicated  methods for tms project --------------
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate ,p.addedby , tu.first_name AS addedByName, p.status , p.updatedby , p.projectid  , p.createddate "
			+ "  FROM tms_project p   LEFT JOIN  tms_assigned_users au ON au.pid = p.pid join tms_users tu on p.addedby = tu.user_id WHERE "
			+ "   p.admin_id = :addedby OR p.addedby = :addedby  OR au.tms_user_id = :addedby  GROUP BY   p.pid", nativeQuery = true)
	public Page<ProjectDTO> findAllTmsProjects(Pageable pageable, Long addedby);
	
	@Query(value = "SELECT p.pid ,p.projectname , p.projectdescription , DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate, p.addedby , tu.first_name AS addedByName, p.status , p.updatedby , p.projectid , p.createddate , p.department "
			+ "FROM tms_project  p   LEFT JOIN  tms_assigned_users au ON au.pid = p.pid join tms_users tu on p.addedby = tu.user_id WHERE    p.admin_id = :addedby OR p.addedby = :addedby  OR au.tms_user_id = :addedby  GROUP BY   p.pid  "
			+ "  AND  (p.projectname LIKE CONCAT('%', :keyword, '%')  OR  p.projectid LIKE CONCAT('%', :keyword, '%')  OR   p.status LIKE CONCAT('%', :keyword, '%') OR   "
			+ "  DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')"
			+ "OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') GROUP BY   p.pid  )", nativeQuery = true)
	public Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword,  @Param("addedby") Long addedby);
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate, p.status , p.updatedby , p.projectid , p.department ,p.createddate FROM   tms_project p , tms_assigned_users au where au.pid= p.pid AND au.tms_user_id=:userid", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUser(Long userid,Pageable pageable );  // --- query for get all projects for tms users --- 
	
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription ,  DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate,p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department FROM tms_project p  , tms_assigned_users au where au.pid= p.pid AND"
			+ " au.tms_user_id=:userid AND (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectid LIKE CONCAT('%', :keyword, '%') or  p.status LIKE CONCAT('%', :keyword, '%')   OR DATE_FORMAT(p.start_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') OR  DATE_FORMAT(p.target_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable, @Param("keyword") String keyword , Long userid); // --- QUERY FOR GET ALL PROJECT BY USER ID FOR TMS USERS ADDED BY KEERTHI

	
	@Query(value = "select bcc_mails AS bccMails ,cc_mails AS ccMails ,email_notification_type As notificationType ,is_enabled AS isEnabled ,subject from tms_email_configuration where admin_id = :adminId and email_notification_type = :notificationType",nativeQuery = true)
   public EmailConfigResponseDto  getEmailNotificationStatus(Long adminId, String notificationType);
	
	@Query(value = "SELECT admin_id  FROM tms_task ts join tms_project tp on ts.pid = tp.pid where ts.taskid = :TaskId",nativeQuery = true)
	public Long getAdminId(Long TaskId);
	
}
