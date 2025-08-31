package com.narvee.ats.auth.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class PrivilegeVO {
	
	// private List<DropdownVO> roleNames;
	private List<DropdownVO> vendor;//
	private List<DropdownVO> recruiter;//
	private List<DropdownVO> tech_support;//
	
	private List<DropdownVO> visa;
	private List<DropdownVO> configuration;
	private List<DropdownVO> qualification;
	private List<DropdownVO> technology_tag; //
	private List<DropdownVO> immigration; //
	private List<DropdownVO> user; //
	// private List<DropdownVO> pre_sales;
	// private List<DropdownVO> sales;
	
	
	private List<DropdownVO> taskmanagement;
	private List<DropdownVO> role;
	private List<DropdownVO> requirement;
	private List<DropdownVO> kpt;
	private List<DropdownVO> company;

	private List<DropdownVO> sales_consultant;
	private List<DropdownVO> sales_submission;
	private List<DropdownVO> sales_interview;
	private List<DropdownVO> sales_closures;
	
	private List<DropdownVO> recruiting_requirement;
	private List<DropdownVO> recruiting_consultant;
	private List<DropdownVO> recruiting_submission;
	private List<DropdownVO> recruiting_interview;
	private List<DropdownVO> recruiting_closures;

	private List<DropdownVO> dom_requirement;
	private List<DropdownVO> dom_consultant;
	private List<DropdownVO> dom_submission;
	private List<DropdownVO> dom_interview;
	private List<DropdownVO> dom_closures;
	
	private List<DropdownVO> sourcing_reports;
	private List<DropdownVO> us_reports;
	private List<DropdownVO> dashboard;
	
	private List<DropdownVO> search;
	private List<DropdownVO> massmailing;
	private List<DropdownVO> h1transfer;
	private List<DropdownVO> presales;
	private List<DropdownVO> open_reqs_job_application;
	private List<DropdownVO> talentpool;
	private List<DropdownVO> vc_cx_profiles;
	
	private List<DropdownVO> vms;
	private List<DropdownVO> sales;
	private List<DropdownVO> recruitment;
	private List<DropdownVO> dom_recruitment;
    private List<DropdownVO> talent_acquisition;
    private List<DropdownVO> people;
    private List<DropdownVO> masters;
    private List<DropdownVO> billpay;
    private List<DropdownVO> sourcing;
    private List<DropdownVO> onboarding;
    private List<DropdownVO> open_requirements;
    private List<DropdownVO> reports;

    private List<DropdownVO> projects;
    private List<DropdownVO> tcvr;
    private List<DropdownVO> view_employee_profile;
    private List<DropdownVO> employee_job_requirements;
    private List<DropdownVO> employee_applied_jobs;
    


    private List<DropdownVO> docsynch;
    private List<DropdownVO> employee_submissions;
    private List<DropdownVO> employee_interviews;
    private List<DropdownVO> employee_report;
    private List<DropdownVO> excel_export;
    private List<DropdownVO> ratings;

    private List<DropdownVO> othercompaniessuperadmin;

	
}
