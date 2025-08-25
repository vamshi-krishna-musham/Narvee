
package com.narvee.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
@Configuration
public class FileConfig {

	
	@Bean
	public MultipartConfigElement multipartConfigElement() {
	    MultipartConfigFactory factory = new MultipartConfigFactory();
	    // set max size per file
	    factory.setMaxFileSize(DataSize.ofMegabytes(10));
	    // set max total upload size
	    factory.setMaxRequestSize(DataSize.ofMegabytes(30));
	    return factory.createMultipartConfig();
	}
}
