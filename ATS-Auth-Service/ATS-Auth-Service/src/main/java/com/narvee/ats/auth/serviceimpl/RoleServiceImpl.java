package com.narvee.ats.auth.serviceimpl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.dto.GetRoles;
import com.narvee.ats.auth.entity.Roles;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.repository.IRoleRepository;
import com.narvee.ats.auth.repository.IUserRepository;
import com.narvee.ats.auth.service.IRoleService;
import com.narvee.ats.auth.util.EncryptionUtil;

@Transactional
@Service
public class RoleServiceImpl implements IRoleService {
	public static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
	@Autowired
	private IRoleRepository iRoleRepo;
	@Autowired
	private IUserRepository userRepo;

	@Override
	public boolean saveRole(Roles role) {
		
		logger.info("RoleServiceImpl.saveRole()");
		
		List<String> finaAllRolByRolName = iRoleRepo.findforGiven(role.getRolename().toLowerCase(), role.getCompanyid());
//		List<String> finaAllRolByRolName = iRoleRepo.findRolByRolName(role.getRolename().toLowerCase());
		if ((finaAllRolByRolName == null || finaAllRolByRolName.isEmpty())) {
			logger.info("Role saved after checking duplicate records available or not"); 
			 iRoleRepo.save(role);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Roles> getAllRoles() {
		logger.info("RoleServiceImpl.saveRole()");
		return iRoleRepo.findAll();
	}

	@Override
	public Roles getRole(Long id) {
		logger.info("RoleServiceImpl.getRole by id()=> " + id);
		return iRoleRepo.findById(id).get();
	}

	@Override
	public int changeStatus(String status, Long id, String rem) {
		logger.info("RoleServiceImpl.changeStatus()status " + status + " id  =>" + id + " remarks =>" + rem);
		return iRoleRepo.toggleStatus(status, id, rem);
	}

	@Override
	public boolean deleteRole(Long id) {
		logger.info("RoleServiceImpl.deleteRole() by id => " + id);
		Users user = userRepo.findByRoleRoleid(id);
		if (user == null) {
			iRoleRepo.deleteById(id);
			return true;
		} else {
			return false;
		}
	}

	public boolean updateRole(Roles role) {
		logger.info("RoleServiceImpl.saveRole()");
		 iRoleRepo.findRolByRolName(role.getRolename().toLowerCase());
		Optional<Roles> roles = iRoleRepo.findByRolenameAndRoleidNot(role.getRolename(), role.getRoleid());
		if ((roles == null || !roles.isPresent())) {
			logger.info("Role saved after checking duplicate records available or not");
			
			 iRoleRepo.save(role);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public List<GetRoles> getRoles(Long cid) {
		logger.info("RoleServiceImpl.getRoles()");
		return iRoleRepo.getAllRoles(cid);
	}

	@Override
	public List<Roles> getAllRolesCompanyWise(String company) throws Exception {
		logger.info("RoleServiceImpl.list of Roles()");
		
		Long companyId=EncryptionUtil.decrypt(company);
		return iRoleRepo.findAllRolesCompanyWise(companyId);
	}

	@Override
	public boolean deleteRoleCompanyWise(Long id) {
		iRoleRepo.deleteById(id);
			return true;
		
	}

	@Override
	public List<Roles> getAllRolesByCompany(Long companyId) {
		logger.info("RoleServiceImpl.getAllRolesByCompany()");
	return  iRoleRepo.findRolesByCompanyId(companyId);
	}

}
