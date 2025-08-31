package com.narvee.ats.auth.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "login_tracker")
public class LoginTracker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String systemIp;

	private String networkIp;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime loginTime;

	private String status;

	private String remarks;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userid")
	private Users user;

}
