package com.narvee.ats.auth.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Response {

	private String fullname;
	private long userid;
	private String token;
	private String roles;
	private long roleno;
	private String department;
	private String designation;
	private List<String> rolePrivileges;
	private List<String> cardPrivileges;
	
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastlogout;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastlogin;

    private String companyid;

}
