package com.narvee.ats.auth.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class DropdownVO {
	private Long id;
	private String name;
	private boolean selected;
	private String cardType;

	public DropdownVO(Long id, String name, String cardType) {
		this.id = id;
		this.name = name;
		this.cardType = cardType;
	}

	public DropdownVO(Long id, String name, boolean selected, String cardType) {
		this.id = id;
		this.name = name;
		this.selected = selected;
		this.cardType = cardType;
	}

}
