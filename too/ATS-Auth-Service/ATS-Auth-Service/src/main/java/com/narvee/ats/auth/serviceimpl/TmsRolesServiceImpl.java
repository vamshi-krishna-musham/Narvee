package com.narvee.ats.auth.serviceimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.client.RequestDTO;
import com.narvee.ats.auth.dto.RequestDto;
import com.narvee.ats.auth.dto.RolesResponseDto;
import com.narvee.ats.auth.entity.TmsRoles;
import com.narvee.ats.auth.repository.TmsRolesRepository;
import com.narvee.ats.auth.service.TmsRolesSerivice;

@Service
public class TmsRolesServiceImpl implements TmsRolesSerivice {
	public static final Logger logger = LoggerFactory.getLogger(TmsRolesServiceImpl.class);

	@Autowired
	private TmsRolesRepository rolesRepository;

	@Override
	public Page<RolesResponseDto> getAllRole(RequestDto  admRequestDto) {
		logger.info("!!! inside class: TmsRolesServiceImpl, !! method: getAllRoles()");
	  String sortField = 	admRequestDto.getSortField();
		Pageable pageable = PageRequest.of(admRequestDto.getPageNo() - 1, admRequestDto.getPageSize());

		if (sortField.equalsIgnoreCase("rolename"))
			sortField = "rolename";
		else if  (sortField.equalsIgnoreCase("status"))
			sortField = "status";
		else if  (sortField.equalsIgnoreCase("admin_id"))
			sortField = "admin_id";
		else if  (sortField.equalsIgnoreCase("createddate"))
			sortField = "createddate";
		else if  (sortField.equalsIgnoreCase("addedby"))
			sortField = "addedby";
		else if (sortField.equalsIgnoreCase("description"))
			sortField = "description";
		else
			sortField = "updateddate";

		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (admRequestDto.getSortOrder() != null && admRequestDto.getSortOrder().equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortField);
		pageable = PageRequest.of(admRequestDto.getPageNo() - 1, admRequestDto.getPageSize(), sort);

		if(admRequestDto.getKeyword().equalsIgnoreCase("empty")) {
		   return rolesRepository.findByAdminId(admRequestDto.getAdminId(),pageable);
		}
		return rolesRepository.findByAdminIdAndKeyword(admRequestDto.getAdminId(),pageable,admRequestDto.getKeyword());
	}

	@Override
	public boolean deleteRole(Long id) {
		logger.info("!!! inside class: TmsRolesServiceImpl, !! method: deleteRole()");
		try {
		rolesRepository.deleteById(id);
		return true;
		}catch(Exception e ) {
			return false;
		}
		
	}

	@Override
	public boolean updateRole(TmsRoles role) {
		logger.info("!!! inside class: TmsRolesServiceImpl, !! method: updateRole()");
		List<TmsRoles> roles = rolesRepository.findByRoleid(role.getRoleid());
		if ((roles != null||!(roles.isEmpty()))) {
			logger.info("Role saved after checking duplicate records available or not"); 	
			rolesRepository.save(role);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean saveRole(TmsRoles role) {
		logger.info("!!! inside class: TmsRolesServiceImpl, !! method: saveRole()");
	//	List<TmsRoles> finaAllRolByRolName = rolesRepository.findByRolename(role.getRolename());
		
		TmsRoles finaAllRolByRolName = rolesRepository.findByRolenameAndAdminId(role.getRolename(),role.getAdminId());
		logger.info("!!! inside class: TmsRolesServiceImpl, !! method: saveRole()"+finaAllRolByRolName);
		if (finaAllRolByRolName == null) {

			logger.info("Role saved after checking duplicate records available or not");
			rolesRepository.save(role);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<TmsRoles> getAllRoleByAdmin(Long adminId) {
		logger.info("!!! inside class: TmsRolesServiceImpl, !! method: getAllRoleByAdmin()");
		return rolesRepository.findRoleByAdminId(adminId);
	}

}
