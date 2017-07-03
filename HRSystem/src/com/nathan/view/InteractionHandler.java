package com.nathan.view;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;
import com.nathan.exception.BillingSuspendException;
import com.nathan.exception.PrintingSuspendException;
import com.nathan.model.AggregatingType;

public class InteractionHandler {

	private static Logger logger = Logger.getLogger(InteractionHandler.class);

	private static String title;

	private static ActionCallback callback;

	private static ActionType actionType = ActionType.Any;

	private static JFrame frame;

	public static void main(String[] args) throws Exception {

		Constant.propUtil.init();

		String companyPath = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
		Object[] companyList = Util.getFoldersUnderPath(companyPath).toArray();
		String company = (String) JOptionPane.showInputDialog(null, "��ѡ��λ��", title, JOptionPane.INFORMATION_MESSAGE,
				null, companyList, companyList[0]);
		logger.debug("��λ�� " + company);

		String path = companyPath + company;
		Object[] projectLeaderList = Util.parseProjectLeadersFromRosterFileUnderPath(path).toArray();
		String projectLeader = (String) JOptionPane.showInputDialog(null, "��ѡ����ӣ�", title,
				JOptionPane.INFORMATION_MESSAGE, null, projectLeaderList, projectLeaderList[0]);
		logger.debug("��ӣ� " + projectLeader);

		showMenu();

		handleRostersPathInput(Constant.ROSTER_ROOT_PATH);

		handleFullUpManual("ddd", 10);

		confirmExit(10);
	}

	public static void setActionCallback(ActionCallback callback) {
		InteractionHandler.callback = callback;
	}

	public static void setFrame(JFrame frame) {
		InteractionHandler.frame = frame;
	}

	public static void showMenu() {
		actionType = ActionType.Any;
		Object[] options = { "���ʱ�����", "����", "��ѯ", "������У��", "��������", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, "��ӭʹ�õ�ʢ������Ŀ����ϵͳ", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == 0) {
			handleBilling();
		}
		if (feedback == 1) {
			handleAggregation();
		}
		if (feedback == 3) {
			handleRosterValidation();
		}
		if (feedback == -1 || feedback == 5) {
			exit();
		}
		if (feedback == 2 || feedback == 4) {
			handleToDo(options[feedback], 0);
		}
	}

