package com.narvee.ats.auth.repository;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.ats.auth.dto.EmpHierarchy;
import com.narvee.ats.auth.dto.GetLastLogout;
import com.narvee.ats.auth.dto.GetRecruiter;
import com.narvee.ats.auth.dto.UserDTO;
import com.narvee.ats.auth.dto.UserInfoDTO;
import com.narvee.ats.auth.entity.Users;

@Repository
@Transactional
public interface IUserRepository extends JpaRepository<Users, Serializable> {
	public Users findByEmail(String email);

	public Users findByRoleRoleid(long roleid);

	@Query(value = "select u.userid, u.fullname,u.pseudoname from users u,roles r where u.status = 'Active' and u.role_roleid=r.roleid and r.rolename in ('Sales Manager', 'Recruiting Manager') ", nativeQuery = true)
	public List<Object[]> managerDropdown();
	
	@Query(value = "select u.userid, u.fullname,u.pseudoname,r.rolename AS role from users u,roles r where u.status = 'Active' and u.role_roleid=r.roleid "
			+ " and r.rolename in ('Sales Manager', 'Recruiting Manager') AND u.companyid = :companyId ", nativeQuery = true)
	public List<UserDTO> AllmanagerDropdown(Long companyId);

	
//	@Query(value = " select u.userid, u.fullname,u.pseudoname from users u,roles r where u.status = 'Active' and u.role_roleid=r.roleid and u. manager = :managerId",nativeQuery = true)
//	public List<Object[]> getAllUsersByManagerId(@Param("managerId") Long managerId);
	

	@Query(value = "SELECT u.userid AS userid, \r\n"
			+ "           u.fullname AS fullname, \r\n"
			+ "           u.pseudoname AS pseudoname, \r\n"
			+ "           r.rolename AS role, \r\n"
			+ "           u.teamlead AS TeamLead\r\n"
			+ "    FROM users u\r\n"
			+ "    JOIN roles r ON u.role_roleid = r.roleid\r\n"
			+ "    WHERE u.manager = :managerId \r\n"
			+ "      AND u.department != 'Consultant' \r\n"
			+ "      AND u.status = 'Active'",nativeQuery = true)
	public List<UserDTO> getAllUsersByManagerId(@Param("managerId") Long managerId);
	
	@Query(value = "select u.userid, u.fullname,u.pseudoname from users u,roles r where u.status = 'Active' and u.role_roleid=r.roleid and r.rolename in ('Team Leader Sales', 'Team Leader Recruiting') and u.manager=:id", nativeQuery = true)
	public List<Object[]> TlDropdown(long id);

	@Modifying
	@Query("UPDATE Users c SET c.status = :status,c.remarks = :rem  WHERE c.userid = :id")
	public int toggleStatus(@Param("status") String status, @Param("id") Long id, @Param("rem") String rem);

	public Users findByEmailAndUseridNot(String email, Long id);

//	@Query(value = "select u.status,u.userid, u.fullname,u.pseudoname,u.department,u.designation,u.email,u.personalcontactnumber,(select  m.pseudoname from users m where m.userid=u.manager) as manager ,\r\n"
//			+ "	(select  t.pseudoname from users t where t.userid=u.teamlead) as teamlead \r\n"
//			+ "	from users u  where  u.userid=:userid", nativeQuery = true)
//	public List<UserInfoDTO> getUserInfo(@Param("userid") long userid);

	@Query(value = "select u.status,u.userid, u.fullname,u.pseudoname,u.department,u.designation,u.email,u.personalcontactnumber,(select  m.pseudoname from users m where m.userid=u.manager) as manager ,\r\n"
			+ "	(select  t.pseudoname from users t where t.userid=u.teamlead) as teamlead \r\n"
			+ "	from users u where   u.userid=:userid", nativeQuery = true)
	public List<UserInfoDTO> getUserInfo(@Param("userid") long userid);

	@Query(value = "select email from users where department in ('Bench Sales', 'Recruiting') and role_roleid in (4,3)", nativeQuery = true)
	public String[] allUsitStaff();

