package com.narvee.ats.auth.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.narvee.ats.auth.commons.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class Users extends AuditModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userid;

	private String fullname;
	private String pseudoname;
	private String email;
	private int isactive;

//	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	private String designation;
	private String status = "Active";	
	private String department;
	@Column(name = "remarks", length = 400)
	private String remarks;

	@OneToOne
	private Roles role;

	// private String resetPasswordToken;

	private long manager;

	private long teamlead;

//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "companycontactnumber")
//	private PhoneNumberFormat companycontactnumber;
//
//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "personalcontactnumber")
//	private PhoneNumberFormat personalcontactnumber;

	// @OneToOne(cascade = CascadeType.ALL)
	@Column(name = "companycontactnumber")
	private String companycontactnumber;

	// @OneToOne(cascade = CascadeType.ALL)
	@Column(name = "personalcontactnumber")
	private String personalcontactnumber;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "lastLogin")
	private LocalDateTime lastLogin;

	@Column(name = "lastLogout")
	private LocalDateTime lastLogout;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(name = "joiningdate")
	private LocalDate joiningdate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(name = "relievingdate")
	private LocalDate relievingdate;

	@Column(name = "personalemail", length = 70)
	private String personalemail;

	@Column(name = "aadharno", length = 60)
	private String aadharno;

	@Column(name = "panno", length = 60)
	private String panno;

	@Column(name = "bankname", length = 150)
	private String bankname;

	@Column(name = "branch", length = 150)
	private String branch;

	@Column(name = "accno", length = 60)
	private String accno;

	@Column(name = "ifsc", length = 50)
	private String ifsc;

	private String systemip;

	private String systemname;

	private boolean locked = false;

	private boolean isteamlead = false;

	private boolean ismanager = false;

	private String pan;
	private String bpassbook;
	private String aadhar;
	private String alternatenumber;
	private String resume;
	@OneToMany
	@JoinColumn(name = "userid")
	List<EmployeeDocuments> eDoc;

	// for checkin's
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@JoinColumn(name = "userid", nullable = false)
//	private List<UserTracker> usertracker;
	// @Column(name = "roleid", nullable = true)
	private String roleid;
	
	// clear later
	private String firstname;
	private String lastname;
	private String reset_password_token;
	
	private String banterno;

	private Long companyid;
	
	@Transient
	private String cid;
	
	private Long added;
}
