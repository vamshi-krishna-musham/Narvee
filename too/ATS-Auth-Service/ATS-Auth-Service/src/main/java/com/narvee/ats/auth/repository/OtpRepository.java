package com.narvee.ats.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.narvee.ats.auth.entity.OTP;

public interface OtpRepository extends JpaRepository<OTP, Long> {

	public Optional<OTP> findByUserid(Long userid);


}
