package com.narvee.dto;

import java.util.List;
import java.util.Set;

import com.narvee.entity.TmsAssignedUsers;
import com.narvee.entity.TmsFileUpload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDto {
	 private ProjectDTO projePage;
	
	 private Set<TmsAssignedUsers> assignUsers;
	//Set<GetUsersDTO> assignUsers;
	 private List<String> assignedUsers; //added by pratiksha 
	 private List<TmsFileUpload> files;

}
