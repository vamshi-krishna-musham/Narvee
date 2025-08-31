package com.narvee.ats.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.narvee.ats.auth.entity.UserTracker;

import feign.Param;

@Repository
public interface ConsultantActivityHistoryRepo extends JpaRepository<UserTracker, Long> {
	Optional<UserTracker> findTopByUserIdOrderByLastloginDesc(Long userId);
	
	List<UserTracker> findAllByUserIdOrderByLastloginDesc(Long userId);
	
	@Query(value = " select * FROM usertracker ua WHERE ua.user_id = :userId AND ua.lastlogin >= :startDate AND ua.lastlogout < :endDate",nativeQuery = true)
	public List<UserTracker> findWorkingHoursByUserId(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
	
	@Query(value = " select * FROM usertracker ua WHERE ua.lastlogin >= :startDate AND ua.lastlogout < :endDate",nativeQuery = true)
	public List<UserTracker> findAllWorkingHours( @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
