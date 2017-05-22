package com.nathan.view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;

public class InteractionHandler {

	private static Logger logger = Logger.getLogger(InteractionHandler.class);

	private static String title = "��ʢ������Ŀ����";

	private static ActionCallback callback;
	
	private static ActionType actionType = ActionType.Any;
	
	private static JFrame frame;

	public static void main(String[] args) throws Exception {
		// Object[] options = { "ȷ��", "ȡ��" };
		// int i = JOptionPane.showOptionDialog(null, "һ����Ʊ", "��ʢ������Ŀ����",
		// JOptionPane.DEFAULT_OPTION,
		// JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		// logger.debug("JOptionPane " + i);
		//
		Constant.propUtil.init();

		String companyPath = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
		Object[] companyList = Util.getFoldersUnderPath(companyPath).toArray();
		String company = (String) JOptionPane.showInputDialog(null, "��ѡ��λ��", title, JOptionPane.INFORMATION_MESSAGE,
				null, companyList, companyList[0]);
		logger.debug("��λ�� " + company);

		String path = companyPath + company;
		Object[] projectLeaderList = Util.parseProjectLeadersFromFileUnderPath(path).toArray();
		String projectLeader = (String) JOptionPane.showInputDialog(null, "��ѡ����ӣ�", title,
				JOptionPane.INFORMATION_MESSAGE, null, projectLeaderList, projectLeaderList[0]);
		logger.debug("��ӣ� " + projectLeader);

		// int a = JOptionPane.showInternalConfirmDialog(null,
		// "��Ʊ�������㣬�Ƿ���Ҫ�˹�����", "���˴���",
		// JOptionPane.YES_NO_CANCEL_OPTION,
		// JOptionPane.INFORMATION_MESSAGE);
		// System.out.println("a��" + a);

		// Object[] options1 = { "�Զ�����", "�˹���Ԥ", "ȡ����Ʊ" };
		// int b = JOptionPane.showOptionDialog(null, "��Ʊ�������㣬��ѡ����ʽ", "���˴���",
		// JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
		// null, options1, options1[2]);
		// System.out.println("b��" + b);
		//
		// String inputValue = JOptionPane.showInputDialog("Please input a
		// value");
		// logger.debug("inputValue " + inputValue);

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
		Object[] options = { "������Ʊ", "���⿪Ʊ", "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, "���ʱ�����", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == -1 || feedback == 2) {
			try {
				callback.returnPerformed(ActionType.Billing);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				InteractionHandler.handleException(e.getMessage());
			}
			return;
//			showMenu();
		}
		if (feedback == 3) {
			exit(ActionType.Billing);
		}
		if (feedback == 0) {
			try {
				actionType = ActionType.Billing;
				callback.actionPerformed(ActionType.Billing);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				handleException(e.getMessage());
			}
		}
		if (feedback == 1) {
			try {
				actionType = ActionType.VirtualBilling;
				callback.actionPerformed(ActionType.VirtualBilling);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				handleException(e.getMessage());
			}
		}
	}
	
	public static void handleAggregation() {
		Object[] options = { "����ѻ���", "�������", "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, "��Ŀ����", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == -1 || feedback == 2) {
//			showMenu();
			return;
		}
		if (feedback == 3) {
			exit();
		}
		if (feedback == 0) {
			try {
				actionType = ActionType.Aggregating;
				callback.actionPerformed(ActionType.Aggregating);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				handleException(e.getMessage());
			}
		}
		if (feedback == 1) {
			handleToDo(options[feedback], 1);
		}
	}
	
	public static String handleCompanyInput() {
		String path = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
		Object[] companyArray = Util.getFoldersUnderPath(path).toArray();
		String company = (String) handleListSelection(companyArray, "��ָ��һ�����ܵ�λ��", title);
		logger.debug("���ܵ�λ�� " + company);
		return company;
	}
	
	private static int handleStartYearMonthIntegerInput(int startTime) {
		Object[] timeArray = Util.buildYearMonthIntArray(startTime);
		Object time = handleListSelection(timeArray, "��ָ����ʼʱ��(YYYYMM)��", title);
		if (time == null) {
			return 0;
		}
		logger.debug("��ʼʱ�䣺 " + time);
		return (Integer)time;
	}
	
	private static int handleEndYearMonthIntegerInput(int startTime) {
		Object[] timeArray = Util.buildYearMonthIntArray(startTime);
		Object time = handleListSelection(timeArray, "��ָ����ֹʱ��(YYYYMM)��", title);
		if (time == null) {
			return 0;
		}
		logger.debug("��ֹʱ�䣺 " + time);
		return (Integer)time;
	}
	
	private static Object handleListSelection(Object[] list, String message, String title) {
		return JOptionPane.showInputDialog(frame, message, title,
				JOptionPane.INFORMATION_MESSAGE, null, list, list[list.length - 1]);
	}
	
	public static InteractionInput handleAggregationInput() {
		title = "����ѻ���";
		String company = handleCompanyInput();
		if (company == null) {
			return null;
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
		input.setStartYearMonth(startYearMonth);
		input.setEndYearMonth(endYearMonth);
		logger.debug(input);
		
		return input;
	}

	public static void handleRosterValidation() {
		try {
			callback.actionPerformed(ActionType.RosterValidation);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			handleException(e.getMessage());
		}
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
//			showMenu();
		}
		if (feedback == 1) {
			exit(actionType);
		}
	}

