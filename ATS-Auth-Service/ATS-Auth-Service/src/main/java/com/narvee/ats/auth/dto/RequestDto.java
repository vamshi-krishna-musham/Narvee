package com.narvee.ats.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RequestDto {
   private int pageNo;
   private int pageSize;
   private String keyword;
   private String sortField;
   private String sortOrder;
   private Long adminId;
}
