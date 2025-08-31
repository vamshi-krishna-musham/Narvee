package com.narvee.ats.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "phonenumber")
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumberFormat {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	private String number;
	private String internationalNumber;
	private String nationalNumber;
	private String e164Number;
	private String countryCode;
	private String dialCode;

}
