package com.narvee.ats.auth.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.narvee.ats.auth.clientdto.FilterTechAndSupportDTO;
import com.narvee.ats.auth.dto.TechAndSupportDTO;
import com.narvee.ats.auth.entity.TechAndSupport;

public interface ITechSupportService {
	public boolean saveTechSupp(TechAndSupport roles);

	public boolean updateTechSupp(TechAndSupport roles);

//	public List<TechAndSupportDTO> getAll(String search);

	public List<TechAndSupportDTO> all();

	public TechAndSupport getTechSupp(Long id);

	public boolean deleteSupp(Long id);

	public int changeStatus(String status, Long id, String remarks);
	
	public Page<TechAndSupportDTO> allWithSortingWithFiltering(FilterTechAndSupportDTO filtertechandsupportdto);


}
