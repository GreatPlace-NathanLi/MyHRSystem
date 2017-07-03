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

		String companyPath = Constant.propUtil.getStringEnEmpty("user.花名册根目录");
		Object[] companyList = Util.getFoldersUnderPath(companyPath).toArray();
		String company = (String) JOptionPane.showInputDialog(null, "请选择单位：", title, JOptionPane.INFORMATION_MESSAGE,
				null, companyList, companyList[0]);
		logger.debug("单位： " + company);

		String path = companyPath + company;
		Object[] projectLeaderList = Util.parseProjectLeadersFromRosterFileUnderPath(path).toArray();
		String projectLeader = (String) JOptionPane.showInputDialog(null, "请选择领队：", title,
				JOptionPane.INFORMATION_MESSAGE, null, projectLeaderList, projectLeaderList[0]);
		logger.debug("领队： " + projectLeader);

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
		Object[] options = { "工资表制作", "汇总", "查询", "花名册校验", "数据设置", "退出" };
		int feedback = JOptionPane.showOptionDialog(frame, "欢迎使用德盛人力项目管理系统", title, JOptionPane.DEFAULT_OPTION,
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
		Object[] options = { "普通开票", "虚拟开票", "返回", "退出" };
		try {
			int feedback = JOptionPane.showOptionDialog(frame, "工资表制作", title, JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			title = "工资表制作";
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
			logger.info("开票被中止！" + se.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			handleException("开票中途出错：" + e.getMessage());
		}
	}

	public static void handleAggregation() {
		title = BillingSystem.systemName;
		Object[] options = { "劳务费汇总", "借款情况汇总", "返回", "退出" };
		try {
			int feedback = JOptionPane.showOptionDialog(frame, "项目汇总", title, JOptionPane.DEFAULT_OPTION,
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
			handleException("汇总中途出错：" + e.getMessage());
		}

	}
	
	public static InteractionInput handleAggregationInput(AggregatingType aggregatingType) {
		if (AggregatingType.劳务费.equals(aggregatingType)) {
			title = "劳务费汇总";
		} else if (AggregatingType.借款情况.equals(aggregatingType)) {
			title = "借款情况汇总";
		}
		
		String company = null;
		if (AggregatingType.劳务费.equals(aggregatingType)) {
			company = handleCompanyInputWithAllCompany();
		} else {
			company = handleCompanyInput();
		}
		if (company == null) {
			return null;
		}
		
		String projectLeader = null;
		if (AggregatingType.借款情况.equals(aggregatingType)) {
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
		String path = Constant.propUtil.getStringEnEmpty("user.花名册根目录");
		Object[] companyArray = Util.getFoldersUnderPath(path).toArray();
		if (companyArray == null || companyArray.length == 0) {
			handleWarning(path + " 目录下没有任何单位！");
			return null;
		}
		String company = (String) handleListSelection(companyArray, "请指定汇总单位：", title);
		logger.debug("汇总单位： " + company);
		return company;
	}
	
	public static String handleCompanyInputWithAllCompany() {
		String path = Constant.propUtil.getStringEnEmpty("user.花名册根目录");
		Object[] companyArray = Util.getFoldersUnderPath(path).toArray();
		if (companyArray == null || companyArray.length == 0) {
			handleWarning(path + " 目录下没有任何单位！");
			return null;
		}
		int length = companyArray.length;
		companyArray = Arrays.copyOf(companyArray, length + 1);
		companyArray[length] = Constant.ALL_COMPANY;
		String company = (String) handleListSelection(companyArray, "请指定汇总单位：", title);
		logger.debug("汇总单位： " + company);
		return company;
	}

	public static String handleProjectLeaderInput(String company) {
		String projectLeader = null;
		if(Constant.YES.equals(Constant.propUtil.getStringEnEmpty("system.allowInputProjectLearderByText"))) {
			Object[] options = {"输入领队", "选择领队", "取消" };
			int feedback = JOptionPane.showOptionDialog(frame, "汇总单位：" + company, title, JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			
			if (feedback == -1 || feedback == 2) {
				return null;
			}
			if (feedback == 0) {
				projectLeader = (String) JOptionPane.showInputDialog(frame, "请输入汇总领队：", title,
						JOptionPane.INFORMATION_MESSAGE, null, null, null);
				logger.debug("输入的汇总领队：" + projectLeader);
				return projectLeader;
			}		
		}
		
		String path = Constant.propUtil.getStringEnEmpty("user.花名册根目录");
		path = path + company;
		Object[] projectLeaderList = Util.parseProjectLeadersFromRosterFileUnderPath(path).toArray();
		if (projectLeaderList == null || projectLeaderList.length == 0) {
			handleWarning(path + " 目录下没有任何领队花名册！");
			return null;
		}
		projectLeader = (String) handleListSelection(projectLeaderList, "请指定汇总领队：", title);
		logger.debug("指定的汇总领队： " + projectLeader);
		return projectLeader;
	}

	private static int handleStartYearMonthIntegerInput(int startTime) {
		Object[] timeArray = Util.buildYearMonthIntArray(startTime);
		Object time = handleListSelection(timeArray, "请指定起始时间(YYYYMM)：", title);
		if (time == null) {
			return 0;
		}
		logger.debug("起始时间： " + time);
		return (Integer) time;
	}

	private static int handleEndYearMonthIntegerInput(int startTime) {
		Object[] timeArray = Util.buildYearMonthIntArray(startTime);
		Object time = handleListSelection(timeArray, "请指定终止时间(YYYYMM)：", title);
		if (time == null) {
			return 0;
		}
		logger.debug("终止时间： " + time);
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
			handleException("校验花名册中途出错：" + e.getMessage());
		}
	}
	
	public static String handleRostersPathInput(String defaultPath) {
		title = BillingSystem.systemName;
		Object[] options = { "路径校验", "单位领队校验", "返回", "退出" };
		int feedback = JOptionPane.showOptionDialog(frame, "花名册校验", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		title = "花名册校验";
		if (feedback == -1 || feedback == 2) {
			return null;
		}
		if (feedback == 3) {
			exit();
		}
		if (feedback == 0) {
			String path = (String) JOptionPane.showInputDialog(frame, "请输入所需校验花名册的路径", title,
					JOptionPane.INFORMATION_MESSAGE, null, null, defaultPath);
			logger.debug("校验花名册路径 ：" + path);
			return path;
		}
		if (feedback == 1) {
			Object[] companyList = Util.getFoldersUnderPath(defaultPath).toArray();
			String company = (String) JOptionPane.showInputDialog(frame, "请指定校验单位：", title,
					JOptionPane.INFORMATION_MESSAGE, null, companyList, companyList[0]);
			logger.debug("校验单位： " + company);

			if (company == null) {
				return null;
			} else {
				Object[] options1 = { "开始校验", "选择领队", "取消"};
				int feedback1 = JOptionPane.showOptionDialog(frame, "校验单位： " + company + "\r\n\r\n请选择下一步：", title, JOptionPane.DEFAULT_OPTION,
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
			String projectLeader = (String) JOptionPane.showInputDialog(frame, "请指定校验领队：", title,
					JOptionPane.INFORMATION_MESSAGE, null, projectLeaderList, projectLeaderList[0]);
			logger.debug("校验领队： " + projectLeader);

			if (projectLeader == null) {
				return null;
			}
			return path1 + Constant.DELIMITER3 + projectLeader;
		}
		return null;
	}

	public static void handleProgressCompleted(String status) {
		Object[] options = { "返回", "退出" };
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
		Object[] options = { "返回", "退出" };
		int feedback = JOptionPane.showOptionDialog(frame, function + "功能正在完善中，敬请期待", title, JOptionPane.DEFAULT_OPTION,
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
		title = "借人处理";
		Object[] options = { "自动借人", "指定借人", "取消开票" };
		int feedback = JOptionPane.showOptionDialog(frame, contractID + "开票人数不足，还差" + remainPayCount + "人，请选择处理方式",
				title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);

		if (feedback == 1) {
			String path = Constant.propUtil.getStringEnEmpty("user.花名册根目录");
			Object[] companyList = Util.getFoldersUnderPath(path).toArray();
			String company = (String) JOptionPane.showInputDialog(frame, "请指定一个借人单位：", title,
					JOptionPane.INFORMATION_MESSAGE, null, companyList, companyList[0]);
			logger.debug("借人单位： " + company);

			if (company == null) {
				handleProgressCompleted("开票中止！");
				throw new BillingSuspendException();
			}

			String path1 = path + company;
			Object[] projectLeaderList = Util.parseProjectLeadersFromRosterFileUnderPath(path1).toArray();
			String projectLeader = (String) JOptionPane.showInputDialog(frame, "请指定一个借人领队：", title,
					JOptionPane.INFORMATION_MESSAGE, null, projectLeaderList, projectLeaderList[0]);
			logger.debug("借人领队： " + projectLeader);

			if (projectLeader == null) {
				handleProgressCompleted("开票中止！");
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

		title = "借人处理";

		// String company = null;
		// do {
		// company = JOptionPane.showInputDialog(null, currentCompany +
		// "名额已用完，还差" + remainPayCount + "人，请另外指定一个借人单位：",
		// title, JOptionPane.INFORMATION_MESSAGE);
		// } while (currentCompany.equals(company) ||
		// Constant.EMPTY_STRING.equals(company));

		String path = Constant.propUtil.getStringEnEmpty("user.花名册根目录");
		Object[] companyList = Util.getFoldersUnderPath(path, currentCompany).toArray();
		String company = (String) JOptionPane.showInputDialog(frame,
				currentCompany + "名额已用完，还差" + remainPayCount + "人，请另外指定一个借人单位：", title, JOptionPane.INFORMATION_MESSAGE,
				null, companyList, companyList[0]);

		InteractionInput input = new InteractionInput();
		input.setCompany(company);
		logger.debug("新借人单位： " + company);

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
			handleProgressCompleted("开票中止！");
			throw new BillingSuspendException();
		}
	}

	public static boolean handleIsGoOn(String message) {
		boolean isGoOn = false;
		Object[] options = { "是", "取消" };
		int feedback = JOptionPane.showOptionDialog(frame, message + "，是否继续？", title, JOptionPane.DEFAULT_OPTION,
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
		JOptionPane.showMessageDialog(frame, message, "开票进度", JOptionPane.INFORMATION_MESSAGE);
	}

	public static String handleConfigPath() {
//		if (!Util.needToCheckConfigPath()) {
//			return Constant.CONFIG_FILE;
//		}
		String path = Util.getSettingPropertiesPath();
//		String path = (String) JOptionPane.showInputDialog(frame, "请输入系统配置文件路径", BillingSystem.systemName,
//				JOptionPane.INFORMATION_MESSAGE, null, null, Util.getSettingPropertiesPath());
//		logger.debug("系统配置文件: " + path);
		if (path == null) {
			logger.error("找不到配置文件!");
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
			String message = "本版本已经超过有效期(" + expireDate + ")，请使用最新版本。";
			logger.error(message);
			JOptionPane.showMessageDialog(frame, message, "警告", JOptionPane.ERROR_MESSAGE);
			exit();
		}
	}

	public static boolean handleWriteRetry(String message) {
		boolean retry = false;
		Object[] options = { "返回", "退出", "重试" };
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
		Object[] options = { "返回", "退出" };
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
		Object[] options = { "返回" };
		JOptionPane.showOptionDialog(frame, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
				options, options[0]);
	}

	public static int confirmExit(int remainPayCount) throws Exception {
		int result = JOptionPane.showConfirmDialog(frame, "是否要取消开票？", "借人处理", JOptionPane.YES_NO_OPTION);
		logger.debug("是否要取消开票？ " + result);
		if (result <= 0) {
			// callback.actionSuspend(ActionType.Billing);
			handleProgressCompleted("开票中止！");
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
		logger.info("退出系统！");
		System.exit(0);
	}

	public static boolean handlePrintTaskConfirmation(String message, int totalToDo, int totalDone)
			throws PrintingSuspendException {
		boolean skip = true;
		Object[] options = { "继续", "跳过", "返回", "退出" };
		message = message + "   ( 进度：" + totalDone + " / " + totalToDo + " )";
		int feedback = JOptionPane.showOptionDialog(frame, message, "打印处理", JOptionPane.DEFAULT_OPTION,
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
		int result = JOptionPane.showConfirmDialog(frame, "是否要取消打印？", "打印处理", JOptionPane.YES_NO_OPTION);
		logger.debug("是否要取消打印？ " + result);
		if (result <= 0) {
			handleProgressCompleted("打印中止！");
			throw new PrintingSuspendException();
		}
		return 1;
	}
}
