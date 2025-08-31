package com.narvee.ats.auth.service;

import java.util.List;

import com.narvee.ats.auth.entity.Privilege;
import com.narvee.ats.auth.request.PrivilegeVO;
import com.narvee.ats.auth.request.RoleToPrivilegesVO;


public interface IPrivilegeService {

	public PrivilegeVO getAllPrivileges();

	public Privilege savePrevileges(Privilege privilege);

	public List<Privilege> allprev();

	public void addPrivilegeToRole(RoleToPrivilegesVO rolesPrivileges) throws Exception;

	public PrivilegeVO getPrivilegesByUsingRoleIdAndCompany(Long roleId, String company);

	public PrivilegeVO getPrivilegesById(Long roleId, Long companyId);


}
