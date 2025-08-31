package com.narvee.ats.auth.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.narvee.ats.auth.commons.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tms_Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TmsUsers extends AuditModel {
 static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String organisationName;
	private String organisationEmail;
	private String email;
	private String contactNumber;
	private Long adminId;
	private String password;
	private Boolean isSuperAdmin;
	private String position;
	private Long addedBy;
    private Long updatedBy;
    private String companyDomain;
    private Long companySize;
    private String industry; 
     
    @Lob
    private byte[] profilePhoto;
    @OneToOne
    private TmsRoles role;
	

}
