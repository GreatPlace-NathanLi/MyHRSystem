package com.nathan.controller;

import com.nathan.common.Constant;

import jxl.common.Logger;

public class MultiBillingOperater extends SingleBillingOperater {

	private static Logger logger = Logger.getLogger(MultiBillingOperater.class);
	
	public static void main(String[] args) throws Exception {
		BillingPlanProcesser billingPlanProcesser = new BillingPlanProcesser();
		RosterProcesser rosterProcesser = new RosterProcesser();
		PayrollSheetProcesser payrollSheetProcesser = new PayrollSheetProcesser();
		
		long startTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("��Ʊ������ʼ...");
		logger.info(Constant.LINE0);

		billingPlanProcesser.processBillingPlanInput(Constant.BILLING_INPUT_FILE);

		billingPlanProcesser.writeBillingOutput(Constant.BILLING_INPUT_FILE, Constant.BILLING_OUTPUT_FILE);
		logger.info("����3 - ���濪Ʊ�ƻ������ " + Constant.BILLING_OUTPUT_FILE);
		logger.info(Constant.LINE1);

		payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser, Constant.PAYROLL_FILE,
				Constant.PAYROLL_TEMPLATE_FILE);
		
		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("��Ʊ������ ��ʱ��" + (endTime - startTime)/1000000 + "����");
		logger.info(Constant.LINE0);

	}
		
}