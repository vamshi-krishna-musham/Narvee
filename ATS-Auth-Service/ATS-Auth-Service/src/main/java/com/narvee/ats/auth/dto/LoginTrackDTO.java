package com.narvee.ats.auth.dto;

import java.time.LocalDateTime;

public interface LoginTrackDTO {

	public String getSystemIp();

	public String getNetworkIp();

	public LocalDateTime getLoginTime();

	public String getStatus();

	public String getRemarks();

}
