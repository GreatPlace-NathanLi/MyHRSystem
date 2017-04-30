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
		logger.debug("当前路径：" + path);
		BillingPlanProcesser billingPlanProcesser = new BillingPlanProcesser();
		RosterProcesser rosterProcesser = new RosterProcesser();
		PayrollSheetProcesser payrollSheetProcesser = new PayrollSheetProcesser();
		PaymentDocumentProcesser paymentDocumentProcesser = new PaymentDocumentProcesser();

		long startTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("开票处理开始...");
		logger.info(Constant.LINE0);

		String billingFile = Constant.propUtil.getStringValue("user.开票计划路径", Constant.BILLING_INPUT_FILE);
		logger.info("步骤1 - 读取开票计划输入： " + billingFile);
		billingPlanProcesser.processBillingPlanInput(billingFile);
		logger.info(Constant.LINE1);

		try {
			logger.info("步骤2 - 处理工资表");
			payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser);
		} catch(Exception e) {
			if (payrollSheetProcesser.isNeededToUpdateBillingPlanBook()) {
				logger.info("开票中途出错， 保存开票计划输出： " + billingFile);
				billingPlanProcesser.writeBillingOutput(billingFile);
			}
			throw e;
		}
		
		logger.info(Constant.LINE1);

		logger.info("步骤3 - 保存开票计划输出： " + billingFile);
		billingPlanProcesser.writeBillingOutput(billingFile);
		logger.info(Constant.LINE1);

		logger.info("步骤4 - 开始制作付款手续单据...");
		paymentDocumentProcesser.processPaymentDocument(billingPlanProcesser.getBillingPlanBook());

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("开票结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
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
