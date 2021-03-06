package com.nathan.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

public class Util {
	
	private static final Logger logger = Logger.getLogger(Util.class);

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
	
	public static Integer[] buildYearMonthIntArray(int startYearMonth) {
		int startYear = startYearMonth / 100;
		int startMonth = startYearMonth % 100;
		ArrayList<Integer> timeList = new ArrayList<Integer>();
		for(int y = startYear; y <= getCurrentYear(); y++) {
			int m, endM;
			if (y == startYear) {
				m = startMonth;
			} else {
				m = 1;
			}
			if (y == getCurrentYear()) {
				endM = getCurrentMonth();
			} else {
				endM = 12;
			}
			for (; m <= endM; m++) {
				timeList.add(y * 100 + m);
			}
		}
		Integer[] a = {};
		return timeList.toArray(a);
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
		return Constant.propUtil.getStringEnEmpty(Constant.CONFIG_工资表_制表人);
	}
	
	public static String getPaymentDocTabulator() {
		return Constant.propUtil.getStringEnEmpty(Constant.CONFIG_付款单据_制表人);
	}
	
	public static String getReviewer() {
		return Constant.propUtil.getStringEnEmpty(Constant.CONFIG_工资表_复核人);
	}

	public static double getHighTemperatureAllowance(int year, int month) throws Exception {
		double highTemperatureAllowance = Double.valueOf(getTimeNodeConfig(Constant.CONFIG_高温补贴金额, year, month));
		String highTemperatureAllowanceMonths = Constant.propUtil.getStringEnEmpty(Constant.CONFIG_高温补贴月份);
		String[] months = highTemperatureAllowanceMonths.split(Constant.DELIMITER2);
		for (String m : months) {
			if (Integer.valueOf(m) == month) {
				return highTemperatureAllowance;
			}
		}
		return 0;
	}

	public static double getSocialSecurityAmount(int year, int month) throws Exception {
		return Double.valueOf(getTimeNodeConfig(Constant.CONFIG_社保金额, year, month));	
	}
	
	public static double getIndividualIncomeTaxThreshold() {
		return Constant.propUtil.getDoubleValue(Constant.CONFIG_个税起征点);
	}
	
	public static double getIndividualIncomeTaxThreshold(int year, int month) throws Exception {
		return Double.valueOf(getTimeNodeConfig(Constant.CONFIG_个税起征点, year, month));
	}
	
	public static String getTimeNodeConfig(String configName, int year, int month) throws Exception {
		String[] s = Constant.propUtil.getStringDisEmpty(configName).split(Constant.DELIMITER2);
		String value = null;
		for (String a : s) {
			int time = Integer.valueOf(a.split(Constant.DELIMITER1)[0].trim());
			if (year * 100 + month >= time) {
				value = a.split(Constant.DELIMITER1)[1].trim();
			}
		}	
		return value;
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
		Collections.sort(folderList);
		return folderList;
	}
	
	public static List<String> getFoldersUnderPath(String path, String filter) {
		File file = new File(path);
		File[] array = file.listFiles();
		ArrayList<String> folderList = new ArrayList<String>();
		if (array == null) {
			return folderList;
		}

		for (int i = 0; i < array.length; i++) {
			if (array[i].isDirectory() && !array[i].getName().equals(filter)) {
				folderList.add(array[i].getName());
			}
		}

		return folderList;
	}

	public static List<String> parseProjectLeadersUnderPath(String path) {
		File file = new File(path);
		File[] array = file.listFiles();
		ArrayList<String> projectLeaderList = new ArrayList<String>();
		if (array == null) {
			return projectLeaderList;
		}

		for (int i = 0; i < array.length; i++) {
			String name = array[i].getName();
			if (array[i].isDirectory()) {
				projectLeaderList.add(name);
			}
			if (array[i].isFile()) {
				projectLeaderList.add(name.substring(0, name.length() - 12));
			}
		}
		System.out.println("parseProjectLeadersUnderPath():" + projectLeaderList);
		return projectLeaderList;
	}
	
