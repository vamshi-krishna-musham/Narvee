package com.narvee.ats.auth.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManagerDTO {
	  public Long id;
	   // public String name;
	    public List<TeamLeadDTO> teamLeads = new ArrayList<>();
	    private List<ExecutiveDTO> directExecutives = new ArrayList<>();
	    
	    
	    
	    public ManagerDTO(Long id) {
	        this.id = id;	       
	    }
	    
	    public List<TeamLeadDTO> getTeamLeads() {
	        return teamLeads;
	    }
	    
	    public List<ExecutiveDTO> getDirectExecutives() {
	        return directExecutives;
	    }

}
