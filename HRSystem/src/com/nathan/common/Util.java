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
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        return year * 10000 + month * 100 + day;
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
	
	public static String getTabulator() {
		return Constant.propUtil.getStringValue("user.制表人", Constant.TABULATOR);
	}
	
	public static String getFileNameFromPath(String filePath) {
		String[] s = filePath.split("/");
		return s[s.length - 1];
	}
	
	public static List<String> getFoldersUnderPath(String path) {
		File file = new File(path);
		File[] array = file.listFiles();
		ArrayList<String> folderList = new ArrayList<String>();

		for (int i = 0; i < array.length; i++) {
			if (array[i].isDirectory()) {
				folderList.add(array[i].getName());
			}
		}

		return folderList;
	}
	
	public static void main(String[] args) {
		Util.getFoldersUnderPath("F:/work/project/德盛人力项目管理系统/in/湛江雷能/2015/");
	}
}
