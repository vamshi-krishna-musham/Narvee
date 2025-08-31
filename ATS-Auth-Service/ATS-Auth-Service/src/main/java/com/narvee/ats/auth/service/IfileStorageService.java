package com.narvee.ats.auth.service;

import org.springframework.web.multipart.MultipartFile;

public interface IfileStorageService {
	public String storeEmployeeFile(MultipartFile file, String name, String type);

	public String storeEmpmultiplefiles(MultipartFile file, String name);
}
