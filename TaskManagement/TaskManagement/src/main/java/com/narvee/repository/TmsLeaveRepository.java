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
}
