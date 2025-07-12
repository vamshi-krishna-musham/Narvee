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

	public TmsFileUpload findByFileNameAndSubtask(String fileName, TmsSubTask updatesubtask);
	
	public TmsFileUpload findByFileNameAndTask(String fileName, TmsTask updatesubtask);
	
	public TmsFileUpload findByFileNameAndProject(String fileName, TmsProject updatesubtask);
}
