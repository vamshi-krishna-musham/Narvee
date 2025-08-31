package com.narvee.ats.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.narvee.ats.auth.dto.RolesResponseDto;
import com.narvee.ats.auth.entity.TmsRoles;
import java.util.List;

public interface TmsRolesRepository extends JpaRepository<TmsRoles, Long> {

	
	List<TmsRoles> findByRolename(String rolename);

	
	List<TmsRoles> findByRoleid(Long roleId);  
	
	List<TmsRoles> findRoleByAdminId(long adminId); 
 
	@Query(value = "SELECT "
			+ "    r.roleid, "
			+ "    r.rolename, "
			+ "    r.status, "
			+ "    r.admin_id, "
			+ "    r.createddate, "
			+ "    r.addedby,"
			+ "    CONCAT(u1.first_name, ' ', COALESCE(u1.middle_name, ''), ' ', u1.last_name) AS addedByName,"
			+ "    r.updateddate, "
			+ "    r.updatedby,"
			+ "    CONCAT(u2.first_name, ' ', COALESCE(u2.middle_name, ''), ' ', u2.last_name) AS updatedByName,"
			+ "    r.description"
			+ "  FROM  "
			+ "    tms_roles r "
			+ " LEFT JOIN tms_users u1 ON r.addedby = u1.user_id "
			+ " LEFT JOIN tms_users u2 ON r.updatedby = u2.user_id "
			+ " WHERE  "
			+ "    r.admin_id = :adminId "
			+ "" ,nativeQuery = true)
	Page<RolesResponseDto> findByAdminId(long adminId,Pageable pageable); 

	TmsRoles findByRolenameAndAdminId(String rolename, long adminId);

	
	@Query(value = "SELECT r.roleid, r.rolename, r.status, r.admin_id, r.createddate, r.addedby,"
			+ "    CONCAT(u1.first_name, ' ', COALESCE(u1.middle_name, ''), ' ', u1.last_name) AS addedByName,r.updateddate, r.updatedby,"
			+ "    CONCAT(u2.first_name, ' ', COALESCE(u2.middle_name, ''), ' ', u2.last_name) AS updatedByName,r.description "
			+ "FROM    tms_roles r "
			+ "LEFT JOIN tms_users u1 ON r.addedby = u1.user_id "
			+ "LEFT JOIN tms_users u2 ON r.updatedby = u2.user_id "
			+ "WHERE "
			+ "    r.admin_id = :adminId "
			+ " and (r.rolename LIKE CONCAT('%',:keyword, '%')  OR DATE_FORMAT(r.createddate, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') "
			+ "OR DATE_FORMAT(r.updateddate, '%Y-%m-%d') LIKE CONCAT('%', :keyword, '%') OR addedByName LIKE CONCAT('%',:keyword, '%') OR updatedByName LIKE CONCAT('%',:keyword, '%') )",nativeQuery = true)
	Page<RolesResponseDto> findByAdminIdAndKeyword(Long adminId, Pageable pageable, String keyword);

}
