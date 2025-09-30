package com.narvee.repository;



import java.time.LocalDate;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.EmailConfigResponseDto;
import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.ProjectDTO;

import com.narvee.entity.TmsProject;
import com.narvee.entity.TmsTask;

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
	
/*@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate ,p.addedby ,  concat(tu.first_name,' ',tu.middle_name,' ',tu.last_name) AS addedByFullname, p.status , p.updatedby ,concat(tu.first_name,' ',tu.middle_name,' ',tu.last_name) AS updatedByFullname, p.projectid  , p.createddate ,p.updateddate"
			+ "  FROM tms_project p   LEFT JOIN  tms_assigned_users au ON au.pid = p.pid join tms_users tu on p.addedby = tu.user_id WHERE "
			+ "   p.admin_id = :addedby OR p.addedby = :addedby  OR au.tms_user_id = :addedby  GROUP BY   p.pid", nativeQuery = true)

	
	public Page<ProjectDTO> findAllTmsProjects(Pageable pageable, Long addedby);
	
	@Query(value = "SELECT p.pid, " +
	        "p.projectname, " +
	        "p.projectdescription, " +
	        "DATE(p.start_date) AS startDate, " +
	        "DATE(p.target_date) AS targetDate, " +
	        "p.addedby, " +

	        "CONCAT(tu.first_name, ' ', tu.middle_name, ' ', tu.last_name) AS addedByFullname, " +

	        "p.status, " +
	        "p.updatedby, " +
	        "p.projectid, " +
	        "p.createddate, " +
	        "p.department, " +

	        "GROUP_CONCAT(CONCAT(auu.first_name, ' ', auu.middle_name, ' ', auu.last_name)) AS assignedTo " +
	        "FROM tms_project p " +
	        "LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
	        "LEFT JOIN tms_users tu ON p.addedby = tu.user_id " +
	        "LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " + // Assigned users
	        "WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby) " +
	        "AND (p.projectname LIKE CONCAT('%', :keyword, '%') " +
	        "OR CONCAT(tu.first_name, ' ', tu.middle_name, ' ', tu.last_name) LIKE CONCAT('%', :keyword, '%') " +
	        "OR CONCAT(auu.first_name, ' ', auu.middle_name, ' ', auu.last_name) LIKE CONCAT('%', :keyword, '%') " + // search assigned user
	        "OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
	        "OR p.status LIKE CONCAT('%', :keyword, '%') " +
	        "OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	        "OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')) " +
	        "GROUP BY p.pid",
	       nativeQuery = true)
	Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable,
	                                               @Param("keyword") String keyword,
	                                               @Param("addedby") Long addedby);

	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate, p.status , p.updatedby , p.projectid , p.department ,p.createddate FROM   tms_project p , tms_assigned_users au where au.pid= p.pid AND au.tms_user_id=:userid", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUser(Long userid,Pageable pageable );  // --- query for get all projects for tms users --- 
	
	@Query(value = "SELECT p.pid, " +



	    // --- query for get all projects for tms users ---
	   @Query(value = "SELECT p.pid, " +
		        "p.projectname, " +
		        "p.projectdescription, " +
		        "DATE(p.start_date) AS startDate, " +
		        "DATE(p.target_date) AS targetDate, " +
		        "p.addedby, " +
		        "CONCAT_WS(' ', tu.first_name, tu.middle_name, tu.last_name) AS addedByFullname, " +
		        "p.status, " +
		        "p.updatedby, " +
		        "p.projectid, " +
		        "p.createddate, " +
		        "p.department, " +
		        "GROUP_CONCAT(DISTINCT CONCAT_WS(' ', auu.first_name, auu.middle_name, auu.last_name)) AS assignedTo " +
		        "FROM tms_project p " +
		        "JOIN tms_assigned_users au ON au.pid = p.pid " +
		        "LEFT JOIN tms_users tu ON p.addedby = tu.user_id " +
		        "LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
		        "WHERE au.tms_user_id = :userid " +
		        "GROUP BY p.pid " +
		        "ORDER BY p.createddate DESC",
		        nativeQuery = true)
		Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable, @Param("userid") Long userid);// added by pratiksha 


	/*@Query(value = "SELECT p.pid ,p.projectname , p.projectdescription , DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate, p.addedby , concat(tu.first_name,' ',tu.middle_name,' ',tu.last_name) AS addedByFullname , p.status , p.updatedby , p.projectid , p.createddate , p.department "

			+ "FROM tms_project  p   LEFT JOIN  tms_assigned_users au ON au.pid = p.pid join tms_users tu on p.addedby = tu.user_id WHERE   ( p.admin_id = :addedby OR p.addedby = :addedby  OR au.tms_user_id = :addedby)  "
			+ "  AND  (p.projectname LIKE CONCAT('%', :keyword, '%') OR concat(tu.first_name,' ',tu.middle_name,' ',tu.last_name) LIKE CONCAT('%', :keyword, '%')  OR  p.projectid LIKE CONCAT('%', :keyword, '%')  OR   p.status LIKE CONCAT('%', :keyword, '%') OR   "
			+ "  DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')"
			+ "OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') ) group by p.pid", nativeQuery = true)

	public Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword,  @Param("addedby") Long addedby);
	                       
@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate, p.status , p.updatedby , p.projectid , p.department ,p.createddate FROM   tms_project p , tms_assigned_users au where au.pid= p.pid AND au.tms_user_id=:userid", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUser(Long userid,Pageable pageable );  // --- query for get all projects for tms users --- 
	
@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription ,  DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate,p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.department FROM tms_project p  , tms_assigned_users au where au.pid= p.pid AND"
			+ " au.tms_user_id=:userid AND (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectid LIKE CONCAT('%', :keyword, '%') or  p.status LIKE CONCAT('%', :keyword, '%')   OR DATE_FORMAT(p.start_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') OR  DATE_FORMAT(p.target_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable, @Param("keyword") String keyword , Long userid); // --- QUERY FOR GET ALL PROJECT BY USER ID FOR TMS USERS ADDED BY KEERTHI */

	//public Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable, @Param("keyword") String keyword,  @Param("addedby") Long addedby);
	// ✅ Repository Interface

	// 1️⃣ Method to fetch all projects (with assignedTo, updatedBy, updatedDate)
	// 1) Admin / manager: all projects (no keyword)

   

