package com.narvee.ats.auth.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.narvee.ats.auth.dto.GetRoles;
import com.narvee.ats.auth.entity.Roles;

public interface IRoleRepository extends JpaRepository<Roles, Serializable> {
	@Query("SELECT r.rolename FROM Roles r WHERE r.rolename=:roleName")
	public List<String> findRolByRolName(@Param("roleName") String roleName);
	
	public Roles findByCompanyidAndRoleid(Long companyid,Long roleId);
	
	public Roles findByRoleidAndCompanyid(Long roleid, Long companyid);

	@Query("SELECT r.rolename FROM Roles r WHERE r.rolename=:roleName and r.companyid=:companyId")
	public List<String> findforGiven(@Param("roleName") String roleName, @Param("companyId") Long companyId);
	
	public Optional<Roles> findByRolenameAndRoleidNot(String rolename, Long id);
	
	@Modifying
	@Query("UPDATE Roles c SET c.status = :status,c.remarks = :rem  WHERE c.roleid = :id")
	public int toggleStatus(@Param("status") String status, @Param("id") Long id, @Param("rem") String rem);
	
	@Query(value = "SELECT roleid, rolename from roles where status!='InActive' AND companyid=:cid ", nativeQuery = true)
	public List<GetRoles> getAllRoles(Long cid);

	@Query(value = "select r.* from roles r where r.companyid=:company", nativeQuery = true)
	public List<Roles> findAllRolesCompanyWise(Long company);
	
	@Query(value = "select * from roles where companyid=:companyId",nativeQuery = true)
	public List<Roles> findRolesByCompanyId(Long companyId);

	@Query(value = "select * from privilege_selected_companiess where roleid_id=:roleId and company_id=:company and priv_id=:priv",nativeQuery = true)
	public List<Object[]> findByGivenRoleIdAndCompanyAndPriv(Long roleId, Long company, Long priv);

	@Query(value = "select priv_id from privilege_selected_companiess where roleid_id=:roleId and company_id=:compnayId",nativeQuery = true)
    public List<Long>	findPrivilegesAssignedByGivenRoleAndCompany(Long roleId, Long compnayId);
}
