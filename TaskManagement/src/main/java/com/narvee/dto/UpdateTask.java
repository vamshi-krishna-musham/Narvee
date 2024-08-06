package com.narvee.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTask {

	private Long taskid;

	private Long updatedby;

	private String status;
	@Column(name = "description", columnDefinition = "MEDIUMTEXT")
	private String description;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate fromdate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate todate;
	
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	private LocalTime ftime;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	private LocalTime ttime;
	
	private String duration;

}
