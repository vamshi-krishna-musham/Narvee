package com.narvee.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.TaskAssignDTO;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.entity.TmsTask;

@Repository
@Transactional
public interface TaskRepository extends JpaRepository<TmsTask, Long> {

	@Query(value = "select max(maxnum) as max from tms_task", nativeQuery = true)
	public Long maxNumber();

	@Query(value = "select * from tms_ticket_tracker where taskid= :taskid order by createddate desc", nativeQuery = true)
	public List<TaskTrackerDTO> ticketTracker(Long taskid);

	@Modifying
	@Query(value = "update  tms_assigned_users set completed= :completed where userid= :userid and assignid= :assignid", nativeQuery = true)
	public void Iscompletd(boolean completed, Long userid, Long assignid);

	@Query(value = "select ad.pseudoname as createdby, t.ticketid, u.pseudoname, t.createddate,t.updateddate,t.targetdate,au.userstatus as  status from tms_task t\r\n"
			+ "   join users ad on t.addedby = ad.userid join tms_task_users tu on t.taskid = tu.taskid  join tms_assigned_users au  on au.assignid=tu.assignedto\r\n"
			+ "   join users u on u.userid= au.userid  and t.taskid = :taskid", nativeQuery = true)
	public List<TaskAssignDTO> taskAssignInfo(Long taskid);

	@Query(value = "select tt.trackid, tt.status  ,tt.duration, tt.description ,t.targetdate, tt.fromdate, tt.todate,tt.ftime ,tt.ttime , t.ticketid , t.taskname , t.taskid ,u.fullname from tms_ticket_tracker tt , tms_task t  ,users u where t.taskid= tt.taskid and u.userid=tt.updatedby and tt.updatedby= :userid order by tt.trackid desc", nativeQuery = true)
	public List<TaskTrackerDTO> trackerByUser(Long userid);

