package com.narvee.usit.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {

	public static final List<String> openApiEndpoints = Arrays.asList("/auth/login/signin", "/eureka","/mail/resetPasswordEmailLink","/mail/userRegistrationMail" ,
			"/technology" , "/consultant/saveCon","/consultant/uploadMultiple" ,"/consultant/visa/visas","/consultant/qualification/all" , "/billpay/invoice/downloadInvoice" , 
			"requirement/getCareersPageRequirements","/task/api/userRegistration","/task/api/login");

	public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints.stream()
			.noneMatch(uri -> request.getURI().getPath().contains(uri));

}