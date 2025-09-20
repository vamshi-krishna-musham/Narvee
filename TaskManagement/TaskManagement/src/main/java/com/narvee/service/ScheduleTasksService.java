package com.narvee.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

public interface ScheduleTasksService {

	public void subtaskDeadlineExceededEmail() throws UnsupportedEncodingException, MessagingException;

	public void TaskdeadlineExceededEmail() throws UnsupportedEncodingException, MessagingException;

}
