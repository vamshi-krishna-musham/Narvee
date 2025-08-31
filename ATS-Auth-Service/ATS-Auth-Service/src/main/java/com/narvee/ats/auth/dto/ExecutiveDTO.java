package com.narvee.ats.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExecutiveDTO {
	public Long  id;
    public String name;
    public String pseudoname;
    public String role;
}
