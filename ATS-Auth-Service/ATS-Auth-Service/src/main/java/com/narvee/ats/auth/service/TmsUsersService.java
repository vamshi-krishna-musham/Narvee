package com.narvee.ats.auth.service;


import java.io.IOException;
import java.util.List;

import javax.mail.Multipart;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.ats.auth.entity.OTP;
import com.narvee.ats.auth.entity.TmsUsers;
import com.narvee.ats.auth.tms.dto.AllTmsUsers;
import com.narvee.ats.auth.tms.dto.GetAllUserRequestDTO;
import com.narvee.ats.auth.tms.dto.TmsUsersDropDown;
import com.narvee.ats.auth.tms.dto.TmsUsersInfo;


public interface TmsUsersService {
	
	public TmsUsersInfo saveUsers(TmsUsersInfo info);
	
	void uploadProfilePhoto (Long userId, MultipartFile photo) throws IOException;
	
	public TmsUsers findByUserId(Long userid);

	public TmsUsers findByEmail(String email);
	
	public void deleteProfilePic(Long id);
	
	public OTP emailVerification(String email);
	
	public boolean isOTPValid(Long id, String enteredOTP);
	
	public void updatePassword(TmsUsers user, String newPsw);
	
	public List<TmsUsersDropDown>  getTmsUsersDropDown(Long adminId);
	
	public Page<AllTmsUsers> getAllUsersByAdmin(GetAllUserRequestDTO allUserRequestDTO );
	
	public void deleteTeamMember(Long TeamMemberId);
	
	public boolean updateTeamMember(TmsUsersInfo tmsUsersInfo);
	
	
	
	

}