	public static void handleToDo(Object function, int level) {
		Object[] options = { "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, function + "�������������У������ڴ�", title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == -1 || feedback == 0) {
			if (level == 0)
//				showMenu();
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
			// String company = JOptionPane.showInputDialog(null, "�����뵥λ��",
			// title, JOptionPane.INFORMATION_MESSAGE);
			// logger.debug("��λ�� " + company);
			// while (Constant.EMPTY_STRING.equals(company)) {
			// company = JOptionPane.showInputDialog(null, "��λ����Ϊ�գ� ��������˵�λ��",
			// title, JOptionPane.INFORMATION_MESSAGE);
			// }
			
			String path = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
			Object[] companyList = Util.getFoldersUnderPath(path).toArray();
			String company = (String) JOptionPane.showInputDialog(frame, "��ָ��һ�����˵�λ��", title,
					JOptionPane.INFORMATION_MESSAGE, null, companyList, companyList[0]);
			logger.debug("���˵�λ�� " + company);

			if (company == null) {
				handleProgressCompleted("��Ʊ��ֹ��");
			}

			// String projectLeader = JOptionPane.showInputDialog(null,
			// "��������ӣ�", title, JOptionPane.INFORMATION_MESSAGE);
			// logger.debug("��ӣ� " + projectLeader);
			// while (Constant.EMPTY_STRING.equals(projectLeader)) {
			// projectLeader = JOptionPane.showInputDialog(null, "��Ӳ���Ϊ�գ�
			// ��������ӣ�", title,
			// JOptionPane.INFORMATION_MESSAGE);
			// }

			String path1 = path + company;
			Object[] projectLeaderList = Util.parseProjectLeadersFromFileUnderPath(path1).toArray();
			String projectLeader = (String) JOptionPane.showInputDialog(frame, "��ָ��һ��������ӣ�", title,
					JOptionPane.INFORMATION_MESSAGE, null, projectLeaderList, projectLeaderList[0]);
			logger.debug("������ӣ� " + projectLeader);

			if (projectLeader == null) {
				handleProgressCompleted("��Ʊ��ֹ��");
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
		
//		String company = null;
//		do {
//			company = JOptionPane.showInputDialog(null, currentCompany + "���������꣬����" + remainPayCount + "�ˣ�������ָ��һ�����˵�λ��",
//					title, JOptionPane.INFORMATION_MESSAGE);
//		} while (currentCompany.equals(company) || Constant.EMPTY_STRING.equals(company));

		String path = Constant.propUtil.getStringEnEmpty("user.�������Ŀ¼");
		Object[] companyList = Util.getFoldersUnderPath(path, currentCompany).toArray();
		String company = (String) JOptionPane.showInputDialog(frame, currentCompany + "���������꣬����" + remainPayCount + "�ˣ�������ָ��һ�����˵�λ��", title,
				JOptionPane.INFORMATION_MESSAGE, null, companyList, companyList[0]);
		
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

	public static void handleIsBillingGoOn(String contractID, String message) throws Exception {
		if (!handleIsGoOn(contractID + message)) {
			callback.actionSuspend(ActionType.Billing);
			handleProgressCompleted("��Ʊ��ֹ��");
		}
	}

	public static boolean handleIsGoOn(String message) throws Exception {
		boolean isGoOn = false;
		Object[] options = { "��", "ȡ��" };
		int feedback = JOptionPane.showOptionDialog(frame, message + "���Ƿ������", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
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
		String path = (String) JOptionPane.showInputDialog(frame, "�����������ļ�·��", "��ʢ������Ŀ����",
				JOptionPane.INFORMATION_MESSAGE, null, null, Constant.CONFIG_FILE);
		logger.debug("ϵͳ�����ļ�: " + path);
		if (path == null) {
			exit();
		}
		return path;
	}

	public static void handleExpireChecking(int expireDate) {
		if (Util.getCurrentDateInt() > expireDate) {
			logger.error("Exception: �����԰汾�Ѿ�������Ч�ڣ���ʹ�����°汾��");
			JOptionPane.showMessageDialog(frame, "�����԰汾�Ѿ�������Ч�ڣ���ʹ�����°汾��", "����", JOptionPane.ERROR_MESSAGE);
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
//			showMenu();
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
//			showMenu();
		}
		if (feedback == 1) {
			exit();
		}
	}

	public static int confirmExit(int remainPayCount) throws Exception {
		int result = JOptionPane.showConfirmDialog(frame, "�Ƿ�Ҫȡ����Ʊ��", "���˴���", JOptionPane.YES_NO_OPTION);
		logger.debug("�Ƿ�Ҫȡ����Ʊ�� " + result);
		if (result <= 0) {
			callback.actionSuspend(ActionType.Billing);
			handleProgressCompleted("��Ʊ��ֹ��");
			return -1;
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

	public static String handleRostersPathInput(String defaultPath) {
		String path = (String) JOptionPane.showInputDialog(frame, "����������У�黨�����·��", "������У��",
				JOptionPane.INFORMATION_MESSAGE, null, null, defaultPath);
		logger.debug("У�黨����·�� ��" + path);
		if (path == null) {
//			showMenu();
		}
		return path;
	}
	
	public static boolean handlePrintTaskConfirmation(String message, int totalToDo, int totalDone) {
		boolean skip = true;
		Object[] options = {"����", "����", "����", "�˳�"};
		message = message + "   ( ���ȣ�" + totalDone + " / " + totalToDo + " )";
		int feedback = JOptionPane.showOptionDialog(frame, message, "��ӡ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		logger.debug("feedback " + feedback);
		if (feedback == -1 || feedback == 2) {
			try {
				callback.returnPerformed(actionType);
			} catch (Exception e) {
				e.printStackTrace();
			}
//			showMenu();
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
}
