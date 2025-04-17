package com.narvee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.narvee.entity.TmsUsers;

@Repository
public interface TmsUsersRepo extends JpaRepository<TmsUsers, Long> {

	Optional<TmsUsers> findByEmail(String email);

}
