package com.narvee.dto;



public interface CompletedStatusCountResponse {
	
	     public String getperiod();     // e.g., 2025-07-01 (daily), 2025-07 (monthly), 2025 (yearly)
	     public String gettype();       // Task / Sub Task / Total
	     Integer getyear();
	     public String getstatus();     // Completed
	     public Long getcount();  

}
