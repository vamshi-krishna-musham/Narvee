package com.narvee.ats.auth.serviceimpl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.narvee.ats.auth.controller.ResumeParseController;
import com.narvee.ats.auth.service.ResumeParseService;

@Service
public class ResumeParseServiceImpl implements ResumeParseService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(ResumeParseController.class);
	
	
	  public String parseResume(String resumeText) {

			  logger.info("!!! inside class: ResumeParseServiceImpl, !! method: resumeParsing"); 
			 
			String skillsPattern = "\\b(Skills|SKILLS|(?i)TECHNICAL SKILLS|(?i)Technical Skill Set|Soft Skills|(?i)Skill Set)"+  // Match skills headers
			                       "\\b[:\\s]*([\\s\\S]+?)(?=\\b(?i)"+ //Matches optional colon (:) or any whitespace characters (optional) after the header.
					                "(?:Experience|Internships|Academic Project|Project Details|Education|summary|Professional Experience|Projects?|Work Experience|Certifications|Profile|Achievements|Competitive Programming)\\b|$)";
			                        
			    Pattern pattern = Pattern.compile(skillsPattern);
			   
		        Matcher matcher = pattern.matcher(resumeText);

		        if (matcher.find()) {
		            return matcher.group(2).trim();
		        }
		    
		        return "Skills section  not found";
		   }
	  
				/* Pattern skillsPattern = Pattern.compile(patternString, Pattern.DOTALL);
				 * Matcher skillsMatcher = skillsPattern.matcher(resumeText);
				 * 
				 * StringBuilder technicalSkills = new StringBuilder(); if
				 * (skillsMatcher.find()) {
				 * technicalSkills.append(skillsMatcher.group(2).trim()).append("\n"); }
				 * 
				 * System.err.println(technicalSkills.toString().trim()); return
				 * technicalSkills.toString().trim();
				 */
			
			  public String convertDocumentToString(MultipartFile file) throws IOException
			  { 
				  logger. info("!!! inside class: ResumeParseServiceImpl, !! method: resumeParsing");
			  if (file.isEmpty()) { 
				  return null;
				  }
			  
			 
		// Read file content into memory
		byte[] fileContentBytes = file.getBytes();
		String fileType = getFileExtension(file.getOriginalFilename());

		String fileContent = null;
		switch (fileType) {
		case "txt":
			fileContent = readTextFileContent(new ByteArrayInputStream(fileContentBytes));
			break;
		case "pdf":
			// System.err.println("pdf");
			fileContent = readPdfFileContent(new ByteArrayInputStream(fileContentBytes));
			break;
		case "docx":
			fileContent = readDocxFileContent(new ByteArrayInputStream(fileContentBytes));
			break;
		case "doc":	
			fileContent = readDocFileContent(new ByteArrayInputStream(fileContentBytes));
			break;
		default:
			fileContent = null;
		}
		
		String Skills = parseResume(fileContent);
		return Skills;
	}

	private String readTextFileContent(InputStream inputStream) throws IOException {
		logger.info("!!! inside class: ResumeParseServiceImpl, !! method: readTextFileContent");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}

	private String readPdfFileContent(InputStream inputStream) throws IOException {
		logger.info("!!! inside class: ResumeParseServiceImpl, !! method: readPdfFileContent");
		try (PDDocument document = PDDocument.load(inputStream)) {
			PDFTextStripper pdfStripper = new PDFTextStripper();
			return pdfStripper.getText(document);
		}
	}

	private String getFileExtension(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return "";
		}
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}

	public static String readDocxFileContent(InputStream inputStream) throws IOException {
		logger.info("!!! inside class: ResumeParseServiceImpl, !! method: readDocxFileContent");
		XWPFDocument document = new XWPFDocument(inputStream);
		XWPFWordExtractor extractor = new XWPFWordExtractor(document);
		String text = extractor.getText();
		extractor.close();
		document.close();
		inputStream.close();
		return text;
	}

	public static String readDocFileContent(InputStream inputStream) throws IOException {
		logger.info("!!! inside class: ResumeParseServiceImpl, !! method: readDocFileContent");
		HWPFDocument document = new HWPFDocument(inputStream);
		WordExtractor extractor = new WordExtractor(document);
		String text = extractor.getText();
		extractor.close();
		document.close();
		inputStream.close();
		return text;
	}
}
/*
 * // Logic parsign Defferent way
 * 
 * public String parseResume(String resumeText) { // Define patterns for
 * "TECHNICAL SKILLS" and "SKILLS" Pattern skillsPattern = Pattern
 * .compile("(?i)(TECHNICAL\\s+SKILLS)(.+?)(?=\\n\\n|$)", Pattern.DOTALL);
 * Matcher skillsMatcher = skillsPattern.matcher(resumeText);
 * 
 * StringBuilder finalSkills = new StringBuilder(); if (skillsMatcher.find()) {
 * // Extract the matched skills section String skillsSection =
 * skillsMatcher.group(2).trim();
 * 
 * // Clean up the section by removing unwanted characters and trimming spaces
 * skillsSection = skillsSection.replaceAll("[?\\t]", "").trim();
 * 
 * // Split the skills section by newline character String[] skillCategories =
 * skillsSection.split("\\r?\\n"); int i=0;
 * 
 * for (String category : skillCategories) {
 * 
 * // Remove everything before the first colon int colonIndex =
 * category.indexOf(':'); if (colonIndex != -1) { String formattedCategory =
 * category.substring(colonIndex + 1).trim(); if(i>0) { finalSkills.append(",");
 * } finalSkills.append(formattedCategory);
 * 
 * i++; } else { finalSkills.append(category.trim()); } }
 * 
 * } return finalSkills.toString(); }
 * 
 * }
 */