	public static List<String> parseProjectLeadersFromFolderUnderPath(String path) {
		File file = new File(path);
		File[] array = file.listFiles();
		ArrayList<String> projectLeaderList = new ArrayList<String>();
		if (array == null) {
			return projectLeaderList;
		}

		for (int i = 0; i < array.length; i++) {
			String name = array[i].getName();
			if (array[i].isDirectory()) {
				projectLeaderList.add(name);
			}
		}
		System.out.println("parseProjectLeadersFromFoldersUnderPath():" + projectLeaderList);
		return projectLeaderList;
	}
	
	public static List<String> parseProjectLeadersFromRosterFileUnderPath(String path) {
		List<File> list = getFiles(path, new ArrayList<File>());
		ArrayList<String> projectLeaderList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String fileName = list.get(i).getName();
			if (list.get(i).isFile() && fileName.contains("花名册")) {
				String projectLeaderName = fileName.substring(0, fileName.length() - 12);
//				logger.debug(fileName + " - " + projectLeaderName);
				projectLeaderList.add(projectLeaderName);
			}
		}
		HashSet<String> h = new HashSet<String>(projectLeaderList);
		projectLeaderList.clear();
		projectLeaderList.addAll(h);
		logger.debug("parseProjectLeadersFromFileUnderPath():" + projectLeaderList);
		Collections.sort(projectLeaderList);
		logger.debug("parseProjectLeadersFromFileUnderPath() sort:" + projectLeaderList);
		return projectLeaderList;
	}

	public static void listAllFileUnderPath(String filepath, ArrayList<String> filesList, String prefixFilter) throws Exception {
		try {
			if (filepath.contains(Constant.DELIMITER3)) {
				String[] s = filepath.split(Constant.DELIMITER33);
				filepath = s[0];
				prefixFilter = s[1];
			}
			logger.debug("List all files under: " + filepath + " with prefixFilter: " + prefixFilter);
			
			File file = new File(filepath);
			if (!file.isDirectory()) {
				// System.out.println("文件");
				// System.out.println("path=" + file.getPath());
//				System.out.println("absolutepath=" + file.getAbsolutePath());
				// System.out.println("name=" + file.getName());
				parseFilePath(file, filesList, prefixFilter);
			} else if (file.isDirectory()) {
				// System.out.println("文件夹");
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						// System.out.println("path=" + readfile.getPath());
//						System.out.println("absolutepath=" + readfile.getAbsolutePath());
						// System.out.println("name=" + readfile.getName());
						parseFilePath(readfile, filesList, prefixFilter);
					} else if (readfile.isDirectory()) {
						listAllFileUnderPath(filepath + "\\" + filelist[i], filesList, prefixFilter);
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("listAllFileUnderPath()   Exception:" + e.getMessage());
		}
	}

	private static void parseFilePath(File file, ArrayList<String> filesList, String prefixFilter) {
		String fileName = file.getName();
		String filePath = file.getAbsolutePath();
		if (fileName.contains("花名册.xls") && (prefixFilter == null || fileName.startsWith(prefixFilter))) {
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
		boolean flag = false;
		try {
			File file = new File(filePath);
			flag = file.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean deleteFile(String filePath) {
		boolean flag = false;
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			flag = file.delete();
			logger.debug(flag + " 删除文件：" + filePath);
		}
		return flag;
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
		logger.info("HOUSEKEEP - " + backupPath);
		List<File> list = Util.getFileSort(backupPath);
		int n = 0;
		for (int i = 0; i < list.size(); i++) {
			long lastModifiedTime = list.get(i).lastModified();
			if (i >= size && lastModifiedTime < System.currentTimeMillis() - days * Constant.ONE_DAY) {
				list.get(i).delete();
				logger.debug("HOUSEKEEP - 清除:" + list.get(i).getName());
				n++;
			}
		}
		logger.info("HOUSEKEEP - 总共清除" + n + "个过期备份文件");
	}
	
	public static boolean isConfigMatched(String valueToMatch, String config) {
		String configValue = Constant.propUtil.getStringEnEmpty(config);
//		logger.debug("正在匹配：" + valueToMatch + " =>> " + configValue);
		if (configValue.contains(Constant.DELIMITER2)) {
			String[] s = configValue.split(Constant.DELIMITER2);
			for (String c : s) {
				if(c.trim().equals(valueToMatch)) {
					return true;
				}
			}
			return false;
		}
		return configValue.trim().equals(valueToMatch) || Constant.ALL.equals(configValue);
	}
	
	public static double getDailyPayByBasePay(double basePay) {
		double dailyPay = 0.0;
		double midBasePay = Constant.propUtil.getDoubleValue(Constant.CONFIG_基本工资中位数, Constant.MID_BASE_PAY);
		if (basePay > midBasePay) {
			dailyPay = getHighDailyPay();
		} else if (basePay == midBasePay) {
			dailyPay = getMidDailyPay();
		} else {
			dailyPay = getLowDailyPay();
		}
		return dailyPay;
	}
	
	public static double getHighDailyPay() {
		 return Constant.propUtil.getDoubleValue(Constant.CONFIG_日薪高, Constant.HIGH_DAILY_PAY);
	}
	
	public static double getMidDailyPay() {
		 return Constant.propUtil.getDoubleValue(Constant.CONFIG_日薪中, Constant.MID_DAILY_PAY);
	}
	
	public static double getLowDailyPay() {
		 return Constant.propUtil.getDoubleValue(Constant.CONFIG_日薪低, Constant.LOW_DAILY_PAY);
	}
	
	public static double getMinPerformancePay() {
		return Constant.propUtil.getDoubleValue(Constant.CONFIG_绩效下限, Constant.MIN_PERFORMANCE_PAY);
	}
	
	public static double getMaxOvertimePay() {
		return Constant.propUtil.getDoubleValue(Constant.CONFIG_加班费上限, Constant.MAX_OVERTIME_PAY);
	}
	
	public static double getMinOvertimePay() {
		return Constant.propUtil.getDoubleValue(Constant.CONFIG_加班费下限, Constant.MIN_OVERTIME_PAY);
	}
	
	public static double getOvertimePayStep() {
		return Constant.propUtil.getDoubleValue(Constant.CONFIG_加班费计算步幅, Constant.OVERTIME_PAY_STEP);
	}
	
	public static String getFooterContents(String company, String contractID, int order) {
		return company + Constant.DELIMITER1 + contractID + Constant.DELIMITER1 + order;
	}
	
	public static boolean needToCheckConfigPath() {
		return Constant.YES.equals(Constant.propUtil.getStringValue(Constant.CONFIG_SYSTEM_checkConfigPath, Constant.YES));
	}
	
	public static boolean needToCheckExpireDate() {
		return Constant.YES.equals(Constant.propUtil.getStringValue(Constant.CONFIG_SYSTEM_checkExpireDate, Constant.YES));
	}
	
	public static void openTxtFile(String filePath) {
		try {
			Runtime.getRuntime().exec("cmd.exe  /c notepad " + filePath);
		} catch (Exception e) {
			logger.error("无法打开文件：" + filePath, e);
		}
	}
	
	public static String getCurrentAppPath() {
		File directory = new File("");// 设定为当前文件夹
		String path = null;
		try {
			path = directory.getAbsolutePath();
		} catch (Exception e) {
		}
//		logger.debug("Current application path: " + path);
		return path;
	}
	
	public static String getSettingPropertiesPath() {
		String path = getCurrentAppPath() + "/config/setting.properties";
		if (!Util.isFileExists(path)) {
			path = Constant.SETTING_CONFIG_FILE;
		} 
		logger.debug("Setting配置文件: " + path);
		return path;
	}
	
	public static String getLog4jPropertiesPath() {
//		String path = Constant.propUtil.getStringEnEmpty(Constant.CONFIG_SYSTEM_log4jconfigPath);
		String path = Constant.EMPTY_STRING;
		if (Constant.EMPTY_STRING.equals(path)) {
			path = getCurrentAppPath() + "/config/log4j.properties";
			if (!Util.isFileExists(path)) {
				path = Constant.LOG4J_CONFIG_FILE;
			}
		}
		logger.debug("Log4j配置文件: " + path);
		return path;
	}

	public static void main(String[] args) throws Exception {
//		Util.parseProjectLeadersFromFolderUnderPath("F:/work/project/德盛人力项目管理系统/in/花名册/湛江雷能");
//		Util.parseProjectLeadersFromRosterFileUnderPath("F:/work/project/德盛人力项目管理系统/in/花名册/雷能电力");
//
//		ArrayList<String> filesList = new ArrayList<String>();
//		Util.listAllFileUnderPath(Constant.ROSTER_ROOT_PATH, filesList, null);
//		System.out.println(filesList);
//
//		System.out.println(getYearFromFilePath("F:/work/project/德盛人力项目管理系统/in/花名册/湛江雷能/2017/张一2017年花名册.xls"));
//
//		List<File> list = getFileSort("F:/work/project/德盛人力项目管理系统/backup/");
//		for (int i = 0; i < list.size(); i++) {
//			if (i >= 100 && list.get(i).lastModified() < System.currentTimeMillis() - Constant.ONE_DAY) {
////				System.out.println("delete " + list.get(i).getName() + " : " + list.get(i).lastModified());
////				list.get(i).delete();
//			}
//		}
//		
//		Constant.propUtil.init();
//		for (int i = 1; i<=12; i++) {
//			logger.debug(201600 + i + "社保金额-" + Util.getSocialSecurityAmount(2016, i));
//		}
//		for (int i = 1; i<=12; i++) {
//			logger.debug(201700 + i + "社保金额-" + Util.getSocialSecurityAmount(2017, i));
//		}
//		
//		for (int i = 1; i<=12; i++) {
//			logger.debug(201600 + i + "个税起征点-" + Util.getIndividualIncomeTaxThreshold(2016, i));
//		}
//		for (int i = 1; i<=12; i++) {
//			logger.debug(201700 + i + "个税起征点-" + Util.getIndividualIncomeTaxThreshold(2017, i));
//		}
//		
//		for (int i = 1; i<=12; i++) {
//			logger.debug(201600 + i + "高温补贴-" + Util.getHighTemperatureAllowance(2016, i));
//		}
//		for (int i = 1; i<=12; i++) {
//			logger.debug(201700 + i + "高温补贴-" + Util.getHighTemperatureAllowance(2017, i));
//		}
//		
//		for (int i : Util.buildYearMonthIntArray(201308)) {
//			logger.debug(i);
//		}
//		
//		logger.debug(isConfigMatched("吉电", Constant.CONFIG_汇总表加班费显示为其他单位));
//		logger.debug(isConfigMatched("吉电", Constant.CONFIG_汇总表标题显示时间单位));
//		logger.debug(1666.00 % 130);
//		logger.debug(1666.00 /130);
//		logger.debug(((int)74.00/10 /7 + 1)*10);
		
//		String s = "F:/work/project/德盛人力项目管理系统/in/花名册/雷能电力";
//		String s1 = s + "*" + "陈志强";
//		String sa[] = s1.split("\\*");
//		logger.debug(sa[0]);
//		logger.debug(sa[1]);
//		
//		ArrayList<String> filesList1 = new ArrayList<String>();
//		Util.listAllFileUnderPath(s1, filesList1, null);
//		for (String file : filesList1) {
//			logger.debug(file);
//		}

		Util.openTxtFile(Constant.SETTING_CONFIG_FILE);
		logger.debug(Util.getSettingPropertiesPath());
		logger.debug(Util.getLog4jPropertiesPath());
	}
	
	
}
