package com.nathan.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
		// System.out.println("日期: " + dateInInteger);

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
				folderList.add(name.substring(0, name.length() - 12));
			}
		}
		System.out.println("parseProjectLeadersUnderPath():" + folderList);
		return folderList;
	}

	public static void listAllFileUnderPath(String filepath, ArrayList<String> filesList) throws Exception {
		try {

			File file = new File(filepath);
			if (!file.isDirectory()) {
				// System.out.println("文件");
				// System.out.println("path=" + file.getPath());
				System.out.println("absolutepath=" + file.getAbsolutePath());
				// System.out.println("name=" + file.getName());
				parseFilePath(file.getAbsolutePath(), filesList);
			} else if (file.isDirectory()) {
				// System.out.println("文件夹");
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						// System.out.println("path=" + readfile.getPath());
						System.out.println("absolutepath=" + readfile.getAbsolutePath());
						// System.out.println("name=" + readfile.getName());
						parseFilePath(readfile.getAbsolutePath(), filesList);
					} else if (readfile.isDirectory()) {
						listAllFileUnderPath(filepath + "\\" + filelist[i], filesList);
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("listAllFileUnderPath()   Exception:" + e.getMessage());
		}
	}

	private static void parseFilePath(String filePath, ArrayList<String> filesList) {
		if (filePath.contains("花名册.xls")) {
			filePath = filePath.replaceAll("\\\\", "/");
			filesList.add(filePath);
		}
	}

	public static int getYearFromFilePath(String filePath) {
		try {
			int year = 0;
			if (filePath != null) {
				String[] s = filePath.split("/");
				try {
					year = Integer.valueOf(s[s.length - 2]);
				} catch (Exception e) {
					String fileName = s[s.length - 1];
					String yearStr = fileName.substring(fileName.length() - 12, fileName.length() - 8);
					// System.out.println("fileName:"+fileName +" " + yearStr);
					year = Integer.valueOf(yearStr);
				}
			}
			return year;
		} catch (Exception e) {
			System.out.println("路径或者文件名不符合规则！");
			throw e;
		}
	}

	public static boolean isFileExists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 
	 * 获取目录下所有文件
	 * 
	 * @param realpath
	 * @param files
	 * @return
	 */
	public static List<File> getFiles(String realpath, List<File> files) {
		File realFile = new File(realpath);
		if (realFile.isDirectory()) {
			File[] subfiles = realFile.listFiles();
			for (File file : subfiles) {
				if (file.isDirectory()) {
					getFiles(file.getAbsolutePath(), files);
				} else {
					files.add(file);
				}
			}
		}
		return files;
	}

	/**
	 * 获取目录下所有文件(按时间排序)
	 * 
	 * @param path
	 * @return
	 */
	public static List<File> getFileSort(String path) {
		List<File> list = getFiles(path, new ArrayList<File>());
		if (list != null && list.size() > 0) {
			Collections.sort(list, new Comparator<File>() {
				public int compare(File file, File newFile) {
					if (file.lastModified() < newFile.lastModified()) {
						return 1;
					} else if (file.lastModified() == newFile.lastModified()) {
						return 0;
					} else {
						return -1;
					}

				}
			});

		}

		return list;
	}
	
	public static void housekeep() {
		int size = Constant.propUtil.getIntValue("system.backup.housekeep.size", 100);
		int days = Constant.propUtil.getIntValue("system.backup.housekeep.days", 7);
		String backupPath = Constant.propUtil.getStringEnEmpty("user.文件备份路径");
		List<File> list = Util.getFileSort(backupPath);
		int n = 0;
		for (int i = 0; i < list.size(); i++) {
			long lastModifiedTime = list.get(i).lastModified();
			if (i >= size && lastModifiedTime < System.currentTimeMillis() - days * Constant.ONE_DAY) {
				list.get(i).delete();
				System.out.println("HOUSEKEEP：清除" + list.get(i).getName());
				n++;
			}
		}
		System.out.println("HOUSEKEEP：总共清除" + n + "个文件");
	}

	public static void main(String[] args) throws Exception {
		Util.parseProjectLeadersUnderPath("F:/work/project/德盛人力项目管理系统/in/湛江雷能/2015/");
		Util.parseProjectLeadersUnderPath("F:/work/project/德盛人力项目管理系统/in/雷能电力/2016/");

		ArrayList<String> filesList = new ArrayList<String>();
		Util.listAllFileUnderPath(Constant.ROSTER_ROOT_PATH, filesList);
		System.out.println(filesList);

		System.out.println(getYearFromFilePath("F:/work/project/德盛人力项目管理系统/in/花名册/湛江雷能/2017/张一2017年花名册.xls"));

		List<File> list = getFileSort("F:/work/project/德盛人力项目管理系统/backup/");
		for (int i = 0; i < list.size(); i++) {
			if (i >= 100 && list.get(i).lastModified() < System.currentTimeMillis() - Constant.ONE_DAY) {
				System.out.println("delete " + list.get(i).getName() + " : " + list.get(i).lastModified());
//				list.get(i).delete();
			}
		}
	}
	
}
