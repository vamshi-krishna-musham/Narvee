package com.narvee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDto {
        private Long id;
	    private String fileName;
	    private String filePath;
	    private String fileType;
}
