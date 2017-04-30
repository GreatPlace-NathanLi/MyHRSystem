package com.nathan.controller;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.PropertiesUtils;
import com.nathan.view.ActionType;
import com.nathan.view.BillingCallback;
import com.nathan.view.InteractionHandler;

public class BillingOperater {

	private static Logger logger = Logger.getLogger(BillingOperater.class);

	private static int expireDate = 20170620;

	public void startBilling() throws Exception {
		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		logger.debug("��ǰ·����" + path);
		BillingPlanProcesser billingPlanProcesser = new BillingPlanProcesser();
		RosterProcesser rosterProcesser = new RosterProcesser();
		PayrollSheetProcesser payrollSheetProcesser = new PayrollSheetProcesser();
		PaymentDocumentProcesser paymentDocumentProcesser = new PaymentDocumentProcesser();

		long startTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("��Ʊ����ʼ...");
		logger.info(Constant.LINE0);

		String billingFile = Constant.propUtil.getStringValue("user.��Ʊ�ƻ�·��", Constant.BILLING_INPUT_FILE);
		logger.info("����1 - ��ȡ��Ʊ�ƻ����룺 " + billingFile);
		billingPlanProcesser.processBillingPlanInput(billingFile);
		logger.info(Constant.LINE1);

		try {
			logger.info("����2 - �����ʱ�");
			payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser);
		} catch(Exception e) {
			if (payrollSheetProcesser.isNeededToUpdateBillingPlanBook()) {
				logger.info("��Ʊ��;���� ���濪Ʊ�ƻ������ " + billingFile);
				billingPlanProcesser.writeBillingOutput(billingFile);
			}
			throw e;
		}
		
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
	}

	public static void main(String[] args) throws Exception {
		try {
			InteractionHandler.handleExpireChecking(expireDate);

			String configFile = InteractionHandler.handleConfigPath();
			Constant.propUtil = new PropertiesUtils(configFile);
			Constant.propUtil.init();

			new BillingCallback().actionPerformed(ActionType.Billing);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			InteractionHandler.handleException(e.getMessage());
		}

	}

}
