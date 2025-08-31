package com.narvee.ats.auth.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.narvee.ats.auth.entity.UserTracker;
import com.narvee.ats.auth.entity.Users;

public interface ConsultantActivtyService {
	public void saveLoginTracking(Users userDetails, HttpServletRequest httpRequest);
    public List<UserTracker> getLoginHistoryByUserId( Long userId);
    public Duration findUserworkingHours(Long userId, LocalDate date);
    public Map<LocalDate, Duration> calculateWorkingHoursBetweenDates(Long userId, LocalDate startDate, LocalDate endDate) ;
    Map<Long, Map.Entry<String, Map<LocalDate, Duration>>> calculateWorkingHoursForAllUsers(LocalDate startDate, LocalDate endDate);
       
       
}
