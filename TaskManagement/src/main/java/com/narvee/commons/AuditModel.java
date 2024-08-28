package com.narvee.commons;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "createddate", nullable = false, updatable = false)
	private LocalDateTime createddate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "updateddate", nullable = false, updatable = true)
	private LocalDateTime updateddate;

	@PrePersist
	public void setCreateddate() {
		ZoneId newYork = ZoneId.of("Asia/Kolkata");
		LocalDateTime now = LocalDateTime.now(newYork);
		this.createddate = now;
		this.updateddate = now;
	}

	@PreUpdate
	public void setUpdateddate() {
		ZoneId newYork = ZoneId.of("Asia/Kolkata");
		LocalDateTime now = LocalDateTime.now(newYork);
		this.updateddate = now;
	}
}
