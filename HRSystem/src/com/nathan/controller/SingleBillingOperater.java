package com.nathan.controller;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;

public class SingleBillingOperater {

	private static Logger logger = Logger.getLogger(SingleBillingOperater.class);

	private static int expireDate = 20170520;

	// public static PropertiesUtils propUtil = new
	// PropertiesUtils("F:/work/project/conf/setting.properties");

	public static void main(String[] args) throws Exception {
		try {
			if (Util.getCurrentDateInt() > expireDate) {
				logger.error("Exception: �����԰汾�Ѿ�������Ч�ڣ���ʹ�����°汾��");
				JOptionPane.showMessageDialog(null, "�����԰汾�Ѿ�������Ч�ڣ���ʹ�����°汾��", "����", JOptionPane.ERROR_MESSAGE);		
				System.exit(0);
			}
			Constant.propUtil.init();
			// PropertyConfigurator.configure(System.getProperty(Constant.propUtil.getStringEnEmpty("system.log4j.conf")));
			BillingPlanProcesser billingPlanProcesser = new BillingPlanProcesser();
			RosterProcesser rosterProcesser = new RosterProcesser();
			PayrollSheetProcesser payrollSheetProcesser = new PayrollSheetProcesser();
			PaymentDocumentProcesser paymentDocumentProcesser = new PaymentDocumentProcesser();

			Object[] options = { "ȷ��", "ȡ��" };
			int a = JOptionPane.showOptionDialog(null, "һ����Ʊ", "��ʢ������Ŀ����", JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			logger.debug("JOptionPane " + a);
			if (a != 0) {
				logger.info("ȡ����Ʊ��");
				System.exit(0);
			}

			long startTime = System.nanoTime();
			logger.info(Constant.LINE0);
			logger.info("��Ʊ����ʼ...");
			logger.info(Constant.LINE0);

			String billingFile = Constant.propUtil.getStringValue("user.��Ʊ�ƻ�·��", Constant.BILLING_INPUT_FILE);
			logger.info("����1 - ��ȡ��Ʊ�ƻ����룺 " + billingFile);
			billingPlanProcesser.processBillingPlanInput(billingFile);
			logger.info(Constant.LINE1);

			logger.info("����2 - ��ʼ�������ʱ�...");
			String payrollFile = Constant.propUtil.getStringValue("user.���ʱ����·��", Constant.PAYROLL_FILE);
			String payrollTemplateFile = Constant.propUtil.getStringValue("user.���ʱ�ģ��·��",
					Constant.PAYROLL_TEMPLATE_FILE);
			payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser,
					payrollFile, payrollTemplateFile);
			logger.info(Constant.LINE1);

			logger.info("����3 - ���濪Ʊ�ƻ������ " + billingFile);
			billingPlanProcesser.writeBillingOutput(billingFile);
			logger.info(Constant.LINE1);

			logger.info("����4 - ��ʼ����������������...");
			paymentDocumentProcesser.processPaymentDocument(billingPlanProcesser.getBillingPlanBook());

			long endTime = System.nanoTime();
			logger.info(Constant.LINE0);
			logger.info("��Ʊ������ ��ʱ��" + (endTime - startTime) / 1000000 + "����");
			logger.info(Constant.LINE0);

			JOptionPane.showMessageDialog(null, "��Ʊ��ɣ�", "��ʢ������Ŀ����", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
		}

	}

}
