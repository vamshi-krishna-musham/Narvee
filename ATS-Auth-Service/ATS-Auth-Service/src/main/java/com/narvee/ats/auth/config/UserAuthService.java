package com.narvee.ats.auth.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.narvee.ats.auth.entity.Roles;
import com.narvee.ats.auth.entity.TmsUsers;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.repository.IUserRepository;
import com.narvee.ats.auth.repository.TmsUsersRepo;

@Service
public class UserAuthService implements UserDetailsService {

	@Autowired
	private IUserRepository repo;

	@Autowired
	private TmsUsersRepo tmsUsersRepo;

	public static final Logger logger = LoggerFactory.getLogger(UserAuthService.class);

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("!!! inside class: UserAuthService, !! method: loadUserByUsername");
	

		String[] parts = username.split("\\|");
		String email = parts[0];
		String loginType = null;
	
		if (parts.length>1 ) {
			loginType = parts[1];
		}

		if (loginType == null) {
			logger.info("!!! inside class: UserAuthService, !! method: loadUserByUsername--> ATS");

			Users users = repo.findByEmail(username);
			
			 if (users == null) {
		            logger.warn("ATS User not found with email: {}", email);
		            throw new UsernameNotFoundException("User not found with email: " + email);
		        }
			Roles role = new Roles();
			String pasw = "";
			List<Roles> rls = new ArrayList<>();
			try {
				role.setRolename(users.getRole().getRolename());
				rls.add(role);
				pasw = users.getPassword();
			} catch (NullPointerException e) {
			}

			List<GrantedAuthority> grantedAuthorities = rls.stream().map(r -> {
				return new SimpleGrantedAuthority(r.getRolename());
			}).collect(Collectors.toList());

			return new org.springframework.security.core.userdetails.User(username, pasw, grantedAuthorities);
		} else {
			logger.info("!!! inside class: UserAuthService, !! method: loadUserByUsername--> TMS");
             
			TmsUsers users = tmsUsersRepo.findByEmail(email).get();
			 if (users == null) {
		            logger.warn("TMS User not found with email: {}", email);
		            throw new UsernameNotFoundException("User not found with email: " + email);
		        }
			Roles role = new Roles();
			String pasw = "";
			List<Roles> rls = new ArrayList<>();
			try {
				role.setRolename("TMS");
				rls.add(role);
				pasw = users.getPassword();
			} catch (NullPointerException e) {
			}

			List<GrantedAuthority> grantedAuthorities = rls.stream().map(r -> {
				return new SimpleGrantedAuthority(r.getRolename());
			}).collect(Collectors.toList());

			return new org.springframework.security.core.userdetails.User(username, pasw, grantedAuthorities);

		}

	}
}
