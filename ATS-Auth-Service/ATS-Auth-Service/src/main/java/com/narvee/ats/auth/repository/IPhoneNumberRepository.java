package com.narvee.ats.auth.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.narvee.ats.auth.entity.PhoneNumberFormat;

public interface IPhoneNumberRepository extends JpaRepository<PhoneNumberFormat, Serializable> {

}
