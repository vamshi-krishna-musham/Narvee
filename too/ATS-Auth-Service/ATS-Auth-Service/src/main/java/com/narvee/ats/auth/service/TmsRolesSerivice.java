package com.narvee.ats.auth.service;


import java.util.List;

import org.springframework.data.domain.Page;

import com.narvee.ats.auth.dto.RequestDto;
import com.narvee.ats.auth.dto.RolesResponseDto;
import com.narvee.ats.auth.entity.TmsRoles;


public interface TmsRolesSerivice {

	boolean updateRole(TmsRoles roles);

	boolean deleteRole(Long id);

	boolean saveRole(TmsRoles roles);

	Page<RolesResponseDto> getAllRole(RequestDto  admRequestDto);
	
	List<TmsRoles> getAllRoleByAdmin (Long adminId);

}
