package com.nathan.common;

public class Constant {
	
	public static final double MID_BASE_PAY = 1500;
	public static final double LOW_DAILY_PAY = 150;
	public static final double MID_DAILY_PAY = 160;
	public static final double HIGH_DAILY_PAY = 170;
	public static final double DEFAULT_OVERTIME_PAY = 70;
	public static final String WORK_PATH = "F:/work/project/德盛人力项目管理系统/";
	public static final String BACKUP_PATH = WORK_PATH + "backup/";
	public static final String CONFIG_PATH = WORK_PATH + "conf/";
	public static final String CONFIG_FILE = CONFIG_PATH + "setting.properties";
	public static final String BILLING_INPUT_FILE = WORK_PATH + "in/开票计划.xls";
	public static final String BILLING_OUTPUT_FILE = WORK_PATH + "out/开票计划.xls";
	public static final String ROSTER_ROOT_PATH = WORK_PATH + "in/花名册/";
	public static final String ROSTER_FILE = ROSTER_ROOT_PATH + "in/花名册/NNNYYYY年花名册.xls";
	public static final String PAYROLL_FILE = WORK_PATH + "out/NNNYYYY年工资表.xls";
	public static final String PAYROLL_TEMPLATE_FILE = WORK_PATH + "template/工资表模板.xls";
	public static final String PAYMENT_DOC_FILE = WORK_PATH + "out/NNNCCCCC付款手续单据.xls";
	public static final String PAYMENT_DOC_TEMPLATE_FILE = WORK_PATH + "template/付款手续单据模板.xls";
	public static final String ATTENDANCE_SHEET_FILE = WORK_PATH + "out/NNNCCCCC考勤表.xls";
	public static final String ATTENDANCE_SHEET_TEMPLATE_FILE = WORK_PATH + "template/考勤表模板.xls";
	public static final String BANK_PAYMENT_SUMMARY_FILE = WORK_PATH + "out/UUUUNNNCCCCC网银汇总表.xls";
	public static final String BANK_PAYMENT_SUMMARY_TEMPLATE_FILE = WORK_PATH + "template/网银汇总表模板.xls";
	public static final String TABULATOR = "谢少芹";

	public static final String LINE0 = "========================================================================";
	public static final String LINE1 = "------------------------------------------------------------------------";
	
	public static final String DELIMITER0 = "|";
	public static final String DELIMITER00 = "\\|";
	public static final String DELIMITER1 = "-";
	public static final String DELIMITER2 = ",";
	public static final String EMPTY_STRING = "";
	
	public static final String MATCHES = "[A-Za-z]:\\\\[^:?\"><*]*";  
	
	public static final String HANDLE_AUTO = "自动";
	public static final String HANDLE_MANUAL = "人工";
	
	public static final String ATTENDANCE_SHEET_FLAG = "是";
	
	public static final String ROSTER_CASH = "花名册";
	public static final String ROSTER_BANK = "网银";
	
	public static PropertiesUtils propUtil = new PropertiesUtils(CONFIG_FILE);
	
	public static final long ONE_DAY = 86400000L;  
	
}
