package com.narvee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private long userid;
	private String fullName;
	private String pseudoname;
	private String email;
	private String department;
	public String createdby;

}
