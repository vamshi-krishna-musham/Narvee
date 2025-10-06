package com.narvee.service.serviceimpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.TmsEmailConfigurationDto;
import com.narvee.entity.TmsEmailConfiguration;
import com.narvee.entity.TmsTask;
import com.narvee.repository.EmailConfigRepo;
import com.narvee.service.service.EmailConfigurationService;

@Service
public class EmailConfigurationServiceImpl implements EmailConfigurationService {
	private static final Logger logger = LoggerFactory.getLogger(EmailConfigurationServiceImpl.class);

	@Autowired
	private EmailConfigRepo emailConfigRepo;

	@Override
	public Map<String, Object> saveEmailConfiguration(List<TmsEmailConfigurationDto> tmsEmailConfigurationDto) {

		logger.info("!!! inside class: EmailConfigurationServiceImpl , !! method: saveEmailConfiguration");

		List<TmsEmailConfiguration> entitiesToSave = new ArrayList<>();
		List<String> duplicates = new ArrayList<>();

		for (TmsEmailConfigurationDto dto : tmsEmailConfigurationDto) {
			boolean exists = emailConfigRepo.existsByAdminIdAndEmailNotificationType(dto.getAdminId(),
					dto.getEmailNotificationType());

			if (!exists) {
				TmsEmailConfiguration email = new TmsEmailConfiguration();
				email.setAdminId(dto.getAdminId());
				email.setEmailNotificationType(dto.getEmailNotificationType());
				email.setIsEnabled(dto.getIsEnabled());
				
				email.setCcMails(dto.getCcMails() != null ? String.join(",", dto.getCcMails()) : null);
				
				email.setBccMails(dto.getBccMails() != null ? String.join(",", dto.getBccMails()) : null);
				
				email.setSubject(dto.getSubject());
				entitiesToSave.add(email);
			} else {
				duplicates.add("Already exists for adminId " + dto.getAdminId() + " and type '"
						+ dto.getEmailNotificationType() + "'");
			}
		}

		List<TmsEmailConfiguration> saved = emailConfigRepo.saveAll(entitiesToSave);

		Map<String, Object> responseMap = new LinkedHashMap<>();
		responseMap.put("saved", saved);
		responseMap.put("duplicates", duplicates);
		return responseMap;
	}

	@Override
	public List<TmsEmailConfiguration> getEmailConfiguration(Long adminId) {
		return emailConfigRepo.findByAdminId(adminId);

	}

	@Override
	public TmsEmailConfiguration updateEmailConfiguration(TmsEmailConfigurationDto dto) {

	    Optional<TmsEmailConfiguration> optional = emailConfigRepo.findById(dto.getId());
	    if (!optional.isPresent()) {
	        throw new RuntimeException("Configuration not found for adminId " + dto.getAdminId()
	                + " and type " + dto.getEmailNotificationType());
	    }

	    TmsEmailConfiguration config = optional.get();

	    config.setIsEnabled(dto.getIsEnabled());
	    config.setSubject(dto.getSubject());

	    // Ensure CC is mapped to CC
	    if (dto.getCcMails() != null && !dto.getCcMails().isEmpty()) {
	        config.setCcMails(String.join(",", dto.getCcMails()));
	    } else {
	        config.setCcMails(null);
	    }
	    


	    // Ensure BCC is mapped to BCC
	    if (dto.getBccMails() != null && !dto.getBccMails().isEmpty()) {
	        config.setBccMails(String.join(",", dto.getBccMails()));
	    } else {
	        config.setBccMails(null);
	    }

	    return emailConfigRepo.save(config);
	}

	@Override
	public void sendTaskReminderEmail(TmsTask task, List<GetUsersDTO> userdetails, boolean overdue) {
		// TODO Auto-generated method stub
		
	}


}
