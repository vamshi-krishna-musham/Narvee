package com.narvee.ats.auth.service;

import java.util.List;

import com.narvee.ats.auth.dto.GetRoles;
import com.narvee.ats.auth.entity.Roles;

public interface IRoleService {
	public boolean saveRole(Roles role);
	public List<Roles> getAllRoles();
	public List<Roles> getAllRolesCompanyWise(String company) throws Exception;
	public Roles getRole(Long id);
	public boolean updateRole(Roles role);
	public int changeStatus(String status, Long id, String rem);
	public boolean deleteRoleCompanyWise(Long id);
	public List<GetRoles> getRoles(Long cid);
	public List<Roles> getAllRolesByCompany(Long companyId);
	public boolean deleteRole(Long id);
}
