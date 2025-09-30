package com.narvee.service.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.dto.FileUploadDto;
import com.narvee.entity.TmsFileUpload;
import com.narvee.entity.TmsProject;
import com.narvee.entity.TmsSubTask;
import com.narvee.entity.TmsTask;
import com.narvee.repository.ProjectRepository;
import com.narvee.repository.SubTaskRepository;
import com.narvee.repository.TaskRepository;
import com.narvee.repository.fileUploadRepository;
import com.narvee.service.service.FileUploadService;
import com.narvee.service.serviceimpl.ProjectServiceImpl;

@Service
public class FileUploadServiceImpl implements FileUploadService {
	
	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
	
	@Autowired
	private fileUploadRepository fileUploadRepository;
	
	@Autowired 
	private ProjectRepository projectRepository;
	
	@Autowired 
	private TaskRepository taskRepository;
	
	@Autowired 
	private SubTaskRepository subTaskRepository;
	
	@Value("${AppFilesDir}")
	private String UPLOAD_DIR;

	@Override
	public List<FileUploadDto> uploadFile(MultipartFile[] files, Long pid, Long taskId, Long subTaskId) {
	    List<TmsFileUpload> tmsFileList = new ArrayList<>();
	    List<FileUploadDto> dtoList = new ArrayList<>();
	       
	    TmsProject project = null;
	    TmsTask task = null;
	    TmsSubTask subTask = null;

	    if (pid != null) {
	        project = projectRepository.findById(pid)
	                .orElseThrow(() -> new RuntimeException("Project not found"));
	    }
	    if (taskId != null) {
	        task = taskRepository.findById(taskId)
	                .orElseThrow(() -> new RuntimeException("Task not found"));
	    }
	    if (subTaskId != null) {
	        subTask = subTaskRepository.findById(subTaskId)
	                .orElseThrow(() -> new RuntimeException("SubTask not found"));
	    }

	    if (files != null && files.length > 0) {
	        try {
	            Files.createDirectories(Paths.get(UPLOAD_DIR));
	        } catch (IOException e) {
	            logger.error("Failed to create upload directory", e);
	            return dtoList;
	        }

	        for (MultipartFile file : files) {
	            try {
	                if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
	                    continue;
	                }

	                String originalFilename = file.getOriginalFilename();
	                String nameWithoutExt = originalFilename;
	                String ext = "";

	                int dotIndex = originalFilename.lastIndexOf('.');
	                if (dotIndex != -1) {
	                    nameWithoutExt = originalFilename.substring(0, dotIndex);
	                    ext = originalFilename.substring(dotIndex);
	                }
	                String referenceId = "";

	                if (subTaskId != null) {
	                    referenceId = "subtask-" + subTask.getSubTaskId();
	                } else if (taskId != null) {
	                    referenceId = "task-" + task.getTicketid();
	                } else if (pid != null) {
	                    referenceId = "project-" + project.getProjectid();
	                } else {
	                    referenceId = "general";
	                }
//
//	                String newFileName = nameWithoutExt + "-" + referenceId + ext;
//	                Path filePath = Paths.get(UPLOAD_DIR, newFileName);
//	                Files.write(filePath, file.getBytes());
	                
	                // Base file name with ID
	    	        String baseFileName = nameWithoutExt + "-" + referenceId;
	    	        String newFileName = baseFileName + ext;
	    	        Path newPath = Paths.get(UPLOAD_DIR, newFileName);
	    	        int count = 1;

	    	        // Check for duplicates and add (1), (2), etc.
	    	        while (Files.exists(newPath)) {
	    	            newFileName = baseFileName + " (" + count + ")" + ext;
	    	            newPath = Paths.get(UPLOAD_DIR, newFileName);
	    	            count++;
	    	        }

	    	        
	    	        Files.write(newPath, file.getBytes());
	                // Prepare entity
	                TmsFileUpload entity = new TmsFileUpload();
	                entity.setFileName(newFileName);
	                entity.setFilePath(newPath.toAbsolutePath().toString());
	                entity.setFileType(file.getContentType());
	                entity.setProject(project);
	                entity.setTask(task);
	                entity.setSubtask(subTask);

	                tmsFileList.add(entity);

	            } catch (IOException e) {
	                logger.error("Failed to save file: " + file.getOriginalFilename(), e);
	            }
	        }

	        List<TmsFileUpload> savedFiles = fileUploadRepository.saveAll(tmsFileList);

	        for (TmsFileUpload file : savedFiles) {
	            FileUploadDto dto = new FileUploadDto();
	            dto.setFileName(file.getFileName());
	            dto.setId(file.getId());
	            dto.setFilePath(file.getFilePath());
	            dto.setFileType(file.getFileType());
	            dtoList.add(dto);
	        }
	    }

