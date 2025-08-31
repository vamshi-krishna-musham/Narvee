package com.narvee.ats.auth.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleToPrivilegesVO {
	private Long roleId;
	private Long companyId;
	private List<Long> privilegeIds;

}
