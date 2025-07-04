package com.narvee.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompletedStatusCountResponse {
	
	     private String period;     // e.g., 2025-07-01 (daily), 2025-07 (monthly), 2025 (yearly)
	    private String type;       // Task / Sub Task / Total
	    private String status;     // Completed
	    private Long count;  

}
