package com.narvee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.narvee.entity.TmsTicketTracker;

public interface StatusTrackerRepo extends JpaRepository<TmsTicketTracker , Long>{

}
