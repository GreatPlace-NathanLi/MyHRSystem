package com.nathan.view;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;

public class InteractionHandler {

	private static Logger logger = Logger.getLogger(InteractionHandler.class);

	private static final String title = "���˴���";
	
	private static ActionCallback callback;

	public static void main(String[] args) throws Exception {
		// Object[] options = { "ȷ��", "ȡ��" };
		// int i = JOptionPane.showOptionDialog(null, "һ����Ʊ", "��ʢ������Ŀ����",
		// JOptionPane.DEFAULT_OPTION,
		// JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		// logger.debug("JOptionPane " + i);
		//
		// Object[] possibleValues = { "First", "Second", "Third" };
		// Object selectedValue = JOptionPane.showInputDialog(null, "Choose
		// one", "Input",
		// JOptionPane.INFORMATION_MESSAGE, null,
		// possibleValues, possibleValues[0]);
		//
		// logger.debug("selectedValue " + selectedValue);

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
	
	public static void showMenu() {
		Object[] options = { "���ʱ�����", "��������", "��ѯ", "����", "������У��", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(null, "��ӭʹ�õ�ʢ������Ŀ����ϵͳ", "��ʢ������Ŀ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		logger.debug("feedback " + feedback);
		if (feedback == 0) {
			handleBilling();

		}
		if (feedback == 4) {
			handleRosterValidation();
		}
		if (feedback == -1 || feedback == 5) {
			exit();
		}
		if (feedback == 1 || feedback == 2 || feedback == 3) {
			handleToDo(options[feedback], 0);
		}
	}

	public static void handleBilling() {
		Object[] options = { "������Ʊ", "���⿪Ʊ", "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(null, "���ʱ�����", "��ʢ������Ŀ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		logger.debug("feedback " + feedback);
		if (feedback == 2) {
			showMenu();
		}
		if (feedback == -1 || feedback == 3) {
			exit();
		}
		if (feedback == 1) {
			handleToDo(options[feedback], 1);
		}
		if (feedback == 0) {
			try {
				callback.actionPerformed(ActionType.Billing);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				handleException(e.getMessage());
			}
		}
	}
	
	public static void handleRosterValidation() {
		try {
			callback.actionPerformed(ActionType.RosterValidation);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			handleException(e.getMessage());
		}
	}

	public static void handleProgressCompleted(String status) {
		Object[] options = { "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(null, status, "��ʢ������Ŀ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		logger.debug("feedback " + feedback);
		if (feedback == 0) {
			showMenu();
		}
		if (feedback == -1 || feedback == 1) {
			exit();
		}
	}

	public static void handleToDo(Object function, int level) {
		Object[] options = { "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(null, function + "�������������У������ڴ�", "��ʢ������Ŀ����",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		logger.debug("feedback " + feedback);
		if (feedback == 0) {
			if (level == 0)
				showMenu();
			if (level == 1)
				handleBilling();
		}
		if (feedback == -1 || feedback == 1) {
			exit();
		}
	}

	public static InteractionInput handleFullUpManual(String contractID, int remainPayCount) throws Exception {
		Object[] options = { "�Զ�����", "ָ������", "ȡ����Ʊ" };
		int feedback = JOptionPane.showOptionDialog(null, contractID + "��Ʊ�������㣬����" + remainPayCount + "�ˣ���ѡ����ʽ", title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);

		if (feedback == 1) {
			String company = JOptionPane.showInputDialog(null, "�����뵥λ��", title, JOptionPane.INFORMATION_MESSAGE);
			logger.debug("��λ�� " + company);
			while (Constant.EMPTY_STRING.equals(company)) {
				company = JOptionPane.showInputDialog(null, "��λ����Ϊ�գ� ��������˵�λ��", title, JOptionPane.INFORMATION_MESSAGE);
			}
			if (company == null) {
				if (confirmExit(remainPayCount) == 1) {
					handleFullUpManual(contractID, remainPayCount);
				}
			}

			String projectLeader = JOptionPane.showInputDialog(null, "��������ӣ�", title, JOptionPane.INFORMATION_MESSAGE);
			logger.debug("��ӣ� " + projectLeader);
			while (Constant.EMPTY_STRING.equals(projectLeader)) {
				projectLeader = JOptionPane.showInputDialog(null, "��Ӳ���Ϊ�գ� ��������ӣ�", title,
						JOptionPane.INFORMATION_MESSAGE);
			}
			if (projectLeader == null) {
				if (confirmExit(remainPayCount) == 1) {
					handleFullUpManual(contractID, remainPayCount);
				}
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

	public static InteractionInput handleFullUpFromOtherCompany(String currentCompany, int remainPayCount) throws Exception {

		String company = null;
		do {
			company = JOptionPane.showInputDialog(null, currentCompany + "���������꣬����" + remainPayCount + "�ˣ�������ָ��һ�����˵�λ��",
					title, JOptionPane.INFORMATION_MESSAGE);
		} while (currentCompany.equals(company) || Constant.EMPTY_STRING.equals(company));

		logger.debug("�½��˵�λ�� " + company);

		if (company == null) {
			if (confirmExit(remainPayCount) == 1) {
				handleFullUpFromOtherCompany(currentCompany, remainPayCount);
			}
		}

		InteractionInput input = new InteractionInput();
		input.setCompany(company);
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
		int feedback = JOptionPane.showOptionDialog(null, message + "���Ƿ������", "��ʢ������Ŀ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == 0) {
			isGoOn = true;
		}
		return isGoOn;
	}
	
	public static void handleBillingProgressReport(String contractID, String status, int totalToDo, int totalDone) {
		String message = contractID + status + " ( " + totalDone + " / " + totalToDo + " )";
		logger.info(message);
		JOptionPane.showMessageDialog(null, message, "��Ʊ����", JOptionPane.INFORMATION_MESSAGE);
	}

	public static String handleConfigPath() {
		String path = (String) JOptionPane.showInputDialog(null, "�����������ļ�·��", "��ʢ������Ŀ����",
				JOptionPane.INFORMATION_MESSAGE, null, null, Constant.CONFIG_FILE);
		logger.debug("�����ļ� " + path);
		if (path == null) {
			exit();
		}
		return path;
	}

	public static void handleExpireChecking(int expireDate) {
		if (Util.getCurrentDateInt() > expireDate) {
			logger.error("Exception: �����԰汾�Ѿ�������Ч�ڣ���ʹ�����°汾��");
			JOptionPane.showMessageDialog(null, "�����԰汾�Ѿ�������Ч�ڣ���ʹ�����°汾��", "����", JOptionPane.ERROR_MESSAGE);
			exit();
		}	
	}
	
	public static boolean handleWriteRetry(String message) {
		boolean retry = false;
		Object[] options = { "����", "�˳�", "����" };
		int feedback = JOptionPane.showOptionDialog(null, message, "��ʢ������Ŀ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		logger.debug("feedback " + feedback);
		if (feedback == 0) {
			showMenu();
		}
		if (feedback == -1 || feedback == 1) {
			exit();
		}
		if (feedback == 2) {
			retry = true;
		}
		return retry;
	}

	public static void handleException(String message) {
		Object[] options = { "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(null, message, "��ʢ������Ŀ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		logger.debug("feedback " + feedback);
		if (feedback == 0) {
			showMenu();
		}
		if (feedback == -1 || feedback == 1) {
			exit();
		}
	}

	public static int confirmExit(int remainPayCount) throws Exception {
		int result = JOptionPane.showConfirmDialog(null, "�Ƿ�Ҫȡ����Ʊ��", "���˴���", JOptionPane.YES_NO_OPTION);
		logger.debug("�Ƿ�Ҫȡ����Ʊ�� " + result);
		if (result <= 0) {
			callback.actionSuspend(ActionType.Billing);
			handleProgressCompleted("��Ʊ��ֹ��");
			return -1;
		}
		return 1;
	}

	public static void exit() {
		logger.info("�˳�ϵͳ��");
		System.exit(0);
	}
	
	public static String handleRostersPathInput(String defaultPath) {
		String path = (String) JOptionPane.showInputDialog(null, "����������У�黨�����·��", "������У��",
				JOptionPane.INFORMATION_MESSAGE, null, null, defaultPath);
		logger.debug("У�黨����·�� ��" + path);
		if (path == null) {
			showMenu();
		}
		return path;
	}
}
