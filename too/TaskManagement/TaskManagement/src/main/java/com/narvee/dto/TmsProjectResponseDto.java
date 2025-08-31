package com.narvee.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmsProjectResponseDto {
	 private Long pId;
	    private String projectid;
	    private String projectName;
	    private String description;
	    private Long addedBy;
	    private Long updatedBy;
	    private String status;
	    private String department;
	    private LocalDateTime createddate;

	    private List<AssignedUsersDto> assignedUsers;

	  
	    private List<FileUploadDto> files;
}
