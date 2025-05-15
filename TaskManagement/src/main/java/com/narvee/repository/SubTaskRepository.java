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
	@Query(value = "UPDATE tms_sub_task SET status=:status , updatedby=:updatedby, updateddate = :updateddate WHERE subtaskid =:subTaskId ", nativeQuery = true)
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

	@Query(value = "SELECT st.subtaskid, st.status,u.fullname, u.pseudoname, st.subtaskname, st.targetdate, t.ticketid, u.email FROM tms_sub_task st JOIN tms_assigned_users au ON st.subtaskid = au.subtaskid\r\n"
			+ "JOIN users u ON au.userid = u.userid JOIN tms_task t ON st.taskid = t.taskid WHERE date(st.targetdate) < :currentDate AND st.status!='to do' AND st.status!='Completed'  ", nativeQuery = true)
	public List<TaskTrackerDTO> getExceededTargetDateSubTasks(LocalDate currentDate);
	
	
	
	//----------------------------tms   replicated methods --------------
	@Query(value = "select st.subtaskid AS subTaskId,  st.subtaskdescription AS description,st.subtaskname ,st.target_date,st.addedby,st.duration,DATE(st.createddate) AS createddate ,st.priority,st.status, "
			+ " st.taskid,t.ticketid,st.updatedby ,DATE(st.updateddate) AS updateddate ,t.taskname  , t.start_date "
			+ "        from tms_sub_task  st join tms_task t where st.taskid =t.taskid and t.ticketid = :ticketId Order by t.updateddate DESC ", nativeQuery = true)
	public Page<TaskTrackerDTO> findSubTaskByTicketid(@Param("ticketId") String ticketId,Pageable pageable);
	
	
	
	@Query(value = "SELECT  st.subtaskid AS subTaskId,  st.subtaskdescription AS description,st.subtaskname ,st.targetdate,st.addedby,st.duration,DATE(st.createddate) AS createddate ,"
			+ "  st.priority,st.status,st.taskid,t.ticketid,st.updatedby ,DATE(st.updateddate) AS updateddate ,t.taskname , t.start_date "
			+ "FROM  tms_sub_task  st join tms_task t where st.taskid = t.taskid and t.ticketid = :ticketId AND ( st.subtaskid LIKE CONCAT('%',:keyword, '%') OR "
			+ "  st.subtaskdescription LIKE CONCAT('%',:keyword, '%') OR st.subtaskname LIKE CONCAT('%',:keyword,  '%') OR DATE_FORMAT(t.targetdate, '%Y-%m-%d') LIKE CONCAT('%',:keyword,  '%')  OR DATE_FORMAT(t.start_date, '%Y-%m-%d')_date LIKE CONCAT('%',:keyword,  '%') "
			+ "OR st.status LIKE CONCAT('%',:keyword, '%') OR st.priority LIKE CONCAT('%',:keyword, '%') OR st.duration LIKE CONCAT('%',:keyword, '%') OR st.taskid LIKE CONCAT('%',:keyword, '%'))", nativeQuery = true)
	public Page<TaskTrackerDTO> findSubTaskByTicketIdWithSearching(@Param("ticketId") String ticketId,
			@Param("keyword") String keyword,Pageable pageable);
	
	@Query(value = "       SELECT \r\n"
			+ "			    st.subtaskid,  \r\n"
			+ "			    NULL AS fullname,   \r\n"
			+ "			    NULL AS email,   \r\n"
			+ "			    creator.full_name as cfullname ,  \r\n"
			+ "			    creator.email as cemail   \r\n"
			+ "			FROM tms_sub_task st  \r\n"
			+ "			JOIN tms_users creator ON st.addedby = creator.user_id    \r\n"
			+ "			WHERE st.subtaskid = :subtaskid \r\n"
			+ "			  \r\n"
			+ "			UNION ALL  \r\n"
			+ "			  \r\n"
			+ "			SELECT   \r\n"
			+ "			    st.subtaskid,   \r\n"
			+ "			    u.full_name ,   \r\n"
			+ "			    u.email,   \r\n"
			+ "			    NULL AS fullname,   \r\n"
			+ "			    NULL AS email  \r\n"
			+ "			FROM tms_sub_task st \r\n"
			+ "			\r\n"
			+ "			JOIN tms_assigned_users au ON st.subtaskid = au.subtaskid  \r\n"
			+ "			JOIN tms_users u ON au.tms_user_id = u.user_id    \r\n"
			+ "			WHERE st.subtaskid = :subtaskid ", nativeQuery = true)
	public List<GetUsersDTO> getSubtaskAssignUsersTms(Long subtaskid);
}
