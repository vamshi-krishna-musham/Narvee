package com.narvee.ats.auth.serviceimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.entity.Privilege;
import com.narvee.ats.auth.entity.Roles;
import com.narvee.ats.auth.repository.IPrivilegeRepository;
import com.narvee.ats.auth.repository.IRoleRepository;
import com.narvee.ats.auth.request.DropdownVO;
import com.narvee.ats.auth.request.PrivilegeVO;
import com.narvee.ats.auth.request.RoleToPrivilegesVO;
import com.narvee.ats.auth.service.IPrivilegeService;
import com.narvee.ats.auth.util.EncryptionUtil;

@Service
public class PrivilegeSeviceImpl implements IPrivilegeService {

	@Autowired
	private IPrivilegeRepository privRepo;

	@Autowired
	private IRoleRepository roleRepo;

//	@Autowired
//	private IUserRepository userRepo;

	@Override
	public PrivilegeVO getAllPrivileges() {

		PrivilegeVO privilegeVO = new PrivilegeVO();

		List<DropdownVO> users = new ArrayList<DropdownVO>();
		List<DropdownVO> vendor = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiter = new ArrayList<DropdownVO>();
		List<DropdownVO> technology_tags = new ArrayList<DropdownVO>();
		List<DropdownVO> tech_support = new ArrayList<DropdownVO>();
		List<DropdownVO> company = new ArrayList<DropdownVO>();
		List<DropdownVO> immigration = new ArrayList<DropdownVO>();

		List<DropdownVO> sales_consultants = new ArrayList<DropdownVO>();
		List<DropdownVO> sales_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> sales_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> sales_closures = new ArrayList<DropdownVO>();

		List<DropdownVO> recruiting_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_consultants = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_closures = new ArrayList<DropdownVO>();

		List<DropdownVO> dom_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_consultants = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_closures = new ArrayList<DropdownVO>();

		List<DropdownVO> requirement = new ArrayList<DropdownVO>();
		List<DropdownVO> visa = new ArrayList<DropdownVO>();
		List<DropdownVO> qualification = new ArrayList<DropdownVO>();
		List<DropdownVO> kpt = new ArrayList<DropdownVO>();
		List<DropdownVO> taskmanagement = new ArrayList<DropdownVO>();

		List<DropdownVO> roles = new ArrayList<DropdownVO>();

		List<DropdownVO> sourcing_reports = new ArrayList<DropdownVO>();
		List<DropdownVO> us_reports = new ArrayList<DropdownVO>();
		List<DropdownVO> dashboard = new ArrayList<DropdownVO>();

		List<DropdownVO> search = new ArrayList<DropdownVO>();
		List<DropdownVO> massmailing = new ArrayList<DropdownVO>();
		List<DropdownVO> h1transfer = new ArrayList<DropdownVO>();
		List<DropdownVO> presales = new ArrayList<DropdownVO>();
		List<DropdownVO> open_reqs_job_application = new ArrayList<DropdownVO>();
		List<DropdownVO> talentpool = new ArrayList<DropdownVO>();
		List<DropdownVO> vc_cx_profiles = new ArrayList<DropdownVO>();

		List<DropdownVO> vms = new ArrayList<DropdownVO>();
		List<DropdownVO> sales = new ArrayList<DropdownVO>();
		List<DropdownVO> recruitment = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_recruitment = new ArrayList<DropdownVO>();
		List<DropdownVO> talent_acquisition = new ArrayList<DropdownVO>();
		List<DropdownVO> people = new ArrayList<DropdownVO>();
		List<DropdownVO> masters = new ArrayList<DropdownVO>();
		List<DropdownVO> billpay = new ArrayList<DropdownVO>();
		List<DropdownVO> sourcing = new ArrayList<DropdownVO>();
		List<DropdownVO> onboarding = new ArrayList<DropdownVO>();
		List<DropdownVO> open_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> reports = new ArrayList<DropdownVO>();

		List<DropdownVO> projects = new ArrayList<DropdownVO>();
		List<DropdownVO> tcvr = new ArrayList<DropdownVO>();
		List<DropdownVO> view_employee_profile = new ArrayList<DropdownVO>();

		List<DropdownVO> employee_job_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> employee_applied_jobs = new ArrayList<DropdownVO>();

		List<DropdownVO> employee_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> employee_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> employee_report = new ArrayList<DropdownVO>();
		List<DropdownVO> ratings = new ArrayList<DropdownVO>();

		List<DropdownVO> docsynch = new ArrayList<DropdownVO>();
		List<DropdownVO> excel_export = new ArrayList<DropdownVO>();

		List<DropdownVO> othercompaniessuperadmin = new ArrayList<DropdownVO>();

		List<Privilege> privileges = privRepo.findAll();

		for (Privilege singlePrivilege : privileges) {

			// USER
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.USER.name())) {
				users.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setUser(users);
			}
			// VENDOR
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VENDOR.name())) {
				vendor.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setVendor(vendor);
			}
			// RECRUITER
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITER.name())) {

				recruiter.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRecruiter(recruiter);
			}
			// TECHNOLOGY TAG
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TECHNOLOGY_TAG.name())) {
				technology_tags.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setTechnology_tag(technology_tags);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TECH_SUPPORT.name())) {

				tech_support.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setTech_support(tech_support);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.IMMIGRATION.name())) {

				immigration.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setImmigration(immigration);
			}
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_CONSULTANT.name())) {

				sales_consultants.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setSales_consultant(sales_consultants);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_SUBMISSION.name())) {

				sales_submissions.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setSales_submission(sales_submissions);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_INTERVIEW.name())) {

				sales_interviews.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));

				privilegeVO.setSales_interview(sales_interviews);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_CLOSURES.name())) {

				sales_closures.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setSales_closures(sales_closures);
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_REQUIREMENT.name())) {

				recruiting_requirements.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRecruiting_requirement(recruiting_requirements);
			}
			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_CONSULTANT.name())) {

				recruiting_consultants.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRecruiting_consultant(recruiting_consultants);
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_SUBMISSION.name())) {

				recruiting_submissions.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRecruiting_submission(recruiting_submissions);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_INTERVIEW.name())) {
				recruiting_interviews.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRecruiting_interview(recruiting_interviews);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_CLOSURES.name())) {
				recruiting_closures.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRecruiting_closures(recruiting_closures);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_REQUIREMENT.name())) {
				dom_requirements.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setDom_requirement(dom_requirements);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_CONSULTANT.name())) {

				dom_consultants.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setDom_consultant(dom_consultants);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_SUBMISSION.name())) {
				dom_submissions.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setDom_submission(dom_submissions);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_INTERVIEW.name())) {

				dom_interviews.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setDom_interview(dom_interviews);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_CLOSURES.name())) {
				dom_closures.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setDom_closures(dom_closures);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VISA.name())) {
				visa.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setVisa(visa);
			}
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.QUALIFICATION.name())) {
				qualification.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setQualification(qualification);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.REQUIREMENT.name())) {
				requirement.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRequirement(requirement);
			}

			// TASKMANAGEMENT
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TASKMANAGEMENT.name())) {
				taskmanagement.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setTaskmanagement(taskmanagement);
			}

			// TASKMANAGEMENT
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.KPT.name())) {
				kpt.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setKpt(kpt);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.ROLE.name())) {
				roles.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRole(roles);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.COMPANY.name())) {
				company.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setCompany(company);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SOURCING_REPORTS.name())) {
				sourcing_reports.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setSourcing_reports(sourcing_reports);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.US_REPORTS.name())) {
				us_reports.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setUs_reports(us_reports);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DASHBOARD.name())) {
				dashboard.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setDashboard(dashboard);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SEARCH.name())) {
				search.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setSearch(search);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.MASS_MAILING.name())) {
				massmailing.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setMassmailing(massmailing);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.H_TRANSFER.name())) {
				h1transfer.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setH1transfer(h1transfer);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.PRESALES.name())) {
				presales.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setPresales(presales);
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.OPEN_REQS_JOB_APPLICATION.name())) {
				open_reqs_job_application.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setOpen_reqs_job_application(open_reqs_job_application);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TALENT_POOL.name())) {
				talentpool.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setTalentpool(talentpool);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VC_CX_PROFILES.name())) {
				vc_cx_profiles.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setVc_cx_profiles(vc_cx_profiles);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VMS.name())) {
				vms.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setVms(vms);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES.name())) {
				sales.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setSales(sales);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITMENT.name())) {
				recruitment.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRecruitment(recruitment);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_RECRUITMENT.name())) {
				dom_recruitment.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setDom_recruitment(dom_recruitment);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TALENT_ACQUISITION.name())) {
				talent_acquisition.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setTalent_acquisition(talent_acquisition);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.PEOPLE.name())) {
				people.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setPeople(people);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.MASTERS.name())) {
				masters.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setMasters(masters);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.BILLPAY.name())) {
				billpay.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setBillpay(billpay);
			}
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SOURCING.name())) {
				sourcing.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setSourcing(sourcing);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.ONBOARDING.name())) {
				onboarding.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setOnboarding(onboarding);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.OPEN_REQUIREMENTS.name())) {
				open_requirements.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setOpen_requirements(open_requirements);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.REPORTS.name())) {
				reports.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setReports(reports);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.PROJECTS.name())) {
				projects.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setProjects(projects);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TCVR.name())) {
				tcvr.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setTcvr(tcvr);
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_VIEW_PROFILE.name())) {
				view_employee_profile.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setView_employee_profile(view_employee_profile);
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_JOB_REQUIREMENTS.name())) {
				employee_job_requirements.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setEmployee_job_requirements(employee_job_requirements);
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_APPLIED_JOBS.name())) {
				employee_applied_jobs.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setEmployee_applied_jobs(employee_applied_jobs);
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_SUBMISSIONS.name())) {
				employee_submissions.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setEmployee_submissions(employee_submissions);
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_INTERVIEWS.name())) {
				employee_interviews.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setEmployee_interviews(employee_interviews);
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_REPORT.name())) {
				employee_report.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setEmployee_report(employee_report);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.EXCEL_EXPORT.name())) {
				excel_export.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setExcel_export(excel_export);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOCSYNCH.name())) {
				docsynch.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setDocsynch(docsynch);
			}

			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RATINGS.name())) {
				ratings.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRatings(ratings);
				;
			}

			if (singlePrivilege.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.OTHERCOMPANIESSUPERADMIN.name())) {
				othercompaniessuperadmin.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setRatings(othercompaniessuperadmin);
			}
		}

		return privilegeVO;
	}

	@Override
	public Privilege savePrevileges(Privilege privilege) {
		return privRepo.save(privilege);
	}

	@Override
	public List<Privilege> allprev() {
		List<Privilege> privileges = privRepo.findAll();
		return privileges;
	}

	@Override
	public PrivilegeVO getPrivilegesById(Long roleId,Long companyId) {

		PrivilegeVO privilegeVO = new PrivilegeVO();
		Roles roles = roleRepo.findByRoleidAndCompanyid(roleId, companyId);

		List<DropdownVO> users = new ArrayList<DropdownVO>();
		List<DropdownVO> vendor = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiter = new ArrayList<DropdownVO>();
		List<DropdownVO> technology_tags = new ArrayList<DropdownVO>();
		List<DropdownVO> tech_support = new ArrayList<DropdownVO>();
		List<DropdownVO> immigration = new ArrayList<DropdownVO>();

		List<DropdownVO> requirement = new ArrayList<DropdownVO>();

		List<DropdownVO> sales_consultants = new ArrayList<DropdownVO>();
		List<DropdownVO> sale_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> sales_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> sales_closures = new ArrayList<DropdownVO>();

		List<DropdownVO> recruiting_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_consultants = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_closures = new ArrayList<DropdownVO>();

		List<DropdownVO> dom_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_consultants = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_closures = new ArrayList<DropdownVO>();

		List<DropdownVO> visa = new ArrayList<DropdownVO>();
		List<DropdownVO> qualification = new ArrayList<DropdownVO>();
		List<DropdownVO> kpt = new ArrayList<DropdownVO>();
		List<DropdownVO> taskmanagement = new ArrayList<DropdownVO>();
		List<DropdownVO> role = new ArrayList<DropdownVO>();
		List<DropdownVO> company = new ArrayList<DropdownVO>();

		List<DropdownVO> sourcing_reports = new ArrayList<DropdownVO>();
		List<DropdownVO> us_reports = new ArrayList<DropdownVO>();
		List<DropdownVO> dashboard = new ArrayList<DropdownVO>();

		List<DropdownVO> search = new ArrayList<DropdownVO>();
		List<DropdownVO> massmailing = new ArrayList<DropdownVO>();
		List<DropdownVO> h1transfer = new ArrayList<DropdownVO>();
		List<DropdownVO> presales = new ArrayList<DropdownVO>();
		List<DropdownVO> open_reqs_job_application = new ArrayList<DropdownVO>();
		List<DropdownVO> talentpool = new ArrayList<DropdownVO>();
		List<DropdownVO> vc_cx_profiles = new ArrayList<DropdownVO>();

		List<DropdownVO> vms = new ArrayList<DropdownVO>();
		List<DropdownVO> sales = new ArrayList<DropdownVO>();
		List<DropdownVO> recruitment = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_recruitment = new ArrayList<DropdownVO>();
		List<DropdownVO> talent_acquisition = new ArrayList<DropdownVO>();
		List<DropdownVO> people = new ArrayList<DropdownVO>();
		List<DropdownVO> masters = new ArrayList<DropdownVO>();
		List<DropdownVO> billpay = new ArrayList<DropdownVO>();
		List<DropdownVO> sourcing = new ArrayList<DropdownVO>();
		List<DropdownVO> onboarding = new ArrayList<DropdownVO>();
		List<DropdownVO> open_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> reports = new ArrayList<DropdownVO>();

		List<DropdownVO> projects = new ArrayList<DropdownVO>();
		List<DropdownVO> tcvr = new ArrayList<DropdownVO>();
		List<DropdownVO> view_employee_profile = new ArrayList<DropdownVO>();

		List<DropdownVO> employee_job_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> employee_applied_jobs = new ArrayList<DropdownVO>();

		List<DropdownVO> docsynch = new ArrayList<DropdownVO>();

		List<DropdownVO> employee_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> employee_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> employee_report = new ArrayList<DropdownVO>();
		List<DropdownVO> excel_export = new ArrayList<DropdownVO>();
		List<DropdownVO> ratings = new ArrayList<DropdownVO>();

		List<DropdownVO> othercompaniessuperadmin = new ArrayList<DropdownVO>();

		Set<Privilege> privileges = roles.getPrivileges();

		boolean flg = false;

		for (Privilege single : privileges) {

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.ROLE.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				role.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.REQUIREMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				requirement.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.KPT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				kpt.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VENDOR.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				vendor.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITER.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruiter.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TECH_SUPPORT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				tech_support.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TECHNOLOGY_TAG.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				technology_tags.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.IMMIGRATION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				immigration.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.USER.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				users.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_CONSULTANT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				sales_consultants.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_SUBMISSION.name())) {

				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				sale_submissions.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_INTERVIEW.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sales_interviews.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_CLOSURES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sales_closures.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_REQUIREMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				recruiting_requirements
						.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_CONSULTANT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				recruiting_consultants.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_SUBMISSION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruiting_submissions.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_INTERVIEW.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruiting_interviews.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_CLOSURES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruiting_closures.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_REQUIREMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_requirements.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_CONSULTANT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_consultants.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_SUBMISSION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_submissions.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_INTERVIEW.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_interviews.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_CLOSURES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_closures.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VISA.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				visa.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.QUALIFICATION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				qualification.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TASKMANAGEMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				taskmanagement.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.COMPANY.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				company.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SOURCING_REPORTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sourcing_reports.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.US_REPORTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				us_reports.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DASHBOARD.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dashboard.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SEARCH.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				search.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.MASS_MAILING.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				massmailing.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.H_TRANSFER.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				h1transfer.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.PRESALES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				presales.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.OPEN_REQS_JOB_APPLICATION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				open_reqs_job_application
						.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TALENT_POOL.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				talentpool.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VC_CX_PROFILES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				vc_cx_profiles.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VMS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				vms.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sales.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruitment.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_RECRUITMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_recruitment.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TALENT_ACQUISITION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				talent_acquisition.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.PEOPLE.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				people.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.MASTERS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				masters.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.BILLPAY.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				billpay.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SOURCING.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sourcing.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.ONBOARDING.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				onboarding.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.OPEN_REQUIREMENTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				open_requirements.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.REPORTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				reports.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.PROJECTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				projects.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TCVR.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				tcvr.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_VIEW_PROFILE.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				view_employee_profile.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_JOB_REQUIREMENTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_job_requirements
						.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_APPLIED_JOBS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_applied_jobs.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_SUBMISSIONS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_submissions.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_INTERVIEWS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_interviews.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_REPORT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_report.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.EXCEL_EXPORT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				excel_export.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOCSYNCH.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				docsynch.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RATINGS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				ratings.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.OTHERCOMPANIESSUPERADMIN.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				othercompaniessuperadmin
						.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

		}
		privilegeVO.setVendor(vendor);
		privilegeVO.setRecruiter(recruiter);
		privilegeVO.setTech_support(tech_support);
		privilegeVO.setTechnology_tag(technology_tags);
		privilegeVO.setImmigration(immigration);
		privilegeVO.setUser(users);

		privilegeVO.setSales_consultant(sales_consultants);
		privilegeVO.setSales_submission(sale_submissions);
		privilegeVO.setSales_interview(sales_interviews);
		privilegeVO.setSales_closures(sales_closures);

		privilegeVO.setRecruiting_requirement(recruiting_requirements);
		privilegeVO.setRecruiting_consultant(recruiting_consultants);
		privilegeVO.setRecruiting_submission(recruiting_submissions);
		privilegeVO.setRecruiting_interview(recruiting_interviews);
		privilegeVO.setRecruiting_closures(recruiting_closures);

		privilegeVO.setDom_requirement(dom_requirements);
		privilegeVO.setDom_consultant(dom_consultants);
		privilegeVO.setDom_submission(dom_submissions);
		privilegeVO.setDom_interview(dom_interviews);
		privilegeVO.setDom_closures(dom_closures);

		privilegeVO.setVisa(visa);
		privilegeVO.setQualification(qualification);
		privilegeVO.setTaskmanagement(taskmanagement);
		privilegeVO.setRole(role);
		privilegeVO.setCompany(company);
		privilegeVO.setRequirement(requirement);
		privilegeVO.setKpt(kpt);

		privilegeVO.setSourcing_reports(sourcing_reports);
		privilegeVO.setUs_reports(us_reports);
		privilegeVO.setDashboard(dashboard);

		privilegeVO.setDashboard(dashboard);
		privilegeVO.setSearch(search);
		privilegeVO.setMassmailing(massmailing);
		privilegeVO.setH1transfer(h1transfer);
		privilegeVO.setPresales(presales);
		privilegeVO.setOpen_reqs_job_application(open_reqs_job_application);
		privilegeVO.setTalentpool(talentpool);
		privilegeVO.setVc_cx_profiles(vc_cx_profiles);
		privilegeVO.setVms(vms);
		privilegeVO.setSales(sales);
		privilegeVO.setRecruitment(recruitment);
		privilegeVO.setDom_recruitment(dom_recruitment);
		privilegeVO.setTalent_acquisition(talent_acquisition);
		privilegeVO.setPeople(people);
		privilegeVO.setMasters(masters);
		privilegeVO.setBillpay(billpay);
		privilegeVO.setSourcing(sourcing);
		privilegeVO.setOnboarding(onboarding);
		privilegeVO.setOpen_requirements(open_requirements);
		privilegeVO.setReports(reports);

		privilegeVO.setProjects(projects);
		privilegeVO.setTcvr(tcvr);
		privilegeVO.setView_employee_profile(view_employee_profile);
		privilegeVO.setEmployee_job_requirements(employee_job_requirements);
		privilegeVO.setEmployee_applied_jobs(employee_applied_jobs);

		privilegeVO.setEmployee_submissions(employee_submissions);
		privilegeVO.setEmployee_interviews(employee_interviews);
		privilegeVO.setEmployee_report(employee_report);
		privilegeVO.setExcel_export(excel_export);

		privilegeVO.setDocsynch(docsynch);
		privilegeVO.setRatings(ratings);

		privilegeVO.setRatings(othercompaniessuperadmin);

		return privilegeVO;
	}

