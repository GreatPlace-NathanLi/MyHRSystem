package com.nathan.controller;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;

public class VirtualBillingOperater extends BillingOperater {
	
	private static Logger logger = Logger.getLogger(VirtualBillingOperater.class);
	
	private boolean isNeededToDeleteFileAfterPrinting = false;
	
	public VirtualBillingOperater() {
		virtualBillingFlag = true;
		isNeededToDeleteFileAfterPrinting = Constant.FLAG_YES
				.equals(Constant.propUtil.getStringValue("user.虚拟开票.打印完成后是否删除文档", Constant.FLAG_NO));
	}
	
	protected String getBillingPlanFilePath() {
		return Constant.propUtil.getStringValue("user.虚拟开票.计划路径", Constant.BILLING_INPUT_FILE);
	}
	
	public void endBilling() throws Exception {
		if (!payrollSheetProcesser.isNeededToUpdateBillingPlanBook()) {
			return;
		}
		logger.info("即将开始打印表格...");
		printingProcesser.processPrintTasks();	
	}
	
	public void saveBillingPlan() throws Exception {
		if (!payrollSheetProcesser.isNeededToUpdateBillingPlanBook()) {
			return;
		}
		logger.info("保存开票计划输出： " + billingFile);
		billingPlanProcesser.writeBillingOutput(billingFile);
		logger.info(Constant.LINE1);
	}
	
	public void deleteFilesAfterPrinting() {
		if(isNeededToDeleteFileAfterPrinting) {
			logger.info("删除虚拟开票生成的文档...");
			printingProcesser.deleteFilesAfterPrinting();
		}
	}

}
