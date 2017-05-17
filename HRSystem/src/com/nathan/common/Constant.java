package com.nathan.common;

public class Constant {
	
	public static final double MID_BASE_PAY = 1500;
	public static final double LOW_DAILY_PAY = 150;
	public static final double MID_DAILY_PAY = 160;
	public static final double HIGH_DAILY_PAY = 170;
	public static final double DEFAULT_OVERTIME_PAY = 70;
	public static final double MIN_PERFORMANCE_PAY = 50;
	public static final String WORK_PATH = "F:/work/project/��ʢ������Ŀ����ϵͳ/";
	public static final String BACKUP_PATH = WORK_PATH + "backup/";
	public static final String CONFIG_PATH = WORK_PATH + "conf/";
	public static final String CONFIG_FILE = CONFIG_PATH + "setting.properties";
	public static final String BILLING_INPUT_FILE = WORK_PATH + "in/��Ʊ�ƻ�.xls";
	public static final String BILLING_OUTPUT_FILE = WORK_PATH + "out/��Ʊ�ƻ�.xls";
	public static final String ROSTER_ROOT_PATH = WORK_PATH + "in/������/";
	public static final String ROSTER_FILE = ROSTER_ROOT_PATH + "in/������/NNNYYYY�껨����.xls";
	public static final String PAYROLL_FILE = WORK_PATH + "out/NNNYYYY�깤�ʱ�.xls";
	public static final String PAYROLL_TEMPLATE_FILE = WORK_PATH + "template/���ʱ�ģ��.xls";
	public static final String PAYMENT_DOC_FILE = WORK_PATH + "out/NNNCCCCC������������.xls";
	public static final String PAYMENT_DOC_TEMPLATE_FILE = WORK_PATH + "template/������������ģ��.xls";
	public static final String ATTENDANCE_SHEET_FILE = WORK_PATH + "out/NNNCCCCC���ڱ�.xls";
	public static final String ATTENDANCE_SHEET_TEMPLATE_FILE = WORK_PATH + "template/���ڱ�ģ��.xls";
	public static final String BANK_PAYMENT_SUMMARY_FILE = WORK_PATH + "out/UUUUNNNCCCCC�������ܱ�.xls";
	public static final String BANK_PAYMENT_SUMMARY_TEMPLATE_FILE = WORK_PATH + "template/�������ܱ�ģ��.xls";
	public static final String TABULATOR = "л����";

	public static final String LINE0 = "========================================================================";
	public static final String LINE1 = "------------------------------------------------------------------------";
	
	public static final String DELIMITER0 = "|";
	public static final String DELIMITER00 = "\\|";
	public static final String DELIMITER1 = "-";
	public static final String DELIMITER2 = ",";
	public static final String EMPTY_STRING = "";
	
	public static final String MATCHES = "[A-Za-z]:\\\\[^:?\"><*]*";  
	
	public static final String HANDLE_AUTO = "�Զ�";
	public static final String HANDLE_MANUAL = "�˹�";
	
	public static final String ATTENDANCE_SHEET_FLAG = "��";
	
	public static final String YES = "Y";
	public static final String NO = "N";
	
	public static final String ROSTER_CASH = "������";
	public static final String ROSTER_BANK = "����";
	
	public static PropertiesUtils propUtil = new PropertiesUtils(CONFIG_FILE);
	
	public static final long ONE_DAY = 86400000L;
	
	public static final String PAGE_SIZE_A4 = "A4";
	public static final String PAGE_SIZE_A5 = "A5";
	
	public static final String CONFIG_��˰������ = "user.��˰������";
	public static final String CONFIG_���²����·� = "user.���²����·�";
	public static final String CONFIG_���²������ = "user.���²������";
	public static final String CONFIG_�籣��� = "user.�籣���";
	public static final String CONFIG_����������λ�� = "user.����������λ��";
	public static final String CONFIG_��Ч���� = "user.��Ч����";
	public static final String CONFIG_��н�� = "user.��н��";
	public static final String CONFIG_��н�� = "user.��н��";
	public static final String CONFIG_��н�� = "user.��н��";
	public static final String CONFIG_�Ʊ��� = "user.�Ʊ���";
	public static final String CONFIG_������ = "user.������";
	public static final String CONFIG_���ܱ�Ӱ����ʾΪ������λ = "user.���ܱ�Ӱ����ʾΪ������λ";
	public static final String CONFIG_���ܱ������ʾʱ�䵥λ = "user.���ܱ������ʾʱ�䵥λ";
	public static final String CONFIG_Ĭ�Ϲ��ʱ�ָ���·� = "user.Ĭ�Ϲ��ʱ�ָ���·�";
	public static final String CONFIG_Ĭ�Ͽ�Ʊ����ʽ = "user.Ĭ�Ͽ�Ʊ����ʽ";
	public static final String CONFIG_Ĭ�Ͻ��˴���ʽ = "user.Ĭ�Ͻ��˴���ʽ";
	public static final String CONFIG_��Ʊ�ƻ�·�� = "user.��Ʊ�ƻ�·��";
	
}
