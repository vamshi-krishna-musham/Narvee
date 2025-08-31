package com.narvee.ats.auth.dto;

import java.time.LocalDateTime;

public interface RolesResponseDto {
	
	 public long getroleid();
	 public String getrolename();
	 public String getstatus();
	 public Long getadmin_id();
	 public  long getaddedby();
	 public  long getupdatedby();
	 public String getaddedByName();
	 public String getupdatedByName();
	 public String  getdescription();
	 public  LocalDateTime  getcreateddate();
	 
}

