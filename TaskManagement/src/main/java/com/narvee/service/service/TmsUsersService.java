package com.narvee.service.service;

import java.util.Optional;

import com.narvee.dto.TmsUsersInfo;
import com.narvee.entity.TmsUsers;

public interface TmsUsersService {
	
	public TmsUsersInfo saveUsers(TmsUsersInfo info);

	public Optional<TmsUsers> findByEmail(String email);
	
	
	

}