//	@Override
//	public Privilege updatePrivileges(List<CompanyPrivilegesDto> companyDtos) {
//		Privilege priv = new Privilege();
//
//		for (CompanyPrivilegesDto dto : companyDtos) {
//			Long privilegeId = dto.getPrivilegeId(); // ✅ You get it here
//			Optional<Privilege> optional = privRepo.findById(privilegeId);

//			if (optional.isPresent()) {
//				priv = optional.get();
//
//				// Remove old mappings for that cardId
////	            priv.getSelectedCompanies().removeIf(mapping -> mapping.getCardId().equals(dto.getCardId()));
//
//				priv.getSelectedCompanies().removeIf(
//						mapping -> mapping.getCardId() != null && mapping.getCardId().equals(dto.getCardId()));
//
//				// Add new mappings
//				List<PrivilegeCompanyCardMapping> newMappings = dto.getSelectedCompanies().stream()
//						.map(companyId -> new PrivilegeCompanyCardMapping(dto.getCardId(), companyId))
//						.collect(Collectors.toList());
//
//				priv.getSelectedCompanies().addAll(newMappings);
//
//				privRepo.save(priv);
//			}
//		}
//		return priv;
//	}

	@Override
	public void addPrivilegeToRole(RoleToPrivilegesVO rolesPrivileges){
		Roles role = roleRepo.findByCompanyidAndRoleid(rolesPrivileges.getCompanyId(),rolesPrivileges.getRoleId());
		
		Set<Privilege> allPrevPrivileges = new HashSet<Privilege>();
		Privilege privlig = null;
		for (Long privilegeId : rolesPrivileges.getPrivilegeIds()) {
			Optional<Privilege> priv = privRepo.findById(privilegeId);
			privlig = priv.get();
			allPrevPrivileges.add(privlig);
		}
		role.setPrivileges(allPrevPrivileges);

		roleRepo.save(role);
	}



