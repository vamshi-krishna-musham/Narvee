package com.narvee.service.serviceimpl;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.narvee.dto.TmsUsersInfo;
import com.narvee.entity.TmsUsers;
import com.narvee.repository.TmsUsersRepo;
import com.narvee.service.service.TmsUsersService;

@Service
public class TmsUsersServiceImpl implements TmsUsersService {
	
	private static final Logger logger = LoggerFactory.getLogger(TmsUsersServiceImpl.class);
	
	
	@Autowired 
	private TmsUsersRepo tmsUsersRepo;
	
	@Autowired 
	private ModelMapper mapper;
	
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public TmsUsersInfo saveUsers(TmsUsersInfo info) {
		logger.info("!!! inside class: TmsUsersServiceImpl, !! method: saveUsers");
		TmsUsers users=	mapper.map(info, TmsUsers.class);	
		  Optional<TmsUsers> existingUser = tmsUsersRepo.findByEmail(info.getEmail());
		    if (existingUser.isPresent()) {
		        throw new IllegalArgumentException("User with email " + info.getEmail() + " already exists");
		    }
		users.setPassword(encoder.encode(info.getPassword()));
		
		 if (info.getAddedBy() == null) {
		       users.setUserRole("SUPER_ADMIN");
		       
		    } else {	        
		        users.setUserRole("TEAM_MEMBER");
		        users.setAddedBy(info.getAddedBy()); 
		    }
		tmsUsersRepo.save(users);
		TmsUsersInfo usersinfo=	mapper.map(users, TmsUsersInfo.class);
		return usersinfo;
	}

	@Override
	public Optional<TmsUsers> findByEmail(String email) {
		 Optional<TmsUsers> existingUser = tmsUsersRepo.findByEmail(email);
		return existingUser;
	}
	
	
	
}
