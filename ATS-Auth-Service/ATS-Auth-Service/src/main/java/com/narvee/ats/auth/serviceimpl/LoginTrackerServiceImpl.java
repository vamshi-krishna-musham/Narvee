package com.narvee.ats.auth.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.dto.LoginTrackDTO;
import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.entity.LoginTracker;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.repository.LoginTrackerRepository;
import com.narvee.ats.auth.service.ILoginTrackerService;

@Service
public class LoginTrackerServiceImpl implements ILoginTrackerService {

	@Autowired
	private LoginTrackerRepository loginTrackerRepository;

	@Override
	public void save(Users user, String systemIp, String networkIp, String status, String remarks) {
		LoginTracker tracker = new LoginTracker();
		tracker.setUser(user);
		tracker.setSystemIp(systemIp);
		tracker.setNetworkIp(networkIp);
		tracker.setStatus(status);
		tracker.setRemarks(remarks);
		loginTrackerRepository.save(tracker);
	}

	@Override
	public Page<LoginTrackDTO> getAllLoginTrack(SortingRequestDTO sortingRequestDTO) {
		String sortField = sortingRequestDTO.getSortField();
		String sortOrder = sortingRequestDTO.getSortOrder();
		Integer pageNo = sortingRequestDTO.getPageNumber();
		Integer pageSize = sortingRequestDTO.getPageSize();
		String keyword = sortingRequestDTO.getKeyword();
		String status = sortingRequestDTO.getStatus();
		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortField.equalsIgnoreCase("SystemIp"))
			sortField = "system_ip";
		else if (sortField.equalsIgnoreCase("NetworkIp"))
			sortField = "network_ip";
		else if (sortField.equalsIgnoreCase("LoginTime"))
			sortField = "LoginTime";
		else if (sortField.equalsIgnoreCase("Status"))
			sortField = "status";
		else if (sortField.equalsIgnoreCase("Remarks"))
			sortField = "remarks";
		if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}

		Sort sort = Sort.by(sortDirection, sortField);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		if (keyword.equalsIgnoreCase("empty")) {
			return loginTrackerRepository.getAllLoginTrackeWithPagination(pageable);

		} else {
			return loginTrackerRepository.getAllLoginTrackeWithKeyword(pageable, keyword);

		}
	}

}
