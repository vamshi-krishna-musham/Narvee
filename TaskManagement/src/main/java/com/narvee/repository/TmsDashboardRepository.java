package com.narvee.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
