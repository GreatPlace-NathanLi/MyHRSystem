package com.nathan.controller;

import com.nathan.common.Constant;

import jxl.common.Logger;

public class SingleBillingOperater {

	private static Logger logger = Logger.getLogger(SingleBillingOperater.class);

	public static void main(String[] args) throws Exception {
		BillingPlanProcesser billingPlanProcesser = new BillingPlanProcesser();
		RosterProcesser rosterProcesser = new RosterProcesser();
		PayrollSheetProcesser payrollSheetProcesser = new PayrollSheetProcesser();

		long startTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("开票处理开始...");
		logger.info(Constant.LINE0);

		billingPlanProcesser.processBillingPlanInput(Constant.BILLING_INPUT_FILE);

		billingPlanProcesser.writeBillingOutput(Constant.BILLING_INPUT_FILE, Constant.BILLING_OUTPUT_FILE);
		logger.info("步骤3 - 保存开票计划输出： " + Constant.BILLING_OUTPUT_FILE);
		logger.info(Constant.LINE1);

		payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser, Constant.PAYROLL_FILE,
				Constant.PAYROLL_TEMPLATE_FILE);

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("开票结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
		logger.info(Constant.LINE0);
	}

}
