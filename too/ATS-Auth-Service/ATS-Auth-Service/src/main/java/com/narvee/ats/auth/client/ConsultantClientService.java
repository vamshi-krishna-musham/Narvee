package com.narvee.ats.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.narvee.ats.auth.dto.RegistrationRequest;

@FeignClient("CONSULTANT-SERVICE")
public interface ConsultantClientService {
	
	@PostMapping("/consultant/saveCon")
	public RegistrationRequest saveConsultant(@RequestBody RegistrationRequest request);
}
