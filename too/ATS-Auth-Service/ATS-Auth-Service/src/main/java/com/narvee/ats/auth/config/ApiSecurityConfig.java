package com.narvee.ats.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.narvee.ats.auth.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

	public static final Logger logger = LoggerFactory.getLogger(ApiSecurityConfig.class);

	@Autowired
	private UserAuthService userAuthService;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private ApiAuthenticationEntryPoint authenticationEntryPoint;

	@Override
	public void configure(WebSecurity web) throws Exception {
		logger.info("!!! inside class: ApiSecurityConfig, !! method: configure(WebSecurity web)");
		web.ignoring().antMatchers("/login/signin", "/login/sendlink", "/login/logout/*", "/login/change_password",
				"/login/kpt/signin", "/login/forgotPassword", "/login/validate/{userId}/{enteredOTP}",
				"/conLogin/emailVerification/{email}", "/conLogin/validateOtp/{id}/{otp}",
				"/conLogin/validateOtp/{id}/{otp}", "/conLogin/consultantRegistration", "/conLogin/signin",
				"/technology/tech", "/technology/getskillsbyid/{id}", "/resume/parse", "ws://localhost:1000/auth/*",
				"/visa/getVisas", "/qualification/getAll","/login/authantication","/login/authSignIn","/tms/login","/tms/userRegistration","/tms/forgotPassword",
				"/tms/emailVerification","/tms/validate/**","/tms/change_password","/tms/authantication-tms","/tms/authSignIn-tms");

	}
	
	//----------------------------------------------------------------------------------------------------------------------------
	
//	private static final String[] WHITE_LIST_URL = { "/login/signin", "/login/sendlink", "/login/logout/*", "/login/change_password",
//			"/login/kpt/signin", "/login/forgotPassword", "/login/validate/{userId}/{enteredOTP}",
//			"/conLogin/emailVerification/{email}", "/conLogin/validateOtp/{id}/{otp}",
//			"/conLogin/validateOtp/{id}/{otp}", "/conLogin/consultantRegistration", "/conLogin/signin",
//			"/technology/tech", "/technology/getskillsbyid/{id}", "/resume/parse", "ws://localhost:1000/auth/*",
//			"/visa/getVisas", "/qualification/getAll","/login/authantication","/login/authSignIn","/tms/login","/tms/userRegistration","/tms/forgotPassword",
//			"/tms/emailVerification","/tms/validate/**","/tms/change_password","/tms/authantication-tms","/tms/authSignIn-tms" };
//
//	
//	 @Override
//	    protected void configure(HttpSecurity http) throws Exception {
//	        http
//	            .cors().and().csrf().disable()
//	            .authorizeHttpRequests()
//	            .antMatchers(WHITE_LIST_URL).permitAll() // whitelist public URLs
//	            .anyRequest().authenticated()
//	            .and()
//	            .exceptionHandling()
//	            .authenticationEntryPoint(authenticationEntryPoint) // handles unauthorized access
//	            .and()
//	            .sessionManagement()
//	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//	        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//	    }
//
	 @Override
	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    	logger.info("!!! inside class: ApiSecurityConfig, !! method: configure(AuthenticationManagerBuilder)");
	        auth.userDetailsService(userAuthService)
	            .passwordEncoder(passwordEncoder());
	    }

	    @Bean
	    @Override
	    public AuthenticationManager authenticationManagerBean() throws Exception {
	    	logger.info("!!! inside class: ApiSecurityConfig, !! method:authenticationManagerBean");
	        return super.authenticationManagerBean();
	    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		logger.info("!!! inside class: ApiSecurityConfig, !! method: configure(HttpSecurity http)");
		http.cors().and().csrf().disable().authorizeRequests().antMatchers("/usit/**").permitAll().anyRequest()
				.authenticated().and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public RegistrationBean jwtAuthFilterRegister(JwtAuthenticationFilter filter) {
		logger.info(
				"!!! inside class: ApiSecurityConfig, !! method: jwtAuthFilterRegister(JwtAuthenticationFilter filter)");
		FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>(filter);
		registrationBean.setEnabled(false);
		return registrationBean;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		logger.info("!!! inside class: ApiSecurityConfig, !! method: passwordEncoder");
		return new BCryptPasswordEncoder();
	}

}