	@Query(value = "SELECT u.userid, CASE WHEN u.pseudoname!='' THEN CONCAT(u.pseudoname) ELSE u.fullname END as fullname FROM users u WHERE u.status = 'Active' AND u.department='Recruiting' AND companyid IN (:cid) order by fullname", nativeQuery = true)
	public List<GetRecruiter> getRecruiter(List<Long> cid);

//	@Query(value = "SELECT u.userid, CASE WHEN u.pseudoname!='' THEN CONCAT(u.fullname, ' - ', u.pseudoname,' (',u.department,')') ELSE u.fullname END as fullname FROM users u WHERE u.status = 'Active' AND u.department='Bench Sales' order by fullname", nativeQuery = true)
//	public List<GetRecruiter> getBenchSalesExecutives();

	@Query(value = "SELECT u.userid, CASE WHEN u.pseudoname!='' THEN CONCAT(u.pseudoname) ELSE u.fullname END as fullname FROM users u WHERE u.status = 'Active' AND u.department='Bench Sales' And u.teamlead=:userid AND companyid = :cid order by fullname", nativeQuery = true)
	public List<GetRecruiter> getBenchSalesTLExecutives(Long userid, Long cid);

	@Query(value = "SELECT u.userid, CASE WHEN u.pseudoname!='' THEN CONCAT(u.pseudoname) ELSE u.fullname END as fullname FROM users u WHERE u.status = 'Active' AND u.department='Bench Sales' And u.manager=:userid AND companyid = :cid order by fullname", nativeQuery = true)
	public List<GetRecruiter> getBenchSalesManagerExecutives(Long userid, Long cid);

	@Query(value = "SELECT u.userid, CASE WHEN u.pseudoname!='' THEN CONCAT(u.pseudoname) ELSE u.fullname END as fullname FROM users u WHERE u.status = 'Active' AND u.department='Bench Sales' AND companyid = :cid order by fullname", nativeQuery = true)
	public List<GetRecruiter> getBenchSuperAdminExecutives(Long cid);

	@Query(value = "SELECT u.userid, CASE WHEN u.pseudoname!='' THEN CONCAT(u.fullname) ELSE u.fullname END as fullname FROM users u WHERE u.status = 'Active' AND u.department='Dom Recruiting' order by fullname", nativeQuery = true)
	public List<GetRecruiter> getDomExecutives();

	// for checkins track
	@Query(value = "select lastlogout from usertracker where userid= :userid order by lastlogout desc limit 1", nativeQuery = true)
	public GetLastLogout getLastLogout(Long userid);

	@Query(value = "select lastlogin from usertracker where userid= :userid order by lastlogin desc limit 1", nativeQuery = true)
	public GetLastLogout getLastLogin(Long userid);

	@Query(value = "select null as createdby,email, pseudoname from users where userid in (:auserid)\r\n" + "union \r\n"
			+ "select pseudoname as createdby,email, pseudoname from users where userid = :userid", nativeQuery = true)
	public List<UserInfoDTO> getTaskAssinedUsersAndCreatedBy(long userid, long auserid[]);

	@Query(value = "SELECT u.pseudoname, u.designation,u.department,u.email,u.isteamlead, u.ismanager, um.pseudoname AS managername,utl.pseudoname AS teamleadname,u.userid , \r\n"
			+ "    r.rolename FROM users u LEFT JOIN users um ON u.manager = um.userid LEFT JOIN  users utl ON u.teamlead = utl.userid JOIN roles r ON u.role_roleid = r.roleid WHERE \r\n"
			+ "    u.status = 'Active' AND (u.department = 'Recruiting' OR u.department = 'Bench Sales') ORDER BY u.department, um.pseudoname, utl.pseudoname, u.pseudoname", nativeQuery = true)
	public List<EmpHierarchy> getempHierarchy();

	public Users findByUserid(long id);

	@Modifying
	@Query("UPDATE Users u SET u.fullname=:fullname, u.email=:email, u.personalcontactnumber=:personalcontactnumber, u.companycontactnumber=:companycontactnumber, u.designation=:designation, u.alternatenumber=:alternatenumber WHERE u.userid=:id")
	public void updateUserProfile(@Param("fullname") String fullname, @Param("email") String email,
			@Param("personalcontactnumber") String personalcontactnumber,
			@Param("companycontactnumber") String companycontactnumber, @Param("designation") String designation,
			@Param("alternatenumber") String alternatenumber, @Param("id") Long id);

