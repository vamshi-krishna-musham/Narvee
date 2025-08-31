package com.narvee.ats.auth.clientdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterTechAndSupportDTO {
	
       private Integer pageNumber;
       private Integer pageSize;
       private String sortField;
       private String sortOrder;
       private String keyword;

}