/*	@Query(value = "SELECT DISTINCT p.pid, " +

	        "p.projectname, " +
	        "p.projectdescription, " +
	        "DATE(p.start_date) AS startDate, " +
	        "DATE(p.target_date) AS targetDate, " +
	        "p.addedby, " +

	        "CONCAT(tu.first_name, ' ', tu.middle_name, ' ', tu.last_name) AS addedByFullname, " +
	        "p.status, " +
	        "p.updatedby, " +
	        "p.projectid, " +
	        "p.createddate, " +
	        "p.department, " +
	        "GROUP_CONCAT(CONCAT(auu.first_name, ' ', auu.middle_name, ' ', auu.last_name)) AS assignedTo " +
	        "FROM tms_project p " +
	        "JOIN tms_assigned_users au ON au.pid = p.pid " +
	        "LEFT JOIN tms_users tu ON p.addedby = tu.user_id " +
	        "LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
	        "WHERE au.tms_user_id = :userid " +
	        "AND (p.projectname LIKE CONCAT('%', :keyword, '%') " +
	        "OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
	        "OR p.status LIKE CONCAT('%', :keyword, '%') " +
	        "OR DATE_FORMAT(p.start_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') " +
	        "OR DATE_FORMAT(p.target_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%')) " +
	        "GROUP BY p.pid",
	       nativeQuery = true)
	Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable,@Param("keyword") String keyword,@Param("userid") Long userid);
	@Query(value = "select bcc_mails AS bccMails ,cc_mails AS ccMails ,email_notification_type As notificationType ,is_enabled AS isEnabled ,subject from tms_email_configuration where admin_id = :adminId and email_notification_type = :notificationType",nativeQuery = true)
   public EmailConfigResponseDto  getEmailNotificationStatus(Long adminId, String notificationType);
	
	@Query(value = "SELECT admin_id  FROM tms_task ts join tms_project tp on ts.pid = tp.pid where ts.taskid = :TaskId",nativeQuery = true)
	public Long getAdminId(Long TaskId);


    // 2) Admin / manager: with keyword filtering (fixed assignedTo/ name search)
    @Query(value = "SELECT DISTINCT p.pid, " +
            "p.projectname, " +
            "p.projectdescription, " +
            "DATE(p.start_date) AS startDate, " +
            "p.target_date AS targetDate, " +
            "p.addedby, " +
            "REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), '  ', ' ') AS addedByFullname, " +
            "p.status, " +
            "p.updatedby, " +
            "CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
            "     THEN REPLACE(CONCAT(TRIM(uu.first_name),' ',TRIM(COALESCE(uu.middle_name,'')),' ',TRIM(uu.last_name)), '  ', ' ') " +
            "     ELSE REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), '  ', ' ') END AS updatedByFullname, " +
            "p.projectid, " +
            "p.createddate, " +
            "p.updateddate, " +
            "p.department, " +
            "GROUP_CONCAT(REPLACE(CONCAT(TRIM(auu.first_name),' ',TRIM(COALESCE(auu.middle_name,'')),' ',TRIM(auu.last_name)), '  ', ' ')) AS assignedTo " +
            "FROM tms_project p " +
            "LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
            "JOIN tms_users tu ON p.addedby = tu.user_id " +
            "LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
            "LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
            "WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby OR p.updatedby = :updatedby) " +
            "AND ( :keyword = '' " +
            "OR p.projectname LIKE CONCAT('%', :keyword, '%') " +
            "OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
            "OR p.status LIKE CONCAT('%', :keyword, '%') " +
            
           
            "OR LOWER(CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "OR LOWER(CONCAT_WS(' ', TRIM(uu.first_name), NULLIF(TRIM(uu.middle_name), ''), TRIM(uu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "OR LOWER(CONCAT_WS(' ', TRIM(auu.first_name), NULLIF(TRIM(auu.middle_name), ''), TRIM(auu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') ) "+
            "OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
            "OR p.target_date = STR_TO_DATE(:keyword, '%d-%m-%Y') " +
           "OR DATE_FORMAT(p.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') )" +
             "GROUP BY p.pid " ,
         
            nativeQuery = true)
    Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable,
                                                   @Param("keyword") String keyword,
                                                   @Param("addedby") Long addedby,
                                                   @Param("updatedby") Long updatedby);

    // 3) Assigned-user list (non-filtered)
    @Query(value = "SELECT DISTINCT p.pid, " +
            "p.projectname, " +
            "p.projectdescription, " +
            "DATE(p.start_date) AS startDate, " +
            "p.target_date AS targetDate, " +
            "p.addedby, " +
            "CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
            "p.status, " +
            "p.updatedby, " +
            "CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
            "     THEN CONCAT(uu.first_name,' ',COALESCE(uu.middle_name,''),' ',uu.last_name) " +
            "     ELSE CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) END AS updatedByFullname, " +
            "p.projectid, " +
            "p.department, " +
            "p.createddate, " +
            "p.updateddate, " +
            "GROUP_CONCAT(DISTINCT CONCAT_WS(' ', auu.first_name, auu.middle_name, auu.last_name)) AS assignedTo " +
            "FROM tms_project p " +
            "JOIN tms_assigned_users au ON au.pid = p.pid " +
            "JOIN tms_users tu ON p.addedby = tu.user_id " +
            "LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
            "LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
            "WHERE au.tms_user_id = :userid " +
            "GROUP BY p.pid " ,
           
            nativeQuery = true)
    Page<ProjectDTO> getAllProjectsByTmsUser(@Param("userid") Long userid, Pageable pageable);
    
 
        // 1) Get all projects for a TMS user with filtering (search + sort)
            


    // 4) Assigned-user list (with keyword)
    @Query(value = "SELECT DISTINCT p.pid, " +
            "p.projectname, " +
            "p.projectdescription, " +
            
            "p.addedby, " +
            "CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
            "p.status, " +
            "p.updatedby, " +
            "CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
            "     THEN CONCAT(uu.first_name,' ',COALESCE(uu.middle_name,''),' ',uu.last_name) " +
            "     ELSE CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) END AS updatedByFullname, " +
            "p.projectid, " +
            "p.department, " +
            "p.createddate, " +
            "p.updateddate, " +
            "DATE(p.start_date) AS startDate, " +
            "p.target_date AS targetDate, " +
            "GROUP_CONCAT(DISTINCT REPLACE(CONCAT(TRIM(auu.first_name),' ',TRIM(COALESCE(auu.middle_name,'')),' ',TRIM(auu.last_name)), '  ', ' ')) AS assignedTo"+
            "FROM tms_project p " +
            "JOIN tms_assigned_users au ON au.pid = p.pid " +
            "JOIN tms_users tu ON p.addedby = tu.user_id " +
            "LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
            "LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
            "WHERE au.tms_user_id = :userid " +
            "AND ( :keyword = '' " +
            "OR p.projectname LIKE CONCAT('%', :keyword, '%') " +
            "OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
            "OR p.status LIKE CONCAT('%', :keyword, '%') " +
            "OR LOWER(CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "OR LOWER(CONCAT_WS(' ', TRIM(uu.first_name), NULLIF(TRIM(uu.middle_name), ''), TRIM(uu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "OR LOWER(CONCAT_WS(' ', TRIM(auu.first_name), NULLIF(TRIM(auu.middle_name), ''), TRIM(auu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') ) " +
           
            "OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
            "OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')"+
          "OR DATE_FORMAT(p.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')) " +
             "GROUP BY p.pid " ,
        
            nativeQuery = true)
    Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable,
                                                   @Param("keyword") String keyword,
                                                   @Param("userid") Long userid);

	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription , p.addedby , DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate, p.status , p.updatedby , p.projectid , p.department ,p.createddate,p.updateddate FROM   tms_project p , tms_assigned_users au where au.pid= p.pid AND au.tms_user_id=:userid", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUser(Long userid,Pageable pageable );  // --- query for get all projects for tms users --- 
	

	
	@Query(value = "SELECT  p.pid , p.projectname , p.projectdescription ,  DATE(p.start_date) AS startDate , DATE(p.target_date) AS targetDate,p.addedby , p.status , p.updatedby , p.projectid , p.createddate , p.updateddate,p.department FROM tms_project p  , tms_assigned_users au where au.pid= p.pid AND"
			+ " au.tms_user_id=:userid AND (p.projectname LIKE CONCAT('%', :keyword, '%') OR p.projectid LIKE CONCAT('%', :keyword, '%') or  p.status LIKE CONCAT('%', :keyword, '%')   OR DATE_FORMAT(p.start_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') OR  DATE_FORMAT(p.target_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable, @Param("keyword") String keyword , Long userid); // --- QUERY FOR GET ALL PROJECT BY USER ID FOR TMS USERS ADDED BY KEERTHI
	/*@Query(value = "SELECT DISTINCT p.pid, " +
		    "p.projectname, " +
		    "p.projectdescription, " +
		    "DATE(p.start_date) AS startDate, " +
		    "DATE(p.target_date) AS targetDate, " +
		    "p.addedby, " +
		    "CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
		    "p.status, " +
		    "p.updatedby, " +
		    "CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
		    "     THEN CONCAT(uu.first_name,' ',COALESCE(uu.middle_name,''),' ',uu.last_name) " +
		    "     ELSE CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) END AS updatedByFullname, " +
		    "p.projectid, " +
		    "p.department, " +
		    "p.createddate, " +
		    "p.updateddate " +
		    "FROM tms_project p " +
		    "JOIN tms_assigned_users au ON au.pid = p.pid " +
		    "JOIN tms_users tu ON p.addedby = tu.user_id " +
		    "LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
		    "WHERE au.tms_user_id = :userid",
		    nativeQuery = true)
		Page<ProjectDTO> getAllProjectsByTmsUser(@Param("userid") Long userid, Pageable pageable);
	

//    @Query("SELECT new com.narvee.dto.GetUsersDTO(u.fullname, u.email, u.createdby) " +
//            "FROM tms_users u JOIN u.projects p WHERE p.projectid = :projectId")
//     List<GetUsersDTO> getByProjectId(Long projectId);
	
	@Query(value = "SELECT CONCAT(u.first_name, ' ', u.last_name), u.email FROM tms_users u WHERE u.user_id = :userId", nativeQuery = true)
	List<Object[]> findFullNameByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT CONCAT(u.first_name, ' ', u.last_name) FROM tms_users u WHERE u.user_id = :userId", nativeQuery = true)
     String findNameByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT DISTINCT p.pid, " +
		    "p.projectname, " +
		    "p.projectdescription, " +
		    "DATE(p.start_date) AS startDate, " +
		    "DATE(p.target_date) AS targetDate, " +
		    "p.addedby, " +
		    "CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
		    "p.status, " +
		    "p.updatedby, " +
		    "CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
		    "     THEN CONCAT(uu.first_name,' ',COALESCE(uu.middle_name,''),' ',uu.last_name) " +
		    "     ELSE CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) END AS updatedByFullname, " +
		    "p.projectid, " +
		    "p.createddate, " +
		    "p.updateddate, " +
		    "p.department " +
		    "FROM tms_project p " +
		    "JOIN tms_assigned_users au ON au.pid = p.pid " +
		    "JOIN tms_users tu ON p.addedby = tu.user_id " +
		    "LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
		    "WHERE au.tms_user_id = :userid " +
		    "AND (p.projectname LIKE CONCAT('%', :keyword, '%') " +
		    "OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
		    "OR p.status LIKE CONCAT('%', :keyword, '%') " +
		    "OR tu.first_name LIKE CONCAT('%', :keyword, '%') " +
		    "OR tu.middle_name LIKE CONCAT('%', :keyword, '%') " +
		    "OR tu.last_name LIKE CONCAT('%', :keyword, '%') " +
		    "OR uu.first_name LIKE CONCAT('%', :keyword, '%') " +
		    "OR uu.middle_name LIKE CONCAT('%', :keyword, '%') " +
		    "OR uu.last_name LIKE CONCAT('%', :keyword, '%') " +
		    "OR DATE_FORMAT(p.start_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') " +
		    "OR DATE_FORMAT(p.target_date, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%'))",
		    nativeQuery = true)
		Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable,
		                                               @Param("keyword") String keyword,
		                                               @Param("userid") Long userid);

*/

