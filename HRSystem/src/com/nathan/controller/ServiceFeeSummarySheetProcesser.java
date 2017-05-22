package com.nathan.controller;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;
import com.nathan.exception.ServiceFeeSummarySheetProcessException;
import com.nathan.model.BillingPlan;
import com.nathan.model.ServiceFeeSummary;
import com.nathan.model.ServiceFeeSummarySheet;
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

public class ServiceFeeSummarySheetProcesser extends AbstractExcelOperater implements AggregatingResultProcesser {

	private static Logger logger = Logger.getLogger(ServiceFeeSummarySheetProcesser.class);

	private SortedMap<String, ServiceFeeSummary> companyYearMonthSummaryMap;

	private ServiceFeeSummarySheet serviceFeeSummarySheet;

	public ServiceFeeSummarySheetProcesser() {
		this.companyYearMonthSummaryMap = new TreeMap<String, ServiceFeeSummary>();
	}

	public ServiceFeeSummarySheet processServiceFeeSummarySheet(List<BillingPlan> resultList, String company, int startYearMonth,
			int endYearMonth) throws Exception {
		for (BillingPlan billingPlan : resultList) {
			ServiceFeeSummary summary = getServiceFeeSummaryFromCache(billingPlan);
			summary.setYearMonthInt(billingPlan.getBillingYearMonthInt());
			summary.add(billingPlan.getInvoiceAmount(), billingPlan.getTotalPay(),
					billingPlan.getTotalAdministrationExpenses());
		}
		serviceFeeSummarySheet = new ServiceFeeSummarySheet();
		serviceFeeSummarySheet.setCompany(company);
		serviceFeeSummarySheet.setStartYearMonthInt(startYearMonth);
		serviceFeeSummarySheet.setEndYearMonthInt(endYearMonth);
		for (ServiceFeeSummary summary : companyYearMonthSummaryMap.values()) {
			summary.setCompany(company);
			serviceFeeSummarySheet.addServiceFeeSummary(summary);
		}
		logger.debug(serviceFeeSummarySheet.getServiceFeeSummaryList());
		
		return serviceFeeSummarySheet;
	}
	
	public void writeServiceFeeSummarySheet(ServiceFeeSummarySheet summarySheet) throws Exception {
		String templatePath = buildServiceFeeSummaryTemplatePath();
		String filePath = buildServiceFeeSummaryFilePath(summarySheet, false);
		writeServiceFeeSummarySheet(templatePath, filePath, summarySheet);
		logger.info("保存劳务费汇总表：" + filePath);
	}
	
	public void printServiceFeeSummarySheet(ServiceFeeSummarySheet summarySheet) throws Exception {
		String templatePath = buildServiceFeeSummaryTemplatePath();
		String filePath = buildServiceFeeSummaryFilePath(summarySheet, true);
		writeServiceFeeSummarySheet(templatePath, filePath, summarySheet);
		PrintingProcesser.resetPrintTasks();
		PrintingProcesser.createExcelPrintTask(SheetType.劳务费汇总表, filePath, 0);
		PrintingProcesser.processPrintTasksIfExists();
		Util.deleteFile(filePath);
	}

	private ServiceFeeSummary getServiceFeeSummaryFromCache(BillingPlan billingPlan) {
		String key = getKey(billingPlan);
		ServiceFeeSummary summary = null;
		if (companyYearMonthSummaryMap.keySet().contains(key)) {
			summary = companyYearMonthSummaryMap.get(key);
		} else {
			summary = new ServiceFeeSummary();
			companyYearMonthSummaryMap.put(key, summary);
		}
		return summary;
	}

	private String getKey(BillingPlan billingPlan) {
		return billingPlan.getProjectUnit() + billingPlan.getBillingYearMonthInt();
	}

	private String buildServiceFeeSummaryFilePath(ServiceFeeSummarySheet summarySheet, boolean isTemp) throws Exception {
		String filePath = Constant.propUtil.getStringDisEmpty("user.汇总.输出路径");
		if (isTemp) {
			filePath = filePath.replace("UUUU", "TEMP_" + summarySheet.getCompany());
		} else {
			filePath = filePath.replace("UUUU", summarySheet.getCompany());
		}	
		filePath = filePath.replace("SSSS", String.valueOf(summarySheet.getStartYearMonthInt()));
		filePath = filePath.replace("EEEE", String.valueOf(summarySheet.getEndYearMonthInt()));
		return filePath;
	}

	private String buildServiceFeeSummaryTemplatePath() throws Exception {
		String paymentDocumentTemplateFile = Constant.propUtil.getStringDisEmpty("user.汇总.模板路径");
		return paymentDocumentTemplateFile;
	}

	public void writeServiceFeeSummarySheet(String templatePath, String filePath, ServiceFeeSummarySheet summarySheet)
			throws Exception {
		try {
			write(templatePath, filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceFeeSummarySheetProcessException("保存劳务费汇总表出错，" + e.getMessage());
		}
	}
	
	protected void preWrite(String filePath) {
		removeServiceFeeSummarySheetIfExists(filePath);
	}
	
	public void removeServiceFeeSummarySheetIfExists(String filePath) {
		if(delete(filePath)) {
			logger.debug("删除劳务费汇总表：" + filePath);
		}
	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {
		WritableSheet sheet = wwb.getSheet(0);

		int rsColumns = sheet.getColumns();
		int rsRows = sheet.getRows();

		logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

		int size = serviceFeeSummarySheet.getServiceFeeSummaryList().size();
		int srcRowIndex = 2;

		for (int r = 0; r < size; r++) {
			int currentRowIndex = r + 3;
			ServiceFeeSummary summary = serviceFeeSummarySheet.getServiceFeeSummaryList().get(r);
			sheet.insertRow(currentRowIndex);
			sheet.setRowView(currentRowIndex, sheet.getRowView(srcRowIndex));

			Label company = new Label(0, currentRowIndex, summary.getCompany(),
					sheet.getCell(0, srcRowIndex).getCellFormat());
			sheet.addCell(company);

			Number yearMonth = new Number(1, currentRowIndex, summary.getYearMonthInt(),
					sheet.getCell(1, srcRowIndex).getCellFormat());
			sheet.addCell(yearMonth);

			Number amount = new Number(2, currentRowIndex, summary.getInvoiceAmount(),
					sheet.getCell(2, srcRowIndex).getCellFormat());
			sheet.addCell(amount);

			Number totalPay = new Number(3, currentRowIndex, summary.getTotalPay(),
					sheet.getCell(3, srcRowIndex).getCellFormat());
			sheet.addCell(totalPay);

			Number fee = new Number(4, currentRowIndex, summary.getTotalAdministrationExpenses(),
					sheet.getCell(4, srcRowIndex).getCellFormat());
			sheet.addCell(fee);

			Label remark = new Label(5, currentRowIndex, summary.getRemark(),
					sheet.getCell(5, srcRowIndex).getCellFormat());
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
	public void save(Object result) {
		logger.debug("save");
		if (serviceFeeSummarySheet!= null && result instanceof ServiceFeeSummarySheet) {
			try {
				writeServiceFeeSummarySheet((ServiceFeeSummarySheet) result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}

	@Override
	public void print(Object result) {
		logger.debug("print");
		if (serviceFeeSummarySheet!= null && result instanceof ServiceFeeSummarySheet) {
			try {
				printServiceFeeSummarySheet((ServiceFeeSummarySheet) result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
}
