package com.nathan.view;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;

public class InteractionHandler {
	
	private static Logger logger = Logger.getLogger(InteractionHandler.class);
	
	private static final String title = "借人处理";
	
	public static void main(String[] args) throws Exception {
		Object[] options = { "确认", "取消" };
		int i = JOptionPane.showOptionDialog(null, "一键开票", "德盛人力项目管理", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		logger.debug("JOptionPane " + i);
		
		Object[] possibleValues = { "First", "Second", "Third" }; 
		Object selectedValue = JOptionPane.showInputDialog(null, "Choose one", "Input", 
		JOptionPane.INFORMATION_MESSAGE, null, 
		possibleValues, possibleValues[0]);
		
		logger.debug("selectedValue " + selectedValue);
		
//		int a = JOptionPane.showInternalConfirmDialog(null, 
//				"开票人数不足，是否需要人工处理？", "借人处理", 
//				JOptionPane.YES_NO_CANCEL_OPTION, 
//				JOptionPane.INFORMATION_MESSAGE); 
//		System.out.println("a：" + a);
		
		Object[] options1 = { "自动借人", "人工干预", "取消开票" }; 
		int b = JOptionPane.showOptionDialog(null, "开票人数不足，请选择处理方式", "借人处理", 
		JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
		null, options1, options1[2]); 
		System.out.println("b：" + b);
		
		String inputValue = JOptionPane.showInputDialog("Please input a value"); 
		logger.debug("inputValue " + inputValue);
		
		handleFullUpManual(10);
		
		confirmExit(10);
	}

	public static InteractionInput handleFullUpManual(int remainPayCount) {
		Object[] options = { "自动借人", "指定借人", "取消开票" }; 
		int feedback = JOptionPane.showOptionDialog(null, "开票人数不足，还差" + remainPayCount + "人，请选择处理方式", title, 
		JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
		null, options, options[2]); 
		
		if (feedback == 1) {
			String company = JOptionPane.showInputDialog(null, "请输入单位：", title, JOptionPane.INFORMATION_MESSAGE);
			logger.debug("单位： " + company);
			while (Constant.EMPTY_STRING.equals(company)) {
				company = JOptionPane.showInputDialog(null, "单位不能为空！ 请输入借人单位：", title, JOptionPane.INFORMATION_MESSAGE);
			}
			if (company == null) {
				confirmExit(remainPayCount);
			}
			
			String projectLeader = JOptionPane.showInputDialog(null, "请输入领队：", title, JOptionPane.INFORMATION_MESSAGE);
			logger.debug("领队： " + projectLeader);
			while (Constant.EMPTY_STRING.equals(projectLeader)) {
				projectLeader = JOptionPane.showInputDialog(null, "领队不能为空！ 请输入领队：", title, JOptionPane.INFORMATION_MESSAGE);
			}
			if (company == null) {
				confirmExit(remainPayCount);
			}
			
			InteractionInput input = new InteractionInput();
			input.setCompany(company);
			input.setProjectLeader(projectLeader);
			return input;
		}
		
		if (feedback == 2 || feedback == -1) {
			exit();
		}
		
		return null;
	}
	
	public static InteractionInput handleFullUpFromOtherCompany(String currentCompany, int remainPayCount) {
		
		String company = null;
		do {
			company = JOptionPane.showInputDialog(null, currentCompany + "名额已用完，还差" + remainPayCount + "人，请另外指定一个借人单位：", title, JOptionPane.INFORMATION_MESSAGE);
		} while(currentCompany.equals(company));
		
		logger.debug("新借人单位： " + company);
		while (Constant.EMPTY_STRING.equals(company)) {
			company = JOptionPane.showInputDialog(null, "单位不能为空！ 请输入借人单位：", title, JOptionPane.INFORMATION_MESSAGE);
		}
		
		if (company == null) {
			exit();
		}
		
		InteractionInput input = new InteractionInput();
		input.setCompany(company);
		return input;
	}
	
	public static void handleFullUpHandlingCompleted() {
		Object[] options = { "是", "取消" };
		int feedback = JOptionPane.showOptionDialog(null, "名额足够，是否继续开票？", "德盛人力项目管理", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == 1 || feedback == -1) {
			exit();
		}
	}
	
	private static void confirmExit(int remainPayCount) {
		int result = JOptionPane.showConfirmDialog(null, "是否要取消开票？", "借人处理", JOptionPane.YES_NO_OPTION); 
		logger.debug("是否要取消开票？ " + result);
		if (result <= 0) {
			exit();
		}
		handleFullUpManual(remainPayCount);
	}
	
	private static void exit() {
			logger.info("取消开票！");
			System.exit(0);
	}
}
