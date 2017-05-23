package com.nathan.controller;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.PropertiesUtils;
import com.nathan.view.ActionType;
import com.nathan.view.BillingSystemCallback;
import com.nathan.view.InteractionHandler;

public class BillingOperater {

	private static Logger logger = Logger.getLogger(BillingOperater.class);

	private static int expireDate = 20170620;
	
	protected String billingFile;
	
	protected BillingPlanProcesser billingPlanProcesser;
	
	protected PayrollSheetProcesser payrollSheetProcesser;
	
	private PaymentDocumentProcesser paymentDocumentProcesser;
	
	private RosterProcesser rosterProcesser;
	
	private BankPaymentSummaryProcesser bankPaymentSummaryProcesser;
	
	protected PrintingProcesser printingProcesser;
	
	protected boolean virtualBillingFlag = false;

	public void startBilling() throws Exception {
		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		logger.debug("��ǰ·����" + path);
		billingPlanProcesser = new BillingPlanProcesser();
		rosterProcesser = new RosterProcesser();
		payrollSheetProcesser = new PayrollSheetProcesser();
		payrollSheetProcesser.setVirtualBillingFlag(virtualBillingFlag);
		printingProcesser = new PrintingProcesser();

		long startTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("��Ʊ����ʼ...");
		logger.info(Constant.LINE0);

		billingFile = getBillingPlanFilePath();
		logger.info("��ȡ��Ʊ�ƻ����룺 " + billingFile);
		billingPlanProcesser.processBillingPlanInput(billingFile);
		logger.info(Constant.LINE1);

		try {
			logger.info("�����ʱ�");
			payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser);
		} catch(Exception e) {
			logger.error("��Ʊ��;����ֹ���߳���");
			endBilling();
			throw e;
		}
		
		logger.info(Constant.LINE1);

		endBilling();

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("��Ʊ������ ��ʱ��" + (endTime - startTime) / 1000000 + "����");
		logger.info(Constant.LINE0);
	}
	
	protected String getBillingPlanFilePath() {
		return Constant.propUtil.getStringValue("user.��Ʊ�ƻ�·��", Constant.BILLING_INPUT_FILE);
	}
	
	public void endBilling() throws Exception {
		if (!payrollSheetProcesser.isNeededToUpdateBillingPlanBook()) {
			return;
		}
		logger.info("���濪Ʊ�ƻ������ " + billingFile);
		billingPlanProcesser.writeBillingOutput(billingFile);
		logger.info(Constant.LINE1);

		logger.info("��ʼ����������������...");
		paymentDocumentProcesser = new PaymentDocumentProcesser();
		paymentDocumentProcesser.processPaymentDocument(billingPlanProcesser.getBillingPlanBook());
		
		if (payrollSheetProcesser.isNeededToCreateBankPaymentSummarySheet()) {
			logger.info("��ʼ�����������ܱ�...");
			bankPaymentSummaryProcesser = new BankPaymentSummaryProcesser();
			bankPaymentSummaryProcesser.processBankPaymentSummary(billingPlanProcesser.getBillingPlanBook());
		}
		
		logger.info("������ʼ��ӡ���...");
		printingProcesser.processPrintTasks();
	}

	public void release() {
		if (printingProcesser != null) {
			printingProcesser.release();
		}
	}
	
	public static void main(String[] args) throws Exception {
		try {
			InteractionHandler.handleExpireChecking(expireDate);

			String configFile = InteractionHandler.handleConfigPath();
			Constant.propUtil = new PropertiesUtils(configFile);
			Constant.propUtil.init();

			new BillingSystemCallback().actionPerformed(ActionType.Billing);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			InteractionHandler.handleException(e.getMessage());
		}

	}

}
