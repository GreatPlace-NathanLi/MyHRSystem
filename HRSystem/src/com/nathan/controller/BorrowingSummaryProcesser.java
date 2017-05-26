package com.nathan.controller;

import java.util.List;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;
import com.nathan.exception.BorrowingSummarySheetProcessException;
import com.nathan.model.AggregatingCriteria;
import com.nathan.model.AggregatingResultSheet;
import com.nathan.model.BillingPlan;
import com.nathan.model.BorrowingSummary;
import com.nathan.model.BorrowingSummarySheet;
import com.nathan.model.SheetType;
import com.nathan.service.AbstractExcelOperater;

import jxl.CellType;
import jxl.FormulaCell;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class BorrowingSummaryProcesser extends AbstractExcelOperater implements AggregatingResultProcesser {

	private static Logger logger = Logger.getLogger(BorrowingSummaryProcesser.class);

	private BorrowingSummarySheet borrowingSummarySheet;
	
	public BorrowingSummarySheet process(Object processingData, AggregatingCriteria criteria) throws Exception {
		
		@SuppressWarnings("unchecked")
		List<BillingPlan> resultList = (List<BillingPlan>)processingData; 
		
		borrowingSummarySheet = new BorrowingSummarySheet();
		borrowingSummarySheet.setCompany(criteria.getCompany());
		borrowingSummarySheet.setProjectLeader(criteria.getProjectLeader());
		borrowingSummarySheet.setStartYearMonthInt(criteria.getStartYearMonthInt());
		borrowingSummarySheet.setEndYearMonthInt(criteria.getEndYearMonthInt());
		for (BillingPlan billingPlan : resultList) {
			BorrowingSummary summary = new BorrowingSummary();
			summary.setCompany(criteria.getCompany());
			summary.setProjectLeader(criteria.getProjectLeader());
			summary.setContractID(billingPlan.getContractID());
			summary.setBorrowingDateInt(billingPlan.getBollowingDateInt());
			summary.setBorrowingAmount(billingPlan.getBollowingAmount());
			summary.setRepaymentDateInt(billingPlan.getRepaymentDateInt());
			summary.setRepaymentAmount(billingPlan.getRepaymentAmount());
			
			borrowingSummarySheet.addBorrowingSummary(summary);
		}

		logger.debug(borrowingSummarySheet.getBorrowingSummaryList());
		
		return borrowingSummarySheet;
	}
	
	public void writeBorrowingSummarySheet(BorrowingSummarySheet summarySheet) throws Exception {
		String templatePath = buildBorrowingSummaryTemplatePath();
		String filePath = buildBorrowingSummaryFilePath(summarySheet, false);
		writeBorrowingSummarySheet(templatePath, filePath, summarySheet);
		logger.info("保存劳务费汇总表：" + filePath);
	}
	
	public void printBorrowingSummarySheet(BorrowingSummarySheet summarySheet) throws Exception {
		String templatePath = buildBorrowingSummaryTemplatePath();
		String filePath = buildBorrowingSummaryFilePath(summarySheet, true);
		writeBorrowingSummarySheet(templatePath, filePath, summarySheet);
		PrintingProcesser.resetPrintTasks();
		PrintingProcesser.createExcelPrintTask(SheetType.劳务费汇总表, filePath, 0);
		PrintingProcesser.processPrintTasksIfExists();
		Util.deleteFile(filePath);
	}

	private String buildBorrowingSummaryFilePath(BorrowingSummarySheet summarySheet, boolean isTemp) throws Exception {
		String filePath = Constant.propUtil.getStringDisEmpty(Constant.CONFIG_汇总_借款情况_输出路径);
		if (isTemp) {
			filePath = filePath.replace("UUUU", "TEMP_" + summarySheet.getCompany());
		} else {
			filePath = filePath.replace("UUUU", summarySheet.getCompany());
		}
		filePath = filePath.replace("NNN", summarySheet.getProjectLeader());
		filePath = filePath.replace("SSSS", String.valueOf(summarySheet.getStartYearMonthInt()));
		filePath = filePath.replace("EEEE", String.valueOf(summarySheet.getEndYearMonthInt()));
		return filePath;
	}

	private String buildBorrowingSummaryTemplatePath() throws Exception {
		String paymentDocumentTemplateFile = Constant.propUtil.getStringDisEmpty(Constant.CONFIG_汇总_借款情况_模板路径);
		return paymentDocumentTemplateFile;
	}

	public void writeBorrowingSummarySheet(String templatePath, String filePath, BorrowingSummarySheet summarySheet)
			throws Exception {
		try {
			write(templatePath, filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BorrowingSummarySheetProcessException("保存劳务费汇总表出错，" + e.getMessage());
		}
	}
	
	protected void preWrite(String filePath) {
		removeBorrowingSummarySheetIfExists(filePath);
	}
	
	public void removeBorrowingSummarySheetIfExists(String filePath) {
		if(delete(filePath)) {
			logger.debug("删除借款情况汇总表：" + filePath);
		}
	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {
		WritableSheet sheet = wwb.getSheet(0);

		int rsColumns = sheet.getColumns();
		int rsRows = sheet.getRows();

		logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

		int size = borrowingSummarySheet.getBorrowingSummaryList().size();
		int srcRowIndex = 2;

		for (int r = 0; r < size; r++) {
			int currentRowIndex = r + 3;
			BorrowingSummary summary = borrowingSummarySheet.getBorrowingSummaryList().get(r);
			sheet.insertRow(currentRowIndex);
			sheet.setRowView(currentRowIndex, sheet.getRowView(srcRowIndex));

			Label projectLeader = new Label(0, currentRowIndex, summary.getProjectLeader(),
					sheet.getCell(0, srcRowIndex).getCellFormat());
			sheet.addCell(projectLeader);
			
			Label company = new Label(1, currentRowIndex, summary.getCompany(),
					sheet.getCell(1, srcRowIndex).getCellFormat());
			sheet.addCell(company);
			
			Label contractID = new Label(2, currentRowIndex, summary.getContractID(),
					sheet.getCell(2, srcRowIndex).getCellFormat());
			sheet.addCell(contractID);

			Number borrowingDate = new Number(3, currentRowIndex, summary.getBorrowingDateInt(),
					sheet.getCell(3, srcRowIndex).getCellFormat());
			sheet.addCell(borrowingDate);

			Number borrowingAmount = new Number(4, currentRowIndex, summary.getBorrowingAmount(),
					sheet.getCell(4, srcRowIndex).getCellFormat());
			sheet.addCell(borrowingAmount);

			Number repaymentDate = new Number(5, currentRowIndex, summary.getRepaymentDateInt(),
					sheet.getCell(5, srcRowIndex).getCellFormat());
			sheet.addCell(repaymentDate);

			Number repaymentAmount = new Number(6, currentRowIndex, summary.getRepaymentAmount(),
					sheet.getCell(6, srcRowIndex).getCellFormat());
			sheet.addCell(repaymentAmount);
			
			Label remark = new Label(7, currentRowIndex, summary.getRemark(),
					sheet.getCell(7, srcRowIndex).getCellFormat());
			sheet.addCell(remark);
		}

		sheet.removeRow(srcRowIndex);

		for (int c = 0; c < rsColumns; c++) {
			WritableCell cell = sheet.getWritableCell(c, srcRowIndex + size + 1);
			if (cell.getType() == CellType.NUMBER_FORMULA) {
				String formula = ((FormulaCell) cell).getFormula();
				formula = formula.replace("4)", String.valueOf(srcRowIndex + size + 1) + ")");
				WritableCell newCell = new Formula(c, srcRowIndex + size + 1, formula);
				newCell.setCellFormat(cell.getCellFormat());
				sheet.addCell(newCell);
			}
		}

	}

	@Override
	public void save(AggregatingResultSheet result) {
		logger.debug("save");
		if (borrowingSummarySheet!= null && result instanceof BorrowingSummarySheet) {
			try {
				writeBorrowingSummarySheet((BorrowingSummarySheet) result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}

	@Override
	public void print(AggregatingResultSheet result) {
		logger.debug("print");
		if (borrowingSummarySheet!= null && result instanceof BorrowingSummarySheet) {
			try {
				printBorrowingSummarySheet((BorrowingSummarySheet) result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}

}
