package com.narvee.ats.auth.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "Employeedocuments")
public class EmployeeDocuments {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long docid;
	private String educationdoc;
	private String filename;
	private Long userid;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "createddate", nullable = false, updatable = true)
	// @UpdateTimestamp
	private LocalDateTime createddate;

	@PrePersist
	public void setCreateddate() {
		ZoneId newYork = ZoneId.of("America/Chicago");
		LocalDateTime now = LocalDateTime.now(newYork);
		this.createddate = now;
	}
}