package com.nathan.common;

public class Constant {
	
	public static final String SYSTEM_NAME = "德盛人力项目管理";
	public static final double MID_BASE_PAY = 1500;
	public static final double LOW_DAILY_PAY = 150;
	public static final double MID_DAILY_PAY = 160;
	public static final double HIGH_DAILY_PAY = 170;
	public static final double OVERTIME_PAY_STEP = 10;
	public static final double MAX_OVERTIME_PAY = 140;
	public static final double MIN_OVERTIME_PAY = 50;
	public static final double MIN_PERFORMANCE_PAY = 50;
	public static final String WORK_PATH = "F:/work/project/德盛人力项目管理系统/";
	public static final String BACKUP_PATH = WORK_PATH + "backup/";
	public static final String CONFIG_PATH = WORK_PATH + "config/";
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
	public static final String DELIMITER3 = "*";
	public static final String DELIMITER33 = "\\*";
	public static final String EMPTY_STRING = "";
	
	public static final String MATCHES = "[A-Za-z]:\\\\[^:?\"><*]*";  
	
	public static final String HANDLE_AUTO = "自动";
	public static final String HANDLE_MANUAL = "人工";
	public static final String HANDLE_禁止 = "禁止";
	
	public static final String ATTENDANCE_SHEET_FLAG = "是";
	public static final String FLAG_YES = "是";
	public static final String FLAG_NO = "否";
	
	public static final String YES = "Y";
	public static final String NO = "N";
	public static final String ALL = "ALL";
	
	public static final String ROSTER_CASH = "花名册";
	public static final String ROSTER_BANK = "网银";
	
	public static PropertiesUtils propUtil = new PropertiesUtils(CONFIG_FILE);
	
	public static final long ONE_DAY = 86400000L;
	
	public static final String PAGE_SIZE_A4 = "A4";
	public static final String PAGE_SIZE_A5 = "A5";
	
	public static final String CONFIG_个税起征点 = "user.个税起征点";
	public static final String CONFIG_高温补贴月份 = "user.高温补贴月份";
	public static final String CONFIG_高温补贴金额 = "user.高温补贴金额";
	public static final String CONFIG_社保金额 = "user.社保金额";
	public static final String CONFIG_基本工资中位数 = "user.基本工资中位数";
	public static final String CONFIG_绩效下限 = "user.绩效下限";
	public static final String CONFIG_日薪高 = "user.日薪高";
	public static final String CONFIG_日薪中 = "user.日薪中";
	public static final String CONFIG_日薪低 = "user.日薪低";
	public static final String CONFIG_加班费上限 = "user.加班费上限";
	public static final String CONFIG_加班费下限 = "user.加班费下限";
	public static final String CONFIG_加班费计算步幅 = "user.加班费计算步幅";
	
	
	public static final String CONFIG_工资表_制表人 = "user.工资表.制表人";
	public static final String CONFIG_工资表_复核人 = "user.工资表.复核人";
	public static final String CONFIG_付款单据_制表人 = "user.付款手续单据.制表人";
	public static final String CONFIG_汇总表加班费显示为其他单位 = "user.汇总表加班费显示为其他单位";
	public static final String CONFIG_汇总表标题显示时间单位 = "user.汇总表标题显示时间单位";
	public static final String CONFIG_汇总表不显示制表人单位 = "user.汇总表不显示制表人单位";
	public static final String CONFIG_默认工资表指定月份 = "user.默认工资表指定月份";
	public static final String CONFIG_默认开票处理方式 = "user.默认开票处理方式";
	public static final String CONFIG_默认借人处理方式 = "user.默认借人处理方式";
	public static final String CONFIG_开票计划路径 = "user.开票计划路径";
	
	public static final String CONFIG_汇总_输入路径 = "user.汇总.输入路径";
	public static final String CONFIG_汇总_劳务费_模板路径 = "user.汇总.劳务费.模板路径";
	public static final String CONFIG_汇总_劳务费_输出路径 = "user.汇总.劳务费.输出路径";
	public static final String CONFIG_汇总_借款情况_模板路径 = "user.汇总.借款情况.模板路径";
	public static final String CONFIG_汇总_借款情况_输出路径 = "user.汇总.借款情况.输出路径";
	
	public static final String CONFIG_SYSTEM_checkConfigPath = "system.checkConfigPath";
	public static final String CONFIG_SYSTEM_checkExpireDate = "system.checkExpireDate";
	public static final String CONFIG_SYSTEM_expireDate = "system.expireDate";
	public static final String CONFIG_SYSTEM_NAME = "system.name";
	public static final String CONFIG_SYSTEM_isNeededToRemoveRosterStatisticsRow = "system.isNeededToRemoveRosterStatisticsRow";
	
}
