package com.narvee.ats.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.narvee.ats.auth.entity.TmsUsers;
import com.narvee.ats.auth.tms.dto.AllTmsUsers;

@Repository
public interface TmsUsersRepo extends JpaRepository<TmsUsers, Long> {

	public Optional<TmsUsers> findByEmail(String email);

	public Optional<TmsUsers> findByEmailAndAdminId(String email, Long adminId);

	public Optional<TmsUsers> findByUserId(Long UserId);

	@Query(value = "select user_id ,  concat(first_name,' ', COALESCE(middle_name, ''),' ' ,last_name) AS  full_name   from tms_users where admin_id = :adminId or user_id = :adminId OR  added_by = :adminId", nativeQuery = true)
	public List<Object[]> getUsersDropDown(Long adminId);

	@Query(value = "    SELECT tr.rolename as roleName, u.user_id AS userId, "
			+ "                 concat(a.first_name,' ', COALESCE(a.middle_name, ''),' ' ,a.last_name) AS adminFullName ,  "
			+ "			             u.contact_number AS phoneNumber, u.email AS email, u.user_role AS userRole,  u.organisation_email ,u.organisation_name, u.company_domain ,\r\n"
			+ "                         u.company_size , u.industry , u.profile_photo,\r\n"
			+ "			              concat(addedByUser.first_name,' ', COALESCE(addedByUser.middle_name, ''),' ' ,addedByUser.last_name) AS addeByFullName , "
			+ "                       concat(u.first_name,' ', COALESCE(u.middle_name, ''),' ' ,u.last_name) AS  userFullName, u.position AS position "             
			+ "			              FROM tms_users u LEFT JOIN tms_users a ON u.admin_id = a.user_id \r\n"
			+ "			               LEFT JOIN tms_users addedByUser ON u.added_by = addedByUser.user_id\r\n"
			+ "		               JOIN tms_roles tr ON tr.roleid = u.role_roleid  "
			+ "			             WHERE u.admin_id = :adminId ", nativeQuery = true)
	public Page<AllTmsUsers> getAllUsersByAdmin(Long adminId, Pageable pageable);

	@Query(value = "  SELECT tr.rolename as roleName, u.user_id AS userId, concat(u.first_name,' ', COALESCE(u.middle_name, ''),' ' ,u.last_name) AS userFullName ,\r\n"
			+ "                 concat(a.first_name,' ', COALESCE(a.middle_name, ''),' ' ,a.last_name) AS adminFullName , \r\n"
			+ "			               u.contact_number AS phoneNumber, u.email AS email, u.user_role AS userRole, u.organisation_email ,u.organisation_name, u.company_domain ,\r\n"
			+ "                         u.company_size , u.industry , u.profile_photo,\r\n"
			+ "			           concat(addedByUser.first_name,' ', COALESCE(addedByUser.middle_name, ''),' ' ,addedByUser.last_name) AS addeByFullName , u.position AS position \r\n"
			+ "			            FROM tms_users u LEFT JOIN tms_users a ON u.admin_id = a.user_id \r\n"
			+ "			              LEFT JOIN tms_users addedByUser ON u.added_by = addedByUser.user_id\r\n"
			+ "			               JOIN tms_roles tr ON tr.roleid = u.role_roleid \r\n"
			+ "						WHERE ( u.admin_id = :adminId )  and "
			+ "(concat(u.first_name,' ', COALESCE(u.middle_name, ''),' ' ,u.last_name) LIKE CONCAT('%',:keyword, '%')"
			+ " OR concat(a.first_name,' ', COALESCE(a.middle_name, ''),' ' ,a.last_name)  LIKE CONCAT('%',:keyword, '%') "
			+ " OR  concat(addedByUser.first_name,' ', COALESCE(addedByUser.middle_name, ''),' ' ,addedByUser.last_name) LIKE CONCAT('%',:keyword, '%') "
			+ "		OR  u.contact_number LIKE CONCAT('%',:keyword, '%') OR  u.email LIKE CONCAT('%',:keyword, '%') "
			+ "			 OR  u.position LIKE CONCAT('%',:keyword, '%') OR  u.organisation_email LIKE CONCAT('%',:keyword, '%')\r\n"
			+ "             "
			+ "   OR roleName LIKE CONCAT('%',:keyword, '%') )", nativeQuery = true)
	public Page<AllTmsUsers> getAllUsersByAdminwithSearching(Long adminId, Pageable pageable, String keyword);