//	@Query(value = "SELECT DISTINCT p.pid, " +
//			"p.projectname, " +
//			"p.projectdescription, " +
//			"DATE(p.start_date) AS startDate, " +
//			"DATE(p.target_date) AS targetDate, " +
//			"p.addedby, " +
//			"REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ') AS addedByFullname, " +
//			"p.status, " +
//			"p.updatedby, " +
//			"CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
//			" THEN REPLACE(CONCAT(TRIM(uu.first_name),' ',TRIM(COALESCE(uu.middle_name,'')),' ',TRIM(uu.last_name)), ' ', ' ') " +
//			" ELSE REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ') END AS updatedByFullname, " +
//			"p.projectid, " +
//			"p.createddate, " +
//			"p.updateddate, " +
//			"p.department, " +
//			"GROUP_CONCAT(DISTINCT REPLACE(CONCAT(TRIM(auu.first_name),' ',TRIM(COALESCE(auu.middle_name,'')),' ',TRIM(auu.last_name)), ' ', ' ')) AS assignedTo " +
//			"FROM tms_project p " +
//			"LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
//			"JOIN tms_users tu ON p.addedby = tu.user_id " +
//			"LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
//			"LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
//			"WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby OR p.updatedby = :updatedby) " +
//			"GROUP BY p.pid " ,
//			nativeQuery = true)
//			Page<ProjectDTO> findAllTmsProjects(Pageable pageable,
//			@Param("addedby") Long addedby,
//			@Param("updatedby") Long updatedby);
//
//			// 2) Admin / manager: with keyword filtering (fixed assignedTo/ name search)
//			@Query(value = "SELECT DISTINCT p.pid, " +
//			"p.projectname, " +
//			"p.projectdescription, " +
//			"DATE(p.start_date) AS startDate, " +
//			"DATE(p.target_date) AS targetDate, " +
//			"p.addedby, " +
//			"REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ') AS addedByFullname, " +
//			"p.status, " +
//			"p.updatedby, " +
//			"CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
//			" THEN REPLACE(CONCAT(TRIM(uu.first_name),' ',TRIM(COALESCE(uu.middle_name,'')),' ',TRIM(uu.last_name)), ' ', ' ') " +
//			" ELSE REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ') END AS updatedByFullname, " +
//			"p.projectid, " +
//			"p.createddate, " +
//			"p.updateddate, " +
//			"p.department, " +
//			"GROUP_CONCAT(REPLACE(CONCAT(TRIM(auu.first_name),' ',TRIM(COALESCE(auu.middle_name,'')),' ',TRIM(auu.last_name)), ' ', ' ')) AS assignedTo " +
//			"FROM tms_project p " +
//			"LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
//			"JOIN tms_users tu ON p.addedby = tu.user_id " +
//			"LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
//			"LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
//			"WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby OR p.updatedby = :updatedby) " +
//			"AND ( :keyword = '' " +
//			"OR p.projectname LIKE CONCAT('%', :keyword, '%') " +
//			"OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
//			"OR p.status LIKE CONCAT('%', :keyword, '%') " +
//			"OR LOWER(CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
//			"OR LOWER(CONCAT_WS(' ', TRIM(uu.first_name), NULLIF(TRIM(uu.middle_name), ''), TRIM(uu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
//			"OR LOWER(CONCAT_WS(' ', TRIM(auu.first_name), NULLIF(TRIM(auu.middle_name), ''), TRIM(auu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') ) "+
//			"OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
//			"OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')"
//			+ "OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
//			"GROUP BY p.pid " ,
//			nativeQuery = true)
//			Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable,
//			@Param("keyword") String keyword,
//			@Param("addedby") Long addedby,
//			@Param("updatedby") Long updatedby);
//
//			// 3) Assigned-user list (non-filtered)
//			@Query(value = "SELECT DISTINCT p.pid, " +
//			"p.projectname, " +
//			"p.projectdescription, " +
//			"DATE(p.start_date) AS startDate, " +
//			"DATE(p.target_date) AS targetDate, " +
//			"p.addedby, " +
//			"CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
//			"p.status, " +
//			"p.updatedby, " +
//			"CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
//			" THEN CONCAT(uu.first_name,' ',COALESCE(uu.middle_name,''),' ',uu.last_name) " +
//			" ELSE CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) END AS updatedByFullname, " +
//			"p.projectid, " +
//			"p.department, " +
//			"p.createddate, " +
//			"p.updateddate, " +
//			"GROUP_CONCAT(DISTINCT CONCAT_WS(' ', auu.first_name, auu.middle_name, auu.last_name)) AS assignedTo " +
//			"FROM tms_project p " +
//			"JOIN tms_assigned_users au ON au.pid = p.pid " +
//			"JOIN tms_users tu ON p.addedby = tu.user_id " +
//			"LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
//			"LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
//			"WHERE au.tms_user_id = :userid " +
//			"GROUP BY p.pid " ,
//			nativeQuery = true)
//			Page<ProjectDTO> getAllProjectsByTmsUser(@Param("userid") Long userid, Pageable pageable);
//
//			// 4) Assigned-user list (with keyword)
//			@Query(value = "SELECT DISTINCT p.pid, " +
//			"p.projectname, " +
//			"p.projectdescription, " +
//			"p.addedby, " +
//			"CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
//			"p.status, " +
//			"p.updatedby, " +
//			"CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
//			" THEN CONCAT(uu.first_name,' ',COALESCE(uu.middle_name,''),' ',uu.last_name) " +
//			" ELSE CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) END AS updatedByFullname, " +
//			"p.projectid, " +
//			"p.department, " +
//			"p.createddate, " +
//			"p.updateddate, " +
//			"DATE(p.start_date) AS startDate, " +
//			"DATE(p.target_date) AS targetDate, " +
//			"GROUP_CONCAT(DISTINCT REPLACE(CONCAT(TRIM(auu.first_name),' ',TRIM(COALESCE(auu.middle_name,'')),' ',TRIM(auu.last_name)), ' ', ' ')) AS assignedTo"+
//			"FROM tms_project p " +
//			"JOIN tms_assigned_users au ON au.pid = p.pid " +
//			"JOIN tms_users tu ON p.addedby = tu.user_id " +
//			"LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
//			"LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
//			"WHERE au.tms_user_id = :userid " +
//			"AND ( :keyword = '' " +
//			"OR p.projectname LIKE CONCAT('%', :keyword, '%') " +
//			"OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
//			"OR p.status LIKE CONCAT('%', :keyword, '%') " +
//			"OR LOWER(CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
//			"OR LOWER(CONCAT_WS(' ', TRIM(uu.first_name), NULLIF(TRIM(uu.middle_name), ''), TRIM(uu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
//			"OR LOWER(CONCAT_WS(' ', TRIM(auu.first_name), NULLIF(TRIM(auu.middle_name), ''), TRIM(auu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') ) " +
//			"OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
//			"OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')"
//			+ "OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
//			"GROUP BY p.pid " ,
//			nativeQuery = true)
//			Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable,
//			@Param("keyword") String keyword,
//			@Param("userid") Long userid);
//
//
//	

