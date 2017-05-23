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
		logger.debug("当前路径：" + path);
		billingPlanProcesser = new BillingPlanProcesser();
		rosterProcesser = new RosterProcesser();
		payrollSheetProcesser = new PayrollSheetProcesser();
		payrollSheetProcesser.setVirtualBillingFlag(virtualBillingFlag);
		printingProcesser = new PrintingProcesser();

		long startTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("开票处理开始...");
		logger.info(Constant.LINE0);

		billingFile = getBillingPlanFilePath();
		logger.info("读取开票计划输入： " + billingFile);
		billingPlanProcesser.processBillingPlanInput(billingFile);
		logger.info(Constant.LINE1);

		try {
			logger.info("处理工资表");
			payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser);
		} catch(Exception e) {
			logger.error("开票中途被中止或者出错");
			endBilling();
			throw e;
		}
		
		logger.info(Constant.LINE1);

		endBilling();

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("开票结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
		logger.info(Constant.LINE0);
	}
	
	protected String getBillingPlanFilePath() {
		return Constant.propUtil.getStringValue("user.开票计划路径", Constant.BILLING_INPUT_FILE);
	}
	
	public void endBilling() throws Exception {
		if (!payrollSheetProcesser.isNeededToUpdateBillingPlanBook()) {
			return;
		}
		logger.info("保存开票计划输出： " + billingFile);
		billingPlanProcesser.writeBillingOutput(billingFile);
		logger.info(Constant.LINE1);

		logger.info("开始制作付款手续单据...");
		paymentDocumentProcesser = new PaymentDocumentProcesser();
		paymentDocumentProcesser.processPaymentDocument(billingPlanProcesser.getBillingPlanBook());
		
		if (payrollSheetProcesser.isNeededToCreateBankPaymentSummarySheet()) {
			logger.info("开始制作网银汇总表...");
			bankPaymentSummaryProcesser = new BankPaymentSummaryProcesser();
			bankPaymentSummaryProcesser.processBankPaymentSummary(billingPlanProcesser.getBillingPlanBook());
		}
		
		logger.info("即将开始打印表格...");
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
