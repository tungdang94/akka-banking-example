package com.example.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static final String DATE_FORMAT_1 = "yyyyMMdd HH:mm:ss";
	
	public static String toDateString(Date date, String pattern) {
		if (date == null || pattern == null || pattern.isEmpty()) {
			return null;
		}
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}
	
	public static String getCurrentDateString() {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT_1);
		return df.format(Calendar.getInstance().getTime());
	}
	
}
