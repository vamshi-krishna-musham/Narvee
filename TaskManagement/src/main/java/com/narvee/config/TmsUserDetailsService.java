package com.narvee.config;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.narvee.entity.TmsUsers;
import com.narvee.repository.TmsUsersRepo;


@Service
public class TmsUserDetailsService  implements UserDetailsService{
	
	@Autowired
	private TmsUsersRepo tmsUsersRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<TmsUsers> userOptional = tmsUsersRepo.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        TmsUsers user = userOptional.get();

        // Using Spring Security's User implementation:
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()  // roles/authorities – you can populate this list as needed
        );
    
	
	}

}