	@Query(value = "SELECT distinct\r\n" + "    CASE \r\n" + "        WHEN tt.trackid IS NULL THEN ass.pseudoname \r\n"
			+ "        ELSE u.pseudoname \r\n" + "    END AS pseudoname,\r\n" + "    \r\n" + "    CASE \r\n"
			+ "        WHEN tt.trackid IS NULL THEN t.status \r\n" + "        ELSE tt.status\r\n"
			+ "    END AS status,\r\n" + "    tt.trackid,\r\n" + "    t.createddate,\r\n" + "    tt.description,\r\n"
			+ "    tt.fromdate,\r\n" + "    tt.todate,\r\n" + "    tt.ftime,\r\n" + "    tt.ttime,\r\n"
			+ "    t.ticketid,\r\n" + "    t.taskname,\r\n" + "    t.taskid,\r\n" + "    t.targetdate,\r\n"
			+ "    t.description AS taskdescription,\r\n" + "    u.fullname\r\n" + "FROM \r\n" + "    task t\r\n"
			+ "LEFT JOIN \r\n" + "    tms_ticket_tracker tt ON t.taskid = tt.taskid\r\n" + "LEFT JOIN \r\n"
			+ "    users u ON u.userid = tt.updatedby \r\n" + "JOIN \r\n"
			+ "    tms_task_users tu ON t.taskid = tu.taskid\r\n" + "JOIN \r\n"
			+ "    tms_assigned_users au ON au.assignid = tu.assignedto\r\n" + "JOIN \r\n"
			+ "    users ass ON ass.userid = au.userid\r\n" + "ORDER BY \r\n"
			+ "    tt.trackid DESC", nativeQuery = true)
	public List<TaskTrackerDTO> allTasksRecords();

	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate, t.updateddate,t.targetdate, tt.trackid, t.status,t.description as taskdescription, tt.description, tt.fromdate, tt.todate,u.pseudoname\r\n"
			+ "FROM\r\n" + "    tms_task t\r\n" + "LEFT JOIN\r\n"
			+ "    tms_ticket_tracker tt ON t.taskid = tt.taskid\r\n" + "LEFT JOIN\r\n"
			+ "    tms_task_users tu ON tu.taskid = t.taskid\r\n" + "LEFT JOIN\r\n"
			+ "    tms_assigned_users au ON au.assignid = tu.assignedto\r\n" + "LEFT JOIN\r\n"
			+ "    users u ON u.userid = au.userid WHERE DATE(t.createddate) >=:fromDate \r\n"
			+ "	AND t.targetdate <=:toDate \r\n" + "		ORDER BY\r\n" + "		 tt.trackid DESC\r\n"
			+ "", nativeQuery = true)
	public List<TaskTrackerDTO> taskReports(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate,t.updateddate, t.targetdate, tt.trackid, t.status,t.description as taskdescription, tt.description, tt.fromdate, tt.todate,u.pseudoname\r\n"
			+ "FROM\r\n" + "    tms_task t\r\n" + "LEFT JOIN\r\n"
			+ "    tms_ticket_tracker tt ON t.taskid = tt.taskid\r\n" + "LEFT JOIN\r\n"
			+ "    tms_task_users tu ON tu.taskid = t.taskid\r\n" + "LEFT JOIN\r\n"
			+ "    tms_assigned_users au ON au.assignid = tu.assignedto\r\n" + "LEFT JOIN\r\n"
			+ "    users u ON u.userid = tu.userid WHERE DATE(t.createddate) >=:fromDate \r\n"
			+ "	AND t.targetdate <=:toDate AND t.department=:dept \r\n" + "		ORDER BY\r\n"
			+ "		 t.ticketid DESC\r\n" + "", nativeQuery = true)
	public List<TaskTrackerDTO> taskReportsByDepartment(@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate, @Param("dept") String dept);

	@Query(value = "SELECT t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,t.pid "
			+ "FROM tms_task t JOIN tms_project p ON t.pid = p.pid WHERE p.projectid =:projectid AND  t.status=:status ", nativeQuery = true)
	public Page<TaskTrackerDTO> getTaskByProjectid(Pageable pageable, @Param("projectid") String projectid,
			@Param("status") String status);

	@Query(value = "SELECT t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,t.pid "
			+ "FROM tms_task t JOIN tms_project p ON t.pid = p.pid WHERE p.projectid =:projectid AND  t.status=:status AND (t.ticketid LIKE CONCAT('%',:keyword, '%') OR t.taskname LIKE CONCAT('%',:keyword, '%') OR t.description LIKE CONCAT('%',:keyword,  '%') OR t.targetdate LIKE CONCAT('%',:keyword,  '%') "
			+ "OR t.status LIKE CONCAT('%',:keyword, '%'))", nativeQuery = true)
	public Page<TaskTrackerDTO> getTaskByProjectIdWithsearching(Pageable pageable, @Param("projectid") String projectid,
			@Param("status") String status, @Param("keyword") String keyword);

	@Query(value = "select u.userid ,u.pseudoname,u.fullname   FROM users u where u.status ='Active' AND u.department=:department ", nativeQuery = true)
	public List<GetUsersDTO> findDepartmentWiseUsers(String department);
