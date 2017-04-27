package com.nathan.view;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;

public class InteractionHandler {
	
	private static Logger logger = Logger.getLogger(InteractionHandler.class);
	
	private static final String title = "���˴���";
	
	public static void main(String[] args) throws Exception {
		Object[] options = { "ȷ��", "ȡ��" };
		int i = JOptionPane.showOptionDialog(null, "һ����Ʊ", "��ʢ������Ŀ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		logger.debug("JOptionPane " + i);
		
		Object[] possibleValues = { "First", "Second", "Third" }; 
		Object selectedValue = JOptionPane.showInputDialog(null, "Choose one", "Input", 
		JOptionPane.INFORMATION_MESSAGE, null, 
		possibleValues, possibleValues[0]);
		
		logger.debug("selectedValue " + selectedValue);
		
//		int a = JOptionPane.showInternalConfirmDialog(null, 
//				"��Ʊ�������㣬�Ƿ���Ҫ�˹�����", "���˴���", 
//				JOptionPane.YES_NO_CANCEL_OPTION, 
//				JOptionPane.INFORMATION_MESSAGE); 
//		System.out.println("a��" + a);
		
		Object[] options1 = { "�Զ�����", "�˹���Ԥ", "ȡ����Ʊ" }; 
		int b = JOptionPane.showOptionDialog(null, "��Ʊ�������㣬��ѡ����ʽ", "���˴���", 
		JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
		null, options1, options1[2]); 
		System.out.println("b��" + b);
		
		String inputValue = JOptionPane.showInputDialog("Please input a value"); 
		logger.debug("inputValue " + inputValue);
		
		handleFullUpManual(10);
		
		confirmExit(10);
	}

	public static InteractionInput handleFullUpManual(int remainPayCount) {
		Object[] options = { "�Զ�����", "ָ������", "ȡ����Ʊ" }; 
		int feedback = JOptionPane.showOptionDialog(null, "��Ʊ�������㣬����" + remainPayCount + "�ˣ���ѡ����ʽ", title, 
		JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
		null, options, options[2]); 
		
		if (feedback == 1) {
			String company = JOptionPane.showInputDialog(null, "�����뵥λ��", title, JOptionPane.INFORMATION_MESSAGE);
			logger.debug("��λ�� " + company);
			while (Constant.EMPTY_STRING.equals(company)) {
				company = JOptionPane.showInputDialog(null, "��λ����Ϊ�գ� ��������˵�λ��", title, JOptionPane.INFORMATION_MESSAGE);
			}
			if (company == null) {
				confirmExit(remainPayCount);
			}
			
			String projectLeader = JOptionPane.showInputDialog(null, "��������ӣ�", title, JOptionPane.INFORMATION_MESSAGE);
			logger.debug("��ӣ� " + projectLeader);
			while (Constant.EMPTY_STRING.equals(projectLeader)) {
				projectLeader = JOptionPane.showInputDialog(null, "��Ӳ���Ϊ�գ� ��������ӣ�", title, JOptionPane.INFORMATION_MESSAGE);
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
			company = JOptionPane.showInputDialog(null, currentCompany + "���������꣬����" + remainPayCount + "�ˣ�������ָ��һ�����˵�λ��", title, JOptionPane.INFORMATION_MESSAGE);
		} while(currentCompany.equals(company));
		
		logger.debug("�½��˵�λ�� " + company);
		while (Constant.EMPTY_STRING.equals(company)) {
			company = JOptionPane.showInputDialog(null, "��λ����Ϊ�գ� ��������˵�λ��", title, JOptionPane.INFORMATION_MESSAGE);
		}
		
		if (company == null) {
			exit();
		}
		
		InteractionInput input = new InteractionInput();
		input.setCompany(company);
		return input;
	}
	
	public static void handleFullUpHandlingCompleted() {
		Object[] options = { "��", "ȡ��" };
		int feedback = JOptionPane.showOptionDialog(null, "�����㹻���Ƿ������Ʊ��", "��ʢ������Ŀ����", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == 1 || feedback == -1) {
			exit();
		}
	}
	
	private static void confirmExit(int remainPayCount) {
		int result = JOptionPane.showConfirmDialog(null, "�Ƿ�Ҫȡ����Ʊ��", "���˴���", JOptionPane.YES_NO_OPTION); 
		logger.debug("�Ƿ�Ҫȡ����Ʊ�� " + result);
		if (result <= 0) {
			exit();
		}
		handleFullUpManual(remainPayCount);
	}
	
	private static void exit() {
			logger.info("ȡ����Ʊ��");
			System.exit(0);
	}
}
