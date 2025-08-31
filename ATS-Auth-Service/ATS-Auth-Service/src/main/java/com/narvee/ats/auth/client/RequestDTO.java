package com.narvee.ats.auth.client;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private String number;
    private int limit;
    private int offset;
    private LocalDate from_date;
    private LocalDate to_date;
    
    
}