	public static void handleBilling() {
		title = BillingSystem.systemName;
		Object[] options = { "��ͨ��Ʊ", "���⿪Ʊ", "����", "�˳�" };
		try {
			int feedback = JOptionPane.showOptionDialog(frame, "���ʱ�����", title, JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			title = "���ʱ�����";
			if (feedback == -1 || feedback == 2) {
				callback.returnPerformed(ActionType.Billing);
				return;
				// showMenu();
			}
			if (feedback == 3) {
				exit(ActionType.Billing);
			}
			if (feedback == 0) {
				actionType = ActionType.Billing;
				callback.actionPerformed(ActionType.Billing);
			}
			if (feedback == 1) {
				actionType = ActionType.VirtualBilling;
				callback.actionPerformed(ActionType.VirtualBilling);
			}
		} catch (BillingSuspendException se) {
			logger.info("��Ʊ����ֹ��" + se.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			handleException("��Ʊ��;����" + e.getMessage());
		}
	}

	public static void handleAggregation() {
		title = BillingSystem.systemName;
		Object[] options = { "����ѻ���", "����������", "����", "�˳�" };
		try {
			int feedback = JOptionPane.showOptionDialog(frame, "��Ŀ����", title, JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (feedback == -1 || feedback == 2) {
				// showMenu();
				return;
			}
			if (feedback == 3) {
				exit();
			}
			if (feedback == 0) {
				actionType = ActionType.ServiceFeeAggregating;
				callback.actionPerformed(ActionType.ServiceFeeAggregating);
			}
			if (feedback == 1) {
				actionType = ActionType.BorrowingAggregating;
				callback.actionPerformed(ActionType.BorrowingAggregating);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			handleException("������;����" + e.getMessage());
		}

	}
	
	public static InteractionInput handleAggregationInput(AggregatingType aggregatingType) {
		if (AggregatingType.�����.equals(aggregatingType)) {
			title = "����ѻ���";
		} else if (AggregatingType.������.equals(aggregatingType)) {
			title = "����������";
		}
		
		String company = null;
		if (AggregatingType.�����.equals(aggregatingType)) {
			company = handleCompanyInputWithAllCompany();
		} else {
			company = handleCompanyInput();
		}
		if (company == null) {
			return null;
		}
		
		String projectLeader = null;
		if (AggregatingType.������.equals(aggregatingType)) {
			projectLeader = handleProjectLeaderInput(company);
			if (projectLeader == null) {
				return null;
			}
		}	

		int startYearMonth = handleStartYearMonthIntegerInput(201101);
		if (startYearMonth == 0) {
			return null;
		}

		int endYearMonth = handleEndYearMonthIntegerInput(startYearMonth);
		if (endYearMonth == 0) {
			return null;
		}

		InteractionInput input = new InteractionInput();
		input.setCompany(company);
		input.setProjectLeader(projectLeader);
		input.setStartYearMonth(startYearMonth);
		input.setEndYearMonth(endYearMonth);
		logger.debug(input);

		return input;
	}

	public static String handleCompanyInput() {
		String path = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
		Object[] companyArray = Util.getFoldersUnderPath(path).toArray();
		if (companyArray == null || companyArray.length == 0) {
			handleWarning(path + " Ŀ¼��û���κε�λ��");
			return null;
		}
		String company = (String) handleListSelection(companyArray, "��ָ�����ܵ�λ��", title);
		logger.debug("���ܵ�λ�� " + company);
		return company;
	}
	
	public static String handleCompanyInputWithAllCompany() {
		String path = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
		Object[] companyArray = Util.getFoldersUnderPath(path).toArray();
		if (companyArray == null || companyArray.length == 0) {
			handleWarning(path + " Ŀ¼��û���κε�λ��");
			return null;
		}
		int length = companyArray.length;
		companyArray = Arrays.copyOf(companyArray, length + 1);
		companyArray[length] = Constant.ALL_COMPANY;
		String company = (String) handleListSelection(companyArray, "��ָ�����ܵ�λ��", title);
		logger.debug("���ܵ�λ�� " + company);
		return company;
	}

	public static String handleProjectLeaderInput(String company) {
		String projectLeader = null;
		if(Constant.YES.equals(Constant.propUtil.getStringEnEmpty("system.allowInputProjectLearderByText"))) {
			Object[] options = {"�������", "ѡ�����", "ȡ��" };
			int feedback = JOptionPane.showOptionDialog(frame, "���ܵ�λ��" + company, title, JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			
			if (feedback == -1 || feedback == 2) {
				return null;
			}
			if (feedback == 0) {
				projectLeader = (String) JOptionPane.showInputDialog(frame, "�����������ӣ�", title,
						JOptionPane.INFORMATION_MESSAGE, null, null, null);
				logger.debug("����Ļ�����ӣ�" + projectLeader);
				return projectLeader;
			}		
		}
		
		String path = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
		path = path + company;
		Object[] projectLeaderList = Util.parseProjectLeadersFromRosterFileUnderPath(path).toArray();
		if (projectLeaderList == null || projectLeaderList.length == 0) {
			handleWarning(path + " Ŀ¼��û���κ���ӻ����ᣡ");
			return null;
		}
		projectLeader = (String) handleListSelection(projectLeaderList, "��ָ��������ӣ�", title);
		logger.debug("ָ���Ļ�����ӣ� " + projectLeader);
		return projectLeader;
	}

	private static int handleStartYearMonthIntegerInput(int startTime) {
		Object[] timeArray = Util.buildYearMonthIntArray(startTime);
		Object time = handleListSelection(timeArray, "��ָ����ʼʱ��(YYYYMM)��", title);
		if (time == null) {
			return 0;
		}
		logger.debug("��ʼʱ�䣺 " + time);
		return (Integer) time;
	}

	private static int handleEndYearMonthIntegerInput(int startTime) {
		Object[] timeArray = Util.buildYearMonthIntArray(startTime);
		Object time = handleListSelection(timeArray, "��ָ����ֹʱ��(YYYYMM)��", title);
		if (time == null) {
			return 0;
		}
		logger.debug("��ֹʱ�䣺 " + time);
		return (Integer) time;
	}

	private static Object handleListSelection(Object[] list, String message, String title) {
		return JOptionPane.showInputDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE, null, list,
				list[list.length - 1]);
	}
	
	public static void handleWarning(String message) {
		logger.info(message);
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
	}

	public static void handleRosterValidation() {
		try {
			callback.actionPerformed(ActionType.RosterValidation);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			handleException("У�黨������;����" + e.getMessage());
		}
	}
	
	public static String handleRostersPathInput(String defaultPath) {
		title = BillingSystem.systemName;
		Object[] options = { "·��У��", "��λ���У��", "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, "������У��", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		title = "������У��";
		if (feedback == -1 || feedback == 2) {
			return null;
		}
		if (feedback == 3) {
			exit();
		}
		if (feedback == 0) {
			String path = (String) JOptionPane.showInputDialog(frame, "����������У�黨�����·��", title,
					JOptionPane.INFORMATION_MESSAGE, null, null, defaultPath);
			logger.debug("У�黨����·�� ��" + path);
			return path;
		}
		if (feedback == 1) {
			Object[] companyList = Util.getFoldersUnderPath(defaultPath).toArray();
			String company = (String) JOptionPane.showInputDialog(frame, "��ָ��У�鵥λ��", title,
					JOptionPane.INFORMATION_MESSAGE, null, companyList, companyList[0]);
			logger.debug("У�鵥λ�� " + company);

			if (company == null) {
				return null;
			} else {
				Object[] options1 = { "��ʼУ��", "ѡ�����", "ȡ��"};
				int feedback1 = JOptionPane.showOptionDialog(frame, "У�鵥λ�� " + company + "\r\n\r\n��ѡ����һ����", title, JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, options1, options1[0]);
				if (feedback1 == -1 || feedback1 == 2) {
					return null;
				}
				if (feedback1 == 0) {
					return defaultPath + company;
				}
			}

			String path1 = defaultPath + company;
			Object[] projectLeaderList = Util.parseProjectLeadersFromRosterFileUnderPath(path1).toArray();
			String projectLeader = (String) JOptionPane.showInputDialog(frame, "��ָ��У����ӣ�", title,
					JOptionPane.INFORMATION_MESSAGE, null, projectLeaderList, projectLeaderList[0]);
			logger.debug("У����ӣ� " + projectLeader);

			if (projectLeader == null) {
				return null;
			}
			return path1 + Constant.DELIMITER3 + projectLeader;
		}
		return null;
	}

	public static void handleProgressCompleted(String status) {
		Object[] options = { "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, status, title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == -1 || feedback == 0) {
			try {
				callback.returnPerformed(actionType);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// showMenu();
		}
		if (feedback == 1) {
			exit(actionType);
		}
	}

	public static void handleToDo(Object function, int level) {
		Object[] options = { "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, function + "�������������У������ڴ�", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == -1 || feedback == 0) {
			if (level == 0)
				// showMenu();
				if (level == 1)
				handleAggregation();
		}
		if (feedback == 1) {
			exit();
		}
	}

	public static InteractionInput handleFullUpManual(String contractID, int remainPayCount) throws Exception {
		title = "���˴���";
		Object[] options = { "�Զ�����", "ָ������", "ȡ����Ʊ" };
		int feedback = JOptionPane.showOptionDialog(frame, contractID + "��Ʊ�������㣬����" + remainPayCount + "�ˣ���ѡ����ʽ",
				title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);

		if (feedback == 1) {
			String path = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
			Object[] companyList = Util.getFoldersUnderPath(path).toArray();
			String company = (String) JOptionPane.showInputDialog(frame, "��ָ��һ�����˵�λ��", title,
					JOptionPane.INFORMATION_MESSAGE, null, companyList, companyList[0]);
			logger.debug("���˵�λ�� " + company);

			if (company == null) {
				handleProgressCompleted("��Ʊ��ֹ��");
				throw new BillingSuspendException();
			}

			String path1 = path + company;
			Object[] projectLeaderList = Util.parseProjectLeadersFromRosterFileUnderPath(path1).toArray();
			String projectLeader = (String) JOptionPane.showInputDialog(frame, "��ָ��һ��������ӣ�", title,
					JOptionPane.INFORMATION_MESSAGE, null, projectLeaderList, projectLeaderList[0]);
			logger.debug("������ӣ� " + projectLeader);

			if (projectLeader == null) {
				handleProgressCompleted("��Ʊ��ֹ��");
				throw new BillingSuspendException();
			}

			InteractionInput input = new InteractionInput();
			input.setCompany(company);
			input.setProjectLeader(projectLeader);
			return input;
		}

		if (feedback == 2 || feedback == -1) {
			if (confirmExit(remainPayCount) == 1) {
				handleFullUpManual(contractID, remainPayCount);
			}
		}

		return null;
	}

	public static InteractionInput handleFullUpFromOtherCompany(String currentCompany, int remainPayCount)
			throws Exception {

		title = "���˴���";

		// String company = null;
		// do {
		// company = JOptionPane.showInputDialog(null, currentCompany +
		// "���������꣬����" + remainPayCount + "�ˣ�������ָ��һ�����˵�λ��",
		// title, JOptionPane.INFORMATION_MESSAGE);
		// } while (currentCompany.equals(company) ||
		// Constant.EMPTY_STRING.equals(company));

		String path = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
		Object[] companyList = Util.getFoldersUnderPath(path, currentCompany).toArray();
		String company = (String) JOptionPane.showInputDialog(frame,
				currentCompany + "���������꣬����" + remainPayCount + "�ˣ�������ָ��һ�����˵�λ��", title, JOptionPane.INFORMATION_MESSAGE,
				null, companyList, companyList[0]);

		InteractionInput input = new InteractionInput();
		input.setCompany(company);
		logger.debug("�½��˵�λ�� " + company);

		if (company == null) {
			if (confirmExit(remainPayCount) == 1) {
				handleFullUpFromOtherCompany(currentCompany, remainPayCount);
			}
		}

		return input;
	}

	public static void handleIsBillingGoOn(String message) throws Exception {
		handleIsBillingGoOn("", message);
	}

	public static void handleIsBillingGoOn(String contractID, String message) throws Exception {
		if (!handleIsGoOn(contractID + message)) {
			// callback.actionSuspend(ActionType.Billing);
			handleProgressCompleted("��Ʊ��ֹ��");
			throw new BillingSuspendException();
		}
	}

	public static boolean handleIsGoOn(String message) {
		boolean isGoOn = false;
		Object[] options = { "��", "ȡ��" };
		int feedback = JOptionPane.showOptionDialog(frame, message + "���Ƿ������", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		logger.debug("handleIsGoOn feedback:" + feedback);
		if (feedback == 0) {
			isGoOn = true;
		}
		return isGoOn;
	}

	public static void handleBillingProgressReport(String contractID, String status, int totalToDo, int totalDone) {
		String message = contractID + status + " ( " + totalDone + " / " + totalToDo + " )";
		logger.info(message);
		JOptionPane.showMessageDialog(frame, message, "��Ʊ����", JOptionPane.INFORMATION_MESSAGE);
	}

	public static String handleConfigPath() {
//		if (!Util.needToCheckConfigPath()) {
//			return Constant.CONFIG_FILE;
//		}
		String path = Util.getSettingPropertiesPath();
//		String path = (String) JOptionPane.showInputDialog(frame, "������ϵͳ�����ļ�·��", BillingSystem.systemName,
//				JOptionPane.INFORMATION_MESSAGE, null, null, Util.getSettingPropertiesPath());
//		logger.debug("ϵͳ�����ļ�: " + path);
		if (path == null) {
			logger.error("�Ҳ��������ļ�!");
			exit();
		}
		return path;
	}

	public static void handleExpireChecking(int expireDate) {
		if (!Util.needToCheckExpireDate()) {
			return;
		}
		expireDate = Constant.propUtil.getIntValue(Constant.CONFIG_SYSTEM_expireDate, expireDate);
		if (Util.getCurrentDateInt() > expireDate) {
			String message = "���汾�Ѿ�������Ч��(" + expireDate + ")����ʹ�����°汾��";
			logger.error(message);
			JOptionPane.showMessageDialog(frame, message, "����", JOptionPane.ERROR_MESSAGE);
			exit();
		}
	}

	public static boolean handleWriteRetry(String message) {
		boolean retry = false;
		Object[] options = { "����", "�˳�", "����" };
		int feedback = JOptionPane.showOptionDialog(frame, message, title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		logger.debug("feedback " + feedback);
		if (feedback == -1 || feedback == 0) {
			// showMenu();
		}
		if (feedback == 1) {
			exit();
		}
		if (feedback == 2) {
			retry = true;
		}
		return retry;
	}

	public static void handleException(String message) {
		Object[] options = { "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, message, title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		if (feedback == -1 || feedback == 0) {
			try {
				callback.returnPerformed(ActionType.Billing);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				InteractionHandler.handleException(e.getMessage());
			}
			return;
			// showMenu();
		}
		if (feedback == 1) {
			exit();
		}
	}

	public static void handleExceptionWarning(String message) {
		Object[] options = { "����" };
		JOptionPane.showOptionDialog(frame, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
				options, options[0]);
	}

	public static int confirmExit(int remainPayCount) throws Exception {
		int result = JOptionPane.showConfirmDialog(frame, "�Ƿ�Ҫȡ����Ʊ��", "���˴���", JOptionPane.YES_NO_OPTION);
		logger.debug("�Ƿ�Ҫȡ����Ʊ�� " + result);
		if (result <= 0) {
			// callback.actionSuspend(ActionType.Billing);
			handleProgressCompleted("��Ʊ��ֹ��");
			throw new BillingSuspendException();
		}
		return 1;
	}

	public static void exit() {
		exit(ActionType.Any);
	}

	public static void exit(ActionType actionType) {
		try {
			callback.exitPerformed(actionType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Util.housekeep();
		logger.info("�˳�ϵͳ��");
		System.exit(0);
	}

	public static boolean handlePrintTaskConfirmation(String message, int totalToDo, int totalDone)
			throws PrintingSuspendException {
		boolean skip = true;
		Object[] options = { "����", "����", "����", "�˳�" };
		message = message + "   ( ���ȣ�" + totalDone + " / " + totalToDo + " )";
		int feedback = JOptionPane.showOptionDialog(frame, message, "��ӡ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		logger.debug("feedback " + feedback);
		if (feedback == -1 || feedback == 2) {
			if (confirmPrintingExit() == 1) {
				handlePrintTaskConfirmation(message, totalToDo, totalDone);
			}
			// showMenu();
		}
		if (feedback == 3) {
			exit(actionType);
		}
		if (feedback == 1) {
			skip = true;
		}
		if (feedback == 0) {
			skip = false;
		}
		return skip;
	}

	public static int confirmPrintingExit() throws PrintingSuspendException {
		int result = JOptionPane.showConfirmDialog(frame, "�Ƿ�Ҫȡ����ӡ��", "��ӡ����", JOptionPane.YES_NO_OPTION);
		logger.debug("�Ƿ�Ҫȡ����ӡ�� " + result);
		if (result <= 0) {
			handleProgressCompleted("��ӡ��ֹ��");
			throw new PrintingSuspendException();
		}
		return 1;
	}
}