	@Query(value = "select u.email  from Users u where u.status='active' and u.email =:email AND companyid=:cid")
	public String duplicateCheckWithEmail(String email, Long cid);

	@Query(value = " SELECT u.createddate, u.personalcontactnumber, u.email, u.fullname, u.designation, u.department, u.manager, u.teamlead, u.userid, u.status, u.pseudoname "
			+ " FROM users u where u.companyid in (:company) and u.status=:estatus  and u.department != 'Consultant' ORDER BY CASE WHEN :sortOrder = 'asc' THEN CASE WHEN :sortField = 'personalcontactnumber' THEN u.personalcontactnumber "
			+ " WHEN :sortField= 'email' THEN u.email WHEN :sortField = 'fullname' THEN u.fullname WHEN :sortField = 'designation' THEN u.designation WHEN :sortField = 'department' THEN u.department "
			+ " WHEN :sortField= 'status' THEN u.status WHEN :sortField= 'createddate' THEN u.createddate END END ASC,CASE WHEN :sortField= 'desc' THEN CASE WHEN :sortField= 'personalcontactnumber' THEN u.personalcontactnumber "
			+ " WHEN :sortField= 'email' THEN u.email WHEN :sortField= 'fullname' THEN u.fullname WHEN :sortField= 'designation' THEN u.designation WHEN :sortField = 'department' THEN u.department "
			+ " WHEN :sortField= 'status' THEN u.status WHEN :sortField= 'createddate' THEN u.createddate END END DESC ", nativeQuery = true, countQuery = "select count(*) from users where status= 'active' ")
	public Page<UserInfoDTO> getAllUsersWithSortingActive(Pageable pageable, @Param("sortField") String sortField,
			@Param("sortOrder") String sortOrder, @Param("estatus") String estatus, List<Long> company);

	@Query(nativeQuery = true, value = "select userid, pseudoname from users where department=:department and status='Active' and companyid=:comanyId")
	public Object[] findDepartmentWiseActiveUsers(String department, Long comanyId);
	

	@Query(value = " SELECT u.createddate, u.personalcontactnumber, u.email, u.fullname, u.designation, u.department, u.manager, u.teamlead, u.userid, u.status, u.pseudoname "
			+ " FROM users u where u.companyid in (:company) and u.status=:estatus and u.department != 'Consultant' ORDER BY CASE WHEN :sortOrder = 'asc' THEN CASE WHEN :sortField = 'personalcontactnumber' THEN u.personalcontactnumber "
			+ " WHEN :sortField= 'email' THEN u.email WHEN :sortField = 'fullname' THEN u.fullname WHEN :sortField = 'designation' THEN u.designation WHEN :sortField = 'department' THEN u.department "
			+ " WHEN :sortField= 'status' THEN u.status WHEN :sortField= 'createddate' THEN u.createddate END END ASC,CASE WHEN :sortField= 'desc' THEN CASE WHEN :sortField= 'personalcontactnumber' THEN u.personalcontactnumber "
			+ " WHEN :sortField= 'email' THEN u.email WHEN :sortField= 'fullname' THEN u.fullname WHEN :sortField= 'designation' THEN u.designation WHEN :sortField = 'department' THEN u.department "
			+ " WHEN :sortField= 'status' THEN u.status WHEN :sortField= 'createddate' THEN u.createddate END END DESC ", nativeQuery = true, countQuery = "select count(*) from users where status= 'inactive' ")
	public Page<UserInfoDTO> getAllUsersWithSortingInactive(Pageable pageable, @Param("sortField") String sortField,
			@Param("sortOrder") String sortOrder, @Param("estatus") String estatus, List<Long> company);

