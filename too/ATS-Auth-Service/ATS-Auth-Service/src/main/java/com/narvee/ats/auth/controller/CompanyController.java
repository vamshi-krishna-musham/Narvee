package com.narvee.ats.auth.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.narvee.ats.auth.commons.RestAPIResponse;
import com.narvee.ats.auth.dto.CompanyDropDown;
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.entity.AssociatedCompanys;
import com.narvee.ats.auth.entity.Company;
import com.narvee.ats.auth.entity.Privilege;
import com.narvee.ats.auth.entity.Roles;
import com.narvee.ats.auth.repository.IRoleRepository;
import com.narvee.ats.auth.service.IAssociatedCompanyService;
import com.narvee.ats.auth.service.ICompanyService;

@RequestMapping("/company")
@RestController
@CrossOrigin
public class CompanyController {
	public static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

	@Autowired
	private ICompanyService service;

	@Autowired
	private IRoleRepository rolesRepo;

	@Autowired
	private IAssociatedCompanyService associatedCmpServ;

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> saveTimeSheet(@RequestBody Company company) {
		logger.info("!!! inside class: CompanyController, !! method: saveTimeSheet ");
		Company saveCompany = service.saveCompany(company);

		if (saveCompany != null) {
			List<Roles> roles = new ArrayList<>();
			String[] roleNames = { "Super Administrator", "Administrator", "Sales Manager", "Recruiting Manager",
					"Team Leader Sales", "Team Leader Recruiting", "Sales Executive", "Recruiter", "Guest",
					"OPT Recruiter", "Developer", "HR Manager", "Sourcing Manager", "Employee", "HR Generalist" };

			for (String roleName : roleNames) {
				Roles role = new Roles();
				role.setRolename(roleName);
				role.setAddedby(company.getAddedby());
				role.setCompanyid(saveCompany.getCompanyid());
				roles.add(role);
			}
			
			 Set<Privilege> privileges = new HashSet<>();
			 for (long i = 8; i <= 15; i++) {
			     Privilege p = new Privilege();
			     p.setId(i);
			     privileges.add(p);
			 }
			 Roles superadmin = roles.get(0);
			 superadmin.setPrivileges(privileges);

			 roles =rolesRepo.saveAll(roles);
			
			
			
			
			

			return new ResponseEntity<>(new RestAPIResponse("success", "saved Company Entity", "Entity Saved"),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("duplicate", "Record Already Exists", "Not Saved"),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> fetchAllCompany() {
		logger.info("!!! inside class: CompanyController, !! method: fetchAllCompany ");
		return new ResponseEntity<>(new RestAPIResponse("success", "getall TimeSheet Data", service.getAllCompany()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getCompanyByID(@PathVariable Long id) {
		logger.info("!!! inside class: CompanyController, !! method: getCompanyByID ");
		Company companyData = service.getCompanyByID(id);
		if (companyData != null) {
			return new ResponseEntity<>(new RestAPIResponse("success", "Company data Fetch SuccessFully", companyData),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("failed", "id is not present", null),
					HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<RestAPIResponse> updateCompany(@RequestBody Company company) {
		logger.info("!!! inside class: CompanyController, !! method: updateCompany ");
		Object obj = service.updateCompany(company);
		return new ResponseEntity<>(new RestAPIResponse("success", "update TimeSheet successfully", obj),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<RestAPIResponse> deleteCompanyById(@PathVariable Long id) {
		logger.info("!!! inside class: CompanyController, !! method: deleteCompanyById ");
		boolean val = service.deleteCompanyByID(id);
		if (val == true) {
			return new ResponseEntity<>(new RestAPIResponse("success", "Company Deleted", ""), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(
					new RestAPIResponse("fail", "Cannot delete the company because it has associated records.", ""),
					HttpStatus.OK);

		}
	}

	@RequestMapping(value = "/allcompanies", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Object[]>> getCompanys(@RequestHeader("AUTHORIZATION") String token) {
		logger.info("!!! inside class: CompanyController, !! method: getCompanys ");
		return new ResponseEntity<List<Object[]>>(service.getcompanies(token), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/getCompaniesDropdown", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Object[]>> getCompanyDropdownforAssociation() {
		logger.info("!!! inside class: CompanyController, !! method: getCompanyDropdownforAssociation ");
		return new ResponseEntity<List<Object[]>>(service.getCompaniesDropdown(), HttpStatus.OK);
	}

	@RequestMapping(value = "/allCompaniesWithSortandFilter", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> fetchAllCompanyWithSortingAndFiltering(
			@RequestBody SortingRequestDTO companysortandfilterdto) {
		logger.info("!!! inside class: CompanyController, !! method: fetchAllCompanyWithSortingAndFiltering ");
		return new ResponseEntity<>(new RestAPIResponse("success", "getall Company Data",
				service.getAllCompaniesWithSortingAndFiltering(companysortandfilterdto)), HttpStatus.OK);
	}

	@RequestMapping(value = "/duplicateCheck/{fieldName}/{input}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> duplicateCheck(@PathVariable String fieldName, @PathVariable String input) {
		logger.info("!!! inside class: CompanyController, !! method: duplicateCheck ");
		boolean duplicate = service.duplicateCheck(fieldName, input);
		if (duplicate) {
			return new ResponseEntity<>(new RestAPIResponse("fail", input + " is Already exists", duplicate),
					HttpStatus.OK);

		} else {

			return new ResponseEntity<>(new RestAPIResponse("success", input + " is available ", duplicate),
					HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/getCompanysDropDown", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getCompanysDropDown() {
		logger.info("!!! inside class: CompanyController, !! method: getCompanysDropDown ");

		List<CompanyDropDown> obj = service.getCompanyDropDown();
		return new ResponseEntity<>(new RestAPIResponse("success", "Companys DropDown Fetched successfully ", obj),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/saveAssociatedcompany", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<RestAPIResponse> saveAssociatedCompany(@RequestBody AssociatedCompanys assCompanys) {
		logger.info("!!! inside class: CompanyController, !! method: saveAssociatedCompany ");
		List<AssociatedCompanys> duplicateCheck = associatedCmpServ.findByAcidAndCompanyCompanyid(assCompanys.getAcid(),
				assCompanys.getCompany().getCompanyid());

		if (duplicateCheck == null || duplicateCheck.isEmpty()) {
			associatedCmpServ.addAssociatedCompany(assCompanys);
			return new ResponseEntity<>(new RestAPIResponse("success", "Associated company saved successfully.", "Saved"),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("fail", "Oops! It looks like this company is already added. Please enter a different one.", "Failed"),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/deleteAssociatedCompany/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<RestAPIResponse> deleteAssociatedCompanyById(@PathVariable Long id) {

		logger.info("!!! inside class: CompanyController, !! method: deleteAssociatedCompanyById ");
		associatedCmpServ.deleteAssociatedCompanyUsingID(id);
		return new ResponseEntity<>(new RestAPIResponse("success", "deleted sucessfuly", "deleted"), HttpStatus.OK);
	}

	@RequestMapping(value = "/getAssociatedCompanyByid/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAssociatedCompanyByID(@PathVariable Long id) {

		logger.info("!!! inside class: CompanyController, !! method: getAssociatedCompanyByID ");

		List<Long> companyData = associatedCmpServ.findByAssociatedCompanyId(id);

		if (companyData != null) {
			return new ResponseEntity<>(
					new RestAPIResponse("success", "Associated Company data Fetch SuccessFully", companyData),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RestAPIResponse("failed", "id is not present", null),
					HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/allAssociatedCompanies", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<RestAPIResponse> getAllAssociatedCompanies() {
		logger.info("!!! inside class: CompanyController, !! method: getAllAssociatedCompanies ");

		return new ResponseEntity<>(new RestAPIResponse("success", "Associated companies fetched successfully",
				associatedCmpServ.getAllAssociatedCompany()), HttpStatus.OK);
	}

}