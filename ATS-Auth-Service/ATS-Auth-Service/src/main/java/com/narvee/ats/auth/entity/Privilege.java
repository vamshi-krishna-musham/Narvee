package com.narvee.ats.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.narvee.ats.auth.commons.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "privilege")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
public class Privilege extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	private String type;

	@Column(name = "description")
	private String description;

	@Column(name = "createdby", insertable = true, updatable = false)
	private Long createdBy;

	@Column(name = "updatedby", insertable = true)
	private Long updatedBy;

	private boolean selected;
	
	@Column(name = "cardType")
	private String cardType ="one";  //one/many

	public enum privilegeType {
		VENDOR, RECRUITER, TECH_SUPPORT, TECHNOLOGY_TAG, IMMIGRATION, USER, SUBMISSION, INTERVIEW, CONSULTANT, VISA,
		QUALIFICATION, TASKMANAGEMENT, ROLE, KPT, REQUIREMENT, COMPANY, SOURCING_REPORTS, US_REPORTS, DASHBOARD, SEARCH, MASS_MAILING, H_TRANSFER, PRESALES, OPEN_REQS_JOB_APPLICATION,
		TALENT_POOL, VC_CX_PROFILES, VMS, SALES, RECRUITMENT, TALENT_ACQUISITION, TASK_MANAGEMENT, PEOPLE, MASTERS, BILLPAY, SOURCING, ONBOARDING, OPEN_REQUIREMENTS, REPORTS, 
		SALES_CONSULTANT, SALES_SUBMISSION, SALES_INTERVIEW, SALES_CLOSURES, RECRUITING_REQUIREMENT, RECRUITING_CONSULTANT, RECRUITING_SUBMISSION, RECRUITING_INTERVIEW, RECRUITING_CLOSURES
		, DOM_REQUIREMENT, DOM_CONSULTANT, DOM_SUBMISSION, DOM_INTERVIEW, DOM_CLOSURES, DOM_RECRUITMENT, PROJECTS, TCVR, EMPLOYEE_LOGIN_VIEW_PROFILE, EMPLOYEE_LOGIN_JOB_REQUIREMENTS, 
		EMPLOYEE_LOGIN_APPLIED_JOBS, EMPLOYEE_LOGIN_SUBMISSIONS, EMPLOYEE_LOGIN_INTERVIEWS, EMPLOYEE_LOGIN_REPORT, EXCEL_EXPORT,DOCSYNCH,RATINGS, OTHERCOMPANIESSUPERADMIN
	}
}
