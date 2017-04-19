package com.nathan.common;

import java.util.Calendar;

public class Util {
	
	public static int getMonthIdentifierColumnIndexInRoster(int month) {
		return 2 * month + 3;
	}
	
	public static int getMonthAmountColumnIndexInRoster(int month) {
		return 2 * month + 4;
	}
	
	public static String getCurrentDateString() {
		Calendar now = Calendar.getInstance();  
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        String dateInInteger = String.valueOf(year * 10000 + month * 100 + day);
        //System.out.println("ÈÕÆÚ: " + dateInInteger);  
        	
        return dateInInteger;
	}

	public static String getCurrentYear() {
		Calendar now = Calendar.getInstance();  
        int year = now.get(Calendar.YEAR);
        return String.valueOf(year);
	}
	
	public static String getCurrentMonth() {
		Calendar now = Calendar.getInstance();  
		int month = now.get(Calendar.MONTH) + 1;
        return String.valueOf(month);
	}
}
