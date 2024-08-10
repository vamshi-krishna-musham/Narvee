package com.narvee.repository;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.TaskAssignDTO;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.entity.Task;

@Repository
@Transactional
public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query(value = "select max(maxnum) as max from task", nativeQuery = true)
	public Long maxNumber();

	@Query(value = "select * from ticket_tracker where taskid=?", nativeQuery = true)
	public List<TaskTrackerDTO> ticketTracker(Long taskid);
	
	

	@Modifying
	@Query(value = "update  assigned_users set completed= :completed where userid= :userid and assignid= :assignid", nativeQuery = true)
	public void Iscompletd(boolean completed, Long userid, Long assignid);

	@Query(value = "select ad.pseudoname as createdby, t.ticketid, u.pseudoname, t.createddate, t.targetdate,au.userstatus as  status from task t\r\n"
			+ "   join users ad on t.addedby = ad.userid join task_users tu on t.taskid = tu.taskid  join assigned_users au  on au.assignid=tu.assignedto\r\n"
			+ "   join users u on u.userid= au.userid  and t.taskid = :taskid", nativeQuery = true)
	public List<TaskAssignDTO> taskAssignInfo(Long taskid);

	@Query(value = "select tt.trackid, tt.status  ,tt.duration, tt.description ,t.targetdate, tt.fromdate, tt.todate,tt.ftime ,tt.ttime , t.ticketid , t.taskname , t.taskid ,u.fullname from ticket_tracker tt , task t  ,users u where t.taskid= tt.taskid and u.userid=tt.updatedby and tt.updatedby= :userid order by tt.trackid desc", nativeQuery = true)
	public List<TaskTrackerDTO> trackerByUser(Long userid);

	@Query(value = "SELECT distinct\r\n"
			+ "    CASE \r\n"
			+ "        WHEN tt.trackid IS NULL THEN ass.pseudoname \r\n"
			+ "        ELSE u.pseudoname \r\n"
			+ "    END AS pseudoname,\r\n"
			+ "    \r\n"
			+ "    CASE \r\n"
			+ "        WHEN tt.trackid IS NULL THEN t.status \r\n"
			+ "        ELSE tt.status\r\n"
			+ "    END AS status,\r\n"
			+ "    tt.trackid,\r\n"
			+ "    t.createddate,\r\n"
			+ "    tt.description,\r\n"
			+ "    tt.fromdate,\r\n"
			+ "    tt.todate,\r\n"
			+ "    tt.ftime,\r\n"
			+ "    tt.ttime,\r\n"
			+ "    t.ticketid,\r\n"
			+ "    t.taskname,\r\n"
			+ "    t.taskid,\r\n"
			+ "    t.targetdate,\r\n"
			+ "    t.description AS taskdescription,\r\n"
			+ "    u.fullname\r\n"
			+ "FROM \r\n"
			+ "    task t\r\n"
			+ "LEFT JOIN \r\n"
			+ "    ticket_tracker tt ON t.taskid = tt.taskid\r\n"
			+ "LEFT JOIN \r\n"
			+ "    users u ON u.userid = tt.updatedby \r\n"
			+ "JOIN \r\n"
			+ "    task_users tu ON t.taskid = tu.taskid\r\n"
			+ "JOIN \r\n"
			+ "    assigned_users au ON au.assignid = tu.assignedto\r\n"
			+ "JOIN \r\n"
			+ "    users ass ON ass.userid = au.userid\r\n"
			+ "ORDER BY \r\n"
			+ "    tt.trackid DESC", nativeQuery = true)
	public List<TaskTrackerDTO> allTasksRecords();
	
	
	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate, t.targetdate, tt.trackid, t.status,t.description as taskdescription, tt.description, tt.fromdate, tt.todate,u.pseudoname\r\n"
			+ "FROM\r\n"
			+ "    task t\r\n"
			+ "LEFT JOIN\r\n"
			+ "    ticket_tracker tt ON t.taskid = tt.taskid\r\n"
			+ "LEFT JOIN\r\n"
			+ "    task_users tu ON tu.taskid = t.taskid\r\n"
			+ "LEFT JOIN\r\n"
			+ "    assigned_users au ON au.assignid = tu.assignedto\r\n"
			+ "LEFT JOIN\r\n"
			+ "    users u ON u.userid = au.userid WHERE DATE(t.createddate) >=:fromDate \r\n"
			+ "	AND t.targetdate <=:toDate \r\n"
			+ "		ORDER BY\r\n"
			+ "		 tt.trackid DESC\r\n"
			+ "" , nativeQuery = true)
	public List<TaskTrackerDTO> taskReports( @Param("fromDate") LocalDate fromDate , @Param("toDate") LocalDate toDate);
	
	
	
	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate, t.targetdate, tt.trackid, t.status,t.description as taskdescription, tt.description, tt.fromdate, tt.todate,u.pseudoname\r\n"
			+ "FROM\r\n"
			+ "    task t\r\n"
			+ "LEFT JOIN\r\n"
			+ "    ticket_tracker tt ON t.taskid = tt.taskid\r\n"
			+ "LEFT JOIN\r\n"
			+ "    task_users tu ON tu.taskid = t.taskid\r\n"
			+ "LEFT JOIN\r\n"
			+ "    assigned_users au ON au.assignid = tu.assignedto\r\n"
			+ "LEFT JOIN\r\n"
			+ "    users u ON u.userid = au.userid WHERE DATE(t.createddate) >=:fromDate \r\n"
			+ "	AND t.targetdate <=:toDate AND t.department=:dept \r\n"
			+ "		ORDER BY\r\n"
			+ "		 t.ticketid DESC\r\n"
			+ "" , nativeQuery = true)
	public List<TaskTrackerDTO> taskReportsByDepartment( @Param("fromDate") LocalDate fromDate , @Param("toDate") LocalDate toDate ,@Param("dept") String dept);
	

	@Query(value = "select t.ticketid, u.pseudoname as pseudoname, t.createddate, u.pseudoname as createdby, t.targetdate, au.userstatus as status, au.userstatus as austatus from task t join task_users tu on t.taskid = tu.taskid  join assigned_users au  on au.assignid=tu.assignedto "
			+ "join users u on u.userid= au.userid and t.taskid = :taskid",nativeQuery = true)
	public Page<TaskAssignDTO> taskAssignInfoWithSortingAndFiltering(Pageable pageable, @Param("taskid")Long taskid);
	
	
	@Query(value = "select t.ticketid, u.pseudoname as pseudoname, t.createddate, u.pseudoname as createdby, t.targetdate, au.userstatus as status, au.userstatus as austatus from task t join task_users tu on t.taskid = tu.taskid  join assigned_users au  on au.assignid=tu.assignedto "
			+ "join users u on u.userid= au.userid and t.taskid = :taskid",nativeQuery = true)
	public List<TaskAssignDTO> taskAssignInfoWithSortingAndFiltering(@Param("taskid")Long taskid);
	

	
	@Query(value = "SELECT tt.trackid,tt.status,tt.duration,tt.description,t.targetdate as targetdate,tt.fromdate,tt.todate,tt.ftime,tt.ttime, ticketid as ticketid, taskname as taskname, t.taskid as taskid, fullname as fullname FROM ticket_tracker tt, task t, users u "
			+ "WHERE t.taskid = tt.taskid AND u.userid = tt.updatedby AND tt.updatedby = :userid ",nativeQuery = true)
	public Page<TaskTrackerDTO> trackerByUserWithSortingAndPagination(Pageable pageable, @Param("userid") Long userid);  //48
	
	
	
	@Query(value = "SELECT tt.trackid,tt.status,tt.duration,tt.description,t.targetdate as targetdate,tt.fromdate,tt.todate,tt.ftime,tt.ttime, ticketid as ticketid, taskname as taskname, t.taskid as taskid, fullname as fullname FROM ticket_tracker tt, task t, users u "
			+ "WHERE t.taskid = tt.taskid AND u.userid = tt.updatedby AND tt.updatedby = :userid ",nativeQuery = true)
	public List<TaskTrackerDTO> trackerByUserWithSortingAndPagination( @Param("userid") Long userid);
	
	
	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate, t.targetdate, tt.trackid as trackid, t.status,t.description as taskdescription, tt.fromdate as fromdate, tt.todate as todate,u.pseudoname as pseudoname FROM task t LEFT JOIN ticket_tracker tt ON t.taskid = tt.taskid "
			+ "	LEFT JOIN task_users tu ON tu.taskid = t.taskid LEFT JOIN assigned_users au ON au.assignid = tu.assignedto LEFT JOIN users u ON u.userid = au.userid "
			+ "WHERE DATE(t.createddate) >=:fromDate AND t.targetdate <=:toDate AND t.department=:dept", nativeQuery = true)
	public Page<TaskTrackerDTO> taskReportsByDepartmentWithSortingAndPagination(Pageable pageable, @Param("fromDate") LocalDate fromDate , @Param("toDate") LocalDate toDate ,@Param("dept") String dept);
	
	
	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate, t.targetdate, tt.trackid as trackid, t.status,t.description as taskdescription, tt.fromdate as fromdate, tt.todate as todate,u.pseudoname as pseudoname FROM task t LEFT JOIN ticket_tracker tt ON t.taskid = tt.taskid "
			+ "	LEFT JOIN task_users tu ON tu.taskid = t.taskid LEFT JOIN assigned_users au ON au.assignid = tu.assignedto LEFT JOIN users u ON u.userid = au.userid "
			+ "WHERE DATE(t.createddate) >=:fromDate AND t.targetdate <=:toDate AND t.department=:dept", nativeQuery = true)
	public List<TaskTrackerDTO> taskReportsByDepartmentWithSortingAndPagination( @Param("fromDate") LocalDate fromDate , @Param("toDate") LocalDate toDate ,@Param("dept") String dept);
	
	

	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate, t.targetdate, tt.trackid as trackid, t.status, t.description as taskdescription, tt.fromdate as fromdate, tt.todate as todate,u.pseudoname as pseudoname FROM task t LEFT JOIN ticket_tracker tt ON t.taskid = tt.taskid "
			+ " LEFT JOIN task_users tu ON tu.taskid = t.taskid LEFT JOIN assigned_users au ON au.assignid = tu.assignedto LEFT JOIN users u ON u.userid = au.userid WHERE DATE(t.createddate) >=:fromDate AND t.targetdate <=:toDate",nativeQuery = true)
	public Page<TaskTrackerDTO> taskReportsWithSortingAndPagination(Pageable pageable, @Param("fromDate") LocalDate fromDate , @Param("toDate") LocalDate toDate);
	

	
	@Query(value = "SELECT t.taskid, t.ticketid, t.taskname, t.createddate, t.targetdate, tt.trackid as trackid, t.status, t.description as taskdescription, tt.fromdate as fromdate, tt.todate as todate,u.pseudoname as pseudoname FROM task t LEFT JOIN ticket_tracker tt ON t.taskid = tt.taskid "
			+ " LEFT JOIN task_users tu ON tu.taskid = t.taskid LEFT JOIN assigned_users au ON au.assignid = tu.assignedto LEFT JOIN users u ON u.userid = au.userid WHERE DATE(t.createddate) >=:fromDate AND t.targetdate <=:toDate",nativeQuery = true)
	public List<TaskTrackerDTO> taskReportsWithSortingAndPagination(@Param("fromDate") LocalDate fromDate , @Param("toDate") LocalDate toDate);
	

	@Query(value = "SELECT DISTINCT CASE WHEN tt.trackid IS NULL THEN ass.pseudoname ELSE u.pseudoname END AS fpseudoname, CASE WHEN tt.trackid IS NULL THEN t.status ELSE tt.status END AS fstatus,tt.trackid as trackid, "
			+ "t.createddate, tt.fromdate as fromdate ,tt.todate as todate, tt.ftime as ftime, tt.ttime as ttime, t.ticketid, t.taskname, t.taskid, t.targetdate, t.description as taskdescription, u.fullname as fullname FROM task t LEFT JOIN ticket_tracker tt ON t.taskid = tt.taskid "
			+ "LEFT JOIN users u ON u.userid = tt.updatedby JOIN task_users tu ON t.taskid = tu.taskid JOIN assigned_users au ON au.assignid = tu.assignedto JOIN users ass ON ass.userid = au.userid", nativeQuery = true)
	public Page<TaskTrackerDTO> allTasksRecordsWithSortingAndPagination(Pageable pageable);
	
	
	@Query(value = "SELECT DISTINCT CASE WHEN tt.trackid IS NULL THEN ass.pseudoname ELSE u.pseudoname END AS fpseudoname, CASE WHEN tt.trackid IS NULL THEN t.status ELSE tt.status END AS fstatus,tt.trackid as trackid, "
			+ "t.createddate, tt.fromdate as fromdate ,tt.todate as todate, tt.ftime as ftime, tt.ttime as ttime, t.ticketid, t.taskname, t.taskid, t.targetdate, t.description as taskdescription, u.fullname as fullname FROM task t LEFT JOIN ticket_tracker tt ON t.taskid = tt.taskid "
			+ "LEFT JOIN users u ON u.userid = tt.updatedby JOIN task_users tu ON t.taskid = tu.taskid JOIN assigned_users au ON au.assignid = tu.assignedto JOIN users ass ON ass.userid = au.userid", nativeQuery = true)
	public List<TaskTrackerDTO> allTasksRecordsWithSortingAndPagination();
	
	@Query(value = "SELECT t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,p.projectid,t.pid "
			+ "FROM Task t JOIN Project p ON t.pid = p.pid WHERE p.projectid=:projectid ", nativeQuery = true)
	public Page<TaskTrackerDTO> getTaskByProjectid(Pageable pageable,String projectid);
	
	@Query(value = "SELECT t.taskid,t.createddate,t.updateddate,t.addedby,t.department,t.description,t.maxnum,t.status,t.targetdate,t.ticketid,t.updatedby,t.taskname,p.projectid,t.pid\r\n"
			+ "FROM Task t JOIN Project p ON t.pid = p.pid WHERE p.projectid=:projectid AND (t.ticketid LIKE CONCAT('%',:keyword, '%') OR t.taskname LIKE CONCAT('%',:keyword, '%') OR t.description LIKE CONCAT('%',:keyword,  '%') OR t.targetdate LIKE CONCAT('%',:keyword,  '%') "
			+ "OR t.status LIKE CONCAT('%',:keyword, '%'))", nativeQuery = true)
	public Page<TaskTrackerDTO> getTaskByProjectIdWithsearching(Pageable pageable,@Param("projectid") String projectid,@Param("keyword") String keyword);
	
	@Query( value = "select u.userid ,u.fullname FROM users u where u.department=:department",nativeQuery = true)
	public Object[] findDepartmentWiseUsers(String department);
	
}
