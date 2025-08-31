package com.narvee.ats.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.narvee.ats.auth.dto.LoginTrackDTO;
import com.narvee.ats.auth.entity.LoginTracker;

public interface LoginTrackerRepository extends JpaRepository<LoginTracker, Long>{
	
	
	@Query(value ="SELECT t.id,t.login_time,t.network_ip,t.remarks,t.status,t.system_ip FROM login_tracker t",nativeQuery =true)
	public Page<LoginTrackDTO> getAllLoginTrackeWithPagination(Pageable pageable);
	
	@Query(value ="SELECT t.id,t.login_time,t.network_ip,t.remarks,t.status,t.system_ip  FROM login_tracker t where \r\n"
			+ "t.login_time LIKE CONCAT('%',:keyword, '%') OR t.network_ip LIKE CONCAT('%',:keyword, '%') OR t.remarks LIKE CONCAT('%',:keyword, '%') \r\n"
			+ "OR t.status LIKE CONCAT('%',:keyword, '%') OR t.system_ip LIKE CONCAT('%',:keyword, '%')" ,nativeQuery =true)
	public Page<LoginTrackDTO> getAllLoginTrackeWithKeyword(Pageable pageable,@Param("keyword") String keyword);
}
 