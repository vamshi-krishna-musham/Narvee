package com.narvee.ats.auth.service;

import com.narvee.ats.auth.entity.TmsPrivilege;
import com.narvee.ats.auth.request.RoleToPrivilegesVO;
import com.narvee.ats.auth.request.TmsPrivilegeVO;


public interface TmsPrivilegeService {

	TmsPrivilegeVO getAllPrivileges();

	TmsPrivilegeVO getPrivilegesById(Long roleId);

	void savePrevileges(TmsPrivilege previleges);

	void addPrivilegeToRole(RoleToPrivilegesVO previleges);

}
