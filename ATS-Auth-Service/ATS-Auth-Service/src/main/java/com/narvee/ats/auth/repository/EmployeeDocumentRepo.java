package com.narvee.ats.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.narvee.ats.auth.entity.EmployeeDocuments;

public interface EmployeeDocumentRepo extends JpaRepository<EmployeeDocuments, Long> {

}
