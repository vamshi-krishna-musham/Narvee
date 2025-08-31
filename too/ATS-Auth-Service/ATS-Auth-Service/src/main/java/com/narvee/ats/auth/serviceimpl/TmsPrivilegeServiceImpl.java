package com.narvee.ats.auth.serviceimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.entity.TmsPrivilege;
import com.narvee.ats.auth.entity.TmsRoles;
import com.narvee.ats.auth.repository.TmsPrivilegeRepository;
import com.narvee.ats.auth.repository.TmsRolesRepository;
import com.narvee.ats.auth.request.DropdownVO;
import com.narvee.ats.auth.request.RoleToPrivilegesVO;
import com.narvee.ats.auth.request.TmsPrivilegeVO;
import com.narvee.ats.auth.service.TmsPrivilegeService;

@Service
public class TmsPrivilegeServiceImpl implements TmsPrivilegeService {
	public static final Logger logger = LoggerFactory.getLogger(TmsPrivilegeServiceImpl.class);

	@Autowired
	private TmsPrivilegeRepository privilegeRepository;

	@Autowired
	private TmsRolesRepository rolesRepository;

	@Override
	public TmsPrivilegeVO getAllPrivileges() {
		logger.info("!!! inside class: TmsPrivilegeServiceImpl , !! method: getAllPrivileges");

		TmsPrivilegeVO privilegeVO = new TmsPrivilegeVO();

		List<DropdownVO> teamMember = new ArrayList<DropdownVO>();
		List<DropdownVO> tasks = new ArrayList<DropdownVO>();
		List<DropdownVO> subTasks = new ArrayList<DropdownVO>();
		List<DropdownVO> projects = new ArrayList<DropdownVO>();

		List<TmsPrivilege> privileges = privilegeRepository.findAll();

		for (TmsPrivilege singlePrivilege : privileges) {

			// TEAM_MEMBERS
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.TmsPrivilege.privilegeType.TEAM_MEMBERS.name())) {
				teamMember.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setTeamMember(teamMember);
			}

			// PROJECTS
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.TmsPrivilege.privilegeType.PROJECTS.name())) {
				projects.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setProjects(projects);
				;
			}

			// TASKS
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.TmsPrivilege.privilegeType.TASKS.name())) {
				tasks.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setTasks(tasks);
				;
			}

			// SUB_TASKS
			if (singlePrivilege.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.TmsPrivilege.privilegeType.SUB_TASKS.name())) {
				subTasks.add(new DropdownVO(singlePrivilege.getId(), singlePrivilege.getName(),
						singlePrivilege.getCardType()));
				privilegeVO.setSubTasks(subTasks);
				;
			}

		}

		return privilegeVO;
	}

	@Override
	public TmsPrivilegeVO getPrivilegesById(Long roleId) {
		logger.info("!!! inside class: TmsPrivilegeServiceImpl , !! method: getPrivilegesById");

		TmsPrivilegeVO privilegeVO = new TmsPrivilegeVO();
		TmsRoles roles = rolesRepository.findById(roleId).get();

		List<DropdownVO> teamMembers = new ArrayList<DropdownVO>();
		List<DropdownVO> projects = new ArrayList<DropdownVO>();
		List<DropdownVO> tasks = new ArrayList<DropdownVO>();
		List<DropdownVO> subTasks = new ArrayList<DropdownVO>();

		Set<TmsPrivilege> privileges = roles.getPrivileges();

		boolean flg = false;

		for (TmsPrivilege single : privileges) {

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.TmsPrivilege.privilegeType.TEAM_MEMBERS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				teamMembers.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.TmsPrivilege.privilegeType.PROJECTS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				projects.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType().equalsIgnoreCase(com.narvee.ats.auth.entity.TmsPrivilege.privilegeType.TASKS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				tasks.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}

			if (single.getType()
					.equalsIgnoreCase(com.narvee.ats.auth.entity.TmsPrivilege.privilegeType.SUB_TASKS.name())) {
				if (single.getId() != null || single.getId() != 0) {
					flg = true;
				}
				subTasks.add(new DropdownVO(single.getId(), single.getName(), flg, single.getCardType()));
			}
		}

		privilegeVO.setTeamMember(teamMembers);
		privilegeVO.setProjects(projects);
		privilegeVO.setTasks(tasks);
		privilegeVO.setSubTasks(subTasks);

		return privilegeVO;
	}

	@Override
	public void savePrevileges(TmsPrivilege previleges) {
		logger.info("!!! inside class: TmsPrivilegeServiceImpl , !! method: savePrevileges");
		privilegeRepository.save(previleges);

	}

	@Override
	public void addPrivilegeToRole(RoleToPrivilegesVO rolesPrivileges) {
		logger.info("!!! inside class: TmsPrivilegeServiceImpl , !! method: addPrivilegeToRole");

		TmsRoles role = rolesRepository.findById(rolesPrivileges.getRoleId()).get();
		Set<TmsPrivilege> allPrevPrivileges = new HashSet<TmsPrivilege>();
		TmsPrivilege privlig = null;
		for (Long privilegeId : rolesPrivileges.getPrivilegeIds()) {
			Optional<TmsPrivilege> priv = privilegeRepository.findById(privilegeId);
			privlig = priv.get();
			allPrevPrivileges.add(privlig);
		}
		role.setPrivileges(allPrevPrivileges);

		rolesRepository.save(role);
	}

}
