package com.narvee.repository;




import java.util.List;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.EmailConfigResponseDto;
import com.narvee.dto.ProjectDTO;

import com.narvee.entity.TmsProject;

@Repository
@EnableJpaRepositories
public interface ProjectRepository extends JpaRepository<TmsProject, Long> {

	@Query(value = "select max(pmaxnum) as max from tms_project", nativeQuery = true)
	public Long pmaxNumber();

	@Query(value = "SELECT p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate ,p.updateddate, p.department "
			+ "FROM tms_project WHERE (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> findAllProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword);
	
	
	@Query(value = "SELECT p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate ,p.updateddate, p.department "
			+ "FROM tms_project p WHERE p.addedby = :addedby  AND  (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or "
			+ "p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> findAllProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword,  @Param("addedby") Long addedby);



	@Query(value = "SELECT  p.pid, p.projectname, p.projectdescription, p.addedby, p.status,"
			+ " p.updatedby, p.projectid, p.createddate, p.department FROM tms_project p ", nativeQuery = true)
	public Page<ProjectDTO> findAllProjects(Pageable pageable);
    
	@Query(
			  value = "SELECT p.pid, p.projectname, p.projectdescription as description,\r\n"
			  		+ "			          p.addedby, p.status as status, p.updatedby,\r\n"
			  		+ "			          p.projectid ,\r\n"
			  		+ "                      p.target_date\r\n"
			  		+ "			          FROM tms_project p ",
			  nativeQuery = true
			)
			List<ProjectDTO> getAllProjectDetails();


	
	
	@Query(value = "SELECT  p.pid, p.projectname, p.projectdescription , p.addedby , p.status ,"
			+ " p.updatedby , p.projectid , p.createddate , p.department  FROM tms_project p where p.pid= :pid ", nativeQuery = true)

	public ProjectDTO getByProjectId(Long pid);
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.department ,p.createddate,p.updateddate FROM   tms_project p , tms_assigned_users au where au.pid= p.pid AND au.userid=:userid", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByUser(Long userid,Pageable pageable );

	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , p.status , p.updatedby , p.projectid , p.createddate ,p.updateddate, p.department FROM tms_project p  , tms_assigned_users au where au.pid= p.pid AND"
			+ " au.userid=:userid AND (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectdescription LIKE CONCAT('%', :keyword, '%') or p.addedby LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByUserFilter(Pageable pageable, @Param("keyword") String keyword , Long userid);
	
	@Query(value = "select  p.projectid  from tms_project p join tms_task t where t.pid = p.pid and taskid =:taskid",nativeQuery = true)
	public String getProjectName(Long taskid);

  /////-----------------Replicated  methods for tms project --------------
	
	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate, p.status , p.updatedby , p.projectid , p.department ,p.createddate FROM   tms_project p , tms_assigned_users au where au.pid= p.pid AND au.tms_user_id=:userid", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUser(Long userid,Pageable pageable );  // --- query for get all projects for tms users --- 
	
	
//	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription ,  DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate,p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department FROM tms_project p  , tms_assigned_users au where au.pid= p.pid AND"
//			+ " au.tms_user_id=:userid AND (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectid LIKE CONCAT('%', :keyword, '%') or  p.status LIKE CONCAT('%', :keyword, '%')   OR DATE_FORMAT(p.start_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') OR  DATE_FORMAT(p.target_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
//	public Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable, @Param("keyword") String keyword , Long userid); // --- QUERY FOR GET ALL PROJECT BY USER ID FOR TMS USERS ADDED BY KEERTHI

	
	@Query(value = "SELECT p.pid, p.projectname, p.projectdescription, DATE(p.start_date) AS startDate, "
            + "DATE(p.target_date) AS targetDate, p.addedby, p.status, p.updatedby, "
            + "p.projectid, p.createddate, p.department "
            + "FROM tms_project p "
            + "JOIN tms_assigned_users au ON au.pid = p.pid "
            + "JOIN tms_users u ON u.user_id = au.tms_user_id "
            + "WHERE au.tms_user_id = :userid "
            + "AND ( "
            + "LOWER(p.projectname) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(p.projectid) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(p.status) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR DATE_FORMAT(p.start_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') "
            + "OR DATE_FORMAT(p.target_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') "
            + "OR LOWER(CONCAT(u.first_name,' ', COALESCE(u.middle_name, ''),' ',u.last_name)) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + ")", 
       nativeQuery = true)
public Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable, @Param("keyword") String keyword , Long userid);


	@Query(value = "select bcc_mails AS bccMails ,cc_mails AS ccMails ,email_notification_type As notificationType ,is_enabled AS isEnabled ,subject from tms_email_configuration where admin_id = :adminId and email_notification_type = :notificationType",nativeQuery = true)
   public EmailConfigResponseDto  getEmailNotificationStatus(Long adminId, String notificationType);
	
	@Query(value = "SELECT admin_id  FROM tms_task ts join tms_project tp on ts.pid = tp.pid where ts.taskid = :TaskId",nativeQuery = true)
	public Long getAdminId(Long TaskId);
	

//    @Query("SELECT new com.narvee.dto.GetUsersDTO(u.fullname, u.email, u.createdby) " +
//            "FROM tms_users u JOIN u.projects p WHERE p.projectid = :projectId")
//     List<GetUsersDTO> getByProjectId(Long projectId);
	
	@Query(value = "SELECT CONCAT(u.first_name, ' ', u.last_name), u.email FROM tms_users u WHERE u.user_id = :userId", nativeQuery = true)
	List<Object[]> findFullNameByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT CONCAT(u.first_name, ' ', u.last_name) FROM tms_users u WHERE u.user_id = :userId", nativeQuery = true)
     String findNameByUserId(@Param("userId") Long userId);



    		

    		
    		
    		@Query(value = " select Admin_id from tms_users where user_id = :userId",nativeQuery = true)
    		Long AdminId (@Param("userId") Long userId);

			




}
	

