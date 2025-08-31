package com.narvee.ats.auth.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.narvee.ats.auth.dto.CompanyDTO;
import com.narvee.ats.auth.dto.CompanyDropDown;
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.entity.Company;


public interface ICompanyService {

	public Company saveCompany(Company company);

	public List<Company> getAllCompany();

	public Company getCompanyByID(Long id);

	public Company updateCompany(Company company);

	public boolean deleteCompanyByID(Long id);

	public List<Object[]> getcompanies(String token);
	
	public Page<CompanyDTO> getAllCompaniesWithSortingAndFiltering(SortingRequestDTO companysortandfilterdto);

	public boolean duplicateCheck(String fieldName, String input);

	public List<CompanyDropDown> getCompanyDropDown();

	public List<Object[]> getCompaniesDropdown();
}