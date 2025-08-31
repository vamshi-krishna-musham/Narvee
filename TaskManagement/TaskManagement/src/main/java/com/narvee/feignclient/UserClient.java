package com.narvee.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.narvee.dto.UserDTO;

@FeignClient("AUTH-SERVICE")
public interface UserClient {

	@GetMapping("auth/users/recruiterlist")
	public List<UserDTO> getUsers(@RequestHeader("AUTHORIZATION") String token);

	@GetMapping("auth/users/findByUserid/{userid}")
	public UserDTO FindByUserid(@RequestHeader("AUTHORIZATION") String token, @PathVariable Long userid);

	@GetMapping("auth/users/findAssinUsersCreatedBy/{userid}/{auserid}")
	public List<UserDTO> getTaskAssinedUsersAndCreatedBy(@RequestHeader("AUTHORIZATION") String token,
			@PathVariable Long userid, @PathVariable List<Long> auserid);

}