	@Query(value = " SELECT u.personalcontactnumber, u.email, u.fullname, u.designation, u.department, u.manager, u.teamlead, u.userid, u.status, u.pseudoname "
			+ " FROM users u WHERE u.status= :status and u.department != 'Consultant' AND (u.personalcontactnumber LIKE CONCAT('%',:keyword, '%') "
			+ " OR u.email LIKE CONCAT('%',:keyword, '%') OR u.fullname LIKE CONCAT('%',:keyword, '%') OR u.designation LIKE CONCAT('%',:keyword, '%') OR u.department LIKE CONCAT('%',:keyword, '%') "
			+ " OR u.status LIKE CONCAT('%',:keyword, '%') OR u.pseudoname LIKE CONCAT('%',:keyword, '%')) ORDER BY CASE WHEN :sortOrder = 'asc' THEN CASE WHEN :sortField = 'personalcontactnumber' THEN u.personalcontactnumber "
			+ " WHEN :sortField= 'email' THEN u.email WHEN :sortField = 'fullname' THEN u.fullname WHEN :sortField = 'designation' THEN u.designation WHEN :sortField = 'department' THEN u.department "
			+ " WHEN :sortField= 'status' THEN u.status END END ASC,CASE WHEN :sortOrder= 'desc' THEN CASE WHEN :sortField= 'personalcontactnumber' THEN u.personalcontactnumber "
			+ " WHEN :sortField= 'email' THEN u.email WHEN :sortField= 'fullname' THEN u.fullname WHEN :sortField= 'designation' THEN u.designation WHEN :sortField = 'department' THEN u.department "
			+ " WHEN :sortField= 'status' THEN u.status END END DESC ", nativeQuery = true)
	public Page<UserInfoDTO> getAllUsersFilterWithSorting(@Param("status") String status, Pageable pageable,
			@Param("keyword") String keyword, @Param("sortField") String sortField,
			@Param("sortOrder") String sortOrder);

	@Query(value = " SELECT u.personalcontactnumber, u.email, u.fullname, u.designation, u.department, u.manager, u.teamlead, u.userid, u.status, u.pseudoname "
			+ " FROM users u where u.status =:status", nativeQuery = true)
	public Page<UserInfoDTO> getAllUsersWithSorting(Pageable pageable, @Param("status") String status);

	@Query(value = " SELECT u.personalcontactnumber, u.email, u.fullname, u.designation, u.department, u.manager, u.teamlead, u.userid, u.status, u.pseudoname "
			+ " FROM users u where u.status =:status", nativeQuery = true)
	public List<UserInfoDTO> getAllUsersWithSorting(@Param("status") String status);

	@Query(value = "select rolename from users u join roles  r On u.role_roleid=r.roleid where u.userid=:userid", nativeQuery = true)
	public String getRoleid(Long userid);

	// Fetch all active managers
	@Query(value = "SELECT u.userid, u.fullname, u.pseudoname FROM users u WHERE u.ismanager = 1 AND u.status = 'Active'", nativeQuery = true)
	public List<UserDTO> findAllManagers();

	// Fetch all active team leads under a manager
	@Query(value = "SELECT u.userid, u.fullname, u.pseudoname FROM users u WHERE u.isteamlead = 1 AND u.manager = :managerId AND u.status = 'Active'", nativeQuery = true)
	public List<UserDTO> findTeamLeadsByManager(@Param("managerId") Long managerId);

	// Fetch all active executives under a team lead
	@Query(value = "SELECT u.userid, u.fullname, u.pseudoname FROM users u WHERE u.teamlead = :teamLeadId AND u.status = 'Active'", nativeQuery = true)
	public List<UserDTO> findExecutiveByTeamLead(@Param("teamLeadId") Long teamLeadId);

	@Query(value = "SELECT u.department FROM Users u WHERE u.userid = :managerId", nativeQuery = true)
	public String findDepartmentByManagerId(@Param("managerId") Long managerId);

	@Query(value = "SELECT * FROM users u WHERE u.companyid IN(:company) order by u.userid desc", nativeQuery = true)
	public List<Users> findUsersByCompany(@Param("company") List<Long> company);

	@Query(value = "select companyid from users where userid=:userid", nativeQuery = true)
	public int findCompanyByUser(Long userid);
		
}
