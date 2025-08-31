package com.narvee.ats.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequestDTO {
	
	public Long userid;
	public String fullname;
	public String email;
	public String personalcontactnumber ;
	public String companycontactnumber;
	public String designation;
	public String alternatenumber ;

}
