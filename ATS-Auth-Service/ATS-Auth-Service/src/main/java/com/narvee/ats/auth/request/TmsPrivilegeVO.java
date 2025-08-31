package com.narvee.ats.auth.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmsPrivilegeVO {
	private List<DropdownVO> teamMember;
	private List<DropdownVO> tasks;
	private List<DropdownVO> subTasks;
	private List<DropdownVO> projects;

}
