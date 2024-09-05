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

import com.narvee.entity.TmsAssignedUsers;

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

	@Query(value = "select ad.pseudoname as createdby, t.ticketid, u.pseudoname, t.createddate, t.targetdate,au.userstatus as  status from tms_task t\r\n"
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

	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate, t.targetdate, tt.trackid, t.status,t.description as taskdescription, tt.description, tt.fromdate, tt.todate,u.pseudoname\r\n"
			+ "FROM\r\n" + "    tms_task t\r\n" + "LEFT JOIN\r\n" + "    tms_ticket_tracker tt ON t.taskid = tt.taskid\r\n"
			+ "LEFT JOIN\r\n" + "    tms_task_users tu ON tu.taskid = t.taskid\r\n" + "LEFT JOIN\r\n"
			+ "    tms_assigned_users au ON au.assignid = tu.assignedto\r\n" + "LEFT JOIN\r\n"
			+ "    users u ON u.userid = au.userid WHERE DATE(t.createddate) >=:fromDate \r\n"
			+ "	AND t.targetdate <=:toDate \r\n" + "		ORDER BY\r\n" + "		 tt.trackid DESC\r\n"
			+ "", nativeQuery = true)
	public List<TaskTrackerDTO> taskReports(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate, t.targetdate, tt.trackid, t.status,t.description as taskdescription, tt.description, tt.fromdate, tt.todate,u.pseudoname\r\n"
			+ "FROM\r\n" + "    tms_task t\r\n" + "LEFT JOIN\r\n" + "    tms_ticket_tracker tt ON t.taskid = tt.taskid\r\n"
			+ "LEFT JOIN\r\n" + "    tms_task_users tu ON tu.taskid = t.taskid\r\n" + "LEFT JOIN\r\n"
			+ "    tms_assigned_users au ON au.assignid = tu.assignedto\r\n" + "LEFT JOIN\r\n"
			+ "    users u ON u.userid = au.userid WHERE DATE(t.createddate) >=:fromDate \r\n"
			+ "	AND t.targetdate <=:toDate AND t.department=:dept \r\n" + "		ORDER BY\r\n"
			+ "		 t.ticketid DESC\r\n" + "", nativeQuery = true)
	public List<TaskTrackerDTO> taskReportsByDepartment(@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate, @Param("dept") String dept);


	@Query(value = "SELECT t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,t.projectid,t.pid"
			+ "FROM tms_task t JOIN tms_project p ON t.pid = p.pid WHERE p.projectid =:projectid AND  t.status=:status ", nativeQuery = true)
	public Page<TaskTrackerDTO> getTaskByProjectid(Pageable pageable, @Param("projectid") String projectid,
			@Param("status") String status);

	@Query(value = "SELECT t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,t.projectid,t.pid "
			+ "FROM tms_task t JOIN tms_project p ON t.pid = p.pid WHERE p.projectid =:projectid AND  t.status=:status AND (t.ticketid LIKE CONCAT('%',:keyword, '%') OR t.taskname LIKE CONCAT('%',:keyword, '%') OR t.description LIKE CONCAT('%',:keyword,  '%') OR t.targetdate LIKE CONCAT('%',:keyword,  '%') "
			+ "OR t.status LIKE CONCAT('%',:keyword, '%'))", nativeQuery = true)
	public Page<TaskTrackerDTO> getTaskByProjectIdWithsearching(Pageable pageable, @Param("projectid") String projectid,
			@Param("status") String status, @Param("keyword") String keyword);

	@Query(value = "select u.userid ,u.pseudoname,u.fullname   FROM users u where u.status ='Active' AND u.department=:department ", nativeQuery = true)
	public List<GetUsersDTO> findDepartmentWiseUsers(String department);

	@Modifying
	@Transactional
	@Query(value = "UPDATE tms_task SET status=:status, updatedby=:updatedby , updateddate = :updateddate WHERE taskid =:taskid ", nativeQuery = true)
	public int updateTaskStatus(@Param("taskid") Long taskid, @Param("status") String status,@Param("updatedby") String updatedby , LocalDateTime updateddate);

	@Query(value = "SELECT  t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,p.projectid,p.pid \r\n"
			+ "			FROM tms_task t right Join tms_project p ON t.pid = p.pid WHERE p.projectid = :projectid ", nativeQuery = true)
	public List<TaskTrackerDTO> findTaskByProjectid(@Param("projectid") String projectid);

	@Query(value = "SELECT t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,p.projectid,t.pid "
			+ "FROM tms_task t JOIN tms_project p ON t.pid = p.pid WHERE p.projectid =:projectid AND (t.ticketid LIKE CONCAT('%',:keyword, '%') OR t.taskname LIKE CONCAT('%',:keyword, '%') OR t.description LIKE CONCAT('%',:keyword,  '%') OR t.targetdate LIKE CONCAT('%',:keyword,  '%') "
			+ "OR t.status LIKE CONCAT('%',:keyword, '%'))", nativeQuery = true)
	public List<TaskTrackerDTO> findTaskByProjectIdWithSearching(@Param("projectid") String projectid,
			@Param("keyword") String keyword);

	@Query(value = "select null as createdby,email, pseudoname from users where userid in (:auserid) union "
			+ "select pseudoname as createdby,email, pseudoname from users where userid = :userid ", nativeQuery = true)
	public List<GetUsersDTO> getTaskAssinedUsersAndCreatedBy(long userid, List<Long> auserid);

	@Query(value = "select t.taskid ,u.fullname , u.pseudoname,u.email from tms_task t , tms_task_users tu , tms_assigned_users au , users u where t.taskid = tu.taskid and  "
			+ "tu.assignedto= au.assignid and au.userid =u.userid and t.taskid=:taskId", nativeQuery = true)
	public List<GetUsersDTO> getAssignUsers(Long taskId);

	@Query(value = "select u.fullname , u.pseudoname  from users u where u.userid = :userid ", nativeQuery = true)
	public GetUsersDTO getUser(Long userid);

	public TmsTask findByTicketid(String ticketid);

	@Query(value = "Select pid FROM tms_project WHERE projectid = :projectid " , nativeQuery = true)
	public Long findPid(String projectid);
	
	@Query(value = "SELECT t.taskid ,u.fullname , u.pseudoname,u.email from tms_task t ,  tms_assigned_users au , users u , tms_sub_task st WHERE "
			+ "au.subtaskid= st.subtaskid and au.userid =u.userid and t.taskid=st.taskid and st.subtaskid=:subtaskid", nativeQuery = true)
	public List<GetUsersDTO> getSubtaskAssignUsers(Long subtaskid);
	
	
	@Query(value = "SELECT u.userid ,u.pseudoname,u.fullname FROM users u , tms_project p , tms_assigned_users au WHERE  au.pid=p.pid AND  u.userid=au.userid AND p.projectid =:projectId", nativeQuery = true)
	public List<GetUsersDTO> getProjectUsers(String projectId);
	
	

	

//	@Query(value = "select t.taskid ,u.fullname , u.pseudoname,u.email from task t , task_users tu , assigned_users au , users u where t.taskid = tu.taskid and  "
//			+ "tu.assignedto= au.assignid and au.userid =u.userid and t.ticketid=:ticketid", nativeQuery = true)
//	public List<GetUsersDTO> getAssignUsers(String ticketid);

}


