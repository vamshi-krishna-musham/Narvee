package com.narvee.ats.auth.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.dto.CompanyDTO;
import com.narvee.ats.auth.dto.CompanyDropDown;
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.entity.Company;
import com.narvee.ats.auth.repository.ICompanyRepository;
import com.narvee.ats.auth.service.ICompanyService;
import com.narvee.ats.auth.util.EncryptionUtil;
import com.narvee.ats.auth.util.JwtUtil;

@Service
public class CompanyServiceImpl implements ICompanyService {
	public static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);
	@Autowired
	private ICompanyRepository repository;

	@Override
	public Company saveCompany(Company company) {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: saveCompany ");
		Optional<Company> entity = null;
		if (company.getCompanyid() == null) {
			entity = repository.findByCompanyname(company.getCompanyname());
		} else {
			entity = repository.findByCompanynameAndCompanyidNot(company.getCompanyname(), company.getCompanyid());
		}
		if (!entity.isPresent()) {
			repository.save(company);
			return company;
		}
		return company;
	}

	@Override
	public List<Company> getAllCompany() {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: getAllCompany ");
		return repository.findAll();
	}

//	@Override
//	public List<CompanyDropDown> getCompanyDropDown() {
//		logger.info("!!! inside class: CompanyServiceImpl, !! method: getAllCompany");
//
//		return repository.findAll().stream().map(company -> {
//			try {
//				return new CompanyDropDown(EncryptionUtil.encrypt(company.getCompanyid()), company.getCompanyname());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//		}).collect(Collectors.toList());
//	}

	@Override
	public Company getCompanyByID(Long id) {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: getCompanyByID ");
		Optional<Company> company = repository.findById(id);
		if (company.isPresent()) {
			return company.get();
		}
		return null;
	}

	@Override
	public Company updateCompany(Company company) {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: updateCompany ");
		Company save = repository.save(company);
		return save;
	}

	@Override
	public boolean deleteCompanyByID(Long id) {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: deleteCompanyByID ");

		Integer isExists = repository.checkCompanyDataExists(id);
		if (isExists==1) {
			return false;
		} else {
			repository.deleteById(id);
			return true;
		}

	}

	@Override
	public List<Object[]> getcompanies(String token) {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: getcompanies ");
		List<Long> assignedCompanies= JwtUtil.getAssociatedCompanyIds(token);
		return repository.getcompanies(assignedCompanies);
	}

	
	@Override
	public Page<CompanyDTO> getAllCompaniesWithSortingAndFiltering(SortingRequestDTO companysortandfilterdto) {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: fetchAllCompanyWithSortingAndFiltering ");
		String sortField = companysortandfilterdto.getSortField();
		String sortOrder = companysortandfilterdto.getSortOrder();
		int pageNo = companysortandfilterdto.getPageNumber();
		int pageSize = companysortandfilterdto.getPageSize();

		if (sortField.equalsIgnoreCase("company")) {
			sortField = "companyname";
		} else if (sortField.equalsIgnoreCase("domain")) {
			sortField = "domain";

		} else if (sortField.equalsIgnoreCase("code")) {
			sortField = "code";

		} else if (sortField.equalsIgnoreCase("addedby")) {
			sortField = "pseudoname";
		}

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortField);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		if (companysortandfilterdto.getKeyword().equalsIgnoreCase("empty")) {
			logger.info(
					"!!! inside class: CompanyServiceImpl, !! method: fetchAllCompanyWithSortingAndFiltering , empty");

			return repository.getAllCompaniesnWithOnlySorting(pageable);
		} else {
			logger.info(
					"!!! inside class: CompanyServiceImpl, !! method: fetchAllCompanyWithSortingAndFiltering , filter");
			return repository.getAllCompaniesnWithSortingAndFiltering(pageable, companysortandfilterdto.getKeyword());
		}

	}

	@Override
	public boolean duplicateCheck(String fieldName, String input) {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: duplicateCheck , filter");
		boolean duplicate = false;
		if (fieldName.equalsIgnoreCase("code")) {
			Company code = repository.findByCode(input);
			if (code != null) {
				duplicate = true;
			} else {
				duplicate = false;
			}

		} else if (fieldName.equalsIgnoreCase("domain")) {
			Company code = repository.findByDomain(input);
			if (code != null) {
				duplicate = true;
			} else {
				duplicate = false;
			}

		} else if (fieldName.equalsIgnoreCase("companyname")) {
			Optional<Company> company = repository.findByCompanyname(input);
			if (company.isPresent()) {
				duplicate = true;
			} else {
				duplicate = false;
			}
		}
		return duplicate;
	}

	@Override
	public List<CompanyDropDown> getCompanyDropDown() {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: getAllCompany");

		return repository.findAll().stream().map(company -> {
			try {
				return new CompanyDropDown(EncryptionUtil.encrypt(company.getCompanyid()), company.getCompanyname());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());
	}

	@Override
	public List<Object[]> getCompaniesDropdown() {
		logger.info("!!! inside class: CompanyServiceImpl, !! method: getCompaniesDropdown");
		return repository.getCompaniesDropdown();
	}

}
