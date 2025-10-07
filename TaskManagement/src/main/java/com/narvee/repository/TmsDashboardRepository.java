package com.narvee.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.dto.CompletedStatusCountResponse;
import com.narvee.dto.ProjectDropDownDTO;
import com.narvee.dto.TaskTrackerDTO;
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
	public List<TmsTaskCountData> getAllCount();  // get All task 
	
	
	@Query(value = " WITH statuses AS (  "
			+ "		 SELECT 'In Progress' AS status  "
			+ "			     UNION ALL SELECT 'To be Tested' "
			+ "			     UNION ALL SELECT 'Blocked'  "
			+ "			     UNION ALL SELECT 'Open'  "
			+ "			     UNION ALL SELECT 'Overdue' "
			+ "              UNION ALL SELECT 'On Hold' "
			+ "              UNION ALL SELECT 'In Review' "
			+ "              UNION ALL SELECT 'Closed' "	
			+ "			 ), " 
		
			+ "			 task_counts AS (  "
			+ "			     SELECT t.status, COUNT(*) AS count "
			+ "			     FROM tms_task t "
			+ "			     JOIN tms_project p ON t.pid = p.pid  "
			+ "			     WHERE  p.admin_id = :adminId  AND  t.status IN  ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed') "
			+ "			     GROUP BY t.status  "
			+ "			 ),  "
			
			+ "			 sub_task_counts AS (   "
			+ "			     SELECT ts.status, COUNT(*) AS count   "
			+ "			     FROM tms_sub_task ts   "
			+ "			     JOIN tms_task t ON ts.taskid = t.taskid   "
			+ "			     JOIN tms_project p ON t.pid = p.pid   "
			+ "			     WHERE   p.admin_id= :adminId AND  ts.status IN  ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed')  "
			+ "			     GROUP BY ts.status   "
			+ "			 ),  "
			
			+ "			 combined_counts AS (   "
			+ "			     SELECT 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count  "
			+ "			     FROM statuses s   "
			+ "			     LEFT JOIN task_counts tc ON s.status = tc.status   "
			+ "			    "
			+ "			     UNION ALL   "
			+ "			    "
			+ "			     SELECT 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count "
			+ "			     FROM statuses s   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.status = stc.status "
			+ "			 ),   "

			+ "			 total_counts AS (   "
			+ "			     SELECT 'Total' AS type, s.status,   "
			+ "			            COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count "
			+ "			     FROM statuses s   "
			+ "			     LEFT JOIN task_counts tc ON s.status = tc.status "
			+ "			     LEFT JOIN sub_task_counts stc ON s.status = stc.status  "
			+ "			 )   "
			
			+ "			 SELECT * FROM combined_counts  "
			+ "			 UNION ALL "
			+ "			 SELECT * FROM total_counts "
			+ "			 ORDER BY "
			+ "			   CASE status  "
			+ "			     WHEN 'In Progress' THEN 1  "
			+ "			     WHEN 'To be Tested' THEN 2 "
			+ "			     WHEN 'Blocked' THEN 3   "
			+ "			     WHEN 'Open' THEN 4   "
			+ "			     WHEN 'Overdue' THEN 5 "
			+ "              WHEN 'On Hold' THEN 6 "
			+ "              WHEN 'In Review' THEN 7 "
			+ "              WHEN 'Closed' THEN 8 "
			+ "			     ELSE 9 "
			+ "			   END,   "
			+ "			   CASE type   "
			+ "			     WHEN 'Task' THEN 1   "
			+ "			     WHEN 'Sub Task' THEN 2   "
			+ "			     WHEN 'Total' THEN 3   "
			+ "			     ELSE 4   "
			+ "			   END ",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByadminId(Long adminId);   // get All task By Admin Id
	
	@Query(value = " WITH statuses AS ( "
			+ "    SELECT 'In Progress' AS status "
			+ "    UNION ALL SELECT 'To be Tested' "
			+ "    UNION ALL SELECT 'Blocked' "
			+ "    UNION ALL SELECT 'Open' "
			+ "    UNION ALL SELECT 'Overdue' "
			+ "    UNION ALL SELECT 'On Hold' "
			+ "    UNION ALL SELECT 'In Review' "
			+ "    UNION ALL SELECT 'Closed' "
			+ "), "
			+ "task_counts AS ( "
			+ "    SELECT t.status, COUNT(*) AS count "
			+ "    FROM tms_task t "
			+ "    JOIN tms_project p ON t.pid = p.pid "
			+ "    LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid "
			+ "    LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto "
			+ "    WHERE t.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed') "
			+ "      AND ( "
			+ "          (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId))   "
			+ "          OR (p.addedby <> :userId AND tau.tms_user_id = :userId)                         "
			+ "      ) "
			+ "    GROUP BY t.status "
			+ "), "
			+ "sub_task_counts AS ( "
			+ "    SELECT ts.status, COUNT(*) AS count "
			+ "FROM tms_sub_task ts "
			+ "JOIN tms_task t ON ts.taskid = t.taskid "
			+ "JOIN tms_project p ON t.pid = p.pid "
			+ "LEFT JOIN tms_assigned_users tau ON tau.subtaskid = ts.subtaskid "
			+ "WHERE ts.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed') "
			+ "  AND ( "
			+ "      (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId)) "
			+ "      OR (p.addedby <> :userId AND tau.tms_user_id = :userId) "
			+ "  ) "
			+ "GROUP BY ts.status "
			+ "), "
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
			+ "ORDER BY "
			+ "  CASE status "
			+ "    WHEN 'In Progress' THEN 1 "
			+ "    WHEN 'To be Tested' THEN 2 "
			+ "    WHEN 'Blocked' THEN 3 "
			+ "    WHEN 'Open' THEN 4 "
			+ "    WHEN 'Overdue' THEN 5 "
			+ "    WHEN 'On Hold' THEN 6 "
			+ "    WHEN 'In Review' THEN 7 "
			+ "    WHEN 'Closed' THEN 8 "
			+ "    ELSE 9 "
			+ "  END, "
			+ "  CASE type "
			+ "    WHEN 'Task' THEN 1 "
			+ "    WHEN 'Sub Task' THEN 2 "
			+ "    WHEN 'Total' THEN 3 "
			+ "    ELSE 4 "
			+ "  END "
			+ " ",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByUserId(Long userId);  // get All task By userId 
	
	@Query(value = "WITH statuses AS (   "
			+ "				SELECT 'In Progress' AS status   "
			+ "			     UNION ALL SELECT 'To be Tested'   "
			+ "			     UNION ALL SELECT 'Blocked'   "
			+ "			     UNION ALL SELECT 'Open'   "
			+ "			     UNION ALL SELECT 'Overdue' "
			+ "              UNION ALL SELECT 'On Hold' "
			+ "              UNION ALL SELECT 'In Review' "
			+ "              UNION ALL SELECT 'Closed' "
			+ "			 ), "
	
			+ "			 task_counts AS (   "
			+ "			     SELECT t.status, COUNT(*) AS count   "
			+ "			     FROM tms_task t   "
			+ "			     JOIN tms_project p ON t.pid = p.pid   "
			+ "			     WHERE  p.admin_id = :UserId AND  p.pid = :pid And  t.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed')  "
			+ "			     GROUP BY t.status   "
			+ "			 ),   "
	
			+ "			 sub_task_counts AS (   "
			+ "			     SELECT ts.status, COUNT(*) AS count   "
			+ "			     FROM tms_sub_task ts   "
			+ "			     JOIN tms_task t ON ts.taskid = t.taskid   "
			+ "			     JOIN tms_project p ON t.pid = p.pid   "
			+ "			     WHERE   p.admin_id = :UserId AND p.pid = :pid AND  ts.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed')  "
			+ "			     GROUP BY ts.status   "
			+ "			 ),   "
	
			+ "			 combined_counts AS (   "
			+ "			     SELECT 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count   "
			+ "			     FROM statuses s   "
			+ "			     LEFT JOIN task_counts tc ON s.status = tc.status   "
			+ "			     UNION ALL   "
			+ "			     SELECT 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count   "
			+ "			     FROM statuses s   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.status = stc.status   "
			+ "			 ),   "
	
			+ "			 total_counts AS (   "
			+ "			     SELECT 'Total' AS type, s.status,   "
			+ "			            COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count   "
			+ "			     FROM statuses s   "
			+ "			     LEFT JOIN task_counts tc ON s.status = tc.status   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.status = stc.status   "
			+ "			 )   "

			+ "			 SELECT * FROM combined_counts   "
			+ "			 UNION ALL   "
			+ "			 SELECT * FROM total_counts   "
			+ "			 ORDER BY    "
			+ "			   CASE status   "
			+ "			     WHEN 'In Progress' THEN 1   "
			+ "			     WHEN 'To be Tested' THEN 2   "
			+ "			     WHEN 'Blocked' THEN 3   "
			+ "			     WHEN 'Open' THEN 4   "
			+ "			     WHEN 'Overdue' THEN 5 "
			+ "              WHEN 'On Hold' THEN 6 "
			+ "              WHEN 'In Review' THEN 7 "
			+ "              WHEN 'Closed' THEN 8 "
			+ "			     ELSE 9 "
			+ "			   END,   "
			+ "			   CASE type   "
			+ "			     WHEN 'Task' THEN 1   "
			+ "			     WHEN 'Sub Task' THEN 2   "
			+ "			     WHEN 'Total' THEN 3   "
			+ "			     ELSE 4   "
			+ "			   END ",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByPidAndAdminId(Long pid,Long UserId); // get All task and subtask count By pid and admin id 
	
	@Query(value = "WITH statuses AS (   "
			+ "				SELECT 'In Progress' AS status   "
			+ "			     UNION ALL SELECT 'To be Tested'   "
			+ "			     UNION ALL SELECT 'Blocked'   "
			+ "			     UNION ALL SELECT 'Open'   "
			+ "			     UNION ALL SELECT 'Overdue' "
			+ "              UNION ALL SELECT 'On Hold' "
			+ "              UNION ALL SELECT 'In Review' "
			+ "              UNION ALL SELECT 'Closed' "
			+ "			 ),"
			+ " "
			+ "      task_counts AS ( "
			+ "           SELECT t.status, COUNT(*) AS count "
			+ "           FROM tms_task t "
			+ "           JOIN tms_project p ON t.pid = p.pid "
			+ "           LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid "
			+ "           LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto "
			+ "           WHERE t.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed') "
			+ "           AND ( "
			+ "                 (p.addedby = :UserId AND (p.addedby = :UserId  OR tau.tms_user_id = :UserId))   "
			+ "                 OR (p.addedby <> :UserId  AND tau.tms_user_id = :UserId)                         "
			+ "               ) and p.pid = :pid "
			+ "          GROUP BY t.status "
			+ "          ), "
			+ "           "
			+ "     sub_task_counts AS ( "
			+ "         SELECT ts.status, COUNT(*) AS count "
			+ "         FROM tms_sub_task ts "
			+ "         JOIN tms_task t ON ts.taskid = t.taskid "
			+ "         JOIN tms_project p ON t.pid = p.pid "
			+ "         LEFT JOIN tms_assigned_users tau ON tau.subtaskid = ts.subtaskid "
			+ "         WHERE ts.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed') "
			+ "          AND ( "
			+ "                    (p.addedby = :UserId AND (p.addedby = :UserId OR tau.tms_user_id = :UserId)) "
			+ "                    OR (p.addedby <> :UserId  AND tau.tms_user_id = :UserId ) "
			+ "              ) and p.pid = :pid "
			+ "        GROUP BY ts.status "
			+ "        ),"
	
			+ "			 combined_counts AS (   "
			+ "			     SELECT 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count   "
			+ "			     FROM statuses s   "
			+ "			     LEFT JOIN task_counts tc ON s.status = tc.status   "
			+ "			     UNION ALL   "
			+ "			     SELECT 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count   "
			+ "			     FROM statuses s   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.status = stc.status   "
			+ "			 ),   "
	
			+ "			 total_counts AS (   "
			+ "			     SELECT 'Total' AS type, s.status,   "
			+ "			            COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count   "
			+ "			     FROM statuses s   "
			+ "			     LEFT JOIN task_counts tc ON s.status = tc.status   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.status = stc.status   "
			+ "			 )   "

			+ "			 SELECT * FROM combined_counts   "
			+ "			 UNION ALL   "
			+ "			 SELECT * FROM total_counts   "
			+ "			 ORDER BY    "
			+ "			   CASE status   "
			+ "			     WHEN 'In Progress' THEN 1   "
			+ "			     WHEN 'To be Tested' THEN 2   "
			+ "			     WHEN 'Blocked' THEN 3   "
			+ "			     WHEN 'Open' THEN 4   "
			+ "			     WHEN 'Overdue' THEN 5 "
			+ "              WHEN 'On Hold' THEN 6 "
			+ "              WHEN 'In Review' THEN 7 "
			+ "              WHEN 'Closed' THEN 8 "
			+ "			     ELSE 9 "
			+ "			   END,   "
			+ "			   CASE type   "
			+ "			     WHEN 'Task' THEN 1   "
			+ "			     WHEN 'Sub Task' THEN 2   "
			+ "			     WHEN 'Total' THEN 3   "
			+ "			     ELSE 4   "
			+ "			   END ",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByPidAndUserId(Long pid,Long UserId); // get All task and subtask count By pid and user id 
	
	
	@Query(value = "WITH statuses AS (  "
			+ "			     SELECT 'In Progress' AS status   "
			+ "			     UNION ALL SELECT 'To be Tested'   "
			+ "			     UNION ALL SELECT 'Blocked'   "
			+ "			     UNION ALL SELECT 'Open'   "
			+ "			     UNION ALL SELECT 'Overdue' "
			+ "              UNION ALL SELECT 'On Hold' "
			+ "              UNION ALL SELECT 'In Review' "
			+ "              UNION ALL SELECT 'Closed' "
			+ "			        ), "
			+ " "
			+ "			        task_counts AS (    "
			+ "			            SELECT t.status, COUNT(*) AS count    "
			+ "			            FROM tms_task t    "
			+ "			            JOIN tms_project p ON t.pid = p.pid   "
			+ "                    WHERE (:pid IS NULL OR p.pid = :pid) "
		    + "                    AND p.admin_id = :userId  AND  t.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed')"
			+ "			              AND (    "
			+ "			                ( :interval = 'daily' AND DATE(t.updateddate) = CURDATE()) OR    "
			+ "			                ( :interval = 'weekly' AND t.updateddate >= CURDATE() - INTERVAL 7 DAY) OR    "
			+ "			                ( :interval = 'monthly' AND t.updateddate >= CURDATE() - INTERVAL 1 MONTH) OR   "
			+ "                         ( :interval = 'yearly' AND t.updateddate >= CURDATE() - INTERVAL 1 YEAR) OR  "
			+ "			                ( :interval = 'all')    "
			+ "			              )    "
			+ "			            GROUP BY t.status    "
			+ "			        ),    "
			+ "			        sub_task_counts AS (    "
			+ "			            SELECT ts.status, COUNT(*) AS count    "
			+ "			            FROM tms_sub_task ts    "
			+ "			            JOIN tms_task t ON ts.taskid = t.taskid    "
			+ "			            JOIN tms_project p ON t.pid = p.pid  "
			+ "			             "
			+ "			            WHERE (:pid IS NULL OR p.pid = :pid) "
			+ "			              AND p.admin_id = :userId  AND   t.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed')"
			+ "			              AND (    "
			+ "			                ( :interval = 'daily' AND DATE(ts.updateddate) = CURDATE()) OR    "
			+ "			                ( :interval = 'weekly' AND ts.updateddate >= CURDATE() - INTERVAL 7 DAY) OR    "
			+ "			                ( :interval = 'monthly' AND ts.updateddate >= CURDATE() - INTERVAL 1 MONTH) OR  "
			+ "                         ( :interval = 'yearly' AND ts.updateddate >= CURDATE() - INTERVAL 1 YEAR) OR  "
			+ "			                ( :interval = 'all')    "
			+ "			              )    "
			+ "			            GROUP BY ts.status    "
			+ "			        ),    "
			+ "			        combined_counts AS (    "
			+ "			            SELECT 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count    "
			+ "			            FROM statuses s    "
			+ "			            LEFT JOIN task_counts tc ON s.status = tc.status    "
			+ "			            UNION ALL    "
			+ "			            SELECT 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count    "
			+ "			            FROM statuses s    "
			+ "			            LEFT JOIN sub_task_counts stc ON s.status = stc.status    "
			+ "			        ),    "
			+ "			        total_counts AS (    "
			+ "			            SELECT 'Total' AS type, s.status,    "
			+ "			                   COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count    "
			+ "			            FROM statuses s    "
			+ "			            LEFT JOIN task_counts tc ON s.status = tc.status    "
			+ "			            LEFT JOIN sub_task_counts stc ON s.status = stc.status    "
			+ "			        )    "
			+ "			        SELECT * FROM combined_counts    "
			+ "			        UNION ALL    "
			+ "			        SELECT * FROM total_counts    "
			+ "			        ORDER BY     "
			+ "			          FIELD(status, 'In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed'),    "
			+ "			          FIELD(type, 'Task', 'Sub Task', 'Total')",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByPidAndAdminIdAndTime(Long pid ,Long userId,String interval);
	
	
	@Query(value = "WITH statuses AS (  "
			+ "			     SELECT 'In Progress' AS status   "
			+ "			     UNION ALL SELECT 'To be Tested'   "
			+ "			     UNION ALL SELECT 'Blocked'   "
			+ "			     UNION ALL SELECT 'Open'   "
			+ "			     UNION ALL SELECT 'Overdue' "
			+ "              UNION ALL SELECT 'On Hold' "
			+ "              UNION ALL SELECT 'In Review' "
			+ "              UNION ALL SELECT 'Closed' "
			+ "			        ),"
			+ " "
			+ "                task_counts AS ( "
			+ "                 SELECT t.status, COUNT(*) AS count "
			+ "                 FROM tms_task t "
			+ "                 JOIN tms_project p ON t.pid = p.pid "
			+ "                 LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid "
			+ "                 LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto "
			+ "                 WHERE t.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed') "
			+ "                   AND ( "
			+ "                         (p.addedby = :userId  AND (p.addedby = :userId  OR tau.tms_user_id = :userId ))   "
			+ "                         OR (p.addedby <> :userId  AND tau.tms_user_id = :userId)                         "
			+ "                       ) and (:pid IS NULL OR p.pid = :pid) "
			+ "			          AND (    "
			+ "			                ( :interval = 'daily' AND DATE(t.last_status_updateddate) = CURDATE()) OR    "
			+ "			                ( :interval = 'weekly' AND t.last_status_updateddate >= CURDATE() - INTERVAL 7 DAY) OR    "
			+ "			                ( :interval = 'monthly' AND t.last_status_updateddate >= CURDATE() - INTERVAL 1 MONTH) OR "
			+ "                         ( :interval = 'yearly' AND t.last_status_updateddate >= CURDATE() - INTERVAL 1 YEAR ) OR  "
			+ "			                ( :interval = 'all')    "
			+ "			              )    "
			+ "			          GROUP BY t.status    "
			+ "			        ),  "
			+ ""
			+ "  "
			+ "			      sub_task_counts AS ( "
			+ "                 SELECT ts.status, COUNT(*) AS count "
			+ "                 FROM tms_sub_task ts "
			+ "                 JOIN tms_task t ON ts.taskid = t.taskid "
			+ "                 JOIN tms_project p ON t.pid = p.pid "
			+ "                 LEFT JOIN tms_assigned_users tau ON tau.subtaskid = ts.subtaskid "
			+ "                 WHERE ts.status IN ('In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed') "
			+ "                   AND ( "
			+ "                           (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId)) "
			+ "                           OR (p.addedby <> :userId AND tau.tms_user_id = :userId) "
			+ "                       )   and (:pid IS NULL OR p.pid = :pid) "
			+ "			          AND (    "
			+ "			                ( :interval = 'daily' AND DATE(ts.last_status_updateddate) = CURDATE()) OR    "
			+ "			                ( :interval = 'weekly' AND ts.last_status_updateddate >= CURDATE() - INTERVAL 7 DAY) OR    "
			+ "			                ( :interval = 'monthly' AND ts.last_status_updateddate >= CURDATE() - INTERVAL 1 MONTH) OR "
			+ "                         ( :interval = 'yearly' AND ts.last_status_updateddate >= CURDATE() - INTERVAL 1 YEAR ) OR    "
			+ "			                ( :interval = 'all')    "
			+ "			              )    "
			+ "			           GROUP BY ts.status    "
			+ "			        ),   "
			+ " "
			+ "			        combined_counts AS (    "
			+ "			            SELECT 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count    "
			+ "			            FROM statuses s    "
			+ "			            LEFT JOIN task_counts tc ON s.status = tc.status    "
			+ "			            UNION ALL    "
			+ "			            SELECT 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count    "
			+ "			            FROM statuses s    "
			+ "			            LEFT JOIN sub_task_counts stc ON s.status = stc.status    "
			+ "			        ),    "
			+ "			        total_counts AS (    "
			+ "			            SELECT 'Total' AS type, s.status,    "
			+ "			                   COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count    "
			+ "			            FROM statuses s    "
			+ "			            LEFT JOIN task_counts tc ON s.status = tc.status    "
			+ "			            LEFT JOIN sub_task_counts stc ON s.status = stc.status    "
			+ "			        )    "
			+ "			        SELECT * FROM combined_counts    "
			+ "			        UNION ALL    "
			+ "			        SELECT * FROM total_counts    "
			+ "			        ORDER BY     "
			+ "			          FIELD(status, 'In Progress', 'To be Tested', 'Blocked', 'Open', 'Overdue','On Hold','In Review','Closed'),    "
			+ "			          FIELD(type, 'Task', 'Sub Task', 'Total')",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByPidAndUserIdAndTime(Long pid ,Long userId,String interval);
	
	
	@Query(value = "SELECT DATE_FORMAT(updateddate, '%Y-%m') AS month, COUNT(*) AS count  "
			+ "FROM  tms_task WHERE  status = :Status AND updateddate IS NOT NULL  GROUP BY  month "
			+ "ORDER BY month ",nativeQuery = true)
	public List<TmsTaskCountData> getTaskCountByMonth(String Status);
	
	@Query(value = "  select Admin_id  from tms_users where user_id = :userId",nativeQuery = true)
	 Long  AdminId (@Param("userId") Long userId);
	
//	@Query(value = "select concat(tms.first_name,' ', COALESCE(tms.middle_name, ''),' ' ,tms.last_name) AS firstName ,"
//			+ "tms.position AS position ,"
//			+ " COUNT(tt.taskid) AS totalAssignedTasks, "
//			+ "    COUNT(CASE WHEN tt.status = 'In Progress' THEN 1 END) AS inProgressTaskCount, "
//			+ "    COUNT(CASE WHEN tt.status = 'Open' THEN 1 END) AS openTaskCount, "
//			+ "    COUNT(CASE WHEN tt.status = 'On Hold' THEN 1 END) AS onHoldTaskCount  ,   "
//			+ "    COUNT(CASE WHEN tt.status = 'Blocked' THEN 1 END) AS blockedTaskCount  ,  "
//			+ "    COUNT(CASE WHEN tt.status = 'To be Tested' THEN 1 END) AS toBeTestedTaskCount ,   "
//			+ "    COUNT(CASE WHEN tt.status = 'In Review' THEN 1 END) AS inReviewCount , "
//			+ "     COUNT(CASE WHEN tt.status = 'Closed' THEN 1 END) AS closedTaskCount , "
//			+ "     COUNT(CASE WHEN tt.status = 'Overdue' THEN 1 END) AS overDueTaskCount "
//			+ "from tms_task  tt join  tms_task_users tu on tt.taskid  = tu.taskid join  tms_assigned_users ttu  on tu.assignedto = ttu.assignid join tms_users  tms on tms.user_id = ttu.tms_user_id "
//			+ "where  (tms.added_by = :adminId or tms.user_id =:adminId)    GROUP BY  "
//			+ "    firstName,tms.position",nativeQuery = true )
//	public List<TmsTaskCountData> getUserTrackerByAdmin(Long adminId );
	
	@Query(value = "select concat(tms.first_name,' ', COALESCE(tms.middle_name, ''),' ' ,tms.last_name)  AS firstName ,"
			+ "tms.position AS position ,"
			+ " COUNT(tt.taskid) AS totalAssignedTasks, "
			+ " \r\n"
			+ "    COUNT(CASE WHEN tt.status = 'In Progress' THEN 1 END) AS inProgressTaskCount, "
			+ "    COUNT(CASE WHEN tt.status = 'Open' THEN 1 END) AS openTaskCount, "
			+ "    COUNT(CASE WHEN tt.status = 'On Hold' THEN 1 END) AS onHoldTaskCount  ,   "
			+ "    COUNT(CASE WHEN tt.status = 'Blocked' THEN 1 END) AS blockedTaskCount  ,  "
			+ "    COUNT(CASE WHEN tt.status = 'To be Tested' THEN 1 END) AS toBeTestedTaskCount ,   "
			+ "    COUNT(CASE WHEN tt.status = 'In Review' THEN 1 END) AS inReviewCount , "
			+ "     COUNT(CASE WHEN tt.status = 'Closed' THEN 1 END) AS closedTaskCount , "
			+ "     COUNT(CASE WHEN tt.status = 'Overdue' THEN 1 END) AS overDueTaskCount "
			+ "from tms_task  tt join  tms_task_users tu on tt.taskid  = tu.taskid join  tms_assigned_users ttu  on tu.assignedto = ttu.assignid join tms_users  tms on tms.user_id = ttu.tms_user_id "
			+ "where  (tms.added_by = :adminId or tms .user_id =:adminId)   and tt.pid = :pid GROUP BY  "
			+ "    firstName,tms.position ",nativeQuery = true )
	public List<TmsTaskCountData> getUserTrackerByAdminAndPid(Long adminId,Long pid );
	
	@Query(value = "SELECT  "
			+ "   concat(tms.first_name,' ', COALESCE(tms.middle_name, ''),' ' ,tms.last_name) AS firstName , "
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
			+ "WHERE (tms.added_by = :adminId OR tms.user_id = :adminId)  AND (:pid IS NULL OR tt.pid = :pid)  "
			+ "GROUP BY firstName,tms.position",nativeQuery = true)
	public  List<TmsTaskCountData> getUserTrackerByAdminAndPidAndTimeInterval(Long adminId, Long pid, String interval );
	
	@Query(value="select distinct p.pid, p.projectid ,p.projectname from tms_project p join tms_assigned_users au ON au.pid=p.pid where admin_id = :addedBy",nativeQuery =true)
	public List<ProjectDropDownDTO> projectDropDownWithAdmin(Long addedBy);
	

	@Query(value="select distinct p.pid, p.projectid ,p.projectname from tms_project p join tms_assigned_users au ON au.pid=p.pid where au.tms_user_id = :userId or p.addedby =:userId",nativeQuery =true)
	public List<ProjectDropDownDTO> projectDropDownWithOutAdmin(@Param("userId") Long userId);
	
	@Query(value = "select Admin_id from tms_project ",nativeQuery = true)
	public List<Long> getAddedBy();
	
	@Query(value = "select tr.rolename from tms_roles tr  join tms_users tu on tu.role_roleid = tr.roleid  where tu.user_id = :user_id",nativeQuery = true)
	public String roleName(Long user_id);
	
	
	@Query(value = "WITH priority AS (   "
			+ "			     SELECT 'High' AS Priority   "
			+ "			     UNION ALL SELECT 'Low'   "
			+ "			     UNION ALL SELECT 'Medium'   "
			+ "			     UNION ALL SELECT 'None'   "
			+ "			 ),   "
			+ "			 task_counts AS (   "
			+ "					   SELECT t.priority, COUNT(*) AS count  "
			+ "					   FROM tms_task t  "
			+ "					   JOIN tms_project p ON t.pid = p.pid   "
			+ "					   WHERE  p.admin_id = :adminId  AND  t.priority IN  ('High', 'Low', 'Medium', 'None')  "
			+ "					   GROUP BY t.priority   "
			+ "					 ),  "
			+ "			 "
			+ "			sub_task_counts AS (    "
			+ "				      SELECT ts.priority, COUNT(*) AS count    "
			+ "					  FROM tms_sub_task ts    "
			+ "					  JOIN tms_task t ON ts.taskid = t.taskid    "
			+ "					  JOIN tms_project p ON t.pid = p.pid    "
			+ "					  WHERE    p.admin_id = :adminId AND  ts.priority IN  ('High', 'Low', 'Medium', 'None')   "
			+ "					  GROUP BY ts.priority    "
			+ "				  ),   "
			+ "			 combined_counts AS (   "
			+ "			     SELECT 'Task' AS type, s.Priority, COALESCE(tc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			    "
			+ "			     UNION ALL   "
			+ "			    "
			+ "			     SELECT 'Sub Task' AS type, s.Priority, COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 ),   "
			+ "			 total_counts AS (   "
			+ "			     SELECT 'Total' AS type, s.Priority,   "
			+ "			     COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 )   "
			+ "			    "
			+ "			 SELECT * FROM combined_counts   "
			+ "			 UNION ALL   "
			+ "			 SELECT * FROM total_counts   "
			+ "			 ORDER BY    "
			+ "			   CASE priority   "
			+ "			     WHEN 'High' THEN 1   "
			+ "			     WHEN 'Low' THEN 2   "
			+ "			     WHEN 'Medium' THEN 3   "
			+ "			     WHEN 'None' THEN 4   "
			+ "			     ELSE 5 "
			+ "			   END,   "
			+ "			   CASE type   "
			+ "			     WHEN 'Task' THEN 1   "
			+ "			     WHEN 'Sub Task' THEN 2   "
			+ "			     WHEN 'Total' THEN 3   "
			+ "			     ELSE 4   "
			+ "			   END",nativeQuery = true)
	public  List<TmsTaskCountData>   getPriorityByAdminId(Long adminId);
	
	@Query(value = "WITH priority AS (   "
			+ "			     SELECT 'High' AS Priority   "
			+ "			     UNION ALL SELECT 'Low'   "
			+ "			     UNION ALL SELECT 'Medium'   "
			+ "			     UNION ALL SELECT 'None'   "
			+ "			      "
			+ "			 ),   "
			+ "			 task_counts AS (   "
			+ "					     SELECT t.priority, COUNT(*) AS count  "
			+ "					     FROM tms_task t  "
			+ "					     JOIN tms_project p ON t.pid = p.pid "
			+ "					     LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid "
			+ "                      LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto "
			+ "                      WHERE t.priority IN ('High', 'Low', 'Medium', 'None') "
			+ "                      AND ( "
			+ "                           (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId))   "
			+ "                           OR (p.addedby <> :userId  AND tau.tms_user_id = :userId )                         "
			+ "                          ) "
			+ "					     GROUP BY t.priority   "
			+ "					 ),   "
			+ "			 "
			+ "					 sub_task_counts AS (    "
			+ "				             SELECT ts.priority, COUNT(*) AS count    "
			+ "					         FROM tms_sub_task ts    "
			+ "					         JOIN tms_task t ON ts.taskid = t.taskid "
			+ "                          JOIN tms_project p ON t.pid = p.pid "
			+ "                          LEFT JOIN tms_assigned_users tau ON tau.subtaskid = ts.subtaskid "
			+ "                          WHERE ts.priority IN ('High', 'Low', 'Medium', 'None') "
			+ "                           AND ( "
			+ "                                 (p.addedby = :userId  AND (p.addedby = :userId  OR tau.tms_user_id = :userId )) "
			+ "                                 OR (p.addedby <> :userId  AND tau.tms_user_id = :userId ) "
			+ "                               ) "
			+ "					        GROUP BY ts.priority     "
			+ "						),   "
			+ "			 combined_counts AS (   "
			+ "			     SELECT 'Task' AS type, s.Priority, COALESCE(tc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			    "
			+ "			     UNION ALL   "
			+ "			    "
			+ "			     SELECT 'Sub Task' AS type, s.Priority, COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 ),   "
			+ "			 total_counts AS (   "
			+ "			     SELECT 'Total' AS type, s.Priority,   "
			+ "			     COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 )   "
			+ "			    "
			+ "			 SELECT * FROM combined_counts   "
			+ "			 UNION ALL   "
			+ "			 SELECT * FROM total_counts   "
			+ "			 ORDER BY    "
			+ "			   CASE priority   "
			+ "			     WHEN 'High' THEN 1   "
			+ "			     WHEN 'Low' THEN 2   "
			+ "			     WHEN 'Medium' THEN 3   "
			+ "			     WHEN 'None' THEN 4   "
			+ "			     ELSE 5 "
			+ "			   END,   "
			+ "			   CASE type   "
			+ "			     WHEN 'Task' THEN 1   "
			+ "			     WHEN 'Sub Task' THEN 2   "
			+ "			     WHEN 'Total' THEN 3   "
			+ "			     ELSE 4   "
			+ "			   END;",nativeQuery = true)
	public  List<TmsTaskCountData>   getPriorityByUserId(Long userId);
	
	@Query(value = "WITH priority AS (   "
			+ "			     SELECT 'High' AS Priority   "
			+ "			     UNION ALL SELECT 'Low'   "
			+ "			     UNION ALL SELECT 'Medium'   "
			+ "			     UNION ALL SELECT 'None'   "
			+ "			      ),   "
			+ "	                task_counts AS (   "
			+ "					             SELECT t.priority, COUNT(*) AS count  "
			+ "						         FROM tms_task t  "
			+ "					             JOIN tms_project p ON t.pid = p.pid   "
			+ "						         WHERE  p.admin_id = :adminId   AND  p.pid = :pid AND  t.priority IN  ('High', 'Low', 'Medium', 'None')  "
			+ "					             GROUP BY t.priority   "
			+ "					           ),  "
			+ "					sub_task_counts AS (    "
			+ "				                 SELECT ts.priority, COUNT(*) AS count    "
			+ "					             FROM tms_sub_task ts    "
			+ "					             JOIN tms_task t ON ts.taskid = t.taskid    "
			+ "						         JOIN tms_project p ON t.pid = p.pid    "
			+ "						         WHERE    p.admin_id = :adminId  AND p.pid = :pid AND  ts.priority IN  ('High', 'Low', 'Medium', 'None')   "
			+ "					             GROUP BY ts.priority    "
			+ "						       ),   "
			+ "			   combined_counts AS (   "
			+ "			     SELECT 'Task' AS type, s.Priority, COALESCE(tc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			    "
			+ "			     UNION ALL   "
			+ "			    "
			+ "			     SELECT 'Sub Task' AS type, s.Priority, COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 ),   "
			+ "			 total_counts AS (   "
			+ "			     SELECT 'Total' AS type, s.Priority,   "
			+ "			     COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 )   "
			+ "			    "
			+ "			 SELECT * FROM combined_counts   "
			+ "			 UNION ALL   "
			+ "			 SELECT * FROM total_counts   "
			+ "			 ORDER BY    "
			+ "			   CASE priority   "
			+ "			     WHEN 'High' THEN 1   "
			+ "			     WHEN 'Low' THEN 2   "
			+ "			     WHEN 'Medium' THEN 3   "
			+ "			     WHEN 'None' THEN 4   "
			+ "			     ELSE 5 "
			+ "			   END,   "
			+ "			   CASE type   "
			+ "			     WHEN 'Task' THEN 1   "
			+ "			     WHEN 'Sub Task' THEN 2   "
			+ "			     WHEN 'Total' THEN 3   "
			+ "			     ELSE 4   "
			+ "			   END;    "
			+ "             ",nativeQuery = true)
	public  List<TmsTaskCountData>   getPriorityByAdminIdAndPid(Long adminId , Long pid);
	
	@Query(value = "WITH priority AS (   "
			+ "			     SELECT 'High' AS Priority   "
			+ "			     UNION ALL SELECT 'Low'   "
			+ "			     UNION ALL SELECT 'Medium'   "
			+ "			     UNION ALL SELECT 'None'   "
			+ "			      "
			+ "			 ),   "
			+ "			 task_counts AS (   "
			+ "					     SELECT t.priority, COUNT(*) AS count  "
			+ "					     FROM tms_task t  "
			+ "					     JOIN tms_project p ON t.pid = p.pid "
			+ "						 LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid "
			+ "                      LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto "
			+ "                      WHERE t.priority IN ('High', 'Low', 'Medium', 'None') "
			+ "                      AND ( "
			+ "                           (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId))   "
			+ "                           OR (p.addedby <> :userId AND tau.tms_user_id = :userId)                         "
			+ "                          ) AND p.pid = :pid "
			+ "					     GROUP BY t.priority   "
			+ "					 ),   "
			+ "			 "
			+ "					 sub_task_counts AS (    "
			+ "				        SELECT ts.priority, COUNT(*) AS count    "
			+ "					    FROM tms_sub_task ts    "
			+ "					    JOIN tms_task t ON ts.taskid = t.taskid "
			+ "                     JOIN tms_project p ON t.pid = p.pid "
			+ "                     LEFT JOIN tms_assigned_users tau ON tau.subtaskid = ts.subtaskid "
			+ "                     WHERE ts.priority IN ('High', 'Low', 'Medium', 'None') "
			+ "                      AND ( "
			+ "                         (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId)) "
			+ "                         OR (p.addedby <> :userId AND tau.tms_user_id = :userId) "
			+ "                          ) AND p.pid = :pid "
			+ "					     GROUP BY ts.priority     "
			+ "						 ),   "
			+ "			 combined_counts AS (   "
			+ "			     SELECT 'Task' AS type, s.Priority, COALESCE(tc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			    "
			+ "			     UNION ALL   "
			+ "			    "
			+ "			     SELECT 'Sub Task' AS type, s.Priority, COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 ),   "
			+ "			 total_counts AS (   "
			+ "			     SELECT 'Total' AS type, s.Priority,   "
			+ "			      COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 )   "
			+ "			    "
			+ "			 SELECT * FROM combined_counts   "
			+ "			 UNION ALL   "
			+ "			 SELECT * FROM total_counts   "
			+ "			 ORDER BY    "
			+ "			   CASE priority   "
			+ "			     WHEN 'High' THEN 1   "
			+ "			     WHEN 'Low' THEN 2   "
			+ "			     WHEN 'Medium' THEN 3   "
			+ "			     WHEN 'None' THEN 4   "
			+ "			     ELSE 5 "
			+ "			   END,   "
			+ "			   CASE type   "
			+ "			     WHEN 'Task' THEN 1   "
			+ "			     WHEN 'Sub Task' THEN 2   "
			+ "			     WHEN 'Total' THEN 3   "
			+ "			     ELSE 4   "
			+ "			   END;",nativeQuery = true)
	public  List<TmsTaskCountData>   getPriorityByUserIdAndpid(Long userId, Long pid);
	
	@Query(value = "WITH priority AS (   "
			+ "			     SELECT 'High' AS Priority   "
			+ "			     UNION ALL SELECT 'Low'   "
			+ "			     UNION ALL SELECT 'Medium'   "
			+ "			     UNION ALL SELECT 'None'   "
			+ "			      "
			+ "			 ),   "
			+ "	              task_counts AS ( "
			+ "                        SELECT t.priority, COUNT(*) AS count "
			+ "                        FROM tms_task t "
			+ "                        JOIN tms_project p ON t.pid = p.pid "
			+ "                        WHERE  p.admin_id = :adminId  "
			+ "                        AND  (:pid IS NULL OR p.pid = :pid) "
			+ "                        AND t.priority IN ('High', 'Low', 'Medium', 'None') "
			+ "			               AND (    "
			+ "			                ( :intervel = 'daily' AND DATE(t.updateddate) = CURDATE()) OR    "
			+ "			                ( :intervel = 'weekly' AND t.updateddate >= CURDATE() - INTERVAL 7 DAY) OR    "
			+ "			                ( :intervel = 'monthly' AND t.updateddate >= CURDATE() - INTERVAL 1 MONTH) OR  "
			+ "                         ( :intervel = 'yearly' AND t.updateddate >= CURDATE() - INTERVAL 1 YEAR) OR "
			+ "			                ( :intervel = 'all')    "
			+ "			              )    "
			+ "			            GROUP BY t.priority    "
			+ "			        ),    "
			+ "                     "
			+ "			 "
			+ "				sub_task_counts AS ( "
			+ "                        SELECT ts.priority, COUNT(*) AS count "
			+ "                        FROM tms_sub_task ts "
			+ "                        JOIN tms_task t ON ts.taskid = t.taskid "
			+ "                        JOIN tms_project p ON t.pid = p.pid "
			+ "                        WHERE  p.admin_id = :adminId   "
			+ "                        AND (:pid IS NULL OR p.pid = :pid)"
			+ "                        AND ts.priority IN ('High', 'Low', 'Medium', 'None') "
			+ "			                AND (    "
			+ "			                ( :intervel = 'daily' AND DATE(ts.updateddate) = CURDATE()) OR    "
			+ "			                ( :intervel = 'weekly' AND ts.updateddate >= CURDATE() - INTERVAL 7 DAY) OR    "
			+ "			                ( :intervel = 'monthly' AND ts.updateddate >= CURDATE() - INTERVAL 1 MONTH) OR    "
			+ "							( :intervel = 'yearly' AND ts.updateddate >= CURDATE() - INTERVAL 1 YEAR) OR "
			+ "			                ( :intervel = 'all')    "
			+ "			              )    "
			+ "			            GROUP BY ts.priority    "
			+ "			        ),    "
			+ "			 combined_counts AS (   "
			+ "			     SELECT 'Task' AS type, s.Priority, COALESCE(tc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			    "
			+ "			     UNION ALL   "
			+ "			    "
			+ "			     SELECT 'Sub Task' AS type, s.Priority, COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 ),   "
			+ "			 total_counts AS (   "
			+ "			     SELECT 'Total' AS type, s.Priority,   "
			+ "			     COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 )   "
			+ "			    "
			+ "			 SELECT * FROM combined_counts   "
			+ "			 UNION ALL   "
			+ "			 SELECT * FROM total_counts   "
			+ "			 ORDER BY    "
			+ "			   CASE priority   "
			+ "			     WHEN 'High' THEN 1   "
			+ "			     WHEN 'Low' THEN 2   "
			+ "			     WHEN 'Medium' THEN 3   "
			+ "			     WHEN 'None' THEN 4   "
			+ "			     ELSE 5 "
			+ "			   END,   "
			+ "			   CASE type   "
			+ "			     WHEN 'Task' THEN 1   "
			+ "			     WHEN 'Sub Task' THEN 2   "
			+ "			     WHEN 'Total' THEN 3   "
			+ "			     ELSE 4   "
			+ "			   END;",nativeQuery = true)
	public  List<TmsTaskCountData>   getPriorityByAdminIdAndPidAndTime(Long adminId,Long pid,String intervel);
	
	@Query(value = "WITH priority AS (   "
			+ "			     SELECT 'High' AS Priority   "
			+ "			     UNION ALL SELECT 'Low'   "
			+ "			     UNION ALL SELECT 'Medium'   "
			+ "			     UNION ALL SELECT 'None'   "
			+ "			      "
			+ "			 ),   "
			+ "	               task_counts AS ( "
			+ "                        SELECT t.priority, COUNT(*) AS count "
			+ "                        FROM tms_task t "
			+ "                        JOIN tms_project p ON t.pid = p.pid "
			+ "                        LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid "
			+ "                        LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto "
			+ "                        WHERE t.priority IN ('High', 'Low', 'Medium', 'None') "
			+ "                        AND ( "
			+ "                            (p.addedby = :userId AND (p.addedby = :userId  OR tau.tms_user_id = :userId ))   "
			+ "                            OR (p.addedby <> :userId  AND tau.tms_user_id = :userId)                         "
			+ "                            ) AND (:pid IS NULL OR p.pid = :pid) "
			+ "			               AND (    "
			+ "			                ( :intervel = 'daily' AND DATE(t.updateddate) = CURDATE()) OR    "
			+ "			                ( :intervel = 'weekly' AND t.updateddate >= CURDATE() - INTERVAL 7 DAY) OR    "
			+ "			                ( :intervel = 'monthly' AND t.updateddate >= CURDATE() - INTERVAL 1 MONTH) OR  "
			+ "                         ( :intervel = 'yearly' AND t.updateddate >= CURDATE() - INTERVAL 1 YEAR) OR "
			+ "			                ( :intervel = 'all')    "
			+ "			              )    "
			+ "			            GROUP BY t.priority    "
			+ "			        ),    "
			+ "                     "
			+ "			 "
			+ "				   sub_task_counts AS ( "
			+ "                      SELECT ts.priority, COUNT(*) AS count "
			+ "                      FROM tms_sub_task ts "
			+ "                      JOIN tms_task t ON ts.taskid = t.taskid "
			+ "                      JOIN tms_project p ON t.pid = p.pid "
			+ "                      LEFT JOIN tms_assigned_users tau ON tau.subtaskid = ts.subtaskid "
			+ "                      WHERE ts.priority IN ('High', 'Low', 'Medium', 'None') "
			+ "                      AND ( "
			+ "                          (p.addedby = :userId AND (p.addedby =  :userId  OR tau.tms_user_id = :userId)) "
			+ "                          OR (p.addedby <> :userId  AND tau.tms_user_id = :userId ) "
			+ "                          ) AND (:pid IS NULL OR p.pid = :pid) "
			+ "			             AND (    "
			+ "			                ( :intervel = 'daily' AND DATE(ts.updateddate) = CURDATE()) OR    "
			+ "			                ( :intervel = 'weekly' AND ts.updateddate >= CURDATE() - INTERVAL 7 DAY) OR    "
			+ "			                ( :intervel = 'monthly' AND ts.updateddate >= CURDATE() - INTERVAL 1 MONTH) OR    "
			+ "							( :intervel = 'yearly' AND ts.updateddate >= CURDATE() - INTERVAL 1 YEAR) OR "
			+ "			                ( :intervel = 'all')    "
			+ "			              )    "
			+ "			            GROUP BY ts.priority    "
			+ "			        ),    "
			+ "			 combined_counts AS (   "
			+ "			     SELECT 'Task' AS type, s.Priority, COALESCE(tc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			    "
			+ "			     UNION ALL   "
			+ "			    "
			+ "			     SELECT 'Sub Task' AS type, s.Priority, COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 ),   "
			+ "			 total_counts AS (   "
			+ "			     SELECT 'Total' AS type, s.Priority,   "
			+ "			     COALESCE(tc.count, 0) + COALESCE(stc.count, 0) AS count   "
			+ "			     FROM priority s   "
			+ "			     LEFT JOIN task_counts tc ON s.Priority = tc.Priority   "
			+ "			     LEFT JOIN sub_task_counts stc ON s.Priority = stc.Priority   "
			+ "			 )   "
			+ "			    "
			+ "			 SELECT * FROM combined_counts   "
			+ "			 UNION ALL   "
			+ "			 SELECT * FROM total_counts   "
			+ "			 ORDER BY    "
			+ "			   CASE priority   "
			+ "			     WHEN 'High' THEN 1   "
			+ "			     WHEN 'Low' THEN 2   "
			+ "			     WHEN 'Medium' THEN 3   "
			+ "			     WHEN 'None' THEN 4   "
			+ "			     ELSE 5 "
			+ "			   END,   "
			+ "			   CASE type   "
			+ "			     WHEN 'Task' THEN 1   "
			+ "			     WHEN 'Sub Task' THEN 2   "
			+ "			     WHEN 'Total' THEN 3   "
			+ "			     ELSE 4   "
			+ "			   END;",nativeQuery = true)
	public  List<TmsTaskCountData>   getPriorityByuserIdAndPidAndTime(Long userId,Long pid, String intervel);
	
	
	@Query(value = 
	        "WITH date_range AS ( " +
	        "    SELECT DATE_ADD(:fromDate, INTERVAL seq DAY) AS period " +
	        "    FROM ( " +
	        "        SELECT 0 AS seq UNION ALL " +
	        "        SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL " +
	        "        SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 " +
	        "    ) AS days " +
	        "), " +
	        "statuses AS ( " +
	        "    SELECT 'closed' AS status " +
	        "), " +
	        "task_counts AS ( " +
	        "    SELECT DATE(t.last_status_updateddate) AS period, COUNT(*) AS count " +
	        "    FROM tms_task t " +
	        "    JOIN tms_project p ON t.pid = p.pid " +
	        "    WHERE t.status = 'closed' " +
	        "      AND DATE(t.last_status_updateddate) BETWEEN :fromDate AND :toDate " +
	        "      AND (p.admin_id = :adminId) AND (:pid IS NULL OR p.pid = :pid)" +
	        "    GROUP BY DATE(t.last_status_updateddate) " +
	        "), " +
	        "sub_task_counts AS ( " +
	        "    SELECT DATE(ts.last_status_updateddate) AS period, COUNT(*) AS count " +
	        "    FROM tms_sub_task ts " +
	        "    JOIN tms_task t ON ts.taskid = t.taskid " +
	        "    JOIN tms_project p ON t.pid = p.pid " +
	        "    WHERE ts.status = 'closed' " +
	        "      AND DATE(ts.last_status_updateddate) BETWEEN :fromDate AND :toDate " +
	        "      AND (p.admin_id = :adminId) AND (:pid IS NULL OR p.pid = :pid)" +
	        "    GROUP BY DATE(ts.last_status_updateddate) " +
	        "), " +
	        "combined_counts AS ( " +
	        "    SELECT dr.period, 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count " +
	        "    FROM date_range dr " +
	        "    CROSS JOIN statuses s " +
	        "    LEFT JOIN task_counts tc ON dr.period = tc.period " +
	        "    UNION ALL " +
	        "    SELECT dr.period, 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count " +
	        "    FROM date_range dr " +
	        "    CROSS JOIN statuses s " +
	        "    LEFT JOIN sub_task_counts stc ON dr.period = stc.period " +
	        "), " +
	        "total_counts AS ( " +
	        "    SELECT period, 'Total' AS type, 'closed' AS status, SUM(count) AS count " +
	        "    FROM combined_counts " +
	        "    GROUP BY period " +
	        ") " +
	        "SELECT * FROM combined_counts " +
	        "UNION ALL " +
	        "SELECT * FROM total_counts " +
	        "ORDER BY period, FIELD(type, 'Task', 'Sub Task', 'Total')",
	        nativeQuery = true)
	    List<CompletedStatusCountResponse> getDailyTaskStatsAdmin(
	        @Param("fromDate") String fromDate,
	        @Param("toDate") String toDate,
	        @Param("adminId") Long adminId,@Param("pid") Long pid
	    );
	    
	    @Query(value = 
	            "WITH date_range AS ( " +
	            "    SELECT DATE_ADD(:fromDate, INTERVAL seq DAY) AS period " +
	            "    FROM ( " +
	            "        SELECT 0 AS seq UNION ALL " +
	            "        SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL " +
	            "        SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 " +
	            "    ) AS days " +
	            "), " +
	            "statuses AS ( " +
	            "    SELECT 'closed' AS status " +
	            "), " +
	            "task_counts AS ( " +
	            "    SELECT DATE(t.last_status_updateddate) AS period, COUNT(*) AS count " +
	            "    FROM tms_task t " +
	            "    JOIN tms_project p ON t.pid = p.pid " +
	            "    LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid " +
	            "    LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto " +
	            "    WHERE t.status = 'closed' " +
	            "      AND DATE(t.last_status_updateddate) BETWEEN :fromDate AND :toDate " +
	            "      AND ( " +
	            "           (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId)) " +
	            "        OR (p.addedby <> :userId AND tau.tms_user_id = :userId) " +
	            "      )  AND (:pid IS NULL OR p.pid = :pid) " +
	            "    GROUP BY DATE(t.last_status_updateddate) " +
	            "), " +
	            "sub_task_counts AS ( " +
	            "    SELECT DATE(ts.last_status_updateddate) AS period, COUNT(*) AS count " +
	            "    FROM tms_sub_task ts " +
	            "    JOIN tms_task t ON ts.taskid = t.taskid " +
	            "    JOIN tms_project p ON t.pid = p.pid " +
	            "    LEFT JOIN tms_assigned_users tau ON tau.subtaskid = ts.subtaskid " +
	            "    WHERE ts.status = 'closed' " +
	            "      AND DATE(ts.last_status_updateddate) BETWEEN :fromDate AND :toDate " +
	            "      AND ( " +
	            "           (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId)) " +
	            "        OR (p.addedby <> :userId AND tau.tms_user_id = :userId) " +
	            "      )  AND (:pid IS NULL OR p.pid = :pid) " +
	            "    GROUP BY DATE(ts.last_status_updateddate) " +
	            "), " +
	            "combined_counts AS ( " +
	            "    SELECT dr.period, 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count " +
	            "    FROM date_range dr " +
	            "    CROSS JOIN statuses s " +
	            "    LEFT JOIN task_counts tc ON dr.period = tc.period " +
	            "    UNION ALL " +
	            "    SELECT dr.period, 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count " +
	            "    FROM date_range dr " +
	            "    CROSS JOIN statuses s " +
	            "    LEFT JOIN sub_task_counts stc ON dr.period = stc.period " +
	            "), " +
	            "total_counts AS ( " +
	            "    SELECT period, 'Total' AS type, 'closed' AS status, SUM(count) AS count " +
	            "    FROM combined_counts " +
	            "    GROUP BY period " +
	            ") " +
	            "SELECT * FROM combined_counts " +
	            "UNION ALL " +
	            "SELECT * FROM total_counts " +
	            "ORDER BY period, FIELD(type, 'Task', 'Sub Task', 'Total')",
	            nativeQuery = true)
	        List<CompletedStatusCountResponse> getDailyTaskStatussUserId(@Param("fromDate") String fromDate,@Param("toDate") String toDate,@Param("userId") Long userId,@Param("pid") Long pid);
	        
	        
	        @Query(value = 
	        	    " WITH month_range AS ( " +
	        	    "     SELECT DATE_FORMAT(DATE_ADD(CONCAT(:year, '-01-01'), INTERVAL seq MONTH), '%Y-%m') AS period " +
	        	    "     FROM ( " +
	        	    "         SELECT 0 AS seq UNION ALL " +
	        	    "         SELECT 1 UNION ALL " +
	        	    "         SELECT 2 UNION ALL " +
	        	    "         SELECT 3 UNION ALL " +
	        	    "         SELECT 4 UNION ALL " +
	        	    "         SELECT 5 UNION ALL " +
	        	    "         SELECT 6 UNION ALL " +
	        	    "         SELECT 7 UNION ALL " +
	        	    "         SELECT 8 UNION ALL " +
	        	    "         SELECT 9 UNION ALL " +
	        	    "         SELECT 10 UNION ALL " +
	        	    "         SELECT 11 " +
	        	    "     ) AS months " +
	        	    " ), " +
	        	    " statuses AS ( " +
	        	    "     SELECT 'closed' AS status " +
	        	    " ), " +
	        	    " task_counts AS ( " +
	        	    "     SELECT DATE_FORMAT(t.last_status_updateddate, '%Y-%m') AS period, COUNT(*) AS count " +
	        	    "     FROM tms_task t " +
	        	    "     JOIN tms_project p ON t.pid = p.pid " +
	        	    "     WHERE t.status = 'closed' " +
	        	    "       AND DATE_FORMAT(t.last_status_updateddate, '%Y-%m') BETWEEN CONCAT(:year, '-01') AND CONCAT(:year, '-12') " +
	        	    "       AND (p.admin_id = :adminId) AND (:pid IS NULL OR p.pid = :pid) " +
	        	    "     GROUP BY DATE_FORMAT(t.last_status_updateddate, '%Y-%m') " +
	        	    " ), " +
	        	    " sub_task_counts AS ( " +
	        	    "     SELECT DATE_FORMAT(ts.last_status_updateddate, '%Y-%m') AS period, COUNT(*) AS count " +
	        	    "     FROM tms_sub_task ts " +
	        	    "     JOIN tms_task t ON ts.taskid = t.taskid " +
	        	    "     JOIN tms_project p ON t.pid = p.pid " +
	        	    "     WHERE ts.status = 'closed' " +
	        	    "       AND DATE_FORMAT(ts.last_status_updateddate, '%Y-%m') BETWEEN CONCAT(:year, '-01') AND CONCAT(:year, '-12') " +
	        	    "       AND (p.admin_id = :adminId) AND (:pid IS NULL OR p.pid = :pid) " +
	        	    "     GROUP BY DATE_FORMAT(ts.last_status_updateddate, '%Y-%m') " +
	        	    " ), " +
	        	    " combined_counts AS ( " +
	        	    "     SELECT mr.period, 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count " +
	        	    "     FROM month_range mr " +
	        	    "     CROSS JOIN statuses s " +
	        	    "     LEFT JOIN task_counts tc ON mr.period = tc.period " +
	        	    "     UNION ALL " +
	        	    "     SELECT mr.period, 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count " +
	        	    "     FROM month_range mr " +
	        	    "     CROSS JOIN statuses s " +
	        	    "     LEFT JOIN sub_task_counts stc ON mr.period = stc.period " +
	        	    " ), " +
	        	    " total_counts AS ( " +
	        	    "     SELECT period, 'Total' AS type, 'closed' AS status, SUM(count) AS count " +
	        	    "     FROM combined_counts " +
	        	    "     GROUP BY period " +
	        	    " ) " +
	        	    " SELECT * FROM combined_counts " +
	        	    " UNION ALL " +
	        	    " SELECT * FROM total_counts " +
	        	    " ORDER BY period, FIELD(type, 'Task', 'Sub Task', 'Total') ",
	        	    nativeQuery = true)
	        	List<CompletedStatusCountResponse> getMonthlyTaskStatsAdmin(@Param("year") int year, @Param("adminId") Long adminId,@Param("pid") Long pid);
	        	
	        	@Query(value =
	        		    "WITH month_range AS ( " +
	        		    "    SELECT DATE_FORMAT(DATE_ADD(CONCAT(:year, '-01-01'), INTERVAL seq MONTH), '%Y-%m') AS period " +
	        		    "    FROM ( " +
	        		    "        SELECT 0 AS seq UNION ALL " +
	        		    "        SELECT 1 UNION ALL " +
	        		    "        SELECT 2 UNION ALL " +
	        		    "        SELECT 3 UNION ALL " +
	        		    "        SELECT 4 UNION ALL " +
	        		    "        SELECT 5 UNION ALL " +
	        		    "        SELECT 6 UNION ALL " +
	        		    "        SELECT 7 UNION ALL " +
	        		    "        SELECT 8 UNION ALL " +
	        		    "        SELECT 9 UNION ALL " +
	        		    "        SELECT 10 UNION ALL " +
	        		    "        SELECT 11 " +
	        		    "    ) AS months " +
	        		    "), " +
	        		    "statuses AS ( " +
	        		    "    SELECT 'closed' AS status " +
	        		    "), " +
	        		    "task_counts AS ( " +
	        		    "    SELECT DATE_FORMAT(t.last_status_updateddate, '%Y-%m') AS period, COUNT(*) AS count " +
	        		    "    FROM tms_task t " +
	        		    "    JOIN tms_project p ON t.pid = p.pid " +
	        		    "    LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid " +
	        		    "    LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto " +
	        		    "    WHERE t.status = 'closed' " +
	        		    "      AND DATE_FORMAT(t.last_status_updateddate, '%Y-%m') BETWEEN CONCAT(:year, '-01') AND CONCAT(:year, '-12') " +
	        		    "      AND ( " +
	        		    "          (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId)) " +
	        		    "          OR (p.addedby <> :userId AND (tau.tms_user_id = :userId OR tau.tms_user_id IS NULL)) " +
	        		 
	        		    "      )  AND (:pid IS NULL OR p.pid = :pid) " +
	        		    "    GROUP BY DATE_FORMAT(t.last_status_updateddate, '%Y-%m') " +
	        		    "), " +
	        		    "sub_task_counts AS ( " +
	        		    "    SELECT DATE_FORMAT(ts.last_status_updateddate, '%Y-%m') AS period, COUNT(*) AS count " +
	        		    "    FROM tms_sub_task ts " +
	        		    "    JOIN tms_task t ON ts.taskid = t.taskid " +
	        		    "    JOIN tms_project p ON t.pid = p.pid " +
	        		    "    LEFT JOIN tms_assigned_users tau ON tau.subtaskid = ts.subtaskid " +
	        		    "    WHERE ts.status = 'closed' " +
	        		    "      AND DATE_FORMAT(ts.last_status_updateddate, '%Y-%m') BETWEEN CONCAT(:year, '-01') AND CONCAT(:year, '-12') " +
	        		    "      AND ( " +
	        		    "          (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId)) " +
	        		    "          OR (p.addedby <> :userId AND (tau.tms_user_id = :userId OR tau.tms_user_id IS NULL)) " +
	        		   
	        		    "      )  AND (:pid IS NULL OR p.pid = :pid) " +
	        		    "    GROUP BY DATE_FORMAT(ts.last_status_updateddate, '%Y-%m') " +
	        		    "), " +
	        		    "combined_counts AS ( " +
	        		    "    SELECT mr.period, 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count " +
	        		    "    FROM month_range mr " +
	        		    "    CROSS JOIN statuses s " +
	        		    "    LEFT JOIN task_counts tc ON mr.period = tc.period " +
	        		    "    UNION ALL " +
	        		    "    SELECT mr.period, 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count " +
	        		    "    FROM month_range mr " +
	        		    "    CROSS JOIN statuses s " +
	        		    "    LEFT JOIN sub_task_counts stc ON mr.period = stc.period " +
	        		    "), " +
	        		    "total_counts AS ( " +
	        		    "    SELECT period, 'Total' AS type, 'closed' AS status, SUM(count) AS count " +
	        		    "    FROM combined_counts " +
	        		    "    GROUP BY period " +
	        		    ") " +
	        		    "SELECT * FROM combined_counts " +
	        		    "UNION ALL " +
	        		    "SELECT * FROM total_counts " +
	        		    "ORDER BY " +
	        		    "  period, " +
	        		    "  FIELD(type, 'Task', 'Sub Task', 'Total') ",
	        		    nativeQuery = true)
	        		List<CompletedStatusCountResponse> getMonthlyTaskStatusUserId(@Param("year") int year, @Param("userId") Long userId,@Param("pid") Long pid);


	        	    @Query(value =
	        	        "WITH year_range AS ( " +
	        	        "    SELECT CONCAT((YEAR(CURDATE()) - 2 + seq), '') AS year " +
	        	        "    FROM ( " +
	        	        "        SELECT 0 AS seq UNION ALL " +
	        	        "        SELECT 1 UNION ALL " +
	        	        "        SELECT 2 UNION ALL " +
	        	        "        SELECT 3 UNION ALL " +
	        	        "        SELECT 4 " +
	        	        "    ) AS years " +
	        	        "), " +
	        	        "statuses AS ( " +
	        	        "    SELECT 'closed' AS status " +
	        	        "), " +
	        	        "task_counts AS ( " +
	        	        "    SELECT YEAR(t.last_status_updateddate) AS year, COUNT(*) AS count " +
	        	        "    FROM tms_task t " +
	        	        "    JOIN tms_project p ON t.pid = p.pid " +
	        	        "    WHERE t.status = 'closed' " +
	        	        "      AND YEAR(t.last_status_updateddate) BETWEEN (YEAR(CURDATE()) - 2) AND (YEAR(CURDATE()) + 2) " +
	        	        "      AND p.admin_id = :adminId " +
	        	        "      AND (:pid IS NULL OR p.pid = :pid) " +
	        	        "    GROUP BY YEAR(t.last_status_updateddate) " +
	        	        "), " +
	        	        "sub_task_counts AS ( " +
	        	        "    SELECT YEAR(ts.last_status_updateddate) AS year, COUNT(*) AS count " +
	        	        "    FROM tms_sub_task ts " +
	        	        "    JOIN tms_task t ON ts.taskid = t.taskid " +
	        	        "    JOIN tms_project p ON t.pid = p.pid " +
	        	        "    WHERE ts.status = 'closed' " +
	        	        "      AND YEAR(ts.last_status_updateddate) BETWEEN (YEAR(CURDATE()) - 2) AND (YEAR(CURDATE()) + 2) " +
	        	        "      AND p.admin_id = :adminId " +
	        	        "      AND (:pid IS NULL OR p.pid = :pid) " +
	        	        "    GROUP BY YEAR(ts.last_status_updateddate) " +
	        	        "), " +
	        	        "combined_counts AS ( " +
	        	        "    SELECT yr.year, 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count " +
	        	        "    FROM year_range yr " +
	        	        "    CROSS JOIN statuses s " +
	        	        "    LEFT JOIN task_counts tc ON yr.year = CONCAT(tc.year, '') " +
	        	        "    UNION ALL " +
	        	        "    SELECT yr.year, 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count " +
	        	        "    FROM year_range yr " +
	        	        "    CROSS JOIN statuses s " +
	        	        "    LEFT JOIN sub_task_counts stc ON yr.year = CONCAT(stc.year, '') " +
	        	        "), " +
	        	        "total_counts AS ( " +
	        	        "    SELECT year, 'Total' AS type, 'closed' AS status, SUM(count) AS count " +
	        	        "    FROM combined_counts " +
	        	        "    GROUP BY year " +
	        	        ") " +
	        	        "SELECT * FROM combined_counts " +
	        	        "UNION ALL " +
	        	        "SELECT * FROM total_counts " +
	        	        "ORDER BY CAST(year AS UNSIGNED), FIELD(type, 'Task', 'Sub Task', 'Total')",
	        	        nativeQuery = true)
	        	    List<CompletedStatusCountResponse> getYearlyTaskStatusAdmin(@Param("adminId") Long adminId, @Param("pid") Long pid);


	        	        @Query(value =
	        	            "WITH year_range AS ( " +
	        	            "    SELECT (YEAR(CURDATE()) - 2 + seq) AS year " +
	        	            "    FROM ( " +
	        	            "        SELECT 0 AS seq UNION ALL " +
	        	            "        SELECT 1 UNION ALL " +
	        	            "        SELECT 2 UNION ALL " +
	        	            "        SELECT 3 UNION ALL " +
	        	            "        SELECT 4 " +
	        	            "    ) AS years " +
	        	            "), " +
	        	            "statuses AS ( " +
	        	            "    SELECT 'closed' AS status " +
	        	            "), " +
	        	            "task_counts AS ( " +
	        	            "    SELECT YEAR(t.last_status_updateddate) AS year, COUNT(*) AS count " +
	        	            "    FROM tms_task t " +
	        	            "    JOIN tms_project p ON t.pid = p.pid " +
	        	            "    LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid " +
	        	            "    LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto " +
	        	            "    WHERE t.status = 'closed' " +
	        	            "      AND YEAR(t.last_status_updateddate) BETWEEN (YEAR(CURDATE()) - 2) AND (YEAR(CURDATE()) + 2) " +
	        	            "      AND (:projectId IS NULL OR p.pid = :projectId) " + // Optional projectId filter
	        	            "      AND ( " +
	        	            "          (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId)) " +
	        	            "          OR (p.addedby <> :userId AND (tau.tms_user_id = :userId OR tau.tms_user_id IS NULL)) " +
	        	            "      ) " +
	        	            "    GROUP BY YEAR(t.last_status_updateddate) " +
	        	            "), " +
	        	            "sub_task_counts AS ( " +
	        	            "    SELECT YEAR(ts.last_status_updateddate) AS year, COUNT(*) AS count " +
	        	            "    FROM tms_sub_task ts " +
	        	            "    JOIN tms_task t ON ts.taskid = t.taskid " +
	        	            "    JOIN tms_project p ON t.pid = p.pid " +
	        	            "    LEFT JOIN tms_assigned_users tau ON tau.subtaskid = ts.subtaskid " +
	        	            "    WHERE ts.status = 'closed' " +
	        	            "      AND YEAR(ts.last_status_updateddate) BETWEEN (YEAR(CURDATE()) - 2) AND (YEAR(CURDATE()) + 2) " +
	        	            "      AND (:projectId IS NULL OR p.pid = :projectId) " + // Optional projectId filter
	        	            "      AND ( " +
	        	            "          (p.addedby = :userId AND (p.addedby = :userId OR tau.tms_user_id = :userId)) " +
	        	            "          OR (p.addedby <> :userId AND (tau.tms_user_id = :userId OR tau.tms_user_id IS NULL)) " +
	        	            "      ) " +
	        	            "    GROUP BY YEAR(ts.last_status_updateddate) " +
	        	            "), " +
	        	            "combined_counts AS ( " +
	        	            "    SELECT yr.year, 'Task' AS type, s.status, COALESCE(tc.count, 0) AS count " +
	        	            "    FROM year_range yr " +
	        	            "    CROSS JOIN statuses s " +
	        	            "    LEFT JOIN task_counts tc ON yr.year = tc.year " +
	        	            "    UNION ALL " +
	        	            "    SELECT yr.year, 'Sub Task' AS type, s.status, COALESCE(stc.count, 0) AS count " +
	        	            "    FROM year_range yr " +
	        	            "    CROSS JOIN statuses s " +
	        	            "    LEFT JOIN sub_task_counts stc ON yr.year = stc.year " +
	        	            "), " +
	        	            "total_counts AS ( " +
	        	            "    SELECT year, 'Total' AS type, 'closed' AS status, SUM(count) AS count " +
	        	            "    FROM combined_counts " +
	        	            "    GROUP BY year " +
	        	            ") " +
	        	            "SELECT * FROM combined_counts " +
	        	            "UNION ALL " +
	        	            "SELECT * FROM total_counts " +
	        	            "ORDER BY year, FIELD(type, 'Task', 'Sub Task', 'Total')",
	        	            nativeQuery = true)
	        	        List<CompletedStatusCountResponse> getYearlyTaskStatusByUser(
	        	            @Param("userId") Long userId,
	        	            @Param("projectId") Long projectId);
	        	    
	        	        
	        	        @Query(value =
	        	        	    "WITH RECURSIVE seq AS ( " +
	        	        	    "    SELECT 0 AS n " +
	        	        	    "    UNION ALL " +
	        	        	    "    SELECT n + 1 FROM seq " +
	        	        	    "    WHERE DATE_ADD(:fromDate, INTERVAL (n * 7) DAY) <= :toDate " +
	        	        	    "), " +
	        	        	    "week_range AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        DATE_ADD(:fromDate, INTERVAL (n * 7) DAY) AS week_start, " + // Sunday
	        	        	    "        DATE_ADD(:fromDate, INTERVAL (n * 7 + 6) DAY) AS week_end, " + // Saturday
	        	        	    "        WEEK(DATE_ADD(:fromDate, INTERVAL (n * 7) DAY), 0) AS week_num, " + // Sunday start
	        	        	    "        YEAR(DATE_ADD(:fromDate, INTERVAL (n * 7) DAY)) AS year_num " +
	        	        	    "    FROM seq " +
	        	        	    "    WHERE DATE_ADD(:fromDate, INTERVAL (n * 7 + 6) DAY) <= :toDate " +
	        	        	    "), " +
	        	        	    "statuses AS ( " +
	        	        	    "    SELECT 'closed' AS status " +
	        	        	    "), " +
	        	        	    "task_counts AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        WEEK(DATE(t.last_status_updateddate), 0) AS week_num, " + // Sunday start
	        	        	    "        YEAR(DATE(t.last_status_updateddate)) AS year_num, " +
	        	        	    "        COUNT(*) AS count " +
	        	        	    "    FROM tms_task t " +
	        	        	    "    JOIN tms_project p ON t.pid = p.pid " +
	        	        	    "    WHERE t.status = 'closed' " +
	        	        	    "      AND DATE(t.last_status_updateddate) BETWEEN :fromDate AND :toDate " +
	        	        	    "      AND p.admin_id = :adminId " + // 🔥 Replaced user filter
	        	        	    "      AND (:pid IS NULL OR p.pid = :pid) " +
	        	        	    "    GROUP BY year_num, week_num " +
	        	        	    "), " +
	        	        	    "sub_task_counts AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        WEEK(DATE(ts.last_status_updateddate), 0) AS week_num, " +  
	        	        	    "        YEAR(DATE(ts.last_status_updateddate)) AS year_num, " +
	        	        	    "        COUNT(*) AS count " +
	        	        	    "    FROM tms_sub_task ts " +
	        	        	    "    JOIN tms_task t ON ts.taskid = t.taskid " +
	        	        	    "    JOIN tms_project p ON t.pid = p.pid " +
	        	        	    "    WHERE ts.status = 'closed' " +
	        	        	    "      AND DATE(ts.last_status_updateddate) BETWEEN :fromDate AND :toDate " +
	        	        	    "      AND p.admin_id = :adminId " + 
	        	        	    "      AND (:pid IS NULL OR p.pid = :pid) " +
	        	        	    "    GROUP BY year_num, week_num " +
	        	        	    "), " +
	        	        	    "combined_counts AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        CONCAT(DATE_FORMAT(wr.week_start, '%d-%m-%Y'), ' to ', DATE_FORMAT(wr.week_end, '%d-%m-%Y')) AS period, " +
	        	        	    "        'Task' AS type, " +
	        	        	    "        s.status, " +
	        	        	    "        COALESCE(tc.count, 0) AS count " +
	        	        	    "    FROM week_range wr " +
	        	        	    "    CROSS JOIN statuses s " +
	        	        	    "    LEFT JOIN task_counts tc " +
	        	        	    "      ON wr.week_num = tc.week_num AND wr.year_num = tc.year_num " +
	        	        	    "    UNION ALL " +
	        	        	    "    SELECT " +
	        	        	    "        CONCAT(DATE_FORMAT(wr.week_start, '%d-%m-%Y'), ' to ', DATE_FORMAT(wr.week_end, '%d-%m-%Y')) AS period, " +
	        	        	    "        'Sub Task' AS type, " +
	        	        	    "        s.status, " +
	        	        	    "        COALESCE(stc.count, 0) AS count " +
	        	        	    "    FROM week_range wr " +
	        	        	    "    CROSS JOIN statuses s " +
	        	        	    "    LEFT JOIN sub_task_counts stc " +
	        	        	    "      ON wr.week_num = stc.week_num AND wr.year_num = stc.year_num " +
	        	        	    "), " +
	        	        	    "total_counts AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        period, " +
	        	        	    "        'Total' AS type, " +
	        	        	    "        'closed' AS status, " +
	        	        	    "        SUM(count) AS count " +
	        	        	    "    FROM combined_counts " +
	        	        	    "    GROUP BY period " +
	        	        	    ") " +
	        	        	    "SELECT * FROM combined_counts " +
	        	        	    "UNION ALL " +
	        	        	    "SELECT * FROM total_counts " +
	        	        	    "ORDER BY STR_TO_DATE(SUBSTRING_INDEX(period, ' ', 1), '%d-%m-%Y'), " +
	        	        	    "         FIELD(type, 'Task', 'Sub Task', 'Total')",
	        	        	    nativeQuery = true)
	        	        	List<CompletedStatusCountResponse> getWeeklyTaskStatsAdmin(
	        	        	    @Param("fromDate") String fromDate,
	        	        	    @Param("toDate") String toDate,
	        	        	    @Param("adminId") Long adminId,
	        	        	    @Param("pid") Long pid);


	        	        @Query(value =
	        	        	    "WITH RECURSIVE seq AS ( " +
	        	        	    "    SELECT 0 AS n " +
	        	        	    "    UNION ALL " +
	        	        	    "    SELECT n + 1 FROM seq " +
	        	        	    "    WHERE DATE_ADD(:fromDate, INTERVAL (n * 7) DAY) <= :toDate " +
	        	        	    "), " +
	        	        	    "week_range AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        DATE_ADD(:fromDate, INTERVAL (n * 7) DAY) AS week_start, " + // Sunday
	        	        	    "        DATE_ADD(:fromDate, INTERVAL (n * 7 + 6) DAY) AS week_end, " + // Saturday
	        	        	    "        WEEK(DATE_ADD(:fromDate, INTERVAL (n * 7) DAY), 0) AS week_num, " + // WEEK(..., 0) = Sunday start
	        	        	    "        YEAR(DATE_ADD(:fromDate, INTERVAL (n * 7) DAY)) AS year_num " +
	        	        	    "    FROM seq " +
	        	        	    "    WHERE DATE_ADD(:fromDate, INTERVAL (n * 7 + 6) DAY) <= :toDate " + // Ensure week_end does not overflow
	        	        	    "), " +
	        	        	    "statuses AS ( " +
	        	        	    "    SELECT 'closed' AS status " +
	        	        	    "), " +
	        	        	    "task_counts AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        WEEK(DATE(t.last_status_updateddate), 0) AS week_num, " + // Sunday start
	        	        	    "        YEAR(DATE(t.last_status_updateddate)) AS year_num, " +
	        	        	    "        COUNT(*) AS count " +
	        	        	    "    FROM tms_task t " +
	        	        	    "    JOIN tms_project p ON t.pid = p.pid " +
	        	        	    "    LEFT JOIN tms_task_users ttu ON t.taskid = ttu.taskid " +
	        	        	    "    LEFT JOIN tms_assigned_users tau ON tau.assignid = ttu.assignedto " +
	        	        	    "    WHERE t.status = 'closed' " +
	        	        	    "      AND DATE(t.last_status_updateddate) BETWEEN :fromDate AND :toDate " +
	        	        	    "      AND ( " +
	        	        	    "          (p.addedby = :userId AND (p.addedby = :userId OR COALESCE(ttu.assignedto, 0) = :userId)) " +
	        	        	    "          OR (p.addedby <> :userId AND (COALESCE(tau.tms_user_id, 0) = :userId OR tau.tms_user_id IS NULL)) " +
	        	        	    "      ) " +
	        	        	    "      AND (:pid IS NULL OR p.pid = :pid) " +
	        	        	    "    GROUP BY year_num, week_num " +
	        	        	    "), " +
	        	        	    "sub_task_counts AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        WEEK(DATE(ts.last_status_updateddate), 0) AS week_num, " + // Sunday start
	        	        	    "        YEAR(DATE(ts.last_status_updateddate)) AS year_num, " +
	        	        	    "        COUNT(*) AS count " +
	        	        	    "    FROM tms_sub_task ts " +
	        	        	    "    JOIN tms_task t ON ts.taskid = t.taskid " +
	        	        	    "    JOIN tms_project p ON t.pid = p.pid " +
	        	        	    "    LEFT JOIN tms_assigned_users tau ON ts.subtaskid = tau.subtaskid " +
	        	        	    "    WHERE ts.status = 'closed' " +
	        	        	    "      AND DATE(ts.last_status_updateddate) BETWEEN :fromDate AND :toDate " +
	        	        	    "      AND ( " +
	        	        	    "          (p.addedby = :userId AND (p.addedby = :userId OR COALESCE(tau.tms_user_id, 0) = :userId)) " +
	        	        	    "          OR (p.addedby <> :userId AND (COALESCE(tau.tms_user_id, 0) = :userId OR tau.tms_user_id IS NULL)) " +
	        	        	    "      ) " +
	        	        	    "      AND (:pid IS NULL OR p.pid = :pid) " +
	        	        	    "    GROUP BY year_num, week_num " +
	        	        	    "), " +
	        	        	    "combined_counts AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        CONCAT(DATE_FORMAT(wr.week_start, '%d-%m-%Y'), ' to ', DATE_FORMAT(wr.week_end, '%d-%m-%Y')) AS period, " +
	        	        	    "        'Task' AS type, " +
	        	        	    "        s.status, " +
	        	        	    "        COALESCE(tc.count, 0) AS count " +
	        	        	    "    FROM week_range wr " +
	        	        	    "    CROSS JOIN statuses s " +
	        	        	    "    LEFT JOIN task_counts tc " +
	        	        	    "      ON wr.week_num = tc.week_num AND wr.year_num = tc.year_num " +
	        	        	    "    UNION ALL " +
	        	        	    "    SELECT " +
	        	        	    "        CONCAT(DATE_FORMAT(wr.week_start, '%d-%m-%Y'), ' to ', DATE_FORMAT(wr.week_end, '%d-%m-%Y')) AS period, " +
	        	        	    "        'Sub Task' AS type, " +
	        	        	    "        s.status, " +
	        	        	    "        COALESCE(stc.count, 0) AS count " +
	        	        	    "    FROM week_range wr " +
	        	        	    "    CROSS JOIN statuses s " +
	        	        	    "    LEFT JOIN sub_task_counts stc " +
	        	        	    "      ON wr.week_num = stc.week_num AND wr.year_num = stc.year_num " +
	        	        	    "), " +
	        	        	    "total_counts AS ( " +
	        	        	    "    SELECT " +
	        	        	    "        period, " +
	        	        	    "        'Total' AS type, " +
	        	        	    "        'closed' AS status, " +
	        	        	    "        SUM(count) AS count " +
	        	        	    "    FROM combined_counts " +
	        	        	    "    GROUP BY period " +
	        	        	    ") " +
	        	        	    "SELECT * FROM combined_counts " +
	        	        	    "UNION ALL " +
	        	        	    "SELECT * FROM total_counts " +
	        	        	    "ORDER BY STR_TO_DATE(SUBSTRING_INDEX(period, ' ', 1), '%d-%m-%Y'), " +
	        	        	    "         FIELD(type, 'Task', 'Sub Task', 'Total')",
	        	        	    nativeQuery = true)
	        	        	List<CompletedStatusCountResponse> getWeeklyTaskStatsUser(    @Param("fromDate") String fromDate, @Param("toDate") String toDate, @Param("userId") Long userId, @Param("pid") Long pid);

	        	        @Query(value = "SELECT CONCAT(tms.first_name, ' ', COALESCE(tms.middle_name, ''), ' ', tms.last_name) AS firstName, " +
	        	                "tms.position AS position, " +

	        	                "COUNT(DISTINCT tt.taskid) + COUNT(DISTINCT tst.subtaskid) AS totalAssignedTasks, " +

	        	                "SUM(CASE WHEN tt.status = 'In Progress' THEN 1 ELSE 0 END) + " +
	        	                "SUM(CASE WHEN tst.status = 'In Progress' THEN 1 ELSE 0 END) AS inProgressTaskCount, " +

	        	                "SUM(CASE WHEN tt.status = 'Closed' THEN 1 ELSE 0 END) + " +
	        	                "SUM(CASE WHEN tst.status = 'Closed' THEN 1 ELSE 0 END) AS closedTaskCount, " +

	        	                "SUM(CASE WHEN tt.status = 'Open' THEN 1 ELSE 0 END) + " +
	        	                "SUM(CASE WHEN tst.status = 'Open' THEN 1 ELSE 0 END) AS openTaskCount, " +

	        	                "SUM(CASE WHEN tt.status = 'On Hold' THEN 1 ELSE 0 END) + " +
	        	                "SUM(CASE WHEN tst.status = 'On Hold' THEN 1 ELSE 0 END) AS onHoldTaskCount, " +

	        	                "SUM(CASE WHEN tt.status = 'Blocked' THEN 1 ELSE 0 END) + " +
	        	                "SUM(CASE WHEN tst.status = 'Blocked' THEN 1 ELSE 0 END) AS blockedTaskCount, " +

	        	                "SUM(CASE WHEN tt.status = 'To be Tested' THEN 1 ELSE 0 END) + " +
	        	                "SUM(CASE WHEN tst.status = 'To be Tested' THEN 1 ELSE 0 END) AS toBeTestedTaskCount, " +

	        	                "SUM(CASE WHEN tt.status = 'In Review' THEN 1 ELSE 0 END) + " +
	        	                "SUM(CASE WHEN tst.status = 'In Review' THEN 1 ELSE 0 END) AS inReviewCount, " +

	        	                "SUM(CASE WHEN tt.status = 'Overdue' THEN 1 ELSE 0 END) + " +
	        	                "SUM(CASE WHEN tst.status = 'Overdue' THEN 1 ELSE 0 END) AS overDueTaskCount " +

	        	                "FROM tms_users tms " +

	        	                "LEFT JOIN tms_assigned_users ttu " +
	        	                "ON tms.user_id = ttu.tms_user_id " +

	        	                "LEFT JOIN tms_task_users tu " +
	        	                "ON ttu.assignid = tu.assignedto " +

	        	                "LEFT JOIN tms_task tt " +
	        	                "ON tu.taskid = tt.taskid " +
	        	                "AND (:projectId IS NULL OR tt.pid = :projectId) " +
	        	                "AND ( " +
	        	                "    :interval IS NULL " +
	        	                "    OR ( " +
	        	                "        (:interval = 'daily' AND DATE(tt.last_status_updateddate) = CURRENT_DATE) " +
	        	                "        OR (:interval = 'weekly' AND YEARWEEK(tt.last_status_updateddate, 1) = YEARWEEK(CURRENT_DATE, 1)) " +
	        	                "        OR (:interval = 'monthly' AND YEAR(tt.last_status_updateddate) = YEAR(CURRENT_DATE) " +
	        	                "                                 AND MONTH(tt.last_status_updateddate) = MONTH(CURRENT_DATE)) " +
	        	                "        OR (:interval = 'yearly' AND YEAR(tt.last_status_updateddate) = YEAR(CURRENT_DATE)) " +
	        	                "    ) " +
	        	                ") " +

	        	                "LEFT JOIN tms_sub_task tst " +
	        	                "ON ttu.subtaskid = tst.subtaskid " +
	        	                "AND EXISTS ( " +
	        	                "    SELECT 1 " +
	        	                "    FROM tms_task t " +
	        	                "    WHERE t.taskid = tst.taskid " +
	        	                "      AND (:projectId IS NULL OR t.pid = :projectId) " +
	        	                ") " +
	        	                "AND ( " +
	        	                "    :interval IS NULL " +
	        	                "    OR ( " +
	        	                "        (:interval = 'daily' AND DATE(tst.last_status_updateddate) = CURRENT_DATE) " +
	        	                "        OR (:interval = 'weekly' AND YEARWEEK(tst.last_status_updateddate, 1) = YEARWEEK(CURRENT_DATE, 1)) " +
	        	                "        OR (:interval = 'monthly' AND YEAR(tst.last_status_updateddate) = YEAR(CURRENT_DATE) " +
	        	                "                                      AND MONTH(tst.last_status_updateddate) = MONTH(CURRENT_DATE)) " +
	        	                "        OR (:interval = 'yearly' AND YEAR(tst.last_status_updateddate) = YEAR(CURRENT_DATE)) " +
	        	                "    ) " +
	        	                ") " +

	        	                "WHERE tms.admin_id = :adminId " +

	        	                "GROUP BY firstName, tms.position", nativeQuery = true)
	        	 List<TmsTaskCountData> getProjectUsersTaskStats( @Param("adminId") Long adminId, @Param("projectId") Long projectId,  @Param("interval") String interval);

    
	        	        @Query(value = 
	        	        	    "SELECT " +
	        	        	    "    CONCAT(tms.first_name, ' ', COALESCE(tms.middle_name, ''), ' ', tms.last_name) AS firstName, " +
	        	        	    "    tms.position AS position, " +

	        	        	    "    COUNT(DISTINCT tt.taskid) + COUNT(DISTINCT tst.subtaskid) AS totalAssignedTasks, " +

	        	        	    "    SUM(CASE WHEN tt.status = 'In Progress' THEN 1 ELSE 0 END) + " +
	        	        	    "    SUM(CASE WHEN tst.status = 'In Progress' THEN 1 ELSE 0 END) AS inProgressTaskCount, " +

	        	        	    "    SUM(CASE WHEN tt.status = 'Closed' THEN 1 ELSE 0 END) + " +
	        	        	    "    SUM(CASE WHEN tst.status = 'Closed' THEN 1 ELSE 0 END) AS closedTaskCount, " +

	        	        	    "    SUM(CASE WHEN tt.status = 'Open' THEN 1 ELSE 0 END) + " +
	        	        	    "    SUM(CASE WHEN tst.status = 'Open' THEN 1 ELSE 0 END) AS openTaskCount, " +

	        	        	    "    SUM(CASE WHEN tt.status = 'On Hold' THEN 1 ELSE 0 END) + " +
	        	        	    "    SUM(CASE WHEN tst.status = 'On Hold' THEN 1 ELSE 0 END) AS onHoldTaskCount, " +

	        	        	    "    SUM(CASE WHEN tt.status = 'Blocked' THEN 1 ELSE 0 END) + " +
	        	        	    "    SUM(CASE WHEN tst.status = 'Blocked' THEN 1 ELSE 0 END) AS blockedTaskCount, " +

	        	        	    "    SUM(CASE WHEN tt.status = 'To be Tested' THEN 1 ELSE 0 END) + " +
	        	        	    "    SUM(CASE WHEN tst.status = 'To be Tested' THEN 1 ELSE 0 END) AS toBeTestedTaskCount, " +

	        	        	    "    SUM(CASE WHEN tt.status = 'In Review' THEN 1 ELSE 0 END) + " +
	        	        	    "    SUM(CASE WHEN tst.status = 'In Review' THEN 1 ELSE 0 END) AS inReviewCount, " +

	        	        	    "    SUM(CASE WHEN tt.status = 'Overdue' THEN 1 ELSE 0 END) + " +
	        	        	    "    SUM(CASE WHEN tst.status = 'Overdue' THEN 1 ELSE 0 END) AS overDueTaskCount " +

	        	        	    "FROM tms_users tms " +

	        	        	    "LEFT JOIN tms_assigned_users ttu " +
	        	        	    "    ON tms.user_id = ttu.tms_user_id " +

	        	        	    "LEFT JOIN tms_task_users tu " +
	        	        	    "    ON ttu.assignid = tu.assignedto " +

	        	        	    "LEFT JOIN tms_task tt " +
	        	        	    "    ON tu.taskid = tt.taskid " +
	        	        	    "    AND (:projectId IS NULL OR tt.pid = :projectId) " +
	        	        	    "    AND ( " +
	        	        	    "        :interval IS NULL " +
	        	        	    "        OR ( " +
	        	        	    "            (:interval = 'daily' AND DATE(tt.last_status_updateddate) = CURRENT_DATE) " +
	        	        	    "            OR (:interval = 'weekly' AND YEARWEEK(tt.last_status_updateddate, 1) = YEARWEEK(CURRENT_DATE, 1)) " +
	        	        	    "            OR (:interval = 'monthly' AND YEAR(tt.last_status_updateddate) = YEAR(CURRENT_DATE) " +
	        	        	    "                                        AND MONTH(tt.last_status_updateddate) = MONTH(CURRENT_DATE)) " +
	        	        	    "            OR (:interval = 'yearly' AND YEAR(tt.last_status_updateddate) = YEAR(CURRENT_DATE)) " +
	        	        	    "        ) " +
	        	        	    "    ) " +

	        	        	    "LEFT JOIN tms_sub_task tst " +
	        	        	    "    ON ttu.subtaskid = tst.subtaskid " +
	        	        	    "    AND EXISTS ( " +
	        	        	    "        SELECT 1 " +
	        	        	    "        FROM tms_task t " +
	        	        	    "        WHERE t.taskid = tst.taskid " +
	        	        	    "          AND (:projectId IS NULL OR t.pid = :projectId) " +
	        	        	    "    ) " +
	        	        	    "    AND ( " +
	        	        	    "        :interval IS NULL " +
	        	        	    "        OR ( " +
	        	        	    "            (:interval = 'daily' AND DATE(tst.last_status_updateddate) = CURRENT_DATE) " +
	        	        	    "            OR (:interval = 'weekly' AND YEARWEEK(tst.last_status_updateddate, 1) = YEARWEEK(CURRENT_DATE, 1)) " +
	        	        	    "            OR (:interval = 'monthly' AND YEAR(tst.last_status_updateddate) = YEAR(CURRENT_DATE) " +
	        	        	    "                                          AND MONTH(tst.last_status_updateddate) = MONTH(CURRENT_DATE)) " +
	        	        	    "            OR (:interval = 'yearly' AND YEAR(tst.last_status_updateddate) = YEAR(CURRENT_DATE)) " +
	        	        	    "        ) " +
	        	        	    "    ) " +

	        	        	    "WHERE tms.user_id = :userId " +

	        	        	    "GROUP BY firstName, tms.position",
	        	        	    nativeQuery = true)
	        	        	List<TmsTaskCountData> getTeamMemberTaskStats(@Param("userId") Long userId, @Param("projectId") Long projectId,@Param("interval") String interval);

	        	        @Query(value = " SELECT  \r\n"
	        	        		+ "	        	                    t.taskid,\r\n"
	        	        		+ "	        	                    DATE(t.createddate) AS createddate,\r\n"
	        	        		+ "	        	                    DATE(t.updateddate) AS updateddate,\r\n"
	        	        		+ "	        	                    t.addedby,\r\n"
	        	        		+ "	        	                    t.department,\r\n"
	        	        		+ "	        	                    t.description,\r\n"
	        	        		+ "	        	                    t.maxnum,\r\n"
	        	        		+ "	        	                    t.status,\r\n"
	        	        		+ "	        	                    t.target_date,\r\n"
	        	        		+ "	        	                    t.ticketid,\r\n"
	        	        		+ "	        	                    t.updatedby,\r\n"
	        	        		+ "	        	                    t.taskname,\r\n"
	        	        		+ "	        	                    p.projectid,\r\n"
	        	        		+ "	        	                    p.pid,\r\n"
	        	        		+ "	        	                    t.duration,\r\n"
	        	        		+ "	        	                    t.priority,\r\n"
	        	        		+ "	        	                    t.start_date,\r\n"
	        	        		+ "	        	                    CONCAT(\r\n"
	        	        		+ "	        	                        COALESCE(u1.first_name, u2.first_name), ' ',\r\n"
	        	        		+ "	        	                        COALESCE(u1.middle_name, u2.middle_name, ''), ' ',\r\n"
	        	        		+ "	        	                        COALESCE(u1.last_name, u2.last_name)\r\n"
	        	        		+ "	        	                    ) AS fullname\r\n"
	        	        		+ "	        	                FROM tms_task t\r\n"
	        	        		+ "	        	                JOIN tms_project p ON t.pid = p.pid\r\n"
	        	        		+ "	        	                LEFT JOIN tms_users u1 ON t.updatedby = u1.user_id\r\n"
	        	        		+ "	        	                LEFT JOIN tms_users u2 ON t.addedby = u2.user_id\r\n"
	        	        		+ "	        	                WHERE (:projectid IS NULL OR p.projectid = :projectid)\r\n"
	        	        		+ "	        	                 AND (\r\n"
	        	        		+ "                                        :intervalType IS NULL OR :intervalType = '' \r\n"
	        	        		+ "	        	                    OR    (:intervalType = 'DAILY'   AND DATE(t.start_date) = CURDATE())\r\n"
	        	        		+ "	        	                     OR (:intervalType = 'WEEKLY'  AND YEARWEEK(t.start_date, 1) = YEARWEEK(CURDATE(), 1))\r\n"
	        	        		+ "	        	                     OR (:intervalType = 'MONTHLY' AND YEAR(t.start_date) = YEAR(CURDATE()) \r\n"
	        	        		+ "	        	                                                    AND MONTH(t.start_date) = MONTH(CURDATE()))\r\n"
	        	        		+ "	        	                     OR (:intervalType = 'YEARLY'  AND YEAR(t.start_date) = YEAR(CURDATE()))\r\n"
	        	        		+ "	        	                  )",
	        	                nativeQuery = true)
	        	            Page<TaskTrackerDTO> findTaskListByTmsProjectid(
	        	            		 Pageable pageble,
	        	                    @Param("projectid") String projectid,
	        	                    @Param("intervalType") String intervalType
	        	                    
	        	            );

	        	        
	        	        @Query(value = "SELECT \r\n"
	        	        		+ "	        	                    t.taskid,\r\n"
	        	        		+ "	        	                    DATE(t.createddate) AS createddate,\r\n"
	        	        		+ "	        	                    DATE(t.updateddate) AS updateddate,\r\n"
	        	        		+ "	        	                    t.addedby,\r\n"
	        	        		+ "	        	                    t.department,\r\n"
	        	        		+ "	        	                    t.description,\r\n"
	        	        		+ "	        	                    t.maxnum,\r\n"
	        	        		+ "	        	                    t.status,\r\n"
	        	        		+ "	        	                    t.target_date,\r\n"
	        	        		+ "	        	                    t.ticketid,\r\n"
	        	        		+ "	        	                    t.updatedby,\r\n"
	        	        		+ "	        	                    t.taskname,\r\n"
	        	        		+ "	        	                    p.projectid,\r\n"
	        	        		+ "	        	                    t.pid,\r\n"
	        	        		+ "	        	                    t.duration,\r\n"
	        	        		+ "	        	                    t.priority,\r\n"
	        	        		+ "	        	                    t.start_date,\r\n"
	        	        		+ "	        	                    CONCAT(\r\n"
	        	        		+ "	        	                        COALESCE(u1.first_name, u2.first_name), ' ',\r\n"
	        	        		+ "	        	                        COALESCE(u1.middle_name, u2.middle_name, ''), ' ',\r\n"
	        	        		+ "	        	                        COALESCE(u1.last_name, u2.last_name)\r\n"
	        	        		+ "	        	                    ) AS fullname\r\n"
	        	        		+ "	        	                FROM tms_task t\r\n"
	        	        		+ "	        	                JOIN tms_project p ON t.pid = p.pid\r\n"
	        	        		+ "	        	                LEFT JOIN tms_users u1 ON t.updatedby = u1.user_id\r\n"
	        	        		+ "	        	                LEFT JOIN tms_users u2 ON t.addedby = u2.user_id\r\n"
	        	        		+ "	        	                WHERE p.projectid = :projectid\r\n"
	        	        		+ "	        	                  AND (\r\n"
	        	        		+ "	        	                        t.ticketid LIKE CONCAT('%', :keyword, '%')\r\n"
	        	        		+ "	        	                     OR DATE_FORMAT(t.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')\r\n"
	        	        		+ "	        	                     OR t.taskname LIKE CONCAT('%', :keyword, '%')\r\n"
	        	        		+ "	        	                     OR DATE_FORMAT(t.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%')\r\n"
	        	        		+ "	        	                     OR t.status LIKE CONCAT('%', :keyword, '%')\r\n"
	        	        		+ "	        	                     OR t.priority LIKE CONCAT('%', :keyword, '%')\r\n"
	        	        		+ "	        	                     OR t.duration LIKE CONCAT('%', :keyword, '%')\r\n"
	        	        		+ "	        	                     OR CONCAT(\r\n"
	        	        		+ "	        	                            COALESCE(u1.first_name, u2.first_name), ' ',\r\n"
	        	        		+ "	        	                            COALESCE(u1.middle_name, u2.middle_name, ''), ' ',\r\n"
	        	        		+ "	        	                            COALESCE(u1.last_name, u2.last_name)\r\n"
	        	        		+ "	        	                        ) LIKE CONCAT('%', :keyword, '%')\r\n"
	        	        		+ "	        	                  )\r\n"
	        	        		+ "	        	                  AND (\r\n"
	        	        		+ "	        	                        (:intervalType = 'DAILY'   AND DATE(t.start_date) = CURDATE())\r\n"
	        	        		+ "	        	                     OR (:intervalType = 'WEEKLY'  AND YEARWEEK(t.start_date, 1) = YEARWEEK(CURDATE(), 1))\r\n"
	        	        		+ "	        	                     OR (:intervalType = 'MONTHLY' AND YEAR(t.start_date) = YEAR(CURDATE()) \r\n"
	        	        		+ "	        	                                                    AND MONTH(t.start_date) = MONTH(CURDATE()))\r\n"
	        	        		+ "	        	                     OR (:intervalType = 'YEARLY'  AND YEAR(t.start_date) = YEAR(CURDATE()))\r\n"
	        	        		+ "	        	                  )",
	        	                nativeQuery = true)
	        	            Page<TaskTrackerDTO> findTaskListByTmsProjectIdWithSearching(
	        	            		 Pageable pageble,
	        	                    @Param("projectid") String projectid,
	        	                    @Param("keyword") String keyword,
	        	                    @Param("intervalType") String intervalType
	        	               
	        	            );
}   

	

