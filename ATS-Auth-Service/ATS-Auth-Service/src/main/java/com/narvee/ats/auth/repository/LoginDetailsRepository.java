package com.narvee.ats.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.narvee.ats.auth.entity.LoginDetails;


public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Long> {
	

}
