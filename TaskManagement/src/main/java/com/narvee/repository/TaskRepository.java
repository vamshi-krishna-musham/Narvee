package com.narvee.repository;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

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
	// clean and refreshh

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
	

}
