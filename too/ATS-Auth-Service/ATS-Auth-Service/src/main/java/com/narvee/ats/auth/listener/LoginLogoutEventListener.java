//package com.narvee.ats.auth.listener;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
//import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
//import org.springframework.security.authentication.event.LogoutSuccessEvent;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import com.narvee.ats.auth.entity.UserTracker;
//import com.narvee.ats.auth.entity.Users;
//import com.narvee.ats.auth.repository.IUserRepository;
//
//@Component
//public class LoginLogoutEventListener implements ApplicationListener<AbstractAuthenticationEvent> {
//
//	@Autowired
//	private IUserRepository userRepo;
//
//	private final Logger logger = LoggerFactory.getLogger(LoginLogoutEventListener.class);
//
//	@Override
//	public void onApplicationEvent(AbstractAuthenticationEvent event) {
//		logger.info("LoginLogoutEventListener = > onApplicationEvent");
//			if (event instanceof AuthenticationSuccessEvent) {
//			logger.info("LoginLogoutEventListener = > Login Functionality");
//			UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
//			LocalDateTime loginTime = LocalDateTime.now();
//			Users user = userRepo.findByEmail(userDetails.getUsername());
//			if (user != null) {
//				//user.setLastLogin(loginTime);
//				List<UserTracker> tracker = new ArrayList<>();
//				UserTracker track = new UserTracker();
//				track.setLastlogin(LocalDateTime.now());
//				track.setUsername(user.getFullname());
//				tracker.add(track);
//				//user.setUsertracker(tracker);
//				//commented by Kiran
//				userRepo.save(user);
//			}
//		} else if (event instanceof LogoutSuccessEvent) {
//			Authentication authentication = event.getAuthentication();
//			if (authentication != null && authentication.getPrincipal() != null) {
//				logger.info("LoginLogoutEventListener = > Logout Functionality");
//				UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//				LocalDateTime logoutTime = LocalDateTime.now();
//				Users user = userRepo.findByEmail(userDetails.getUsername());
//				if (user != null) {
//					user.setLastLogout(logoutTime);
//					List<UserTracker> tracker = new ArrayList<>();
//					UserTracker track = new UserTracker();
//					track.setLastlogout(LocalDateTime.now());
//					track.setUsername(user.getFullname());
//					tracker.add(track);
//					//user.setUsertracker(tracker);
//					//commented by Kiran
//				userRepo.save(user);
//				}
//			}
//		}
//	}
//
//}
