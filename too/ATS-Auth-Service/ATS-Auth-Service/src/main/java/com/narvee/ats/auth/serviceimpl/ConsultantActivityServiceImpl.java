package com.narvee.ats.auth.serviceimpl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narvee.ats.auth.entity.UserTracker;
import com.narvee.ats.auth.entity.Users;
import com.narvee.ats.auth.repository.ConsultantActivityHistoryRepo;
import com.narvee.ats.auth.service.ConsultantActivtyService;

@Service
public class ConsultantActivityServiceImpl implements ConsultantActivtyService {
     @Autowired
     private ConsultantActivityHistoryRepo activityHistoryRepo;
	
	// added by keerthi
	   @Override
		public void saveLoginTracking(Users userDetails, HttpServletRequest httpRequest) {
		  //  String clientIp = getClientIp(httpRequest);
		   String publicIp = getPublicIP();
		    // Save login details to the UserTracker table
		    UserTracker userTracker = new UserTracker();
		    userTracker.setUserId(userDetails.getUserid());
		    userTracker.setLastlogin(LocalDateTime.now());
		    userTracker.setUsername(userDetails.getFullname());
		    userTracker.setIpAddress(publicIp);
		    activityHistoryRepo.save(userTracker);
		}	
	   
	    public static String getPublicIP() {
	        String publicIp = "Unavailable";
	        try {
	            HttpClient client = HttpClient.newHttpClient();
	            HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create("https://api64.ipify.org?format=text")) // Fetches IPv4 or IPv6 public IP
	                .build();
	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	            publicIp = response.body();
	        } catch (IOException | InterruptedException e) {
	            e.printStackTrace();
	        }
	        return publicIp;
	    }
			
	 public List<UserTracker> getLoginHistoryByUserId(Long userId) {
	        return activityHistoryRepo.findAllByUserIdOrderByLastloginDesc(userId);
	    }
	 	 
     // calculate working hours by given date
	@Override
	public Duration findUserworkingHours(Long userId, LocalDate date) {
		LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<UserTracker> activities = activityHistoryRepo.findWorkingHoursByUserId(userId, startOfDay, endOfDay);

        Duration totalDuration = Duration.ZERO;

        for (UserTracker activity : activities) {
            if (activity.getLastlogout() != null) {
                totalDuration = totalDuration.plus(
                    Duration.between(activity.getLastlogin(), activity.getLastlogout())
                );
            }
        }
        return totalDuration;	
	}
    
	// calculate working hours between dates
	@Override
	public Map<LocalDate, Duration> calculateWorkingHoursBetweenDates(Long userId, LocalDate startDate,LocalDate endDate) {
		Map<LocalDate, Duration> dailyWorkingHours = new LinkedHashMap<>();
		
        // Iterate over each date in the range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            List<UserTracker> activities = activityHistoryRepo.findWorkingHoursByUserId(userId, startOfDay, endOfDay);
            Duration totalDuration = Duration.ZERO;
            for (UserTracker activity : activities) {
                if (activity.getLastlogout() != null) {
                    totalDuration = totalDuration.plus(
                        Duration.between(activity.getLastlogin(), activity.getLastlogout())
                    );
                }
            }
            dailyWorkingHours.put(date, totalDuration);
        }
        return dailyWorkingHours; 	
	}

//	@Override
//	public Map<Long, Map<LocalDate, Duration>> calculateWorkingHoursForAllUsers(LocalDate startDate,LocalDate endDate) {
//		 Map<Long, Map<LocalDate, Duration>> userWorkingHoursMap = new LinkedHashMap<>();
//	        // Convert to LocalDateTime for the query range
//	        LocalDateTime startOfDay = startDate.atStartOfDay();
//	        LocalDateTime endOfDay = endDate.plusDays(1).atStartOfDay(); // Ensure we capture the entire last day
//
//	        // Fetch all activities within the date range
//	        List<UserTracker> activities = activityHistoryRepo.findAllWorkingHours(startOfDay, endOfDay);
//
//	        // Iterate through all activities and aggregate them by user and date
//	        for (UserTracker activity : activities) {
//	            Long userId = activity.getUserId();
//	            LocalDate activityDate = activity.getLastlogin().toLocalDate();
//
//	            // Initialize the user's data if not already present
//	            userWorkingHoursMap.putIfAbsent(userId, new LinkedHashMap<>());
//	            Map<LocalDate, Duration> userDaysMap = userWorkingHoursMap.get(userId);
//
//	            // Aggregate working hours for the day
//	            Duration totalDuration = userDaysMap.getOrDefault(activityDate, Duration.ZERO);
//	            if (activity.getLastlogout() != null) {
//	                totalDuration = totalDuration.plus(Duration.between(activity.getLastlogin(), activity.getLastlogout()));
//	            }
//
//	            // Update the working hours map for that user and date
//	            userDaysMap.put(activityDate, totalDuration);
//	        }
//
//	        return userWorkingHoursMap;
//	}	
	@Override
	public Map<Long, Map.Entry<String, Map<LocalDate, Duration>>> calculateWorkingHoursForAllUsers(LocalDate startDate, LocalDate endDate) {
	    // Map to store userId -> (userName, Map<Date, Duration>)
	    Map<Long, Map.Entry<String, Map<LocalDate, Duration>>> userWorkingHoursMap = new LinkedHashMap<>();

	    LocalDateTime startOfDay = startDate.atStartOfDay();
	    LocalDateTime endOfDay = endDate.plusDays(1).atStartOfDay(); // Capture the last day

	  
	    List<UserTracker> activities = activityHistoryRepo.findAllWorkingHours(startOfDay, endOfDay);

	   
	    for (UserTracker activity : activities) {
	        Long userId = activity.getUserId();
	        String userName = activity.getUsername(); // Assuming `UserTracker` has `userName`
	        LocalDate activityDate = activity.getLastlogin().toLocalDate();

	        
	        userWorkingHoursMap.putIfAbsent(userId, Map.entry(userName, new LinkedHashMap<>()));
	        Map<LocalDate, Duration> userDaysMap = userWorkingHoursMap.get(userId).getValue();

	        
	        Duration totalDuration = userDaysMap.getOrDefault(activityDate, Duration.ZERO);
	        if (activity.getLastlogout() != null) {
	            totalDuration = totalDuration.plus(Duration.between(activity.getLastlogin(), activity.getLastlogout()));
	        }

	       
	        userDaysMap.put(activityDate, totalDuration);
	    }

	    return userWorkingHoursMap;
	}

	
	
	
}


