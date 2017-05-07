package com.nathan.view;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;

public class InteractionHandler {

	private static Logger logger = Logger.getLogger(InteractionHandler.class);

	private static final String title = "借人处理";
	
	private static ActionCallback callback;

	public static void main(String[] args) throws Exception {
		// Object[] options = { "确认", "取消" };
		// int i = JOptionPane.showOptionDialog(null, "一键开票", "德盛人力项目管理",
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
		// "开票人数不足，是否需要人工处理？", "借人处理",
		// JOptionPane.YES_NO_CANCEL_OPTION,
		// JOptionPane.INFORMATION_MESSAGE);
		// System.out.println("a：" + a);

		// Object[] options1 = { "自动借人", "人工干预", "取消开票" };
		// int b = JOptionPane.showOptionDialog(null, "开票人数不足，请选择处理方式", "借人处理",
		// JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
		// null, options1, options1[2]);
		// System.out.println("b：" + b);
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
		Object[] options = { "工资表制作", "数据设置", "查询", "汇总", "花名册校验", "退出" };
		int feedback = JOptionPane.showOptionDialog(null, "欢迎使用德盛人力项目管理系统", "德盛人力项目管理", JOptionPane.DEFAULT_OPTION,
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
		Object[] options = { "正常开票", "虚拟开票", "返回", "退出" };
		int feedback = JOptionPane.showOptionDialog(null, "工资表制作", "德盛人力项目管理", JOptionPane.DEFAULT_OPTION,
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
		Object[] options = { "返回", "退出" };
		int feedback = JOptionPane.showOptionDialog(null, status, "德盛人力项目管理", JOptionPane.DEFAULT_OPTION,
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
		Object[] options = { "返回", "退出" };
		int feedback = JOptionPane.showOptionDialog(null, function + "功能正在完善中，敬请期待", "德盛人力项目管理",
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
		Object[] options = { "自动借人", "指定借人", "取消开票" };
		int feedback = JOptionPane.showOptionDialog(null, contractID + "开票人数不足，还差" + remainPayCount + "人，请选择处理方式", title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);

		if (feedback == 1) {
			String company = JOptionPane.showInputDialog(null, "请输入单位：", title, JOptionPane.INFORMATION_MESSAGE);
			logger.debug("单位： " + company);
			while (Constant.EMPTY_STRING.equals(company)) {
				company = JOptionPane.showInputDialog(null, "单位不能为空！ 请输入借人单位：", title, JOptionPane.INFORMATION_MESSAGE);
			}
			if (company == null) {
				if (confirmExit(remainPayCount) == 1) {
					handleFullUpManual(contractID, remainPayCount);
				}
			}

			String projectLeader = JOptionPane.showInputDialog(null, "请输入领队：", title, JOptionPane.INFORMATION_MESSAGE);
			logger.debug("领队： " + projectLeader);
			while (Constant.EMPTY_STRING.equals(projectLeader)) {
				projectLeader = JOptionPane.showInputDialog(null, "领队不能为空！ 请输入领队：", title,
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
			company = JOptionPane.showInputDialog(null, currentCompany + "名额已用完，还差" + remainPayCount + "人，请另外指定一个借人单位：",
					title, JOptionPane.INFORMATION_MESSAGE);
		} while (currentCompany.equals(company) || Constant.EMPTY_STRING.equals(company));

		logger.debug("新借人单位： " + company);

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
			handleProgressCompleted("开票中止！");
		}
	}
	
	public static boolean handleIsGoOn(String message) throws Exception {
		boolean isGoOn = false;
		Object[] options = { "是", "取消" };
		int feedback = JOptionPane.showOptionDialog(null, message + "，是否继续？", "德盛人力项目管理", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == 0) {
			isGoOn = true;
		}
		return isGoOn;
	}
	
	public static void handleBillingProgressReport(String contractID, String status, int totalToDo, int totalDone) {
		String message = contractID + status + " ( " + totalDone + " / " + totalToDo + " )";
		logger.info(message);
		JOptionPane.showMessageDialog(null, message, "开票进度", JOptionPane.INFORMATION_MESSAGE);
	}

	public static String handleConfigPath() {
		String path = (String) JOptionPane.showInputDialog(null, "请输入配置文件路径", "德盛人力项目管理",
				JOptionPane.INFORMATION_MESSAGE, null, null, Constant.CONFIG_FILE);
		logger.debug("配置文件 " + path);
		if (path == null) {
			exit();
		}
		return path;
	}

	public static void handleExpireChecking(int expireDate) {
		if (Util.getCurrentDateInt() > expireDate) {
			logger.error("Exception: 本测试版本已经超过有效期，请使用最新版本。");
			JOptionPane.showMessageDialog(null, "本测试版本已经超过有效期，请使用最新版本。", "警告", JOptionPane.ERROR_MESSAGE);
			exit();
		}	
	}
	
	public static boolean handleWriteRetry(String message) {
		boolean retry = false;
		Object[] options = { "返回", "退出", "重试" };
		int feedback = JOptionPane.showOptionDialog(null, message, "德盛人力项目管理", JOptionPane.DEFAULT_OPTION,
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
		Object[] options = { "返回", "退出" };
		int feedback = JOptionPane.showOptionDialog(null, message, "德盛人力项目管理", JOptionPane.DEFAULT_OPTION,
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
		int result = JOptionPane.showConfirmDialog(null, "是否要取消开票？", "借人处理", JOptionPane.YES_NO_OPTION);
		logger.debug("是否要取消开票？ " + result);
		if (result <= 0) {
			callback.actionSuspend(ActionType.Billing);
			handleProgressCompleted("开票中止！");
			return -1;
		}
		return 1;
	}

	public static void exit() {
		logger.info("退出系统！");
		System.exit(0);
	}
	
	public static String handleRostersPathInput(String defaultPath) {
		String path = (String) JOptionPane.showInputDialog(null, "请输入所需校验花名册的路径", "花名册校验",
				JOptionPane.INFORMATION_MESSAGE, null, null, defaultPath);
		logger.debug("校验花名册路径 ：" + path);
		if (path == null) {
			showMenu();
		}
		return path;
	}
}
