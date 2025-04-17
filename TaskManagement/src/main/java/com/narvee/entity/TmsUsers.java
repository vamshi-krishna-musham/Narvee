package com.narvee.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tms_Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmsUsers {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	private String FullName;
	
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	private String email;
	private String contactNumber;
	private String addedBy;
	
	@NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must be at least 8 characters and include uppercase, lowercase, digit, and special character"
    )
	private String password;
	private String userRole;
	

}
