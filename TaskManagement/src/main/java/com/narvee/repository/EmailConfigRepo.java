package com.narvee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.narvee.entity.TmsEmailConfiguration;

@Repository
public interface EmailConfigRepo extends JpaRepository<TmsEmailConfiguration, Long>{

	boolean existsByAdminIdAndEmailNotificationType(Long adminId, String emailNotificationType);

	Optional<TmsEmailConfiguration> findByAdminIdAndEmailNotificationType(Long adminId, String emailNotificationType);
	List<TmsEmailConfiguration> findByAdminId(Long adminId);

}
