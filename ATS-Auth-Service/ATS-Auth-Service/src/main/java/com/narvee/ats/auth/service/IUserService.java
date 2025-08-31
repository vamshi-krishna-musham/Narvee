package com.narvee.ats.auth.service;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import com.narvee.ats.auth.dto.EmpHierarchy;
import com.narvee.ats.auth.dto.GetLastLogout;
import com.narvee.ats.auth.dto.GetRecruiter;
import com.narvee.ats.auth.dto.ManagerDTO;
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.dto.UpdateProfileRequestDTO;
import com.narvee.ats.auth.dto.UserDTO;
import com.narvee.ats.auth.dto.UserInfoDTO;
import com.narvee.ats.auth.entity.EmployeeDocuments;
import com.narvee.ats.auth.entity.Users;

public interface IUserService {
	public Users saveUser(Users users, String token);

	public List<Object[]> managerDropDown();
	
	public List<UserDTO> AllmanagerDropDown(String companyId);

	public List<Object[]> TLDropDown(long id);
	
	public ManagerDTO getAllUsersByManagerId(Long managerId);

	public boolean deleteUsers(Long id);

	public int changeStatus(String status, Long id, String rem);

	public Users finduserById(Long id);

	public Users updateUser(Users users);

	public List<UserInfoDTO> finduserInfoById(Long id);

	public String[] allUsitStaff();

	// for checkins track
	public GetLastLogout getLastLogout(Long userid);

	public GetLastLogout getLastLogin(Long userid);

	public List<GetRecruiter> getRecruiter(String token);

	public List<GetRecruiter> getSalesExecutives(Long userid ,Long companyId);

	public List<GetRecruiter> getDomExecutives();

	public List<UserInfoDTO> getTaskAssinedUsersAndCreatedBy(long userid, long[] auserid);

	public EmployeeDocuments uploadfiles(String files, long id);

	public Resource download(long id) throws FileNotFoundException;

	public Resource downloadfile(long id, String doctype) throws FileNotFoundException;

	public int removeFile(long id, String type);

	public int removeMultipleFile(long id);

	public List<EmpHierarchy> getempHierarchy();

	public int unlockUser(Users users);

	public String duplicateCheckWithEmail(String email,Long cid);

	public Page<UserInfoDTO> getAllUsersWithSorting(SortingRequestDTO sortingRequestDTO, String token);

	public boolean updateUserprofile(UpdateProfileRequestDTO updateProfileRequestDTO);

	public Object[] getDepartmentWiseUsers(String department, String companyId);

	public List<UserDTO> getAllManagers();

	public List<UserDTO> getTeamLeadsByManager(Long managerId);

	public List<UserDTO> getExecutivesByTeamLead(Long teamLeadId);

	public boolean getValidDateCompanyGiven(String companyid);

	List<Users> getAllUsers(String status, List<Long> companyid);
}