//	@Override
//	public List<PrivilegeCompanyCardProjection> getCardsForGivenCompany(Long company) {
//
//		return privRepo.findPrivilegeIdsByCompanyId(company);
//	}
//
//	@Override
//	public List<PrivilegeCompanyCardProjection> fetchAllSelectedCompanies() {
//		return privRepo.findAllSelectedCompanies();
//	}

	@Override
	public PrivilegeVO getPrivilegesByUsingRoleIdAndCompany(Long roleId, String compnyId) {

		PrivilegeVO privilegeVO = new PrivilegeVO();

		Long compnayId = null;
		try {
			compnayId = EncryptionUtil.decrypt(compnyId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<DropdownVO> users = new ArrayList<DropdownVO>();
		List<DropdownVO> vendor = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiter = new ArrayList<DropdownVO>();
		List<DropdownVO> technology_tags = new ArrayList<DropdownVO>();
		List<DropdownVO> tech_support = new ArrayList<DropdownVO>();
		List<DropdownVO> immigration = new ArrayList<DropdownVO>();

		List<DropdownVO> requirement = new ArrayList<DropdownVO>();

		List<DropdownVO> sales_consultants = new ArrayList<DropdownVO>();
		List<DropdownVO> sale_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> sales_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> sales_closures = new ArrayList<DropdownVO>();

		List<DropdownVO> recruiting_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_consultants = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> recruiting_closures = new ArrayList<DropdownVO>();

		List<DropdownVO> dom_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_consultants = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_closures = new ArrayList<DropdownVO>();

		List<DropdownVO> visa = new ArrayList<DropdownVO>();
		List<DropdownVO> qualification = new ArrayList<DropdownVO>();
		List<DropdownVO> kpt = new ArrayList<DropdownVO>();
		List<DropdownVO> taskmanagement = new ArrayList<DropdownVO>();
		List<DropdownVO> role = new ArrayList<DropdownVO>();
		List<DropdownVO> company = new ArrayList<DropdownVO>();

		List<DropdownVO> sourcing_reports = new ArrayList<DropdownVO>();
		List<DropdownVO> us_reports = new ArrayList<DropdownVO>();
		List<DropdownVO> dashboard = new ArrayList<DropdownVO>();

		List<DropdownVO> search = new ArrayList<DropdownVO>();
		List<DropdownVO> massmailing = new ArrayList<DropdownVO>();
		List<DropdownVO> h1transfer = new ArrayList<DropdownVO>();
		List<DropdownVO> presales = new ArrayList<DropdownVO>();
		List<DropdownVO> open_reqs_job_application = new ArrayList<DropdownVO>();
		List<DropdownVO> talentpool = new ArrayList<DropdownVO>();
		List<DropdownVO> vc_cx_profiles = new ArrayList<DropdownVO>();

		List<DropdownVO> vms = new ArrayList<DropdownVO>();
		List<DropdownVO> sales = new ArrayList<DropdownVO>();
		List<DropdownVO> recruitment = new ArrayList<DropdownVO>();
		List<DropdownVO> dom_recruitment = new ArrayList<DropdownVO>();
		List<DropdownVO> talent_acquisition = new ArrayList<DropdownVO>();
		List<DropdownVO> people = new ArrayList<DropdownVO>();
		List<DropdownVO> masters = new ArrayList<DropdownVO>();
		List<DropdownVO> billpay = new ArrayList<DropdownVO>();
		List<DropdownVO> sourcing = new ArrayList<DropdownVO>();
		List<DropdownVO> onboarding = new ArrayList<DropdownVO>();
		List<DropdownVO> open_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> reports = new ArrayList<DropdownVO>();

		List<DropdownVO> projects = new ArrayList<DropdownVO>();
		List<DropdownVO> tcvr = new ArrayList<DropdownVO>();
		List<DropdownVO> view_employee_profile = new ArrayList<DropdownVO>();

		List<DropdownVO> employee_job_requirements = new ArrayList<DropdownVO>();
		List<DropdownVO> employee_applied_jobs = new ArrayList<DropdownVO>();

		List<DropdownVO> docsynch = new ArrayList<DropdownVO>();

		List<DropdownVO> employee_submissions = new ArrayList<DropdownVO>();
		List<DropdownVO> employee_interviews = new ArrayList<DropdownVO>();
		List<DropdownVO> employee_report = new ArrayList<DropdownVO>();
		List<DropdownVO> excel_export = new ArrayList<DropdownVO>();
		List<DropdownVO> ratings = new ArrayList<DropdownVO>();

		List<DropdownVO> othercompaniessuperadmin = new ArrayList<DropdownVO>();

		List<Long> selectedPrivs = roleRepo.findPrivilegesAssignedByGivenRoleAndCompany(roleId, compnayId);

		Set<Privilege> privilegesTableData = new HashSet<>();

		for (Long selectedId : selectedPrivs) {
			privilegesTableData.add(privRepo.findById(selectedId).get());
		}

		boolean flg = false;

		for (Privilege single : privilegesTableData) {

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.ROLE.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				role.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.REQUIREMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				requirement.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.KPT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				kpt.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VENDOR.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				vendor.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITER.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruiter.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TECH_SUPPORT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				tech_support.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TECHNOLOGY_TAG.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				technology_tags.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.IMMIGRATION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				immigration.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.USER.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				users.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_CONSULTANT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				sales_consultants.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_SUBMISSION.name())) {

				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				sale_submissions.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_INTERVIEW.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sales_interviews.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES_CLOSURES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sales_closures.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_REQUIREMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				recruiting_requirements
						.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_CONSULTANT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				recruiting_consultants.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_SUBMISSION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruiting_submissions.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_INTERVIEW.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruiting_interviews.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITING_CLOSURES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruiting_closures.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_REQUIREMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_requirements.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_CONSULTANT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_consultants.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_SUBMISSION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_submissions.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_INTERVIEW.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_interviews.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_CLOSURES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_closures.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VISA.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				visa.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.QUALIFICATION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				qualification.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TASKMANAGEMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				taskmanagement.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.COMPANY.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}

				company.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SOURCING_REPORTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sourcing_reports.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.US_REPORTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				us_reports.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DASHBOARD.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dashboard.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SEARCH.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				search.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.MASS_MAILING.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				massmailing.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.H_TRANSFER.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				h1transfer.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.PRESALES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				presales.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.OPEN_REQS_JOB_APPLICATION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				open_reqs_job_application
						.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TALENT_POOL.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				talentpool.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VC_CX_PROFILES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				vc_cx_profiles.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.VMS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				vms.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SALES.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sales.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RECRUITMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				recruitment.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOM_RECRUITMENT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				dom_recruitment.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TALENT_ACQUISITION.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				talent_acquisition.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.PEOPLE.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				people.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.MASTERS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				masters.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.BILLPAY.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				billpay.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.SOURCING.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				sourcing.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.ONBOARDING.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				onboarding.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.OPEN_REQUIREMENTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				open_requirements.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.REPORTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				reports.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.PROJECTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				projects.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.TCVR.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				tcvr.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_VIEW_PROFILE.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				view_employee_profile.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_JOB_REQUIREMENTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_job_requirements
						.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_APPLIED_JOBS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_applied_jobs.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_SUBMISSIONS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_submissions.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_INTERVIEWS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_interviews.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.EMPLOYEE_LOGIN_REPORT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				employee_report.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.EXCEL_EXPORT.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				excel_export.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.DOCSYNCH.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				docsynch.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.Privilege.privilegeType.RATINGS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				docsynch.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(
					com.narvee.ats.auth.entity.Privilege.privilegeType.OTHERCOMPANIESSUPERADMIN.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				othercompaniessuperadmin
						.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

		}

		privilegeVO.setVendor(vendor);
		privilegeVO.setRecruiter(recruiter);
		privilegeVO.setTech_support(tech_support);
		privilegeVO.setTechnology_tag(technology_tags);
		privilegeVO.setImmigration(immigration);
		privilegeVO.setUser(users);

		privilegeVO.setSales_consultant(sales_consultants);
		privilegeVO.setSales_submission(sale_submissions);
		privilegeVO.setSales_interview(sales_interviews);
		privilegeVO.setSales_closures(sales_closures);

		privilegeVO.setRecruiting_requirement(recruiting_requirements);
		privilegeVO.setRecruiting_consultant(recruiting_consultants);
		privilegeVO.setRecruiting_submission(recruiting_submissions);
		privilegeVO.setRecruiting_interview(recruiting_interviews);
		privilegeVO.setRecruiting_closures(recruiting_closures);

		privilegeVO.setDom_requirement(dom_requirements);
		privilegeVO.setDom_consultant(dom_consultants);
		privilegeVO.setDom_submission(dom_submissions);
		privilegeVO.setDom_interview(dom_interviews);
		privilegeVO.setDom_closures(dom_closures);

		privilegeVO.setVisa(visa);
		privilegeVO.setQualification(qualification);
		privilegeVO.setTaskmanagement(taskmanagement);
		privilegeVO.setRole(role);
		privilegeVO.setCompany(company);
		privilegeVO.setRequirement(requirement);
		privilegeVO.setKpt(kpt);

		privilegeVO.setSourcing_reports(sourcing_reports);
		privilegeVO.setUs_reports(us_reports);
		privilegeVO.setDashboard(dashboard);

		privilegeVO.setDashboard(dashboard);
		privilegeVO.setSearch(search);
		privilegeVO.setMassmailing(massmailing);
		privilegeVO.setH1transfer(h1transfer);
		privilegeVO.setPresales(presales);
		privilegeVO.setOpen_reqs_job_application(open_reqs_job_application);
		privilegeVO.setTalentpool(talentpool);
		privilegeVO.setVc_cx_profiles(vc_cx_profiles);
		privilegeVO.setVms(vms);
		privilegeVO.setSales(sales);
		privilegeVO.setRecruitment(recruitment);
		privilegeVO.setDom_recruitment(dom_recruitment);
		privilegeVO.setTalent_acquisition(talent_acquisition);
		privilegeVO.setPeople(people);
		privilegeVO.setMasters(masters);
		privilegeVO.setBillpay(billpay);
		privilegeVO.setSourcing(sourcing);
		privilegeVO.setOnboarding(onboarding);
		privilegeVO.setOpen_requirements(open_requirements);
		privilegeVO.setReports(reports);

		privilegeVO.setProjects(projects);
		privilegeVO.setTcvr(tcvr);
		privilegeVO.setView_employee_profile(view_employee_profile);
		privilegeVO.setEmployee_job_requirements(employee_job_requirements);
		privilegeVO.setEmployee_applied_jobs(employee_applied_jobs);

		privilegeVO.setEmployee_submissions(employee_submissions);
		privilegeVO.setEmployee_interviews(employee_interviews);
		privilegeVO.setEmployee_report(employee_report);
		privilegeVO.setExcel_export(excel_export);

		privilegeVO.setDocsynch(docsynch);
		privilegeVO.setRatings(ratings);

		privilegeVO.setRatings(othercompaniessuperadmin);

		return privilegeVO;
	}

}
