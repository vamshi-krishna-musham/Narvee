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

import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.SubTaskUserDTO;
import com.narvee.dto.TaskTrackerDTO;
import com.narvee.entity.TmsSubTask;
import com.narvee.entity.TmsTask;

public interface SubTaskRepository extends JpaRepository<TmsSubTask, Long> {

	@Query(value = "select s.subtaskid, s.subtaskname, s.subtaskdescription, s.status, s.targetdate, u.fullname as addedby from tms_sub_task s , users u where u.userid = s.addedby ", nativeQuery = true)
	public Page<SubTaskUserDTO> getSubTaskUser(Pageable pageable);

	@Query(value = "select s.subtaskid, s.subtaskname, s.subtaskdescription, s.status, s.targetdate, u.fullname as addedby from tms_sub_task s, users u where u.userid = s.addedby "
			+ "And (s.subtaskname LIKE CONCAT('%', :keyword, '%') OR s.subtaskdescription LIKE CONCAT('%', :keyword, '%') OR s.status LIKE CONCAT('%', :keyword, '%') "
			+ "OR  s.targetdate LIKE CONCAT('%', :keyword, '%') OR u.fullname LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<SubTaskUserDTO> getSubTaskUserFiltering(Pageable pageable, @Param("keyword") String keyword);

	@Query(value = "select * from tms_sub_task s WHERE (s.subTaskName LIKE CONCAT('%', :keyword, '%') OR s.subTaskDescription LIKE CONCAT('%', :keyword, '%') or s.status LIKE CONCAT('%', :keyword, '%') "
			+ " or s.targetDate LIKE CONCAT('%', :keyword, '%') OR s.startDate LIKE CONCAT('%', :keyword, '%')) ", nativeQuery = true)
	public Page<TmsSubTask> getAllSubTasksSortingAndFiltering(Pageable pageable, @Param("keyword") String keyword);

	public List<TmsSubTask> findByTaskTicketid(String ticketid);

	@Query(value = "select st.subtaskid ,u.fullname , u.pseudoname from tms_sub_task st , tms_assigned_users au , users u , tms_task t where st.subtaskid =au.subtaskid and t.taskid=st.taskid AND\r\n"
			+ "			  au.userid =u.userid and t.ticketid= :ticketid", nativeQuery = true)
	public List<GetUsersDTO> getAssignUsers(String ticketid);

	@Modifying
	@Transactional
	@Query(value = "UPDATE tms_sub_task SET status=:status , updatedby=:updatedby, last_status_updateddate = :updateddate WHERE subtaskid =:subTaskId ", nativeQuery = true)
	public int updateTaskStatus(@Param("subTaskId") Long subTaskId, @Param("status") String status,
			@Param("updatedby") Long updatedby, LocalDateTime updateddate);

	@Query(value = "SELECT taskid FROM tms_task WHERE ticketid= :ticketid", nativeQuery = true)
	public Long findTaskId(String ticketid);

	@Query(value = " select projectname , taskname , ticketid FROM tms_task t , tms_project p , tms_sub_task st WHERE  p.pid = t.pid AND t.taskid =st.taskid AND st.subtaskid=:subtaskid", nativeQuery = true)
	public GetUsersDTO GetPorjectNameAndTaskName(Long subtaskid);

	@Query(value = "select st.subtaskid ,u.fullname , u.pseudoname from tms_sub_task st , tms_assigned_users au , users u  , tms_task t where st.subtaskid =au.subtaskid and t.taskid=st.taskid AND\r\n"
			+ "			  au.userid =u.userid and st.subtaskid= :subTaskId", nativeQuery = true)
	public List<GetUsersDTO> getAssignUsers(Long subTaskId);

	@Query(value = "select u.fullname , u.pseudoname  from users u where u.userid = :userid ", nativeQuery = true)
	public GetUsersDTO getUser(Long userid);

	@Query(value = "select tt.taskid , st.subtaskid , tt.status,tt.createddate , tt.description ,tt.updatedby from tms_ticket_tracker tt , tms_sub_task st  WHERE tt.subtaskid= st.subtaskid and st.subtaskid=:subtaskid order by tt.createddate desc", nativeQuery = true)
	public List<TaskTrackerDTO> ticketTrackerBySubTaskId(Long subtaskid);

	@Query(value = "SELECT st.subtaskid, st.status,u.fullname, u.pseudoname, st.subtaskname, st.target_date, t.ticketid, u.email FROM tms_sub_task st JOIN tms_assigned_users au ON st.subtaskid = au.subtaskid\r\n"
			+ "JOIN users u ON au.userid = u.userid JOIN tms_task t ON st.taskid = t.taskid WHERE date(st.target_date) < :currentDate AND st.status!='to do' AND st.status!='Completed'  ", nativeQuery = true)
	public List<TaskTrackerDTO> getExceededTargetDateSubTasks(LocalDate currentDate);
	
	
	
	//----------------------------tms   replicated methods --------------
	@Query(value =
	  "SELECT DISTINCT "
	+ "  st.subtaskid AS subTaskId, "
	+ "  st.subtaskdescription AS description, "
	+ "  st.subtaskname, "
    +"st.target_date,"
	+ "  st.addedby, "
	+ "  st.duration, "
	+ "  DATE(st.createddate) AS createddate, "
	+ "  st.priority, "
	+ "  st.status, "
	+ "  st.taskid, "
	+ "  t.ticketid, "
	+ "  st.updatedby, "
	+ "  DATE(st.updateddate) AS updateddate, "
	+ "  t.taskname, "
	+ "  st.start_date AS start_date, "
	+ "  st.subtasktoken_id AS subtasktokenid, " 
	+ "  CONCAT_WS(' ', NULLIF(TRIM(u1.first_name), ''), NULLIF(TRIM(u1.middle_name), ''), NULLIF(TRIM(u1.last_name), '')) AS fullname, "+
	  "  CONCAT_WS(' ', NULLIF(TRIM(u2.first_name), ''), NULLIF(TRIM(u2.middle_name), ''), NULLIF(TRIM(u2.last_name), '')) AS addedbyfullname " //for addedby 
	+ " FROM tms_sub_task st "
	+ "JOIN tms_task t ON st.taskid = t.taskid "
	+ "LEFT JOIN tms_users u1 ON st.updatedby = u1.user_id "
	+ "LEFT JOIN tms_users u2 ON st.addedby = u2.user_id "
	+ "LEFT JOIN tms_assigned_users au ON st.subtaskid = au.subtaskid " +
     "LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
     "WHERE t.ticketid = :ticketId " +
     "GROUP BY st.subtaskid",
     nativeQuery = true)
	public Page<TaskTrackerDTO> findSubTaskByTicketid(@Param("ticketId") String ticketId, Pageable pageable);
	
	

	@Query(value =
			  "SELECT au.tms_user_id AS userid, " +
			  "  CONCAT_WS(' ', NULLIF(TRIM(u.first_name), ''), NULLIF(TRIM(u.middle_name), ''), NULLIF(TRIM(u.last_name), '')) AS fullname, " +
			  "  u.email AS email " +
			  "FROM tms_assigned_users au " +
			  "JOIN tms_users u ON au.tms_user_id = u.user_id " +
			  "WHERE au.subtaskid = :subtaskid",
			  nativeQuery = true)
			public List<GetUsersDTO> getSubtaskAssignUsersTms(@Param("subtaskid") Long subtaskid);
	@Query(value = "SELECT * FROM (" +
	        " SELECT DISTINCT st.subtaskid AS subtaskid, " +
	        " st.subtaskname, " +
	        " st.subtaskdescription AS description, " +
	        " st.taskid, " +
	        " t.ticketid, " +
	        " st.addedby, " +
	        " st.updatedby, " +
	        " st.priority, " +
	        " st.status, " +
	        " st.duration, " +
	        " st.start_date, " +
	        " st.target_date, " +
	        " st.createddate, " +
	        " st.updateddate, " +
	        " st.subtasktoken_id AS subtasktokenid, " +
	        " st.subtaskmaxnum, " +
	        " t.taskname, " +
	        " CONCAT_WS(' ', NULLIF(u2.first_name, ''), NULLIF(u2.middle_name, ''), NULLIF(u2.last_name, '')) AS addedByFullname, " +
	        " CONCAT_WS(' ', NULLIF(u1.first_name, ''), NULLIF(u1.middle_name, ''), NULLIF(u1.last_name, '')) AS fullname, " +
	        " GROUP_CONCAT(DISTINCT CONCAT_WS(' ', NULLIF(auu.first_name, ''), NULLIF(auu.middle_name, ''), NULLIF(auu.last_name, ''))) AS assignedTo " +
	        " FROM tms_sub_task st " +
	        " JOIN tms_task t ON st.taskid = t.taskid " +
	        " LEFT JOIN tms_users u1 ON st.updatedby = u1.user_id " +
	        " LEFT JOIN tms_users u2 ON st.addedby = u2.user_id " +
	        " LEFT JOIN tms_assigned_users au ON au.subtaskid = st.subtaskid " +
	        " LEFT JOIN tms_users auu ON au.tms_user_id = auu.user_id " +
	        " WHERE t.ticketid = :ticketId " +
	        " GROUP BY st.subtaskid" +
	        ") t " +
	        "WHERE (:keyword = '' " +
	        " OR LOWER(COALESCE(t.subtaskname, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	        " OR LOWER(COALESCE(t.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	        " OR CAST(t.subtasktokenid AS CHAR) LIKE CONCAT('%', :keyword, '%') " +
	        " OR CAST(t.taskid AS CHAR) LIKE CONCAT('%', :keyword, '%') " +
	        " OR DATE_FORMAT(t.start_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	        " OR DATE_FORMAT(t.target_date, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	        " OR DATE_FORMAT(t.updateddate, '%d-%m-%Y') LIKE CONCAT('%', :keyword, '%') " +
	        " OR LOWER(COALESCE(t.status, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	        " OR LOWER(COALESCE(t.priority, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	        " OR CAST(t.duration AS CHAR) LIKE CONCAT('%', :keyword, '%') " +
	        " OR LOWER(COALESCE(t.addedByFullname, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	        " OR LOWER(COALESCE(t.fullname, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	        " OR LOWER(COALESCE(t.assignedTo, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	        ")",
	    countQuery = "SELECT COUNT(*) FROM (" +
	        " SELECT st.subtaskid " +
	        " FROM tms_sub_task st " +
	        " JOIN tms_task t ON st.taskid = t.taskid " +
	        " LEFT JOIN tms_users u1 ON st.updatedby = u1.user_id " +
	        " LEFT JOIN tms_users u2 ON st.addedby = u2.user_id " +
	        " LEFT JOIN tms_assigned_users au ON au.subtaskid = st.subtaskid " +
	        " GROUP BY st.subtaskid" +
	        ") t",
	    nativeQuery = true
	)
	Page<TaskTrackerDTO> findSubTaskByTicketIdWithSearching(
	        @Param("ticketId") String ticketId,
	        @Param("keyword") String keyword,
	        Pageable pageable
	);


	@Query(value = "select subtaskname from tms_sub_task where subtaskid = :subTaskId",nativeQuery = true)

	public String getSubTaskName(Long subTaskId);
	
	@Query(value = "SELECT * FROM tms_sub_task", nativeQuery = true)
	public List<TaskTrackerDTO>getAll( );
	
	@Query(value = "SELECT CONCAT(u.first_name, ' ', u.last_name) FROM tms_users u WHERE u.user_id = :userId", nativeQuery = true)
    String findNameByUserId(@Param("userId") String string);
	
	@Query(value = "SELECT p.subtasktoken_id, p.subtaskname, p.subtaskdescription, p.addedby, p.status, p.start_date, p.target_date, p.createddate, p.updateddate, p.duration, p.priority,p.subtaskid ,p.last_status_updateddate,subtaskmaxnum,	p.updatedby, p.taskid, p.createddate FROM tms_sub_task p", nativeQuery = true)
	public List<TmsSubTask>  getAllSubTaskDeatils();
	

	@Query(value = "select max(subtaskmaxnum) as max from tms_sub_task", nativeQuery = true)
	public Long subtaskmaxnum();

}

