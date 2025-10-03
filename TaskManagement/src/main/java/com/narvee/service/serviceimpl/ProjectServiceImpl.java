package com.narvee.service.serviceimpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.narvee.dto.EmailConfigResponseDto;
import com.narvee.dto.GetUsersDTO;
import com.narvee.dto.ProjectDTO;
import com.narvee.dto.ProjectResponseDto;
import com.narvee.dto.RequestDTO;
import com.narvee.entity.TmsAssignedUsers;
import com.narvee.entity.TmsFileUpload;
import com.narvee.entity.TmsProject;
import com.narvee.repository.ProjectRepository;
import com.narvee.repository.TaskRepository;
import com.narvee.repository.fileUploadRepository;
import com.narvee.service.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

	@Autowired
	private EmailServiceImpl emailService;

	@Autowired
	private TmsEmailServiceImpl TmsEmailService;

	@Autowired
	private TaskRepository repository;

	@Autowired
	private ProjectRepository projectrepository;

	@Autowired
	private fileUploadRepository fileUploadRepository;

	private static final int DIGIT_PADDING = 4;

	@Value("${AppFilesDir}")
	private String UPLOAD_DIR;

	@Override
	public TmsProject saveproject(TmsProject project) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: saveProject");

		Long pmaxnumber = projectrepository.pmaxNumber();
		if (pmaxnumber == null) {
			pmaxnumber = 0L;
		}
		String valueWithPadding = String.format("%0" + DIGIT_PADDING + "d", pmaxnumber + 1);
		String value = "PROJ" + valueWithPadding;
		project.setProjectid(value);
		projectrepository.save(project);
		project.setPmaxnum(pmaxnumber + 1);

		Set<TmsAssignedUsers> addedByToAssignedUsers = project.getAssignedto();

		List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getUserid)
				.collect(Collectors.toList());
		List<GetUsersDTO> user = repository.getTaskAssinedUsersAndCreatedBy(project.getAddedBy(), usersids);
		try {

			emailService.sendCreateProjectEmail(project, user, true);

		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}

		return project;
	}

	@Override
	public TmsProject findByprojectId(Long pid) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: findByprojectId");
		TmsProject project = projectrepository.findById(pid).get();
		for (TmsAssignedUsers aUser : project.getAssignedto()) {
			GetUsersDTO user = repository.getUser(aUser.getUserid());
			aUser.setFullname(user.getFullname());
			aUser.setPseudoname(user.getPseudoname());
		}
		return project;

	}

	@Override
	public void deleteProject(Long pid) { // it will work for both ATS and TMS users
		logger.info("!!! inside class: ProjectServiceImpl , !! method: deleteProject");
		TmsProject project = projectrepository.findById(pid)
				.orElseThrow(() -> new RuntimeException("Project not found"));

		if (!project.getTasks().isEmpty()) {
			throw new IllegalStateException("Cannot delete project: It has associated tasks.");
		}
		projectrepository.deleteById(pid);
	}

	@Override
	public boolean updateproject(TmsProject updateproject) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: updateproject");
		Optional<TmsProject> optionalProject = projectrepository.findById(updateproject.getPId());
		if (optionalProject.isPresent()) {
			TmsProject project = optionalProject.get();
			project.setProjectName(updateproject.getProjectName());
			project.setAddedBy(updateproject.getAddedBy());
			project.setUpdatedBy(updateproject.getUpdatedBy());
			project.setDescription(updateproject.getDescription());
			project.setStatus(updateproject.getStatus());
			project.setTasks(updateproject.getTasks());
			project.setAssignedto(updateproject.getAssignedto());
			project.setDepartment(updateproject.getDepartment());
			projectrepository.save(project);

			Set<TmsAssignedUsers> addedByToAssignedUsers = project.getAssignedto();
			List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getUserid)
					.collect(Collectors.toList());
			List<GetUsersDTO> user = repository.getTaskAssinedUsersAndCreatedBy(project.getUpdatedBy(), usersids);
			try {
				emailService.sendCreateProjectEmail(project, user, false);
			} catch (UnsupportedEncodingException | MessagingException e) {
				e.printStackTrace();
			}

			return true;
		} else {
			return false;
		}

	}

	@Override
	public Page<ProjectDTO> findAllProjects(RequestDTO requestresponsedto) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: findAllProjects");

		System.err.println(requestresponsedto);
		String sortorder = requestresponsedto.getSortOrder();
		String sortfield = requestresponsedto.getSortField();
		String keyword = requestresponsedto.getKeyword();
		Integer pageNo = requestresponsedto.getPageNumber();
		Integer pageSize = requestresponsedto.getPageSize();
		Long userid = requestresponsedto.getUserid();
		String access = requestresponsedto.getAccess();

		if (sortfield.equalsIgnoreCase("projectid"))
			sortfield = "projectId";
		else if (sortfield.equalsIgnoreCase("projectname"))
			sortfield = "projectName";
		else if (sortfield.equalsIgnoreCase("status"))
			sortfield = "status";
		else if (sortfield.equalsIgnoreCase("addedBy"))
			sortfield = "addedBy";
		else
			sortfield = "updateddate";


		
		Sort.Direction sortDirection = Sort.Direction.ASC;
		if (sortorder != null && sortorder.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortfield);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		if (access.equalsIgnoreCase("SUPER_ADMIN")) { // changed for tms users Super Administrator to SUPER_ADMIN
			if (keyword.equalsIgnoreCase("empty")) {
				logger.info("!!! inside class: ProjectServiceImpl , !! method: findAllProjects, Empty");
				return projectrepository.findAllProjects(pageable, keyword);
			} else {
				logger.info("!!! inside class: ProjectServiceImpl , !! method: findAllProjects, Filter");
				return projectrepository.findAllProjectWithFiltering(pageable, keyword);

			}

		} else {

			if (keyword.equalsIgnoreCase("empty")) {
				logger.info("!!! inside class: ProjectServiceImpl , !! method: getAllProjectsByUser, Empty");
				return projectrepository.getAllProjectsByUser(userid, pageable);
			} else {
				logger.info("!!! inside class: ProjectServiceImpl , !! method: getAllProjectsByUserFilter, Filter");
				return projectrepository.getAllProjectsByUserFilter(pageable, keyword, userid);

			}
		}

	}

	// --------------------------------------- all methods replicated for tms users
	// Added By keerthi ----------------------
	@Override
	public TmsProject saveTmsproject(TmsProject project, List<MultipartFile> files) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: saveTmsproject");

		Long pmaxnumber = projectrepository.pmaxNumber();
		if (pmaxnumber == null) {
			pmaxnumber = 0L;
		}
		String valueWithPadding = String.format("%0" + DIGIT_PADDING + "d", pmaxnumber + 1);
		String value = "PROJ" + valueWithPadding;
		project.setProjectid(value);
		project.setPmaxnum(pmaxnumber + 1);
		TmsProject savedProject = projectrepository.save(project);

		Set<TmsAssignedUsers> addedByToAssignedUsers = project.getAssignedto();

		List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getTmsUserId)
				.collect(Collectors.toList());
		List<GetUsersDTO> user = repository.getTaskAssinedTmsUsersAndCreatedBy(project.getAddedBy(), usersids);
		try {
			
			
			EmailConfigResponseDto dto = projectrepository.getEmailNotificationStatus(project.getAdminId(), "PROJECT_CREATE");
			if(dto != null) {
			System.err.println("is enable  " + dto.getIsEnabled());
			if (Boolean.TRUE.equals(dto.getIsEnabled())) {
				String subject = dto.getSubject();
				List<String> ccList = Arrays.stream(Optional.ofNullable(dto.getCcMails()).orElse("").split(","))
						.map(String::trim).filter(str -> !str.isEmpty()).collect(Collectors.toList());

				List<String> bccList = Arrays.stream(Optional.ofNullable(dto.getBccMails()).orElse("").split(","))
						.map(String::trim).filter(str -> !str.isEmpty()).collect(Collectors.toList());
				TmsEmailService.sendCreateProjectEmail(project, user, true, subject, ccList, bccList);
			}
		}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		if (files != null && !files.isEmpty()) {
			
			List<TmsFileUpload> projectFiles = new ArrayList<>();

			try {
				Files.createDirectories(Paths.get(UPLOAD_DIR));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (MultipartFile file : files) {
				try {
					String originalFilename = file.getOriginalFilename();
					if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
						continue;
					}

					String nameWithoutExt = originalFilename;
					String ext = "";

					int dotIndex = originalFilename.lastIndexOf('.');
					if (dotIndex != -1) {
						nameWithoutExt = originalFilename.substring(0, dotIndex);
						ext = originalFilename.substring(dotIndex);
					}

					String newFileName = nameWithoutExt + "-" + savedProject.getProjectid() + ext;
					Path filePath = Paths.get(UPLOAD_DIR, newFileName);
					Files.write(filePath, file.getBytes());

					TmsFileUpload projectFile = new TmsFileUpload();
					projectFile.setFileName(newFileName);
					projectFile.setFilePath(filePath.toAbsolutePath().toString());
					projectFile.setFileType(file.getContentType());
					projectFile.setProject(savedProject);

					projectFiles.add(projectFile);

				} catch (IOException e) {
					logger.error("Failed to save file: " + file.getOriginalFilename(), e);
				}

				savedProject.getFiles().addAll(projectFiles);
				fileUploadRepository.saveAll(projectFiles);
			}
		}
		return project;
	}

	@Override
	public TmsProject findByprojectIdTms(Long pid) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: findByprojectId For Tms ");
		TmsProject project = projectrepository.findById(pid).get();
		for (TmsAssignedUsers aUser : project.getAssignedto()) {
			GetUsersDTO user = repository.gettmsUser(aUser.getTmsUserId());
			aUser.setFullname(user.getFullname());

		}

		if (project.getFiles() != null) {
			for (TmsFileUpload file : project.getFiles()) {
				file.getFileName(); // trigger loading
				file.getFilePath();
				file.getFileType();
			}
		}
		return project;
	}

	@Override
	public TmsProject updateprojectTms(TmsProject updateproject, List<MultipartFile> files) {
		logger.info("!!! inside class: ProjectServiceImpl , !! method: updateprojectTms ,!! for tms users");
		Optional<TmsProject> optionalProject = projectrepository.findById(updateproject.getPId());
		if (!optionalProject.isPresent()) {
			throw new RuntimeException("project Id  not found with ID: " + updateproject.getPId());
		}

		TmsProject project = optionalProject.get();
		
		if ("completed".equalsIgnoreCase(updateproject.getStatus())) {
		    long incompleteTasks = repository.countByProjectIdAndStatusNotTask(updateproject.getPId(), "closed");
		  //  long incompleteSubTasks = repository.countByProjectIdAndStatusNot(updateproject.getPId(), "closed");

		    if (incompleteTasks > 0 ) {
		        throw new RuntimeException("Cannot mark project as completed. Some tasks are still not closed.");
		    }

		   
		}	
		//added by vaishnavi
		project.setProjectName(updateproject.getProjectName());
		if (updateproject.getAddedBy() != null && 
			    !updateproject.getAddedBy().equals(project.getAddedBy())) {
			    logger.warn("Attempt to change addedBy ignored. Original creator retained: " + project.getAddedBy());
			}
		project.setAdminId(updateproject.getAdminId());
		project.setUpdatedBy(updateproject.getUpdatedBy());
		project.setDescription(updateproject.getDescription());
		project.setStatus(updateproject.getStatus());
		// project.setTasks(updateproject.getTasks());
		project.setAssignedto(updateproject.getAssignedto());
		project.setDepartment(updateproject.getDepartment());
		project.setStartDate(updateproject.getStartDate());
		project.setTargetDate(updateproject.getTargetDate());

		// project.setFiles(updateproject.getFiles());

		projectrepository.save(project);

		if (files != null && !files.isEmpty()) {
			List<TmsFileUpload> uploadedFiles = files.stream().filter(file -> file != null && !file.isEmpty())
					.map(file -> {
						String ext = Optional.ofNullable(file.getOriginalFilename()).filter(f -> f.contains("."))
								.map(f -> f.substring(f.lastIndexOf("."))).orElse("");
						String baseName = file.getOriginalFilename().replace(ext, "");
						String fileName = baseName + "-" + project.getProjectid() + ext;
						String fullPath = UPLOAD_DIR + fileName;

						try {
							Files.write(Paths.get(fullPath), file.getBytes());
						} catch (IOException e) {
							throw new RuntimeException("Failed to save file: " + fileName, e);
						}
						TmsFileUpload existing = fileUploadRepository.findByFileNameAndProject(fileName, updateproject);
						if (existing != null) {

							existing.setFileType(file.getContentType());
							existing.setFilePath(fullPath);
							return existing;
						} else {

							TmsFileUpload f = new TmsFileUpload();
							f.setFileName(fileName);
							f.setFilePath(fullPath);
							f.setFileType(file.getContentType());
							f.setProject(updateproject);
							return f;
						}
					}).collect(Collectors.toList());

			project.getFiles().addAll(uploadedFiles);
		}

		Set<TmsAssignedUsers> addedByToAssignedUsers = project.getAssignedto();
		List<Long> usersids = addedByToAssignedUsers.stream().map(TmsAssignedUsers::getTmsUserId)
				.collect(Collectors.toList());

		List<GetUsersDTO> user = repository.getTaskAssinedTmsUsersAndCreatedBy(project.getUpdatedBy(), usersids);
		TmsProject tmsProject = projectrepository.save(project);
		try {
			
			EmailConfigResponseDto dto = projectrepository.getEmailNotificationStatus(project.getAdminId(), "PROJECT_UPDATE");
			if(dto != null) {
			if (Boolean.TRUE.equals(dto.getIsEnabled())) {
				String subject = dto.getSubject();
				List<String> ccList = Arrays.stream(Optional.ofNullable(dto.getCcMails()).orElse("").split(","))
						.map(String::trim).filter(str -> !str.isEmpty()).collect(Collectors.toList());

				List<String> bccList = Arrays.stream(Optional.ofNullable(dto.getBccMails()).orElse("").split(","))
						.map(String::trim).filter(str -> !str.isEmpty()).collect(Collectors.toList());
				TmsEmailService.sendCreateProjectEmail(project, user, false, subject, ccList, bccList);
			}
		}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return tmsProject;

	}

	@Override
	public Page<ProjectResponseDto> findTmsAllProjects(RequestDTO requestresponsedto) {
	    logger.info("Inside ProjectServiceImpl → findTmsAllProjects");

	    // Extract request params
	    String sortOrder = requestresponsedto.getSortOrder();
	    String sortField = requestresponsedto.getSortField();
	    String keyword = requestresponsedto.getKeyword();
	    Integer pageNo = requestresponsedto.getPageNumber();
	    Integer pageSize = requestresponsedto.getPageSize();
	    Long userId = requestresponsedto.getUserid();
	    String access = requestresponsedto.getAccess();

	    // Normalize sort field → map DTO names to DB/projection names
	    String sf = (sortField == null) ? "" : sortField.trim().toLowerCase();

	    switch (sf) {
	        case "projectid": sortField = "projectId"; break;
	        case "projectname": sortField = "projectName"; break;
	        case "status": sortField = "status"; break;
	        case "projectdescription": sortField = "projectdescription"; break;
	        case "addedby": sortField = "addedByFullname"; break;   // ✅ changed
	        case "updatedby": sortField = "updatedByFullname"; break; // ✅ changed
	        case "startdate": sortField = "startDate"; break;
	        case "duedate":
	        case "targetdate":
	            sortField = "targetDate"; break;

	        case "updateddate": sortField = "updateddate"; break;
	        case "department": sortField = "department"; break;
	        default: sortField = "createdDate";
	    }

	    // Build pageable with sorting
	    Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
	    /*Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(direction, sortField));*/
	    Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), pageSize, Sort.by(direction, sortField));
  
	    
	    
	    //normalize keyword (empty string means no filter)
	    String keywordParam = (keyword == null || keyword.trim().isEmpty() || "empty".equalsIgnoreCase(keyword))
	            ? ""
	            : keyword.trim(); 
	    
	    // Pick repository method
	    Page<ProjectDTO> page;
	    if ("SUPER ADMIN".equalsIgnoreCase(access) || "ADMIN".equalsIgnoreCase(access) || "PROJECT MANAGER".equalsIgnoreCase(access)) {
	        Long adminId = "SUPER ADMIN".equalsIgnoreCase(access) ? userId : projectrepository.AdminId(userId);

	        page = ("empty".equalsIgnoreCase(keyword) || keyword == null)
	                ? projectrepository.findAllTmsProjects(pageable, adminId, userId)
	                : projectrepository.findAllTmsProjectWithFiltering(pageable, keyword, adminId, userId);

	    } else {
	        page = ("empty".equalsIgnoreCase(keyword) || keyword == null)
	                ? projectrepository.getAllProjectsByTmsUser(userId, pageable)
	                : projectrepository.getAllProjectsByTmsUserFilter(pageable, keyword, userId);
	    }

	    // Normalize keyword for manual filtering
	    String normalizedKeyword = (keyword != null) ? keyword.replaceAll("\\s+", " ").trim().toLowerCase() : "";

	    // Map results to response DTO
	    List<ProjectResponseDto> allProjects = page.stream()
	            .map(projectDTO -> {
	                TmsProject project = projectrepository.findById(projectDTO.getPid()).orElse(null);
	                ProjectResponseDto proj = new ProjectResponseDto();
	                proj.setProjePage(projectDTO);

	                // AssignedTo handling
	                List<String> assignedUserNames = new ArrayList<>();
	                if (projectDTO.getAssignedTo() != null && !projectDTO.getAssignedTo().isEmpty()) {
	                    assignedUserNames = Arrays.stream(projectDTO.getAssignedTo().split(","))
	                            .map(String::trim)
	                            .filter(s -> !s.isEmpty())
	                            .collect(Collectors.toList());
	                }
	                proj.setAssignedUsers(assignedUserNames);

	                // Additional mapping from entity
	             
	                if (project != null) {
	                    Set<TmsAssignedUsers> assignedUsersWithNames = project.getAssignedto().stream()
	                            .map(assignUser -> {
	                                GetUsersDTO user = repository.gettmsUser(assignUser.getTmsUserId());
	                                if (user != null) assignUser.setFullname(user.getFullname());
	                                return assignUser;
	                            }).collect(Collectors.toSet());
	                    proj.setAssignUsers(assignedUsersWithNames);
	                    proj.setFiles(project.getFiles());
	                }

	                // Manual keyword filter (for safety/fallback)
	                boolean matchesKeyword = "empty".equalsIgnoreCase(keyword) || normalizedKeyword.isEmpty();
	                if (!matchesKeyword) { DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	                matchesKeyword =
	                        (projectDTO.getProjectid() != null && projectDTO.getProjectid().toLowerCase().contains(normalizedKeyword))
	                        || (projectDTO.getProjectname() != null && projectDTO.getProjectname().toLowerCase().contains(normalizedKeyword))
	                        || (projectDTO.getStartDate() != null && 
	                            projectDTO.getStartDate().format(formatter).toLowerCase().contains(normalizedKeyword))
	                        || (projectDTO.getTargetDate() != null && 
	                            projectDTO.getTargetDate().format(formatter).toLowerCase().contains(normalizedKeyword))
	                        || (projectDTO.getUpdateddate() != null && 
	                            projectDTO.getUpdateddate().format(formatter).toLowerCase().contains(normalizedKeyword))
	                        || (projectDTO.getStatus() != null && projectDTO.getStatus().replaceAll("\\s+", " ").trim().toLowerCase().contains(normalizedKeyword))
	                        || (projectDTO.getAddedByFullname() != null && projectDTO.getAddedByFullname().replaceAll("\\s+", " ").trim().toLowerCase().contains(normalizedKeyword))
	                        || (projectDTO.getUpdatedByFullname() != null && projectDTO.getUpdatedByFullname().replaceAll("\\s+", " ").trim().toLowerCase().contains(normalizedKeyword))
	                        || assignedUserNames.stream().anyMatch(name ->
	                            name.replaceAll("\\s+", " ").trim().toLowerCase().contains(normalizedKeyword));
	                }

	                return matchesKeyword ? proj : null;
	            })
	           
	            .filter(Objects::nonNull)
	            .collect(Collectors.toList());

	   

	    return new PageImpl<>(allProjects, page.getPageable(), page.getTotalElements());
//added by pratiksha
	
	}}

