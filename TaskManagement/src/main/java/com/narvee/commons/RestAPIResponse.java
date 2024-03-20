package com.narvee.commons;

import java.time.LocalDateTime;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.annotation.JsonFormat;

@ConfigurationProperties(prefix = "rest.api.response")
@PropertySource(value = "classpath:application.yml")
public class RestAPIResponse {

	public String status;
	public String message;
	public Object data;
	public int pagesize;
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", locale = "hi_IN", timezone = "IST")
	private LocalDateTime timeStamp;

	private RestAPIResponse() {
		timeStamp = LocalDateTime.now();
	}

	public RestAPIResponse(String status) {
		this();
		this.status = status;
	}

	public RestAPIResponse(String status, String message) {
		this();
		this.status = status;
		this.message = message;
	}

	public RestAPIResponse(int pagesize, Object data) {
		this();
		this.pagesize = pagesize;
		this.data = data;
	}

	public RestAPIResponse(String status, String message, Object data) {
		this();
		this.status = status;
		this.message = message;
		this.data = data;
	}

}
