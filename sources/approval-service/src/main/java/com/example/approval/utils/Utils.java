package com.example.approval.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	public static String getCurrentDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
}