//added vais
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE tms_task SET status=:status, updatedby=:updatedby , updateddate = :updateddate WHERE taskid =:taskid ", nativeQuery = true)
	public int updateTaskStatus(@Param("taskid") Long taskid, @Param("status") String status,
			@Param("updatedby") String updatedby, @Param("updateddate") LocalDateTime updateddate);

	@Query(value = "SELECT  t.taskid,DATE(t.createddate) As createddate,DATE(t.updateddate) AS updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,p.projectid,p.pid,t.duration,t.priority  \r\n"
			+ "			FROM tms_task t  Join tms_project p ON t.pid = p.pid WHERE p.projectid = :projectid Order by t.updateddate DESC ", nativeQuery = true)
	public List<TaskTrackerDTO> findTaskByProjectid(@Param("projectid") String projectid);

	@Query(value = "SELECT t.taskid, DATE(t.createddate) As createddate, DATE(t.updateddate) AS updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,p.projectid,t.pid, t.duration , t.priority  "
			+ "FROM tms_task t JOIN tms_project p ON t.pid = p.pid WHERE p.projectid =:projectid AND (t.ticketid LIKE CONCAT('%',:keyword, '%') OR t.taskname LIKE CONCAT('%',:keyword, '%') OR t.description LIKE CONCAT('%',:keyword,  '%') OR t.targetdate LIKE CONCAT('%',:keyword,  '%') "
			+ "OR t.status LIKE CONCAT('%',:keyword, '%'))", nativeQuery = true)
	public List<TaskTrackerDTO> findTaskByProjectIdWithSearching(@Param("projectid") String projectid,
			@Param("keyword") String keyword);

	@Query(value = "select null as createdby,email, pseudoname from users where userid in (:auserid) union "
			+ "select pseudoname as createdby,email, null as pseudoname from users where userid = :userid ", nativeQuery = true)
	public List<GetUsersDTO> getTaskAssinedUsersAndCreatedBy(long userid, List<Long> auserid);
	

	@Query(value = "select null as createdby,email, concat(first_name,' ',middle_name,' ',last_name) AS fullname  from tms_users where user_id in (:auserid) union "
			+ "select concat(first_name,' ',middle_name,' ',last_name) as createdby,email, null as fullname from tms_users where user_id = :userid ", nativeQuery = true)
	public List<GetUsersDTO> getTaskAssinedTmsUsersAndCreatedBy(long userid, List<Long> auserid);   // tms Users 

	@Query(value = "SELECT "
			+ "    t.taskid, "
			+ "    NULL AS fullname, "
			+ "    NULL AS pseudoname, "
			+ "    NULL AS email, "
			+ "    creator.fullname as cfullname ,"
			+ "    creator.pseudoname as cpseudoname , "
			+ "    creator.email as cemail "
			+ "FROM tms_task t "
			+ "JOIN users creator ON t.addedby = creator.userid  "
			+ "WHERE t.taskid =:taskId "
		
			+ "UNION ALL "
		
			+ "SELECT "
			+ "    t.taskid, "
			+ "    u.fullname , "
			+ "    u.pseudoname , "
			+ "    u.email, \r\n"
			+ "    NULL AS fullname, "
			+ "    NULL AS pseudoname, "
			+ "    NULL AS email "
			+ "FROM tms_task t "
			+ "JOIN tms_task_users tu ON t.taskid = tu.taskid "
			+ "JOIN tms_assigned_users au ON tu.assignedto = au.assignid "
			+ "JOIN users u ON au.userid = u.userid  "
			+ "WHERE t.taskid =:taskId ", nativeQuery = true)
	public List<GetUsersDTO> getAssignUsers(Long taskId);

	@Query(value = "select st.subtaskid ,u.fullname , u.pseudoname,u.email from tms_sub_task st , tms_task_users tu , tms_assigned_users au , users u where st.subtaskid = tu.taskid and \r\n"
			+ "			tu.assignedto= au.assignid and au.userid =u.userid and st.taskid=:subTaskId", nativeQuery = true)
	public List<GetUsersDTO> getSubTaskAssignUsers(Long subTaskId);

	@Query(value = "select u.fullname , u.pseudoname ,u.email from users u where u.userid = :userid ", nativeQuery = true)
	public GetUsersDTO getUser(Long userid);
	

	public TmsTask findByTicketid(String ticketid);

	@Query(value = "Select pid FROM tms_project WHERE projectid = :projectid ", nativeQuery = true)
	public Long findPid(String projectid);

	@Query(value = "SELECT \r\n"
			+ "    t.taskid, \r\n"
			+ "    NULL AS fullname, \r\n"
			+ "    NULL AS pseudoname, \r\n"
			+ "    NULL AS email, \r\n"
			+ "    creator.fullname AS cfullname, \r\n"
			+ "    creator.pseudoname AS cpseudoname, \r\n"
			+ "    creator.email AS cemail \r\n"
			+ "FROM tms_task t\r\n"
			+ "JOIN tms_sub_task ts ON ts.taskid = t.taskid \r\n"
			+ "JOIN users creator ON ts.addedby = creator.userid  \r\n"
			+ "WHERE ts.subtaskid =:subtaskid \r\n"
			+ "UNION ALL\r\n"
			+ "SELECT \r\n"
			+ "    t.taskid, \r\n"
			+ "    u.fullname, \r\n"
			+ "    u.pseudoname, \r\n"
			+ "    u.email, \r\n"
			+ "    NULL AS cfullname, \r\n"
			+ "    NULL AS cpseudoname, \r\n"
			+ "    NULL AS cemail\r\n"
			+ "FROM tms_task t\r\n"
			+ "JOIN tms_task_users tu ON t.taskid = tu.taskid\r\n"
			+ "JOIN tms_assigned_users au ON tu.assignedto = au.assignid\r\n"
			+ "JOIN users u ON au.userid = u.userid\r\n"
			+ "JOIN tms_sub_task ts ON  ts.taskid=t.taskid\r\n"
			+ "WHERE ts.subtaskid =:subtaskid ;", nativeQuery = true)
	public List<GetUsersDTO> getSubtaskAssignUsers(Long subtaskid);

	@Query(value = "SELECT u.userid ,u.pseudoname,u.fullname FROM users u , tms_project p , tms_assigned_users au WHERE  au.pid=p.pid AND  u.userid=au.userid AND p.projectid =:projectId", nativeQuery = true)
	public List<GetUsersDTO> getProjectUsers(String projectId);

	@Query(value = "SELECT t.taskid, t.status,u.fullname, u.pseudoname, t.taskname, t.targetdate, t.ticketid, u.email FROM tms_task t\r\n"
			+ "	join tms_task_users tu on tu.taskid=t.taskid\r\n"
			+ "	join tms_assigned_users au on au.assignid =tu.assignedto\r\n" + "	join users u on au.userid=u.userid\r\n"
			+ "	WHERE date(t.targetdate) < :currentDate AND t.status!='to do' AND t.status!='Completed'", nativeQuery = true)
	public List<TaskTrackerDTO> getExceededTargetDateSubTasks(LocalDate currentDate);
	
	
	
	
	
	
	
	
	
	
	
	
	
	//----------------------------all methods replicated for tms project -------------------
	
	/*@Query(value = "select u.full_name As fullname ,u.email from tms_users u where u.user_id = :userid ", nativeQuery = true)
	public GetUsersDTO getTmsUser(Long userid);  // added by keerthi for tms users */
	@Query(value = "SELECT u.user_id AS userId, " +
            " CONCAT_WS(' ', NULLIF(TRIM(u.first_name), ''), NULLIF(TRIM(u.middle_name), ''), NULLIF(TRIM(u.last_name), '')) AS fullname " +
            "FROM tms_users u, tms_project p, tms_assigned_users au " +
            "WHERE au.pid = p.pid AND u.user_id = au.tms_user_id AND p.projectid = :projectId",
            nativeQuery = true)
    public List<GetUsersDTO> getProjectByTmsUsers(@Param("projectId") String projectId);

	/*@Query(value = "select u.full_name FROM tms_users u  JOIN tms_task t ON t.updatedby = u.user_id WHERE t.updatedby = :updatedby and t.taskid = :taskid ",nativeQuery = true)
	public String getUpdatedByName(Long updatedby,Long taskid);*/@Query(value = "SELECT TRIM(u.full_name) FROM tms_users u JOIN tms_task t ON t.updatedby = u.user_id " +
            "WHERE t.updatedby = :updatedby AND t.taskid = :taskid", nativeQuery = true)
    public String getUpdatedByName(@Param("updatedby") Long updatedby, @Param("taskid") Long taskid);

	
	/*@Query(value = "SELECT u.user_id As userId ,concat(u.first_name,' ',COALESCE(u.middle_name, ''),' ',u.last_name)   As fullName FROM tms_users u , tms_project p , tms_assigned_users au WHERE  au.pid=p.pid AND  u.user_id=au.tms_user_id AND p.projectid =:projectId", nativeQuery = true)
	public List<GetUsersDTO> getProjectByTmsUsers(String projectId);*/
	@Query(value = "SELECT CONCAT_WS(' ', NULLIF(TRIM(u.first_name), ''), NULLIF(TRIM(u.middle_name), ''), NULLIF(TRIM(u.last_name), '')) AS fullname, u.email AS email " +
            "FROM tms_users u WHERE u.user_id = :userid", nativeQuery = true)
    public GetUsersDTO getTmsUser(@Param("userid") Long userid);


	/*@Query(value = "SELECT t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,t.pid,t.duration "
			+ "FROM tms_task t JOIN tms_project p ON t.pid = p.pid WHERE p.projectid =:projectid AND  t.status=:status ", nativeQuery = true)
	public Page<TaskTrackerDTO> getTmsTaskByProjectid(Pageable pageable, @Param("projectid") String projectid,
			@Param("status") String status);*/@Query(value = "SELECT t.taskid, t.createddate, t.updateddate, t.addedby, t.department, t.description, t.maxnum, t.status, t.targetdate, t.ticketid, t.updatedby, t.taskname, t.pid, t.duration " +
		            "FROM tms_task t JOIN tms_project p ON t.pid = p.pid " +
		            "WHERE p.projectid = :projectid AND t.status = :status",
		            nativeQuery = true)
		    public Page<TaskTrackerDTO> getTmsTaskByProjectid(Pageable pageable, @Param("projectid") String projectid,
		            @Param("status") String status);
	
	@Query(value = "SELECT t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,t.pid ,t.duration"
			+ "FROM tms_task t JOIN tms_project p ON t.pid = p.pid WHERE p.projectid =:projectid AND  t.status=:status AND (t.ticketid LIKE CONCAT('%',:keyword, '%') OR t.taskname LIKE CONCAT('%',:keyword, '%') OR t.description LIKE CONCAT('%',:keyword,  '%') OR t.targetdate LIKE CONCAT('%',:keyword,  '%') "
			+ "OR t.status LIKE CONCAT('%',:keyword, '%'))", nativeQuery = true)
	public Page<TaskTrackerDTO> getTmsTaskByProjectIdWithsearching(Pageable pageable, @Param("projectid") String projectid,
			@Param("status") String status, @Param("keyword") String keyword);
	
	/*@Query(value = 
			"       SELECT "
			+ "			  t.taskid,  "
			+ "			    NULL AS fullname, "
			+ "			    NULL AS email, "
			+ "             NULL AS profile  ,   "
			+ "			    concat(creator.first_name,' ',COALESCE(creator.middle_name, ''),' ',creator.last_name)  as cfullname ,"
			+ "			    creator.email as cemail, "
			+ "             creator.profile_photo as cprofile   "
			+ "			FROM tms_task t\r\n"
			+ "			JOIN tms_users creator ON t.addedby = creator.user_id  "
			+ "			WHERE t.taskid = :taskId "
		
			+ "			UNION ALL "
		  
			+ "			SELECT  "
			+ "			    t.taskid,  "
			+ "		   concat(u.first_name,' ',COALESCE(u.middle_name, ''),' ',u.last_name) AS full_name  , "
			+ "			    u.email, "
			+ "             u.profile_photo AS profile ,   "
			+ "			   NULL AS fullname,  "
			+ "			   NULL AS email ,"
			+ "            NULL AS profile   "
			+ "			FROM tms_task t  "
			+ "			JOIN tms_task_users tu ON t.taskid = tu.taskid  "
			+ "			JOIN tms_assigned_users au ON tu.assignedto = au.assignid  "
			+ "			JOIN tms_users u ON au.tms_user_id = u.user_id  "
			+ "		WHERE t.taskid = :taskId ", nativeQuery = true) 

	public List<GetUsersDTO> getTmsAssignUsers(Long taskId);*/
	@Query(value =
	        "SELECT " +
	        "  t.taskid, " +
	        "  CONCAT_WS(' ', NULLIF(TRIM(creator.first_name), ''), NULLIF(TRIM(creator.middle_name), ''), NULLIF(TRIM(creator.last_name), '')) AS fullname, " +
	        "  creator.email AS email " +
	        "FROM tms_task t " +
	        "JOIN tms_users creator ON t.addedby = creator.user_id " +
	        "WHERE t.taskid = :taskId " +
	        "UNION ALL " +
	        "SELECT " +
	        "  t.taskid, " +
	        "  CONCAT_WS(' ', NULLIF(TRIM(u.first_name), ''), NULLIF(TRIM(u.middle_name), ''), NULLIF(TRIM(u.last_name), '')) AS fullname, " +
	        "  u.email AS email " +
	        "FROM tms_task t " +
	        "JOIN tms_task_users tu ON t.taskid = tu.taskid " +
	        "JOIN tms_assigned_users au ON tu.assignedto = au.assignid " +
	        "JOIN tms_users u ON au.tms_user_id = u.user_id " +
	        "WHERE t.taskid = :taskId",
	        nativeQuery = true)
	    public List<GetUsersDTO> getTmsAssignUsers(@Param("taskId") Long taskId);
	/*@Query(value = " select concat(u.first_name,' ', COALESCE(u.middle_name, ''),' ',u.last_name) AS fullname ,u.email from tms_users u where u.user_id = :userid ", nativeQuery = true)

	public GetUsersDTO gettmsUser(Long userid);
	
	@Query(value = "select ad.full_name as createdby, t.ticketid, u.full_name, t.createddate, t.targetdate,au.userstatus as  status from tms_task t\r\n"
			+ "   join tms_users ad on t.addedby = ad.user_id join tms_task_users tu on t.taskid = tu.taskid  join tms_assigned_users au  on au.assignid=tu.assignedto\r\n"
			+ "   join tms_users u on u.user_id= au.tms_user_id  and t.taskid = :taskid", nativeQuery = true)
	public List<TaskAssignDTO> taskTmsAssignInfo(Long taskid);*/
	
	@Query(value = "SELECT CONCAT_WS(' ', NULLIF(TRIM(u.first_name), ''), NULLIF(TRIM(u.middle_name), ''), NULLIF(TRIM(u.last_name), '')) AS fullname, u.email AS email " +
            "FROM tms_users u WHERE u.user_id = :userid", nativeQuery = true)
    public GetUsersDTO gettmsUser(@Param("userid") Long userid);

    // 8) task assign info (keeps existing aliasing; adjust if your DTO expects different names)
    @Query(value = "SELECT ad.full_name as createdby, t.ticketid, u.full_name, t.createddate, t.targetdate, au.userstatus as status " +
            "FROM tms_task t " +
            "JOIN tms_users ad on t.addedby = ad.user_id " +
            "JOIN tms_task_users tu on t.taskid = tu.taskid " +
            "JOIN tms_assigned_users au on au.assignid = tu.assignedto " +
            "JOIN tms_users u on u.user_id = au.tms_user_id " +
            "WHERE t.taskid = :taskid", nativeQuery = true)
    public List<TaskAssignDTO> taskTmsAssignInfo(@Param("taskid") Long taskid);
	
	/*@Query(value = "SELECT  t.taskid,DATE(t.createddate) As createddate,DATE(t.updateddate) AS updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.target_date,t.ticketid,t.updatedby,"
			+ " t.taskname,p.projectid,p.pid,t.duration,t.priority ,t.start_date , CONCAT( "
			+ "        COALESCE(u1.first_name, u2.first_name), ' ',"
			+ "        COALESCE(u1.middle_name, u2.middle_name, ''), ' ',"
			+ "        COALESCE(u1.last_name, u2.last_name) "
			+ "    ) AS fullname "
			+ "	  FROM tms_task t  Join tms_project p ON t.pid = p.pid "
			+ "    LEFT JOIN tms_users u1 ON t.updatedby = u1.user_id  "
			+ "    LEFT JOIN tms_users u2 ON t.addedby = u2.user_id  "
			+ "    WHERE p.projectid = :projectid ", nativeQuery = true)
	public Page<TaskTrackerDTO> findTaskByTmsProjectid(@Param("projectid") String projectid,Pageable pageable);
	
	@Query(value = "SELECT t.taskid, DATE(t.createddate) As createddate, DATE(t.updateddate) AS updateddate,t.addedby,t.department,t.description,t.maxnum, "
			+ " t.status,t.target_date,t.ticketid,t.updatedby,t.taskname,p.projectid,t.pid, t.duration , t.priority  ,t.start_date , "
			+ " CONCAT(  "
			+ "			     COALESCE(u1.first_name, u2.first_name), ' ',"
			+ "			     COALESCE(u1.middle_name, u2.middle_name, ''), ' ', "
			+ "			     COALESCE(u1.last_name, u2.last_name)  "
			+ "		     ) AS fullname "
			+ "  FROM tms_task t JOIN tms_project p ON t.pid = p.pid  "
			+ "  LEFT JOIN tms_users u1 ON t.updatedby = u1.user_id   "
			+ "  LEFT JOIN tms_users u2 ON t.addedby = u2.user_id  "
			+ "  WHERE p.projectid =:projectid AND (t.ticketid LIKE CONCAT('%',:keyword, '%') OR DATE_FORMAT(t.start_date,'%d-%m-%Y') LIKE CONCAT('%',:keyword,  '%')  OR "
			+ " t.taskname LIKE CONCAT('%',:keyword, '%')  OR DATE_FORMAT(t.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') "
			+ " OR t.status LIKE CONCAT('%',:keyword, '%') OR t.priority LIKE CONCAT('%',:keyword, '%') OR t.duration LIKE CONCAT('%',:keyword, '%')  OR "
			+ "   CONCAT( "
			+ "			          COALESCE(u1.first_name, u2.first_name), ' ', "
			+ "		            COALESCE(u1.middle_name, u2.middle_name, ''), ' ', "
			+ "			           COALESCE(u1.last_name, u2.last_name)  "
			+ "			        ) LIKE CONCAT('%', :keyword, '%') )", nativeQuery = true)
	public Page<TaskTrackerDTO> findTaskByTmsProjectIdWithSearching(@Param("projectid") String projectid,
			@Param("keyword") String keyword,Pageable pageable);*/
    @Query(value = "SELECT t.taskid, DATE(t.createddate) AS createddate, DATE(t.updateddate) AS updateddate, t.addedby, t.department, t.description, t.maxnum, t.status, t.target_date, t.ticketid, t.updatedby, t.taskname, p.projectid, p.pid, t.duration, t.priority, t.start_date, " +
            "CONCAT_WS(' ', NULLIF(TRIM(u1.first_name), ''), NULLIF(TRIM(u1.middle_name), ''), NULLIF(TRIM(u1.last_name), '')) AS fullname " +
            "FROM tms_task t JOIN tms_project p ON t.pid = p.pid " +
            "LEFT JOIN tms_users u1 ON t.updatedby = u1.user_id " +
            "LEFT JOIN tms_users u2 ON t.addedby = u2.user_id " +
            "WHERE p.projectid = :projectid", nativeQuery = true)
    public Page<TaskTrackerDTO> findTaskByTmsProjectid(@Param("projectid") String projectid, Pageable pageable);

    // 10) SEARCHING query: includes assigned-user join AND normalized fullname; case-insensitive search
    @Query(value = "SELECT DISTINCT  " +
            "  t.taskid, DATE(t.createddate) AS createddate, DATE(t.updateddate) AS updateddate, t.addedby, t.department, t.description, t.maxnum, t.status, t.target_date, t.ticketid, t.updatedby, t.taskname, p.projectid, t.pid, t.duration, t.priority, t.start_date, " +
            "  CONCAT_WS(' ', NULLIF(TRIM(u1.first_name), ''), NULLIF(TRIM(u1.middle_name), ''), NULLIF(TRIM(u1.last_name), '')) AS fullname " +
            "FROM tms_task t " +
            "JOIN tms_project p ON t.pid = p.pid " +
            "LEFT JOIN tms_users u1 ON t.updatedby = u1.user_id " +
            "LEFT JOIN tms_users u2 ON t.addedby = u2.user_id " +
            "LEFT JOIN tms_task_users tu ON t.taskid = tu.taskid " +
            "LEFT JOIN tms_assigned_users au ON tu.assignedto = au.assignid " +
            "LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
            "WHERE p.projectid = :projectid AND ( " +
            "  LOWER(t.ticketid) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  LOWER(t.taskname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  DATE_FORMAT(t.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') OR " +
            "  DATE_FORMAT(t.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') OR " +
            "  DATE_FORMAT(t.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') OR " +
            "  LOWER(t.status) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  LOWER(t.priority) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  LOWER(t.duration) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  LOWER(CONCAT_WS(' ', NULLIF(TRIM(u1.first_name), ''), NULLIF(TRIM(u1.middle_name), ''), NULLIF(TRIM(u1.last_name), ''))) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  LOWER(CONCAT_WS(' ', NULLIF(TRIM(auu.first_name), ''), NULLIF(TRIM(auu.middle_name), ''), NULLIF(TRIM(auu.last_name), ''))) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            ")", nativeQuery = true)
    public Page<TaskTrackerDTO> findTaskByTmsProjectIdWithSearching(@Param("projectid") String projectid,
            @Param("keyword") String keyword, Pageable pageable);
	
	@Query(value = "Select taskid FROM tms_task WHERE ticketid = :ticketid ", nativeQuery = true)
	public Long findTicketId(String ticketid);
	
	
	@Query(value = "SELECT status, COUNT(*) AS count FROM tms_task GROUP BY status", nativeQuery = true)
	List<Object[]> countTasksByStatus();
	
	@Query(value = "SELECT status, COUNT(*) AS count FROM tms_task  t  where t.pid = :pid GROUP BY status", nativeQuery = true)
	List<Object[]> countTasksByStatusAndPid( Long pid);
	

	@Query(value = "SELECT status, COUNT(*) AS count FROM tms_task t  join tms_task_users tu on  \r\n"
			+ "         t.taskid = tu.taskid join tms_assigned_users au on au.assignid = tu.assignedto join tms_users u on au.tms_user_id = u.user_id where u.user_id = :userid"
			+ "         GROUP BY status",nativeQuery = true)
	List<Object[]> countTasksByStatusAndUser( Long userid);
	
	
	@Query(value = "SELECT  user_role from tms_users where user_id = :userid",nativeQuery=true)
	public String getUserRole(long userid);
   
	@Query(value = "SELECT status, COUNT(*) AS count FROM tms_task t  join tms_task_users tu on  \r\n"
			+ "         t.taskid = tu.taskid join tms_assigned_users au on au.assignid = tu.assignedto join tms_users u on au.tms_user_id = u.user_id where u.user_id = :userid and t.pid = :pid"
			+ "         GROUP BY status",nativeQuery = true)	
	public List<Object[]> getTaskCountByUserAndPid(Long pid, Long userid);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE tms_task SET status=:status, updatedby=:updatedby , updateddate = :updateddate WHERE taskid =:taskid ", nativeQuery = true)
	public int updateTmsTaskStatus(@Param("taskid") Long taskid, @Param("status") String status,
			@Param("updatedby") Long updatedby, LocalDateTime updateddate);
	
	@Query(value = "  select taskname from tms_task where taskid  =  :TaskId",nativeQuery = true)
	public String getTaskName(Long TaskId);
	
	
	@Query(value = "SELECT COUNT(*) FROM tms_task t WHERE t.pid = :projectId AND t.status <> :status",nativeQuery = true)
	long countByProjectIdAndStatusNotTask(@Param("projectId") Long projectId, @Param("status") String status);
	
	@Query(value = "SELECT COUNT(*) FROM tms_sub_task st WHERE st.taskid = :taskid AND st.status <> :status",nativeQuery = true)
	long countByProjectIdAndStatusNot(@Param("taskid") Long taskid, @Param("status") String status);


	
}
