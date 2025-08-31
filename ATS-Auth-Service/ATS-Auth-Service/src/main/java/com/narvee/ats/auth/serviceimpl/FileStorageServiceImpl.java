package com.narvee.ats.auth.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.ats.auth.exception.FileStorageException;
import com.narvee.ats.auth.service.IfileStorageService;
@Service
public class FileStorageServiceImpl implements IfileStorageService {
	private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

	@Value("${emp.upload-dir}")
	private String empfilesLocation;

	// store hr module files
	@Override
	public String storeEmployeeFile(MultipartFile file, String name, String type) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		String extension = fileName.substring(fileName.indexOf(".") + 1);
		StringBuilder newfile = new StringBuilder();
		StringBuilder rndm = new StringBuilder();
		Random random = new Random();
		int x;
		newfile.append(name + "-");
		for (int i = 1; i <= 5; i++) {
			x = 1 + random.nextInt(5);
			newfile.append(x);
		}
		newfile.append("#" + type + "." + extension);
		File file2 = new File(empfilesLocation);
		logger.info(" file location ", empfilesLocation);
		if (!file2.exists()) {
			file2.mkdir();
		}
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			InputStream inR = file.getInputStream();
			// OutputStream osResume = new FileOutputStream(file2.getAbsolutePath() + "\\" +
			// newfile);
			OutputStream osResume = new FileOutputStream(file2.getAbsolutePath() + "/" + newfile);
			IOUtils.copy(inR, osResume);
			return newfile.toString();
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	@Override
	public String storeEmpmultiplefiles(MultipartFile file, String name) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		// String storeLocation = "D:/stores2";
		InputStream inR = null;
		OutputStream osResume = null;

		File file2 = new File(empfilesLocation);
		logger.info(" multiple file location ", empfilesLocation);
		if (!file2.exists()) {
			file2.mkdir();
		}
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			inR = file.getInputStream();
			// osResume = new FileOutputStream(file2.getAbsolutePath() + "\\" + fileName);
			osResume = new FileOutputStream(file2.getAbsolutePath() + "/" + fileName);
			IOUtils.copy(inR, osResume);
			return fileName;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}

		finally {
			try {
				inR.close();
				osResume.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
