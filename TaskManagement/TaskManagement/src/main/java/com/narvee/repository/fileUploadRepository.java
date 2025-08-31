package com.narvee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.entity.TmsFileUpload;
import com.narvee.entity.TmsProject;
import com.narvee.entity.TmsSubTask;
import com.narvee.entity.TmsTask;

@Repository
public interface fileUploadRepository extends JpaRepository<TmsFileUpload, Long> {

	
	@Query("SELECT f FROM TmsFileUpload f WHERE f.task.taskid = :taskid")
	public List<TmsFileUpload> getTaskFiles(Long taskid);
	
	@Query("SELECT f FROM TmsFileUpload f WHERE f.subtask.subTaskId = :subTaskId")
	List<TmsFileUpload> getFilesBySubTaskId(@Param("subTaskId") Long subtaskid);
	

     @Query(value = "select id,sub_task_id,task_id,file_name ,file_type ,file_path ,pid from tms_file_upload where pid = :pid",nativeQuery = true)
     List<TmsFileUpload> getProjectFiles(Long pid);
     
     @Query(value = "select id,sub_task_id,pid,file_name ,file_type ,file_path ,task_id from tms_file_upload where task_id  = :taskId",nativeQuery = true)
     List<TmsFileUpload> getTaskFile(Long taskId);
     
     @Query(value = "select id,sub_task_id,pid,task_id,file_name ,file_type ,file_path ,sub_task_id from tms_file_upload where sub_task_id  = :subTaskId",nativeQuery = true)
     List<TmsFileUpload> getSubTaskFile(Long subTaskId);
     
     
	public TmsFileUpload findByFileNameAndSubtask(String fileName, TmsSubTask updatesubtask);
	
	public TmsFileUpload findByFileNameAndTask(String fileName, TmsTask updatesubtask);
	
	public TmsFileUpload findByFileNameAndProject(String fileName, TmsProject updatesubtask);
}
