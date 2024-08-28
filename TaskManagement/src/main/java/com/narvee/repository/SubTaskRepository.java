package com.narvee.repository;

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
import com.narvee.entity.SubTask;

public interface SubTaskRepository extends JpaRepository<SubTask, Long> {

	@Query(value = "select s.subtaskid, s.subtaskname, s.subtaskdescription, s.status, s.targetdate, u.fullname as addedby from sub_task s, users u where u.userid = s.addedby ", nativeQuery = true)
	public Page<SubTaskUserDTO> getSubTaskUser(Pageable pageable);

	@Query(value = "select s.subtaskid, s.subtaskname, s.subtaskdescription, s.status, s.targetdate, u.fullname as addedby from sub_task s, users u where u.userid = s.addedby "
			+ "And (s.subtaskname LIKE CONCAT('%', :keyword, '%') OR s.subtaskdescription LIKE CONCAT('%', :keyword, '%') OR s.status LIKE CONCAT('%', :keyword, '%') "
			+ "OR  s.targetdate LIKE CONCAT('%', :keyword, '%') OR u.fullname LIKE CONCAT('%', :keyword, '%'))", nativeQuery = true)
	public Page<SubTaskUserDTO> getSubTaskUserFiltering(Pageable pageable, @Param("keyword") String keyword);

	@Query(value = "select * from sub_task s WHERE (s.subTaskName LIKE CONCAT('%', :keyword, '%') OR s.subTaskDescription LIKE CONCAT('%', :keyword, '%') or s.status LIKE CONCAT('%', :keyword, '%') "
			+ " or s.targetDate LIKE CONCAT('%', :keyword, '%')) ", nativeQuery = true)
	public Page<SubTask> getAllSubTasksSortingAndFiltering(Pageable pageable, @Param("keyword") String keyword);

	public List<SubTask> findByTaskTicketid(String ticketid);

	@Query(value = "select st.subtaskid ,u.fullname , u.pseudoname from sub_task st , assigned_users au , users u  ,task t where st.subtaskid =au.subtaskid and t.taskid=st.taskid AND\r\n"
			+ "			  au.userid =u.userid and t.ticketid= :ticketid", nativeQuery = true)
	public List<GetUsersDTO> getAssignUsers(String ticketid);

	@Modifying
	@Transactional
	@Query(value = "UPDATE sub_task SET status=:status WHERE subtaskid =:subTaskId ", nativeQuery = true)
	public int updateTaskStatus(@Param("subTaskId") Long subTaskId, @Param("status") String status);

	@Query(value = "SELECT taskid FROM task WHERE ticketid= :ticketid" , nativeQuery = true)
	public Long findTaskId(String ticketid);

}
