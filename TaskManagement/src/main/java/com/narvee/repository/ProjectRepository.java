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
	
	
	 /*@Query(value = "SELECT DISTINCT p.pid, " +
	    		"p.projectname, " +
	    		"p.projectdescription, " +
	    		"DATE(p.start_date) AS startDate, " +
	    		"DATE(p.target_date) AS targetDate, " +
	    		"p.addedby, " +
	    		
	    		"p.status, " +
	    		"p.updatedby, " +
	    		"CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
	    		" THEN REPLACE(CONCAT(TRIM(uu.first_name),' ',TRIM(COALESCE(uu.middle_name,'')),' ',TRIM(uu.last_name)), ' ', ' ') " +
	    		" ELSE REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ') END AS updatedByFullname, " +
	 
	    		"p.projectid, " +
	    		"p.createddate, " +
	    		"p.updateddate, " +
	       		"REPLACE(CONCAT(TRIM(tu.first_name),' ',TRIM(COALESCE(tu.middle_name,'')),' ',TRIM(tu.last_name)), ' ', ' ')  AS addedByFullname ,  " +
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
		      
		        "OR LOWER(CONCAT_WS(' ', TRIM(auu.first_name), NULLIF(TRIM(auu.middle_name), ''), TRIM(auu.last_name))) LIKE CONCAT('%', LOWER(:keyword), '%') " +
		       
		        "OR DATE_FORMAT(p.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
		    
		        "OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
		    
		        "OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') ) " +
		        "GROUP BY p.pid ",
		        nativeQuery = true)
		Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable,
		                                                @Param("keyword") String keyword,
		                                                @Param("addedby") Long addedby,
		                                                @Param("updatedby") Long updatedby);

	    			 @Query(value = "SELECT p.pid, " +
	    				        "p.projectname, " +
	    				        "p.projectdescription, " +
	    				        "DATE(p.start_date) AS startDate, " +
	    				        "DATE(p.target_date) AS targetDate, " +
	    				        "p.status, " +
	    				        "p.addedby, " +
	    				        "CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
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
	    				        "JOIN tms_assigned_users au_main ON au_main.pid = p.pid AND au_main.tms_user_id = :userid " +
	    				        "JOIN tms_users tu ON p.addedby = tu.user_id " +
	    				        "LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
	    				        "LEFT JOIN tms_assigned_users au_all ON au_all.pid = p.pid " +
	    				        "LEFT JOIN tms_users auu ON au_all.tms_user_id = auu.user_id " +
	    				        "GROUP BY p.pid",
	    				        nativeQuery = true)
	    				Page<ProjectDTO> getAllProjectsByTmsUser(@Param("userid") Long userid, Pageable pageable);
	    			 @Query(value = "SELECT p.pid, " +
	    				        "p.projectname, " +
	    				        "p.projectdescription, " +
	    				        "DATE(p.start_date) AS startDate, " +
	    				        "DATE(p.target_date) AS targetDate, " +
	    				        "p.status, " +
	    				        "p.addedby, " +
	    				        "CONCAT(tu.first_name,' ',COALESCE(tu.middle_name,''),' ',tu.last_name) AS addedByFullname, " +
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
	    				        "JOIN tms_assigned_users au_main ON au_main.pid = p.pid AND au_main.tms_user_id = :userid " +
	    				        "JOIN tms_users tu ON p.addedby = tu.user_id " +
	    				        "LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
	    				        "LEFT JOIN tms_assigned_users au_all ON au_all.pid = p.pid " +
	    				        "LEFT JOIN tms_users auu ON au_all.tms_user_id = auu.user_id " +
	    				        "WHERE (:keyword = '' " +
	    				        "   OR p.projectname LIKE CONCAT('%', :keyword, '%') " +
	    				        "   OR p.projectid LIKE CONCAT('%', :keyword, '%') " +
	    				        "   OR p.status LIKE CONCAT('%', :keyword, '%') " +
	    				        "   OR LOWER(CONCAT_WS(' ', tu.first_name, tu.middle_name, tu.last_name)) LIKE CONCAT('%', LOWER(:keyword), '%') " +
	    				        "   OR LOWER(CONCAT_WS(' ', uu.first_name, uu.middle_name, uu.last_name)) LIKE CONCAT('%', LOWER(:keyword), '%') " +
	    				        "   OR LOWER(CONCAT_WS(' ', auu.first_name, auu.middle_name, auu.last_name)) LIKE CONCAT('%', LOWER(:keyword), '%') " +
	    				        "   OR DATE_FORMAT(p.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	    				        "   OR DATE_FORMAT(p.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	    				        "   OR DATE_FORMAT(p.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')) " +
	    				        "GROUP BY p.pid",
	    				        nativeQuery = true)
	    				Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable,
	    				                                               @Param("keyword") String keyword,
	    				                                               @Param("userid") Long userid);
*/  
	@Query(value = "SELECT * FROM (" +
	        " SELECT DISTINCT p.pid, " +
	        " p.projectname, " +
	        " p.projectdescription, " +
	        " DATE(p.start_date) AS startDate, " +
	        " DATE(p.target_date) AS targetDate, " +
	        " p.addedby, " +
	        " CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name)) AS addedByFullname, " +
	        " p.status, " +
	        " p.updatedby, " +
	        " CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
	        "      THEN CONCAT_WS(' ', TRIM(uu.first_name), NULLIF(TRIM(uu.middle_name), ''), TRIM(uu.last_name)) " +
	        "      ELSE CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name)) END AS updatedByFullname, " +
	        " p.projectid, " +
	        " p.createddate, " +
	        " p.updateddate, " +
	        " p.department, " +
	        " GROUP_CONCAT(DISTINCT CONCAT_WS(' ', TRIM(auu.first_name), NULLIF(TRIM(auu.middle_name), ''), TRIM(auu.last_name))) AS assignedTo " +
	        " FROM tms_project p " +
	        " LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
	        " JOIN tms_users tu ON p.addedby = tu.user_id " +
	        " LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
	        " LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
	        " WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby OR p.updatedby = :updatedby) " +
	        " GROUP BY p.pid " +
	        ") t",countQuery = "SELECT COUNT(*) FROM (" +
	                 " SELECT p.pid " +
	                 " FROM tms_project p " +
	                 " LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
	                 " WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby OR p.updatedby = :updatedby) " +
	                 " GROUP BY p.pid" +
	                 ") t",
	    nativeQuery = true
	)
	Page<ProjectDTO> findAllTmsProjects(Pageable pageable,
	                                    @Param("addedby") Long addedby,
	                                    @Param("updatedby") Long updatedby);

	@Query(value = "SELECT * FROM (" +
	        " SELECT DISTINCT p.pid, " +
	        " p.projectname, " +
	        " p.projectdescription, " +
	        " DATE(p.start_date) AS startDate, " +
	        " DATE(p.target_date) AS targetDate, " +
	        " p.addedby, " +
	        " CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name)) AS addedByFullname, " +
	        " p.status, " +
	        " p.updatedby, " +
	        " CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
	        "      THEN CONCAT_WS(' ', TRIM(uu.first_name), NULLIF(TRIM(uu.middle_name), ''), TRIM(uu.last_name)) " +
	        "      ELSE CONCAT_WS(' ', TRIM(tu.first_name), NULLIF(TRIM(tu.middle_name), ''), TRIM(tu.last_name)) END AS updatedByFullname, " +
	        " p.projectid, " +
	        " p.createddate, " +
	        " p.updateddate, " +
	        " p.department, " +
	        " GROUP_CONCAT(DISTINCT CONCAT_WS(' ', TRIM(auu.first_name), NULLIF(TRIM(auu.middle_name), ''), TRIM(auu.last_name))) AS assignedTo " +
	        " FROM tms_project p " +
	        " LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
	        " JOIN tms_users tu ON p.addedby = tu.user_id " +
	        " LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
	        " LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
	        " WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby OR p.updatedby = :updatedby) " +
	        " GROUP BY p.pid " +
	        ") t " +
	        "WHERE (:keyword = '' " +
	        " OR t.projectname LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.projectid LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.status LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.addedByFullname LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.updatedByFullname LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.assignedTo LIKE CONCAT('%', :keyword, '%') " +
	        " OR DATE_FORMAT(t.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	        " OR DATE_FORMAT(t.startDate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	        " OR DATE_FORMAT(t.targetDate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')) ",
	        countQuery = "SELECT COUNT(*) FROM (" +
	                 " SELECT p.pid " +
	                 " FROM tms_project p " +
	                 " LEFT JOIN tms_assigned_users au ON au.pid = p.pid " +
	                 " WHERE (p.admin_id = :addedby OR p.addedby = :addedby OR au.tms_user_id = :addedby OR p.updatedby = :updatedby) " +
	                 " GROUP BY p.pid" +
	                 ") t",
	    nativeQuery = true
	)
	Page<ProjectDTO> findAllTmsProjectWithFiltering(Pageable pageable,
	                                                @Param("keyword") String keyword,
	                                                @Param("addedby") Long addedby,
	                                                @Param("updatedby") Long updatedby);

	@Query(value = "SELECT * FROM (" +
	        " SELECT p.pid, " +
	        " p.projectname, " +
	        " p.projectdescription, " +
	        " DATE(p.start_date) AS startDate, " +
	        " DATE(p.target_date) AS targetDate, " +
	        " p.status, " +
	        " p.addedby, " +
	        " CONCAT_WS(' ', tu.first_name, tu.middle_name, tu.last_name) AS addedByFullname, " +
	        " p.updatedby, " +
	        " CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
	        "      THEN CONCAT_WS(' ', uu.first_name, uu.middle_name, uu.last_name) " +
	        "      ELSE CONCAT_WS(' ', tu.first_name, tu.middle_name, tu.last_name) END AS updatedByFullname, " +
	        " p.projectid, " +
	        " p.department, " +
	        " p.createddate, " +
	        " p.updateddate, " +
	        " GROUP_CONCAT(DISTINCT CONCAT_WS(' ', auu.first_name, auu.middle_name, auu.last_name)) AS assignedTo " +
	        " FROM tms_project p " +
	        " JOIN tms_assigned_users au_main ON au_main.pid = p.pid AND au_main.tms_user_id = :userid " +
	        " JOIN tms_users tu ON p.addedby = tu.user_id " +
	        " LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
	        " LEFT JOIN tms_assigned_users au_all ON au_all.pid = p.pid " +
	        " LEFT JOIN tms_users auu ON au_all.tms_user_id = auu.user_id " +
	        " GROUP BY p.pid " +
	        ") t",
	        countQuery = "SELECT COUNT(*) FROM (" +
	                 " SELECT p.pid " +
	                 " FROM tms_project p " +
	                 " JOIN tms_assigned_users au_main ON au_main.pid = p.pid AND au_main.tms_user_id = :userid " +
	                 " GROUP BY p.pid" +
	                 ") t",
	    nativeQuery = true
	)
	Page<ProjectDTO> getAllProjectsByTmsUser(@Param("userid") Long userid, Pageable pageable);

	@Query(value = "SELECT * FROM (" +
	        " SELECT p.pid, " +
	        " p.projectname, " +
	        " p.projectdescription, " +
	        " DATE(p.start_date) AS startDate, " +
	        " DATE(p.target_date) AS targetDate, " +
	        " p.status, " +
	        " p.addedby, " +
	        " CONCAT_WS(' ', tu.first_name, tu.middle_name, tu.last_name) AS addedByFullname, " +
	        " p.updatedby, " +
	        " CASE WHEN p.updatedby IS NOT NULL AND p.updatedby <> p.addedby " +
	        "      THEN CONCAT_WS(' ', uu.first_name, uu.middle_name, uu.last_name) " +
	        "      ELSE CONCAT_WS(' ', tu.first_name, tu.middle_name, tu.last_name) END AS updatedByFullname, " +
	        " p.projectid, " +
	        " p.department, " +
	        " p.createddate, " +
	        " p.updateddate, " +
	        " GROUP_CONCAT(DISTINCT CONCAT_WS(' ', auu.first_name, auu.middle_name, auu.last_name)) AS assignedTo " +
	        " FROM tms_project p " +
	        " JOIN tms_assigned_users au_main ON au_main.pid = p.pid AND au_main.tms_user_id = :userid " +
	        " JOIN tms_users tu ON p.addedby = tu.user_id " +
	        " LEFT JOIN tms_users uu ON p.updatedby = uu.user_id " +
	        " LEFT JOIN tms_assigned_users au_all ON au_all.pid = p.pid " +
	        " LEFT JOIN tms_users auu ON au_all.tms_user_id = auu.user_id " +
	        " GROUP BY p.pid " +
	        ") t " +
	        "WHERE (:keyword = '' " +
	        " OR t.projectname LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.projectid LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.status LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.addedByFullname LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.updatedByFullname LIKE CONCAT('%', :keyword, '%') " +
	        " OR t.assignedTo LIKE CONCAT('%', :keyword, '%') " +
	        " OR DATE_FORMAT(t.startDate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	        " OR DATE_FORMAT(t.targetDate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	        " OR DATE_FORMAT(t.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')) ",
	        countQuery = "SELECT COUNT(*) FROM (" +
	                 " SELECT p.pid " +
	                 " FROM tms_project p " +
	                 " JOIN tms_assigned_users au_main ON au_main.pid = p.pid AND au_main.tms_user_id = :userid " +
	                 " GROUP BY p.pid" +
	                 ") t",
	    nativeQuery = true
	)
	Page<ProjectDTO> getAllProjectsByTmsUserFilter(Pageable pageable,
	                                               @Param("keyword") String keyword,
	                                               @Param("userid") Long userid);

		@Query(value = "select bcc_mails AS bccMails ,cc_mails AS ccMails ,email_notification_type As notificationType ,is_enabled AS isEnabled ,subject from tms_email_configuration where admin_id = :adminId and email_notification_type = :notificationType",nativeQuery = true)
	    		public EmailConfigResponseDto getEmailNotificationStatus(Long adminId, String notificationType);
	  	
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
	

