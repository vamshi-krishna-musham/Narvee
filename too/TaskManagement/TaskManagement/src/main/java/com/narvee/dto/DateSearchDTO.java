package com.narvee.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateSearchDTO {

	private LocalDate startDate;

	private LocalDate targetDate;

	private String department = "empty";
}
