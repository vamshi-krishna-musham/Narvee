package com.narvee.ats.auth.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamLeadDTO {
	 public Long id;
	    public String name;
	    public String pseudoname;
	    public String role;
	    
	    public List<ExecutiveDTO> executives = new ArrayList<>();
	    
	    
	    public TeamLeadDTO(Long id, String name,String pseudoname , String role) {
	        this.id = id;
	        this.name = name;
	        this.pseudoname = pseudoname;
	        this.role = role;
	    }
	    
	    public List<ExecutiveDTO> getExecutives() {
	        return executives;
	    }

}
