package com.narvee.ats.auth.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardPrivilegeVO {

	private List<DropdownVO> vendor;//
	private List<DropdownVO> recruiter;//
	private List<DropdownVO> tech_support;//
	private List<DropdownVO> consultant;
	private List<DropdownVO> visa;
	private List<DropdownVO> configuration;
	private List<DropdownVO> qualification;
	private List<DropdownVO> technology_tag; //
	private List<DropdownVO> immigration; //
	private List<DropdownVO> user; //
	// private List<DropdownVO> pre_sales;
	// private List<DropdownVO> sales;
	private List<DropdownVO> submission;
	private List<DropdownVO> interview;
	private List<DropdownVO> taskmanagement;
	private List<DropdownVO> role;
	private List<DropdownVO> requirement;
	private List<DropdownVO> kpt;
	private List<DropdownVO> company;

	private List<DropdownVO> recruiting_submission;
	private List<DropdownVO> recruiting_interview;
	private List<DropdownVO> recruiting_requirement;

	private List<DropdownVO> dom_submission;
	private List<DropdownVO> dom_interview;
	private List<DropdownVO> dom_requirement;
	
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
	
	
	
}
