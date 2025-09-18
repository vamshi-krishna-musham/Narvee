package com.narvee.service.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.narvee.dto.FileUploadDto;

public interface FileUploadService {
   
	
	  public List<FileUploadDto> uploadFile(MultipartFile[] files, Long pid, Long taskId, Long subTaskId);
	  FileUploadDto replaceFile(Long id, MultipartFile newFile);
	  public void DeleteFile(Long  fileId);
	  public FileUploadDto getFile(long id);
	  public  List<FileUploadDto> getAllFiles(Long pid, Long TassId, Long subTaskId);
	  
}
