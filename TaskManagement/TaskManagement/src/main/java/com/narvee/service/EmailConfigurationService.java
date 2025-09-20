package com.narvee.service;

import java.util.List;
import java.util.Map;

import com.narvee.dto.TmsEmailConfigurationDto;
import com.narvee.entity.TmsEmailConfiguration;

public interface  EmailConfigurationService {
	
	 Map<String, Object>  saveEmailConfiguration(List<TmsEmailConfigurationDto> tmsEmailConfigurationDto);
	 public TmsEmailConfiguration updateEmailConfiguration(TmsEmailConfigurationDto dto);
	 List<TmsEmailConfiguration> getEmailConfiguration(Long adminId);
	 

}
