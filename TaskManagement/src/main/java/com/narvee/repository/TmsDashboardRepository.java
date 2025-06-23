package com.narvee.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.ProjectDropDownDTO;
import com.narvee.dto.TmsTaskCountData;
import com.narvee.entity.TmsProject;

@Repository
public interface TmsDashboardRepository extends JpaRepository<TmsProject, Long> {

	@Query(value= " WITH statuses AS ( SELECT 'In Progress' AS status	UNION ALL SELECT 'Completed' UNION ALL SELECT 'Blocked' UNION ALL SELECT 'OPEN'  UNION ALL SELECT 'OVERDUE'), "
		
			+ "	task_counts AS (SELECT t.status, COUNT(*) AS count FROM tms_task t JOIN tms_project p ON t.pid = p.pid WHERE t.status IN ('In Progress', 'Completed', 'Blocked', 'OPEN', 'OVERDUE') "
			+ "		GROUP BY t.status ),"
	
			+ "	sub_task_counts AS ( SELECT ts.status, COUNT(*) AS count FROM tms_sub_task ts JOIN tms_task t ON ts.taskid = t.taskid JOIN tms_project p ON t.pid = p.pid "
			+ "		WHERE ts.status IN ('In Progress', 'Completed', 'Blocked', 'OPEN', 'OVERDUE' ) GROUP BY ts.status ),"

			+ "	combined_counts AS ( SELECT 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count FROM statuses s LEFT JOIN task_counts tc ON s.status = tc.status 	UNION ALL "
			
			+ "		SELECT 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count FROM statuses s "
			+ "		LEFT JOIN sub_task_counts stc ON s.status = stc.status ),"

			+ "	total_counts AS ( SELECT 'Total' AS type, s.status, COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count FROM statuses s LEFT JOIN task_counts tc ON s.status = tc.status "
			+ "		LEFT JOIN sub_task_counts stc ON s.status = stc.status )"
			
			+ "	SELECT * FROM combined_counts UNION ALL 	SELECT * FROM total_counts "
			+ "	ORDER BY "
			+ "	  CASE status "
			+ "		WHEN 'In Progress' THEN 1 "
			+ "		WHEN 'Completed' THEN 2 "
			+ "		WHEN 'OPEN' THEN 3 "
			+ "		WHEN 'Blocked' THEN 4 "
			+ "        WHEN 'OVERDUE' THEN 5 "
			+ "		ELSE 6 "
			+ "	  END, "
			+ "	  CASE type  "
			+ "		WHEN 'Task' THEN 1 "
			+ "		WHEN 'Sub Task' THEN 2 "
			+ "		WHEN 'Total' THEN 3 "
			+ "		ELSE 4 "
			+ "	  END",nativeQuery = true)
	public List<TmsTaskCountData> getAllCount();
	
	
	@Query(value = "WITH statuses AS ( "
			+ "    SELECT 'In Progress' AS status "
			+ "    UNION ALL SELECT 'Completed' "
			+ "    UNION ALL SELECT 'Blocked' "
			+ "    UNION ALL SELECT 'OPEN' "
			+ "    UNION ALL SELECT 'OVERDUE' "
			+ "), "

			+ "task_counts AS (  "
			+ "    SELECT t.status, COUNT(*) AS count "
			+ "    FROM tms_task t  "
			+ "    JOIN tms_project p ON t.pid = p.pid  "
			+ "    WHERE p.pid = :projectId AND t.status IN ('In Progress', 'Completed', 'Blocked', 'OPEN', 'OVERDUE') "
			+ "    GROUP BY t.status  "
			+ "), "
			 

			+ "sub_task_counts AS ("
			+ "    SELECT ts.status, COUNT(*) AS count "
			+ "    FROM tms_sub_task ts "
			+ "    JOIN tms_task t ON ts.taskid = t.taskid "
			+ "    JOIN tms_project p ON t.pid = p.pid "
			+ "    WHERE p.pid = :projectId AND ts.status IN ('In Progress', 'Completed', 'Blocked', 'OPEN', 'OVERDUE') "
			+ "    GROUP BY ts.status "
			+ "), "
		
			+ "combined_counts AS ( "
			+ "    SELECT 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count "
			+ "    FROM statuses s "
			+ "    LEFT JOIN task_counts tc ON s.status = tc.status "

			+ "    UNION ALL  "
		
			+ "    SELECT 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count "
			+ "    FROM statuses s  "
			+ "    LEFT JOIN sub_task_counts stc ON s.status = stc.status "
			+ "), "

			+ "total_counts AS ( "
			+ "    SELECT 'Total' AS type, s.status, "
			+ "           COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count "
			+ "    FROM statuses s "
			+ "    LEFT JOIN task_counts tc ON s.status = tc.status "
			+ "    LEFT JOIN sub_task_counts stc ON s.status = stc.status "
			+ ") "
			
			+ "SELECT * FROM combined_counts "
			+ "UNION ALL "
			+ "SELECT * FROM total_counts  "
			+ "ORDER BY  "
			+ "  CASE status "
			+ "    WHEN 'In Progress' THEN 1 "
			+ "    WHEN 'Completed' THEN 2 "
			+ "    WHEN 'OPEN' THEN 3 "
			+ "    WHEN 'Blocked' THEN 4 "
			+ "    WHEN 'OVERDUE' THEN 5 "
			+ "    ELSE 6 "
			+ "  END, "
			+ "  CASE type "
			+ "    WHEN 'Task' THEN 1 "
			+ "    WHEN 'Sub Task' THEN 2 "
			+ "    WHEN 'Total' THEN 3 "
			+ "    ELSE 4 "
			+ "  END ",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByProjectId(Long projectId);
	
	@Query(value = "WITH statuses AS ( "
			+ "    SELECT 'In Progress' AS status "
			+ "    UNION ALL SELECT 'Completed' "
			+ "    UNION ALL SELECT 'Blocked' "
			+ "    UNION ALL SELECT 'OPEN' "
			+ "    UNION ALL SELECT 'OVERDUE' "
			+ "), "
			+ " "

			+ "task_counts AS ( "
			+ "    SELECT t.status, COUNT(*) AS count "
			+ "    FROM tms_task t "
			+ "    JOIN tms_project p ON t.pid = p.pid "
			+ "    JOIN tms_assigned_users au ON au.pid=t.pid  "
			+ "    WHERE p.pid = :pid AND au.tms_user_id= :UserId AND  t.status IN ('In Progress', 'Completed', 'Blocked', 'OPEN', 'OVERDUE') "
			+ "    GROUP BY t.status "
			+ "), "
			+ " "
		
			+ "sub_task_counts AS ( "
			+ "    SELECT ts.status, COUNT(*) AS count "
			+ "    FROM tms_sub_task ts "
			+ "    JOIN tms_task t ON ts.taskid = t.taskid "
			+ "    JOIN tms_project p ON t.pid = p.pid "
			+ "	JOIN tms_assigned_users au ON au.pid=t.pid  "
			+ "    WHERE p.pid = :pid AND au.tms_user_id= :UserId AND  ts.status IN ('In Progress', 'Completed', 'Blocked', 'OPEN', 'OVERDUE') "
			+ "    GROUP BY ts.status "
			+ "), "
			+ " "
			
			+ "combined_counts AS ( "
			+ "    SELECT 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count "
			+ "    FROM statuses s "
			+ "    LEFT JOIN task_counts tc ON s.status = tc.status "
			+ " "
			+ "    UNION ALL "
			+ " "
			+ "    SELECT 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count "
			+ "    FROM statuses s "
			+ "    LEFT JOIN sub_task_counts stc ON s.status = stc.status "
			+ "), "
			+ " "
		
			+ "total_counts AS ( "
			+ "    SELECT 'Total' AS type, s.status, "
			+ "           COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count "
			+ "    FROM statuses s "
			+ "    LEFT JOIN task_counts tc ON s.status = tc.status "
			+ "    LEFT JOIN sub_task_counts stc ON s.status = stc.status "
			+ ") "
			+ " "
		
			+ "SELECT * FROM combined_counts "
			+ "UNION ALL "
			+ "SELECT * FROM total_counts "
			+ "ORDER BY  "
			+ "  CASE status "
			+ "    WHEN 'In Progress' THEN 1 "
			+ "    WHEN 'Completed' THEN 2 "
			+ "    WHEN 'OPEN' THEN 3 "
			+ "    WHEN 'Blocked' THEN 4 "
			+ "    WHEN 'OVERDUE' THEN 5 "
			+ "    ELSE 6 "
			+ "  END, "
			+ "  CASE type "
			+ "    WHEN 'Task' THEN 1 "
			+ "    WHEN 'Sub Task' THEN 2 "
			+ "    WHEN 'Total' THEN 3 "
			+ "    ELSE 4 "
			+ "  END ",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByPidAndUserId(Long pid,Long UserId);
	
	@Query(value = "WITH statuses AS ("
			+ "            SELECT 'In Progress' AS status"
			+ "            UNION ALL SELECT 'Completed'"
			+ "            UNION ALL SELECT 'Blocked'"
			+ "            UNION ALL SELECT 'OPEN'"
			+ "            UNION ALL SELECT 'OVERDUE'" 
			+ "        ),  "
			+ "        task_counts AS (  "
			+ "            SELECT t.status, COUNT(*) AS count  "
			+ "            FROM tms_task t  "
			+ "            JOIN tms_project p ON t.pid = p.pid  "
			+ "            JOIN tms_assigned_users au ON au.pid = t.pid  "
			+ "            WHERE p.pid = :pid  "
			+ "              AND au.tms_user_id = :userId  "
			+ "              AND (  "
			+ "                (:interval = 'daily' AND DATE(t.updateddate) = CURDATE()) OR  "
			+ "                (:interval = 'weekly' AND t.updateddate >= CURDATE() - INTERVAL 7 DAY) OR  "
			+ "                (:interval = 'monthly' AND t.updateddate >= CURDATE() - INTERVAL 1 MONTH) OR  "
			+ "                (:interval = 'all')  "
			+ "              )  "
			+ "            GROUP BY t.status  "
			+ "        ),  "
			+ "        sub_task_counts AS (  "
			+ "            SELECT ts.status, COUNT(*) AS count  "
			+ "            FROM tms_sub_task ts  "
			+ "            JOIN tms_task t ON ts.taskid = t.taskid  "
			+ "            JOIN tms_project p ON t.pid = p.pid  "
			+ "            JOIN tms_assigned_users au ON au.pid = t.pid  "
			+ "            WHERE p.pid = :pid  "
			+ "              AND au.tms_user_id = :userId  "
			+ "              AND (  "
			+ "                (:interval = 'daily' AND DATE(ts.updateddate) = CURDATE()) OR  "
			+ "                (:interval = 'weekly' AND ts.updateddate >= CURDATE() - INTERVAL 7 DAY) OR  "
			+ "                (:interval = 'monthly' AND ts.updateddate >= CURDATE() - INTERVAL 1 MONTH) OR  "
			+ "                (:interval = 'all')  "
			+ "              )  "
			+ "            GROUP BY ts.status  "
			+ "        ),  "
			+ "        combined_counts AS (  "
			+ "            SELECT 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count  "
			+ "            FROM statuses s  "
			+ "            LEFT JOIN task_counts tc ON s.status = tc.status  "
			+ "            UNION ALL  "
			+ "            SELECT 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count  "
			+ "            FROM statuses s  "
			+ "            LEFT JOIN sub_task_counts stc ON s.status = stc.status  "
			+ "        ),  "
			+ "        total_counts AS (  "
			+ "            SELECT 'Total' AS type, s.status,  "
			+ "                   COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count  "
			+ "            FROM statuses s  "
			+ "            LEFT JOIN task_counts tc ON s.status = tc.status  "
			+ "            LEFT JOIN sub_task_counts stc ON s.status = stc.status  "
			+ "        )  "
			+ "        SELECT * FROM combined_counts  "
			+ "        UNION ALL  "
			+ "        SELECT * FROM total_counts  "
			+ "        ORDER BY   "
			+ "          FIELD(status, 'In Progress', 'Completed', 'OPEN', 'Blocked', 'OVERDUE'),  "
			+ "          FIELD(type, 'Task', 'Sub Task', 'Total')",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByPidAndUserIdAndTime(Long pid ,Long userId,String interval);
	
	
	@Query(value = "SELECT DATE_FORMAT(updateddate, '%Y-%m') AS month, COUNT(*) AS count  "
			+ "FROM  tms_task WHERE  status = :Status AND updateddate IS NOT NULL  GROUP BY  month "
			+ "ORDER BY month ",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByMonth(String Status);
	
	@Query(value = "select tms.first_name ,"
			+ "tms.position AS position ,"
			+ " COUNT(tt.taskid) AS total_assigned_tasks, "
			+ " \r\n"
			+ "    COUNT(CASE WHEN tt.status = 'In Progress' THEN 1 END) AS inprogress_count, "
			+ "    COUNT(CASE WHEN tt.status = 'Open' THEN 1 END) AS open_count, "
			+ "    COUNT(CASE WHEN tt.status = 'On Hold' THEN 1 END) AS OnHold_count  ,   "
			+ "    COUNT(CASE WHEN tt.status = 'Blocked' THEN 1 END) AS Blocked_count  ,  "
			+ "    COUNT(CASE WHEN tt.status = 'To be Tested' THEN 1 END) AS ToBeTested_count ,   "
			+ "    COUNT(CASE WHEN tt.status = 'In Review' THEN 1 END) AS InReview_count , "
			+ "     COUNT(CASE WHEN tt.status = 'Closed' THEN 1 END) AS Closed_count , "
			+ "     COUNT(CASE WHEN tt.status = 'Overdue' THEN 1 END) AS Overdue_count "
			+ "from tms_task  tt join  tms_task_users tu on tt.taskid  = tu.taskid join  tms_assigned_users ttu  on tu.assignedto = ttu.assignid join tms_users  tms on tms.user_id = ttu.tms_user_id "
			+ "where  (tms.added_by = :adminId or tms .user_id =:adminId)    GROUP BY  "
			+ "    tms.first_name,tms.position",nativeQuery = true )
	public List<TmsTaskCountData> getUserTrackerByAdmin(Long adminId );
	
	@Query(value = "select tms.first_name ,"
			+ "tms.position AS position ,"
			+ " COUNT(tt.taskid) AS total_assigned_tasks, "
			+ " \r\n"
			+ "    COUNT(CASE WHEN tt.status = 'In Progress' THEN 1 END) AS inprogress_count, "
			+ "    COUNT(CASE WHEN tt.status = 'Open' THEN 1 END) AS open_count, "
			+ "    COUNT(CASE WHEN tt.status = 'On Hold' THEN 1 END) AS OnHold_count  ,   "
			+ "    COUNT(CASE WHEN tt.status = 'Blocked' THEN 1 END) AS Blocked_count  ,  "
			+ "    COUNT(CASE WHEN tt.status = 'To be Tested' THEN 1 END) AS ToBeTested_count ,   "
			+ "    COUNT(CASE WHEN tt.status = 'In Review' THEN 1 END) AS InReview_count , "
			+ "     COUNT(CASE WHEN tt.status = 'Closed' THEN 1 END) AS Closed_count , "
			+ "     COUNT(CASE WHEN tt.status = 'Overdue' THEN 1 END) AS Overdue_count "
			+ "from tms_task  tt join  tms_task_users tu on tt.taskid  = tu.taskid join  tms_assigned_users ttu  on tu.assignedto = ttu.assignid join tms_users  tms on tms.user_id = ttu.tms_user_id "
			+ "where  (tms.added_by = :adminId or tms .user_id =:adminId)   and tt.pid = :pid GROUP BY  "
			+ "    tms.first_name,tms.position ",nativeQuery = true )
	public List<TmsTaskCountData> getUserTrackerByAdminAndPid(Long adminId,Long pid );
	
	@Query(value = "SELECT  "
			+ "    tms.first_name AS firstName , "
			+ " tms.position AS position ,"
			
			+ "    COUNT(tt.taskid) AS totalAssignedTasks, "
		
			+ "    SUM(CASE WHEN tt.status = 'In Progress' AND ( "
			+ "                    (:interval = 'DAILY' AND DATE(tt.updateddate) = CURDATE()) OR "
			+ "                    (:interval = 'WEEKLY' AND YEARWEEK(tt.updateddate, 1) = YEARWEEK(CURDATE(), 1)) OR "
			+ "                    (:interval = 'MONTHLY' AND YEAR(tt.updateddate) = YEAR(CURDATE()) AND MONTH(tt.updateddate) = MONTH(CURDATE())) "
			+ "                ) "
			+ "             THEN 1 ELSE 0 END) AS inProgressTaskCount, "
			
			+ "    SUM(CASE WHEN tt.status = 'Open' AND ( "
			+ "                    (:interval = 'DAILY' AND DATE(tt.updateddate) = CURDATE()) OR "
			+ "                    (:interval = 'WEEKLY' AND YEARWEEK(tt.updateddate, 1) = YEARWEEK(CURDATE(), 1)) OR "
			+ "                    (:interval = 'MONTHLY' AND YEAR(tt.updateddate) = YEAR(CURDATE()) AND MONTH(tt.updateddate) = MONTH(CURDATE())) "
			+ "                ) "
			+ "             THEN 1 ELSE 0 END) AS openTaskCount, " 

			+ "    SUM(CASE WHEN tt.status = 'On Hold' AND ( "
			+ "                    (:interval = 'DAILY' AND DATE(tt.updateddate) = CURDATE()) OR "
			+ "                    (:interval = 'WEEKLY' AND YEARWEEK(tt.updateddate, 1) = YEARWEEK(CURDATE(), 1)) OR "
			+ "                    (:interval = 'MONTHLY' AND YEAR(tt.updateddate) = YEAR(CURDATE()) AND MONTH(tt.updateddate) = MONTH(CURDATE())) "
			+ "                ) "
			+ "             THEN 1 ELSE 0 END) AS onHoldTaskCount, "
		
			+ "    SUM(CASE WHEN tt.status = 'Blocked' AND ( "
			+ "                    (:interval = 'DAILY' AND DATE(tt.updateddate) = CURDATE()) OR "
			+ "                    (:interval = 'WEEKLY' AND YEARWEEK(tt.updateddate, 1) = YEARWEEK(CURDATE(), 1)) OR "
			+ "                    (:interval = 'MONTHLY' AND YEAR(tt.updateddate) = YEAR(CURDATE()) AND MONTH(tt.updateddate) = MONTH(CURDATE())) "
			+ "                ) "
			+ "             THEN 1 ELSE 0 END) AS blockedTaskCount, "

			+ "    SUM(CASE WHEN tt.status = 'To be Tested' AND ( "
			+ "                    (:interval = 'DAILY' AND DATE(tt.updateddate) = CURDATE()) OR "
			+ "                    (:interval = 'WEEKLY' AND YEARWEEK(tt.updateddate, 1) = YEARWEEK(CURDATE(), 1)) OR "
			+ "                    (:interval = 'MONTHLY' AND YEAR(tt.updateddate) = YEAR(CURDATE()) AND MONTH(tt.updateddate) = MONTH(CURDATE())) "
			+ "                ) "
			+ "             THEN 1 ELSE 0 END) AS toBeTestedTaskCount, "
			
			+ "    SUM(CASE WHEN tt.status = 'In Review' AND ( "
			+ "                    (:interval = 'DAILY' AND DATE(tt.updateddate) = CURDATE()) OR "
			+ "                    (:interval = 'WEEKLY' AND YEARWEEK(tt.updateddate, 1) = YEARWEEK(CURDATE(), 1)) OR "
			+ "                    (:interval = 'MONTHLY' AND YEAR(tt.updateddate) = YEAR(CURDATE()) AND MONTH(tt.updateddate) = MONTH(CURDATE())) "
			+ "                ) "
			+ "             THEN 1 ELSE 0 END) AS inReviewCount, "
		
			+ "    SUM(CASE WHEN tt.status = 'Closed' AND ( "
			+ "                    (:interval = 'DAILY' AND DATE(tt.updateddate) = CURDATE()) OR "
			+ "                    (:interval = 'WEEKLY' AND YEARWEEK(tt.updateddate, 1) = YEARWEEK(CURDATE(), 1)) OR "
			+ "                    (:interval = 'MONTHLY' AND YEAR(tt.updateddate) = YEAR(CURDATE()) AND MONTH(tt.updateddate) = MONTH(CURDATE())) "
			+ "                ) "
			+ "             THEN 1 ELSE 0 END) AS closedTaskCount, "

			+ "    SUM(CASE WHEN tt.status = 'Overdue' AND ( "
			+ "                    (:interval = 'DAILY' AND DATE(tt.updateddate) = CURDATE()) OR "
			+ "                    (:interval = 'WEEKLY' AND YEARWEEK(tt.updateddate, 1) = YEARWEEK(CURDATE(), 1)) OR "
			+ "                    (:interval = 'MONTHLY' AND YEAR(tt.updateddate) = YEAR(CURDATE()) AND MONTH(tt.updateddate) = MONTH(CURDATE())) "
			+ "                ) "
			+ "             THEN 1 ELSE 0 END) AS overDueTaskCount "
	
			+ "FROM tms_task tt "
			+ "JOIN tms_task_users tu ON tt.taskid = tu.taskid "
			+ "JOIN tms_assigned_users ttu ON tu.assignedto = ttu.assignid "
			+ "JOIN tms_users tms ON tms.user_id = ttu.tms_user_id "
			+ "WHERE (tms.added_by = :adminId OR tms.user_id = :adminId)  AND tt.pid = :pid "
			+ "GROUP BY tms.first_name,tms.position",nativeQuery = true)
	public  List<TmsTaskCountData> getUserTrackerByAdminAndPidAndTimeInterval(Long adminId, Long pid, String interval );
	
	@Query(value="select distinct p.pid, p.projectid ,p.projectname from tms_project p join tms_assigned_users au ON au.pid=p.pid where addedby = :addedBy",nativeQuery =true)
	public List<ProjectDropDownDTO> projectDropDownWithAdmin(Long addedBy);
	

	@Query(value="select distinct p.pid, p.projectid ,p.projectname from tms_project p join tms_assigned_users au ON au.pid=p.pid where au.tms_user_id = :userId",nativeQuery =true)
	public List<ProjectDropDownDTO> projectDropDownWithOutAdmin(@Param("userId") Long userId);
	
	@Query(value = "select addedby from tms_project ",nativeQuery = true)
	public List<Long> getAddedBy();
	
}
