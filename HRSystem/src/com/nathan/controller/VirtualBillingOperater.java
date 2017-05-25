package com.nathan.controller;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;

public class VirtualBillingOperater extends BillingOperater {
	
	private static Logger logger = Logger.getLogger(VirtualBillingOperater.class);
	
	private boolean isNeededToDeleteFileAfterPrinting = false;
	
	public VirtualBillingOperater() {
		virtualBillingFlag = true;
		isNeededToDeleteFileAfterPrinting = Constant.FLAG_YES
				.equals(Constant.propUtil.getStringValue("user.���⿪Ʊ.��ӡ��ɺ��Ƿ�ɾ���ĵ�", Constant.FLAG_NO));
	}
	
	protected String getBillingPlanFilePath() {
		return Constant.propUtil.getStringValue("user.���⿪Ʊ.�ƻ�·��", Constant.BILLING_INPUT_FILE);
	}
	
	public void endBilling() throws Exception {
		if (!payrollSheetProcesser.isNeededToUpdateBillingPlanBook()) {
			return;
		}
		logger.info("������ʼ��ӡ���...");
		printingProcesser.processPrintTasks();	
	}
	
	public void saveBillingPlan() throws Exception {
		if (!payrollSheetProcesser.isNeededToUpdateBillingPlanBook()) {
			return;
		}
		logger.info("���濪Ʊ�ƻ������ " + billingFile);
		billingPlanProcesser.writeBillingOutput(billingFile);
		logger.info(Constant.LINE1);
	}
	
	public void deleteFilesAfterPrinting() {
		if(isNeededToDeleteFileAfterPrinting) {
			logger.info("ɾ�����⿪Ʊ���ɵ��ĵ�...");
			printingProcesser.deleteFilesAfterPrinting();
		}
	}

}
