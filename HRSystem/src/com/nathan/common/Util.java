package com.nathan.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Util {
	
	public static int getMonthIdentifierColumnIndexInRoster(int month) {
		return 2 * month + 3;
	}
	
	public static int getMonthAmountColumnIndexInRoster(int month) {
		return 2 * month + 4;
	}
	
	public static String getCurrentDateString() {
        String dateInInteger = String.valueOf(getCurrentDateInt());
        //System.out.println("日期: " + dateInInteger);  
        	
        return dateInInteger;
	}
	
	public static int getCurrentDateInt() {
		Calendar now = Calendar.getInstance();  
        int day = now.get(Calendar.DAY_OF_MONTH);
        return getCurrentYearMonthInt() * 100 + day;
	}
	
	public static int getCurrentYearMonthInt() {
		Calendar now = Calendar.getInstance();  
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;

        return year * 100 + month;
	}
	
	public static int getCurrentYear() {
		Calendar now = Calendar.getInstance();  
        int year = now.get(Calendar.YEAR);
        return year;
	}
	
	public static int getCurrentMonth() {
		Calendar now = Calendar.getInstance();  
		int month = now.get(Calendar.MONTH) + 1;
        return month;
	}

	public static String getCurrentYearString() {
        return String.valueOf(getCurrentYear());
	}
	
	public static String getCurrentMonthString() {
        return String.valueOf(getCurrentMonth());
	}
	
	public static String getTabulator() {
		return Constant.propUtil.getStringValue("user.制表人", Constant.TABULATOR);
	}
	
	public static double getHighTemperatureAllowance(int month) {
		double highTemperatureAllowance = Constant.propUtil.getDoubleValue("user.高温补贴金额");
		String highTemperatureAllowanceMonths = Constant.propUtil.getStringEnEmpty("user.高温补贴月份");
		String[] months = highTemperatureAllowanceMonths.split(Constant.DELIMITER2);
		for (String m : months) {
			if (Integer.valueOf(m) == month) {
				return highTemperatureAllowance;
			}
		}
		return 0;
	}
	
	public static double getSocialSecurityAmount() {
		return Constant.propUtil.getDoubleValue("user.社保金额");
	}
	
	public static double getIndividualIncomeTaxThreshold() {
		return Constant.propUtil.getDoubleValue("user.个税起征点");
	}
	
	public static String getFileNameFromPath(String filePath) {
		String[] s = filePath.split("/");
		return s[s.length - 1];
	}
	
	public static List<String> getFoldersUnderPath(String path) {
		File file = new File(path);
		File[] array = file.listFiles();
		ArrayList<String> folderList = new ArrayList<String>();
		if (array == null) {
			return folderList;
		}	

		for (int i = 0; i < array.length; i++) {
			if (array[i].isDirectory()) {
				folderList.add(array[i].getName());
			}
		}

		return folderList;
	}
	
	public static List<String> parseProjectLeadersUnderPath(String path) {
		File file = new File(path);
		File[] array = file.listFiles();
		ArrayList<String> folderList = new ArrayList<String>();
		if (array == null) {
			return folderList;
		}	

		for (int i = 0; i < array.length; i++) {
			String name = array[i].getName();
			if (array[i].isDirectory()) {
				folderList.add(name);
			}
			if (array[i].isFile()) {
				folderList.add(name.substring(0, name.length()-12));
			}
		}
		System.out.println("parseProjectLeadersUnderPath():" + folderList);
		return folderList;
	}
	
	public static void main(String[] args) {
		System.out.println(Util.getFoldersUnderPath("F:/work/project/德盛人力项目管理系统/in/湛江雷能/2015/"));
		Util.parseProjectLeadersUnderPath("F:/work/project/德盛人力项目管理系统/in/雷能电力/2016/");
	}
}
