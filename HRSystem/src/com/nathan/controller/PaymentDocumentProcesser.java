package com.nathan.controller;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;
import com.nathan.exception.PaymentDocumentProcessException;
import com.nathan.model.BillingPlan;
import com.nathan.model.BillingPlan.BillingStatus;
import com.nathan.model.BillingPlanBook;
import com.nathan.model.SheetType;
import com.nathan.service.AbstractExcelOperater;

import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class PaymentDocumentProcesser extends AbstractExcelOperater {

	private static Logger logger = Logger.getLogger(PaymentDocumentProcesser.class);

	private BillingPlan billingPlan;
	private String filePath;

	public void processPaymentDocument(BillingPlanBook billingPlanBook) throws PaymentDocumentProcessException {
		for (BillingPlan billingPlan : billingPlanBook.getBillingPlanList()) {
			if (billingPlan.isToCreate() && BillingStatus.已制作.equals(billingPlan.getBillingStatus())) {
				String paymentDocumentFile = buildPaymentDocumentFilePath(billingPlan);
				String paymentDocumentTemplateFile = buildPaymentDocumentTemplatePath(billingPlan);		
				writePaymentDocument(paymentDocumentTemplateFile, paymentDocumentFile, billingPlan);
				logger.info("创建付款手续单据：" + paymentDocumentFile);
			}		
		}
	}

	public void readPaymentDocumentTemplate(String templatePath) throws PaymentDocumentProcessException {
		try {
			read(templatePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new PaymentDocumentProcessException("读取付款手续单据模板出错，" + e.getMessage());
		}
	}
	
	public void removePaymentDocumentIfExists(String filePath) {
		if(delete(filePath)) {
			logger.debug("删除付款手续单据：" + filePath);
		}
	}

	public void writePaymentDocument(String templatePath, String filePath, BillingPlan billingPlan)
			throws PaymentDocumentProcessException {
		try {
			this.billingPlan = billingPlan;
			this.filePath = filePath;
			write(templatePath, filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new PaymentDocumentProcessException("保存付款手续单据出错，" + e.getMessage());
		}
	}
	
	protected void preWrite(String filePath) {
		removePaymentDocumentIfExists(filePath);
	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {
		writePaymentVoucher(wwb);
		writeExpenditureProof(wwb);
		writeExpenseDetails(wwb);
		
		PrintingProcesser.createExcelPrintTask(SheetType.费用明细表, filePath, 0);
		PrintingProcesser.createExcelPrintTask(SheetType.支出证明单, filePath, 1);
		PrintingProcesser.createExcelPrintTask(SheetType.付款凭据, filePath, 2);
	}

	private void writePaymentVoucher(WritableWorkbook wwb) throws Exception {
		WritableSheet sheet = wwb.getSheet(SheetType.付款凭据.name());
		
		logInfo(sheet);

		Label tabulator = new Label(3, 2, Util.getPaymentDocTabulator(), sheet.getCell(3, 2).getCellFormat());
		sheet.addCell(tabulator);

		Label contractID = new Label(1, 3, billingPlan.getContractID(), sheet.getCell(1, 3).getCellFormat());
		sheet.addCell(contractID);

		Label projectName = new Label(2, 3, billingPlan.getProjectName(), sheet.getCell(2, 3).getCellFormat());
		sheet.addCell(projectName);

		Label projectLeader = new Label(3, 6, billingPlan.getProjectLeader(), sheet.getCell(3, 6).getCellFormat());
		sheet.addCell(projectLeader);
		
		Label billingDate = new Label(3, 8, billingPlan.getBillingDateString(), sheet.getCell(3, 8).getCellFormat());
		sheet.addCell(billingDate);

		Number totalPay = new Number(5, 5, billingPlan.getTotalPay(), sheet.getCell(5, 5).getCellFormat());
		sheet.addCell(totalPay);

		Number totalAdministrationExpenses = new Number(5, 6, billingPlan.getTotalAdministrationExpenses(),
				sheet.getCell(5, 6).getCellFormat());
		sheet.addCell(totalAdministrationExpenses);
		
		buildFooter(sheet, SheetType.付款凭据);
	}

	private void writeExpenditureProof(WritableWorkbook wwb) throws Exception {
		WritableSheet sheet = wwb.getSheet(SheetType.支出证明单.name());

		logInfo(sheet);

		Label summary = new Label(2, 4, billingPlan.getProjectUnit() + "派遣队工资款", sheet.getCell(2, 4).getCellFormat());
		sheet.addCell(summary);
		
		buildFooter(sheet, SheetType.支出证明单);
	}

	private void writeExpenseDetails(WritableWorkbook wwb) throws Exception {
		WritableSheet sheet = wwb.getSheet(SheetType.费用明细表.name());

		logInfo(sheet);
		
		WritableCell cell = sheet.getWritableCell(0, 0);
		String title = ((Label) cell).getString();
		title = title.replace("YYYY", Util.getCurrentYearString());
		title = title.replace("MM", Util.getCurrentMonthString());
		((Label) cell).setString(title);

		cell = sheet.getWritableCell(0, 6);
		String tabulator = ((Label) cell).getString();
		tabulator = tabulator.replace("NNN", Util.getPaymentDocTabulator());
		((Label) cell).setString(tabulator);

		Label projectUnit = new Label(0, 2, billingPlan.getProjectUnit(), sheet.getCell(0, 2).getCellFormat());
		sheet.addCell(projectUnit);
		
		Label projectLeader = new Label(1, 2, billingPlan.getProjectLeader(), sheet.getCell(1, 2).getCellFormat());
		sheet.addCell(projectLeader);

		Label contractID = new Label(2, 2, billingPlan.getContractID(), sheet.getCell(2, 2).getCellFormat());
		sheet.addCell(contractID);

		Number totalPay = new Number(3, 2, billingPlan.getTotalPay(), sheet.getCell(3, 2).getCellFormat());
		sheet.addCell(totalPay);
		
		Number payCount = new Number(5, 2, billingPlan.getPayCount(), sheet.getCell(5, 2).getCellFormat());
		sheet.addCell(payCount);

		Number totalAdministrationExpenses = new Number(6, 2, billingPlan.getTotalAdministrationExpenses(),
				sheet.getCell(6, 2).getCellFormat());
		sheet.addCell(totalAdministrationExpenses);
		
		buildHeader(sheet, SheetType.费用明细表);
	}
	
	private void buildHeader(WritableSheet sheet, SheetType type) {
		writeHeader(sheet, Util.getFooterContents(billingPlan.getProjectUnit(),
				billingPlan.getContractID(), type.getSheetID()));
	}
	
	private void buildFooter(WritableSheet sheet, SheetType type) {
		writeFooter(sheet, Util.getFooterContents(billingPlan.getProjectUnit(),
				billingPlan.getContractID(), type.getSheetID()));
	}
	
	private String buildPaymentDocumentFilePath(BillingPlan billingPlan) {
		String paymentDocFile = Constant.propUtil.getStringValue("user.付款手续单据输出路径", Constant.PAYMENT_DOC_FILE);
		String filePath = paymentDocFile.replace("UUUU", billingPlan.getProjectUnit());
		filePath = filePath.replace("NNN", billingPlan.getProjectLeader());
		filePath = filePath.replace("CCCCC", billingPlan.getContractID());
		return filePath;
	}
	
	private String buildPaymentDocumentTemplatePath(BillingPlan billingPlan) {
		String paymentDocumentTemplateFile = Constant.propUtil.getStringValue("user.付款手续单据模板路径",Constant.PAYMENT_DOC_TEMPLATE_FILE);
		return paymentDocumentTemplateFile;
	}

	public static void main(String[] args) throws Exception {
		BillingPlanProcesser billingPlanProcesser = new BillingPlanProcesser();
		PaymentDocumentProcesser paymentDocumentProcesser = new PaymentDocumentProcesser();

		Constant.propUtil.init();
		long startTime = System.nanoTime();

		logger.info("步骤1 - 读取开票计划输入： " + Constant.BILLING_INPUT_FILE);
		billingPlanProcesser.processBillingPlanInput(Constant.BILLING_INPUT_FILE);
		logger.info(Constant.LINE1);

		logger.info("步骤2 - 保存开票计划输出： " + Constant.BILLING_INPUT_FILE);
		billingPlanProcesser.writeBillingOutput(Constant.BILLING_INPUT_FILE);
		logger.info(Constant.LINE1);
		
//		paymentDocumentProcesser.readPaymentDocumentTemplate(Constant.PAYMENT_DOC_TEMPLATE_FILE);
		logger.info("步骤3 - 保存付款手续单据输出 ");
		
		paymentDocumentProcesser.processPaymentDocument(billingPlanProcesser.getBillingPlanBook());

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("开票计划读写结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
		logger.info(Constant.LINE0);
	}

}