//	
    @Query(value = "SELECT DISTINCT p.pid, " +
    		"p.projectname, " +
    		"p.projectdescription, " +
    		"DATE(p.start_date) AS startDate, " +
    		"DATE(p.target_date) AS targetDate, " +
    		"p.addedby, " +
    		"REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ') AS addedByFullname, " +
    		"p.status, " +
    		"p.updatedby, " +
    		"CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
    		" THEN REPLACE(CONCAT(TRIM(uu.first_name),' ',TRIM(COALESCE(uu.middle_name,'')),' ',TRIM(uu.last_name)), ' ', ' ') " +
    		" ELSE REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ') END AS updatedByFullname, " +
    		"p.projectid, " +
    		"p.createddate, " +
    		"p.updateddate, " +
    		"p.department, " +
    		"GROUP_CONCAT(DISTINCT REPLACE(CONCAT(TRIM(auu.first_name),' ',TRIM(COALESCE(auu.middle_name,'')),' ',TRIM(auu.last_name)), ' ', ' ')) AS assignedTo " +
    		"FROM tms_project p " +
    		"LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
    		"JOIN tms_users tu ON p.addedby = tu.user_id " +
    		"LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
    		"LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
    		"WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby OR p.updatedby = :updatedby) " +
    		"GROUP BY p.pid " ,
    		nativeQuery = true)
    		Page<ProjectDTO> findAllTmsProjects(Pageable pageable,
    		@Param("addedby") Long addedby,
    		@Param("updatedby") Long updatedby);

    		// 2) Admin / manager: with keyword filtering (fixed assignedTo/ name search)
    		@Query(value = "SELECT DISTINCT p.pid, " +
    		"p.projectname, " +
    		"p.projectdescription, " +
    		"DATE(p.start_date) AS startDate, " +
    		"DATE(p.target_date) AS targetDate, " +
    		"p.addedby, " +
    		"REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ') AS addedByFullname, " +
    		"p.status, " +
    		"p.updatedby, " +
    		"CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
    		" THEN REPLACE(CONCAT(TRIM(uu.first_name),' ',TRIM(COALESCE(uu.middle_name,'')),' ',TRIM(uu.last_name)), ' ', ' ') " +
    		" ELSE REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ') END AS updatedByFullname, " +
    		"p.projectid, " +
    		"p.createddate, " +
    		"p.updateddate, " +
    		"p.department, " +
    		"GROUP_CONCAT(REPLACE(CONCAT(TRIM(auu.first_name),' ',TRIM(COALESCE(auu.middle_name,'')),' ',TRIM(auu.last_name)), ' ', ' ')) AS assignedTo " +
    		"FROM tms_project p " +
    		"LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
    		"JOIN tms_users tu ON p.addedby = tu.user_id " +
    		"LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
    		"LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
    		"WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby OR p.updatedby = :updatedby) " +
    		"AND ( :keyword = '' " +
    		"OR p.projectname LIKE CONCAT('%', :keyword, '%') " +
    		"OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
    		"OR p.status LIKE CONCAT('%', :keyword, '%') " +
    		"OR LOWER(CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
    		"OR LOWER(CONCAT_WS(' ', TRIM(uu.first_name), NULLIF(TRIM(uu.middle_name), ''), TRIM(uu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
    		"OR LOWER(CONCAT_WS(' ', TRIM(auu.first_name), NULLIF(TRIM(auu.middle_name), ''), TRIM(auu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') ) "+
    		"OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
    		"OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')"
    		+ "OR DATE_FORMAT(p.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
    		"GROUP BY p.pid " ,
    		nativeQuery = true)
    		Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable,
    		@Param("keyword") String keyword,
    		@Param("addedby") Long addedby,
    		@Param("updatedby") Long updatedby);

    		// 3) Assigned-user list (non-filtered)
    		@Query(value = "SELECT DISTINCT p.pid, " +
    		"p.projectname, " +
    		"p.projectdescription, " +
    		"DATE(p.start_date) AS startDate, " +
    		"DATE(p.target_date) AS targetDate, " +
    		"p.addedby, " +
    		"CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
    		"p.status, " +
    		"p.updatedby, " +
    		"CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
    		" THEN CONCAT(uu.first_name,' ',COALESCE(uu.middle_name,''),' ',uu.last_name) " +
    		" ELSE CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) END AS updatedByFullname, " +
    		"p.projectid, " +
    		"p.department, " +
    		"p.createddate, " +
    		"p.updateddate, " +
    		"GROUP_CONCAT(DISTINCT CONCAT_WS(' ', auu.first_name, auu.middle_name, auu.last_name)) AS assignedTo " +
    		"FROM tms_project p " +
    		"JOIN tms_assigned_users au ON au.pid = p.pid " +
    		"JOIN tms_users tu ON p.addedby = tu.user_id " +
    		"LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
    		"LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
    		"WHERE au.tms_user_id = :userid " +
    		"GROUP BY p.pid " ,
    		nativeQuery = true)
    		Page<ProjectDTO> getAllProjectsByTmsUser(@Param("userid") Long userid, Pageable pageable);
    		// 1) Get all projects for a TMS user with filtering (search + sort)

    		// 4) Assigned-user list (with keyword)
    		@Query(value = "SELECT DISTINCT p.pid, " +
    		"p.projectname, " +
    		"p.projectdescription, " +
    		"p.addedby, " +
    		"CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
    		"p.status, " +
    		"p.updatedby, " +
    		"CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
    		" THEN CONCAT(uu.first_name,' ',COALESCE(uu.middle_name,''),' ',uu.last_name) " +
    		" ELSE CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) END AS updatedByFullname, " +
    		"p.projectid, " +
    		"p.department, " +
    		"p.createddate, " +
    		"p.updateddate, " +
    		"DATE(p.start_date) AS startDate, " +
    		"DATE(p.target_date) AS targetDate, " +
    		"GROUP_CONCAT(DISTINCT REPLACE(CONCAT(TRIM(auu.first_name),' ',TRIM(COALESCE(auu.middle_name,'')),' ',TRIM(auu.last_name)), ' ', ' ')) AS assignedTo"+
    		"FROM tms_project p " +
    		"JOIN tms_assigned_users au ON au.pid = p.pid " +
    		"JOIN tms_users tu ON p.addedby = tu.user_id " +
    		"LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
    		"LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
    		"WHERE au.tms_user_id = :userid " +
    		"AND ( :keyword = '' " +
    		"OR p.projectname LIKE CONCAT('%', :keyword, '%') " +
    		"OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
    		"OR p.status LIKE CONCAT('%', :keyword, '%') " +
    		"OR LOWER(CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
    		"OR LOWER(CONCAT_WS(' ', TRIM(uu.first_name), NULLIF(TRIM(uu.middle_name), ''), TRIM(uu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
    		"OR LOWER(CONCAT_WS(' ', TRIM(auu.first_name), NULLIF(TRIM(auu.middle_name), ''), TRIM(auu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') ) " +
    		"OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
    		"OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')"
    		+ "OR DATE_FORMAT(p.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
    		"GROUP BY p.pid " ,
    		nativeQuery = true)
    		Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable,
    		@Param("keyword") String keyword,
    		@Param("userid") Long userid);
    		
    		@Query(value = "select bcc_mails AS bccMails ,cc_mails AS ccMails ,email_notification_type As notificationType ,is_enabled AS isEnabled ,subject from tms_email_configuration where admin_id = :adminId and email_notification_type = :notificationType",nativeQuery = true)
    		public EmailConfigResponseDto getEmailNotificationStatus(Long adminId, String notificationType);
    		
    		@Query(value = "SELECT admin_id FROM tms_task ts join tms_project tp on ts.pid = tp.pid where ts.taskid = :TaskId",nativeQuery = true)
    		public Long getAdminId(Long TaskId);
    		
    		@Query(value = " select Admin_id from tms_users where user_id = :userId",nativeQuery = true)
    		Long AdminId (@Param("userId") Long userId);

			




}
	

