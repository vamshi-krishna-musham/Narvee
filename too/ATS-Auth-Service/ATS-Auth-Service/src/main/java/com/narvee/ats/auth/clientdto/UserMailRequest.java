package com.narvee.ats.auth.clientdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMailRequest {
	private String email;
	private String username;
}