	@Query(value = "select tr.rolename from tms_users  tu join  tms_roles tr on tu.role_roleid = tr.roleid where   user_id = :user_id", nativeQuery = true)
	public String getUserRole(Long user_id);

	@Query(value = " SELECT tr.rolename as roleName, u.user_id AS userId, concat(u.first_name,' ', COALESCE(u.middle_name, ''),' ' ,u.last_name) AS userFullName  ,\r\n"
			+ "                 concat(a.first_name,' ', COALESCE(a.middle_name, ''),' ' ,a.last_name) AS adminFullName ,\r\n"
			+ "			             u.contact_number AS phoneNumber, u.email AS email, u.user_role AS userRole,  u.organisation_email ,u.organisation_name, u.company_domain ,\r\n"
			+ "                         u.company_size , u.industry , u.profile_photo,\r\n"
			+ "			                concat(addedByUser.first_name,' ', COALESCE(addedByUser.middle_name, ''),' ' ,addedByUser.last_name) AS addeByFullName , u.position AS position \r\n"
			+ "		             FROM tms_users u LEFT JOIN tms_users a ON u.admin_id = a.user_id \r\n"
			+ "			              LEFT JOIN tms_users addedByUser ON u.added_by = addedByUser.user_id\r\n"
			+ "			                JOIN tms_roles tr ON tr.roleid = u.role_roleid \r\n"
			+ "			              WHERE u.added_by = :adminId ", nativeQuery = true)
	public Page<AllTmsUsers> getAllUsersByAddedBy(Long adminId, Pageable pageable);

	@Query(value = "                         SELECT tr.rolename as roleName, u.user_id AS userId,  concat(u.first_name,' ', COALESCE(u.middle_name, ''),' ' ,u.last_name) AS userFullName,\r\n"
			+ "                 concat(a.first_name,' ', COALESCE(a.middle_name, ''),' ' ,a.last_name) AS adminFullName ,\r\n"
			+ "			             u.contact_number AS phoneNumber, u.email AS email, u.user_role AS userRole,  u.organisation_email ,u.organisation_name, u.company_domain ,\r\n"
			+ "                         u.company_size , u.industry , u.profile_photo,\r\n"
			+ "			               concat(addedByUser.first_name,' ', COALESCE(addedByUser.middle_name, ''),' ' ,addedByUser.last_name) AS addeByFullName , u.position AS position \r\n"
			+ "			               FROM tms_users u LEFT JOIN tms_users a ON u.admin_id = a.user_id \r\n"
			+ "		              LEFT JOIN tms_users addedByUser ON u.added_by = addedByUser.user_id\r\n"
			+ "			               JOIN tms_roles tr ON tr.roleid = u.role_roleid \r\n"
			+ "			               WHERE u.added_by = :adminId  and "
			+ "(u.first_name LIKE CONCAT('%',:keyword, '%') OR u.middle_name LIKE CONCAT('%',:keyword, '%')  "
			+ "OR u.last_name LIKE CONCAT('%',:keyword, '%') "
			+ "		OR  u.contact_number LIKE CONCAT('%',:keyword, '%') OR  u.email LIKE CONCAT('%',:keyword, '%') "
			+ "			OR   u.position LIKE CONCAT('%',:keyword, '%') OR  u.organisation_email LIKE CONCAT('%',:keyword, '%')\r\n" 
			+ "     OR roleName LIKE CONCAT('%',:keyword, '%') )", nativeQuery = true)
	public Page<AllTmsUsers> getAllUsersByAddedBywithSearching(Long adminId, Pageable pageable, String keyword);
	
	
	
	@Query(value = "select tr.rolename from tms_roles tr  join tms_users tu on tu.role_roleid = tr.roleid  where tu.user_id = :user_id",nativeQuery = true)
	public String roleName(Long user_id);
	
	@Query(value = "SELECT COUNT(*) FROM tms_assigned_users WHERE tms_user_id = :userId",nativeQuery = true)
	 Long  isUserAssignedToAnyProject(@Param("userId") Long userId);
	
	@Query(value = "  select Admin_id  from tms_users where user_id = :userId",nativeQuery = true)
	 Long  AdminId (@Param("userId") Long userId);

}
