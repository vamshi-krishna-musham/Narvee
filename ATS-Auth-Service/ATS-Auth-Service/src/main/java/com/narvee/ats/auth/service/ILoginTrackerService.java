package com.narvee.ats.auth.service;

import org.springframework.data.domain.Page;

import com.narvee.ats.auth.dto.LoginTrackDTO;
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.entity.Users;

public interface ILoginTrackerService {

	public void save(Users user, String systemIp, String networkIp, String status, String remarks);
	public Page<LoginTrackDTO> getAllLoginTrack(SortingRequestDTO sortingRequestDTO);
	
}
