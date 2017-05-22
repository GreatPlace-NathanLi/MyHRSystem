package com.nathan.view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class ActionHandler {

	private static Logger logger = Logger.getLogger(InteractionHandler.class);

	private static String title = "��ʢ������Ŀ����";

	private static ActionCallback callback;

	public static void setActionCallback(ActionCallback callback) {
		ActionHandler.callback = callback;
		InteractionHandler.setActionCallback(callback);
	}
	
	private static JFrame frame;
	
	public static void setFrame(JFrame frame) {
		ActionHandler.frame = frame;
		InteractionHandler.setFrame(frame);
	}

	public static void handleBilling() {
		Object[] options = { "������Ʊ", "���⿪Ʊ", "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, "���ʱ�����", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == 2) {
			try {
				callback.returnPerformed(ActionType.Billing);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				InteractionHandler.handleException(e.getMessage());
			}
			return;
		}
		if (feedback == -1 || feedback == 3) {
			InteractionHandler.exit(ActionType.Billing);
		}
		if (feedback == 0) {
			try {
				callback.actionPerformed(ActionType.Billing);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				InteractionHandler.handleException(e.getMessage());
			}
		}
		if (feedback == 1) {
			try {
				callback.actionPerformed(ActionType.VirtualBilling);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				InteractionHandler.handleException(e.getMessage());
			}
		}
	}
	
	public static void handleAggregation() {
		Object[] options = { "����ѻ���", "�������", "����", "�˳�" };
		int feedback = JOptionPane.showOptionDialog(frame, "��Ŀ����", title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (feedback == 2) {
			return;
		}
		if (feedback == -1 || feedback == 3) {
			InteractionHandler.exit();
		}
		if (feedback == 0) {
			try {
				callback.actionPerformed(ActionType.Aggregating);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				InteractionHandler.handleException(e.getMessage());
			}
		}
		if (feedback == 1) {
			InteractionHandler.handleToDo(options[feedback], 1);
		}
	}		

	public static void handleRosterValidation() {
		try {
			callback.actionPerformed(ActionType.RosterValidation);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			InteractionHandler.handleException(e.getMessage());
		}
	}

}
