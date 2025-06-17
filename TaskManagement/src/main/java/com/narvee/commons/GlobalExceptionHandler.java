package com.narvee.commons;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ MaxUploadSizeExceededException.class, SizeLimitExceededException.class })
	public ResponseEntity<RestAPIResponse> handleMaxSizeException(Exception ex) {
		return new ResponseEntity<RestAPIResponse>(
				new RestAPIResponse("fail", "File too large! Maximum allowed size is 2MB."),
				HttpStatus.OK);

	}

}
