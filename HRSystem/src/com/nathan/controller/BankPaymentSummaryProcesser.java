package com.nathan.controller;

import java.util.List;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.exception.BankPaymentSummaryException;
import com.nathan.model.BankPaymentSummary;
import com.nathan.model.BillingPlan;
import com.nathan.model.BillingPlan.BillingStatus;
import com.nathan.model.BillingPlanBook;
import com.nathan.model.Payroll;
import com.nathan.model.PayrollSheet;
import com.nathan.model.SubBillingPlan;
import com.nathan.service.AbstractExcelOperater;

import jxl.CellType;
import jxl.FormulaCell;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class BankPaymentSummaryProcesser extends AbstractExcelOperater {

	private static Logger logger = Logger.getLogger(BankPaymentSummaryProcesser.class);

	private BankPaymentSummary bankPaymentSummary;

	public void processBankPaymentSummary(BillingPlanBook billingPlanBook) throws BankPaymentSummaryException {
		for (BillingPlan billingPlan : billingPlanBook.getBillingPlanList()) {
			if (billingPlan.isToCreate() && BillingStatus.已制作.equals(billingPlan.getBillingStatus())
					&& billingPlan.isBankPaymentSummaryRequired()) {
				String bankPaymentSummaryFile = buildBankPaymentSummaryFilePath(billingPlan);
				String bankPaymentSummaryTemplateFile = buildBankPaymentSummaryTemplatePath(billingPlan);
				BankPaymentSummary bankPaymentSummary = buildBankPaymentSummary(billingPlan);
				writeBankPaymentSummary(bankPaymentSummaryTemplateFile, bankPaymentSummaryFile, bankPaymentSummary);
				logger.info("保存网银汇总表：" + bankPaymentSummaryFile);
			}
		}
	}

	public BankPaymentSummary buildBankPaymentSummary(BillingPlan billingPlan) {
		BankPaymentSummary bankPaymentSummary = new BankPaymentSummary();
		bankPaymentSummary.setContractID(billingPlan.getContractID());
		for (SubBillingPlan subPlan : billingPlan.getSubPlanList()) {
			List<PayrollSheet> payrollSheetList = subPlan.getPayrollSheetList();
			for (PayrollSheet payrollSheet : payrollSheetList) {
				List<Payroll> payrollList = payrollSheet.getPayrollList();
				for (Payroll payroll : payrollList) {
					bankPaymentSummary.addName(payroll.getName());
					bankPaymentSummary.putPayment(payroll.getName(), payroll.getActualPay());
				}
			}
		}
		return bankPaymentSummary;
	}

	private String buildBankPaymentSummaryFilePath(BillingPlan billingPlan) {
		String summaryFile = Constant.propUtil.getStringValue("user.网银汇总表输出路径", Constant.BANK_PAYMENT_SUMMARY_FILE);
		String filePath = summaryFile.replace("UUUU", billingPlan.getProjectUnit());
		filePath = filePath.replace("NNN", billingPlan.getProjectLeader());
		filePath = filePath.replace("CCCCC", billingPlan.getContractID());
		return filePath;
	}

	private String buildBankPaymentSummaryTemplatePath(BillingPlan billingPlan) {
		String paymentDocumentTemplateFile = Constant.propUtil.getStringValue("user.网银汇总表模板路径",
				Constant.BANK_PAYMENT_SUMMARY_TEMPLATE_FILE);
		return paymentDocumentTemplateFile;
	}

	public void writeBankPaymentSummary(String templatePath, String filePath, BankPaymentSummary bankPaymentSummary)
			throws BankPaymentSummaryException {
		try {
			this.bankPaymentSummary = bankPaymentSummary;
			write(templatePath, filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BankPaymentSummaryException("保存网银汇总表出错，" + e.getMessage());
		}
	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {

		wwb.copySheet(0, bankPaymentSummary.getContractID(), 1);
		WritableSheet sheet = wwb.getSheet(1);

		fillBankPayments(sheet);
		
		int rsColumns = sheet.getColumns();
		int rsRows = sheet.getRows();

		logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

		wwb.removeSheet(0);
	}

	private void fillBankPayments(WritableSheet sheet) throws Exception {

		WritableCell cell = sheet.getWritableCell(0, 1);
		String contractInfo = ((Label) cell).getString();
//		logger.debug("contractInfo:" + contractInfo + "C ID:" + bankPaymentSummary.getContractID());
		contractInfo = contractInfo.replace("CCCCC", bankPaymentSummary.getContractID());
		((Label) cell).setString(contractInfo);

		int srcRowIndex = 3;

		int index = 1;
//		for (Entry<String, Double> entry : bankPaymentSummary.getNamePayMap().entrySet()) {
		for (String name : bankPaymentSummary.getNameList()) {			
			int currentRowIndex = index + srcRowIndex;
			sheet.insertRow(currentRowIndex);

			Number order = new Number(0, currentRowIndex, index, sheet.getCell(0, srcRowIndex).getCellFormat());
			sheet.addCell(order);

			Label nameLable = new Label(1, currentRowIndex, name, sheet.getCell(1, srcRowIndex).getCellFormat());
			sheet.addCell(nameLable);

			Number totalPay = new Number(2, currentRowIndex, bankPaymentSummary.getPayment(name),
					sheet.getCell(2, srcRowIndex).getCellFormat());
			sheet.addCell(totalPay);

			index++;
		}

		sheet.removeRow(srcRowIndex);

		int size = index - 1;
		cell = sheet.getWritableCell(2, srcRowIndex + size);
		// logger.debug("c" + 2 + ",r" + (srcRowIndex + size) + ", cellType: " +
		// cell.getType());
		if (cell.getType() == CellType.NUMBER_FORMULA) {
			String formula = ((FormulaCell) cell).getFormula();
			formula = formula.replace("4)", String.valueOf(srcRowIndex + size) + ")");
			WritableCell newCell = new Formula(2, srcRowIndex + size, formula);
			newCell.setCellFormat(cell.getCellFormat());
			sheet.addCell(newCell);
		}

	}

	protected void preWrite(String filePath) {
		removeBankPaymentSummaryIfExists(filePath);
	}

	public void removeBankPaymentSummaryIfExists(String filePath) {
		if (delete(filePath)) {
			logger.debug("删除旧网银汇总表：" + filePath);
		}
	}
}