	    return dtoList;
	}

	@Override
	public FileUploadDto replaceFile(Long id, MultipartFile newFile) {
	    TmsFileUpload fileRecord = fileUploadRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("File not found with id: " + id));

	    try {
	        // Delete old file from disk (optional but recommended)
	        File oldFile = new File(fileRecord.getFilePath());
	        if (oldFile.exists()) {
	            oldFile.delete();
	        }

	        // Build new file name
	        String originalFilename = newFile.getOriginalFilename();
	        if (originalFilename == null || originalFilename.isEmpty()) {
	            throw new RuntimeException("Invalid file name");
	        }

	        String nameWithoutExt = originalFilename;
	        String ext = "";
	        int dotIndex = originalFilename.lastIndexOf('.');
	        if (dotIndex != -1) {
	            nameWithoutExt = originalFilename.substring(0, dotIndex);
	            ext = originalFilename.substring(dotIndex);
	        }

	        // Reference ID for name (project/task/subtask)
	        String refId = "general";
	        if (fileRecord.getSubtask() != null) {
	            refId = "subtask-" + fileRecord.getSubtask().getSubTaskId();
	        } else if (fileRecord.getTask() != null) {
	            refId = "task-" + fileRecord.getTask().getTicketid();
	        } else if (fileRecord.getProject() != null) {
	            refId = "project-" + fileRecord.getProject().getProjectid();
	        }

	        String newFileName = nameWithoutExt + "-" + refId + ext;
	        Path newPath = Paths.get(UPLOAD_DIR, newFileName);

	        // Save new file
	        Files.write(newPath, newFile.getBytes());

	        // Update DB record
	        fileRecord.setFileName(newFileName);
	        fileRecord.setFilePath(newPath.toAbsolutePath().toString());
	        fileRecord.setFileType(newFile.getContentType());
	        
	  

	        fileUploadRepository.save(fileRecord);


	        // Build DTO
	        FileUploadDto dto = new FileUploadDto();
	        dto.setId(fileRecord.getId());
	        dto.setFileName(fileRecord.getFileName());
	        dto.setFilePath(fileRecord.getFilePath());
	        dto.setFileType(fileRecord.getFileType());

	        return dto;

	    } catch (IOException e) {
	        throw new RuntimeException("Failed to replace file", e);
	    }
	}


	@Override
	public void DeleteFile(Long fileId) {
	TmsFileUpload fileUpload  =	fileUploadRepository.findById(fileId)
			.orElseThrow(() -> new RuntimeException("File not found"));
	 fileUploadRepository.delete(fileUpload);
		 
	}

	@Override
	public FileUploadDto getFile(long id) {
		TmsFileUpload fileUpload  =	fileUploadRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("File not found"));
		FileUploadDto dto = new FileUploadDto();
		dto.setId(fileUpload.getId());
		dto.setFileName(fileUpload.getFileName());
		dto.setFilePath(fileUpload.getFilePath());
		dto.setFileType(fileUpload.getFileType());
		return dto;
	}

	@Override
	public List<FileUploadDto> getAllFiles(Long pid, Long taskId, Long subTaskId) {
		 List<TmsFileUpload> uploads;

		    if (pid != null) {
		        uploads = fileUploadRepository.getProjectFiles(pid);
		    } else if (taskId != null) {
		        uploads = fileUploadRepository.getTaskFile(taskId);
		    } else if ( subTaskId!= null) {
		        uploads = fileUploadRepository.getSubTaskFile(subTaskId);
		    } else {
		        throw new RuntimeException("Please provide at least one ID (projectId/taskId/subTaskId)");
		    }

		    return uploads.stream().map(file -> {
		        FileUploadDto dto = new FileUploadDto();
		        
		        String savedFileName = file.getFileName(); 
	
		        String extension = savedFileName.substring(savedFileName.lastIndexOf("."));
		        Pattern pattern = Pattern.compile("^(.*?)-(task|project|subtask)-[A-Za-z0-9]+(?: \\((\\d+)\\))?\\.[^.]+$");
		        Matcher matcher = pattern.matcher(savedFileName);

		        String originalName;
		        if (matcher.find()) {
		            String baseName = matcher.group(1);              
		            String copySuffix = matcher.group(3);           
		            originalName = baseName + (copySuffix != null ? " (" + copySuffix + ")" : "") + extension;
		        } else {
		            originalName = savedFileName; // fallback
		        }

		      
		        dto.setFileName(originalName);


		        //dto.setFileName(file.getFileName());
		        dto.setId(file.getId());
		        dto.setFilePath(file.getFilePath());
		        dto.setFileType(file.getFileType());
		        return dto;
		    }).collect(Collectors.toList());
	}

}
