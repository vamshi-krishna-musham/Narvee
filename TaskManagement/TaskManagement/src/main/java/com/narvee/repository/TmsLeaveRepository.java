package com.narvee.repository;
import java.util.List;

import com.narvee.dto.LeaveUserDTO;
import com.narvee.entity.TmsLeave;

import feign.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TmsLeaveRepository extends JpaRepository<TmsLeave, Long> {
    List<TmsLeave> findByStatus(String status);
    List<TmsLeave> findByUserId(Long userId);
    List<TmsLeave> findByStatusAndUserIdNot(String status, Long userId);
    @Query(value = "SELECT user_id AS userid, first_name, email FROM tms_users WHERE user_id = :userId", nativeQuery = true)
    List<LeaveUserDTO> getLeaveUser(@Param("userId") Long userId);
    @Query(value = """
    SELECT user_id AS userid, 
        first_name AS firstname, 
        email,
        organisation_name AS organisation,
        position
    FROM tms_users 
    WHERE is_super_admin = 1 
    OR LOWER(position) = 'admin'
    OR LOWER(position) = 'project manager'
    """, nativeQuery = true)
    List<LeaveUserDTO> getAllAdminTypes();

    @Query(value = """
        SELECT l.*,u.position
        FROM tms_leaves l
        JOIN tms_users u ON l.user_id = u.user_id
        WHERE u.organisation_name = :orgName
        AND l.status = 'PENDING'
        AND l.user_id <> :managerId
        """, nativeQuery = true)
    List<TmsLeave> findPendingByOrganisationForSuperAdmin(
        @Param("orgName") String orgName,
        @Param("managerId") Long managerId
    );
    @Query(value = """
        select l.* 
        from tms_leaves l 
        join tms_users u on l.user_id=u.user_id 
        where is_super_admin=false and position!='Admin' 
        and l.user_id <> :managerId 
        and u.organisation_name = :orgName
        """, nativeQuery = true)
    List<TmsLeave> findPendingLeavesForAdmin(
        @Param("orgName") String orgName,
        @Param("managerId") Long managerId
    );
    @Query(value = """
        select l.* 
        from tms_leaves l 
        join tms_users u on l.user_id=u.user_id 
        where u.is_super_admin=false 
        and u.position!='Admin' 
        and u.position!='Project Manager' 
        and l.user_id <> :managerId 
        and u.organisation_name = :orgName
        """, nativeQuery = true)
    List<TmsLeave> findPendingByOrganisationForProjectManager(
        @Param("orgName") String orgName,
        @Param("managerId") Long managerId
    );
    @Query(value = """
        SELECT l.* 
        FROM tms_leaves l 
        JOIN tms_users u ON l.user_id = u.user_id
        WHERE LOWER(u.position) = 'admin'
        """, nativeQuery = true)
    List<TmsLeave> findAllPending();

}
