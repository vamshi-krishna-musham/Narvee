package com.narvee.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormat {
	
	public static String formatDate(String dateString) {
		 try {
	            // Attempt to parse as a LocalDateTime first
	            LocalDateTime dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
	            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	            return dateTime.format(outputFormatter);
	        } catch (DateTimeParseException e) {
	            // If parsing as LocalDateTime fails, attempt to parse as a LocalDate
	            try {
	                LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
	                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	                return date.format(outputFormatter);
	            } catch (DateTimeParseException ex) {
	                ex.printStackTrace();
	                return "";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "";
	        }
	    }
	}
