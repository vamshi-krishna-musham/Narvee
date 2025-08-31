package com.narvee.ats.auth.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.narvee.ats.auth.dto.SortingRequestDTO;
import com.narvee.ats.auth.entity.Qualification;


public interface IQualificationService {

	public boolean saveQualification(Qualification qualification);

	public Qualification getQualificationById(Long id);

	public List<Qualification> getAllQualifications();

	public void deleteQualificationById(Long id);

	public boolean update(Qualification qualification);
	
	public Page<Qualification> getAllQualificationWithSortingAndFiltering(SortingRequestDTO qualificationsortandfilterdto);
}
