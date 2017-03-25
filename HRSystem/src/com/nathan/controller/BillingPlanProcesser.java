package com.nathan.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.nathan.model.BillingPlan;
import com.nathan.model.BillingPlanBook;

import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class BillingPlanProcesser {
	
	private static Logger logger = Logger.getLogger(BillingPlanProcesser.class);
	
	//private ArrayList<BillingPlan> billingPlanList;
	
	private BillingPlanBook billingPlanBook;
	
	public BillingPlanProcesser() {
		this.billingPlanBook = new BillingPlanBook();
	}
	
	public void processBillingPlanInput(String filePath) {
		logger.info("步骤1 - 读取开票计划输入： " + filePath);
		readBillingInput(filePath);
//		logger.info(LINE1);
		
		logger.info("步骤2 - 计算开票计划结果...");
		calculateBillingOutput();
		billingPlanBook.buildProjectLeaderBillingPlanMap();
		billingPlanBook.buildProjectLeaderPayrollCountMap();
		
		logger.info("开票计划： " + billingPlanBook);
//		logger.info(LINE1);
	}

	public void readBillingInput(String filePath) {
		
		Workbook readwb = null;

		try {
			// 直接从本地文件创建Workbook
			InputStream instream = new FileInputStream(filePath);

			readwb = Workbook.getWorkbook(instream);
			Sheet readsheet = readwb.getSheet(0);
			
			int rsColumns = readsheet.getColumns();
			int rsRows = readsheet.getRows();

			logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

//			// 获取指定单元格的对象引用
//
//			for (int i = 0; i < rsRows; i++)
//
//			{
//
//				for (int j = 0; j < rsColumns; j++)
//
//				{
//
//					Cell cell = readsheet.getCell(j, i);
//
//					System.out.print(cell.getContents() + " ");
//
//					// System.out.println(cell.getType());
//
//				}
//
//				System.out.println();
//
//			}
			
			for (int r = 2; r < rsRows; r++) {
				createBillingObjectFromInputSheet(readsheet, r);
			}

			

			instream.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readwb.close();
		}

	}

	/**
	 * @return the billingPlanBook
	 */
	public BillingPlanBook getBillingPlanBook() {
		return billingPlanBook;
	}


	/**
	 * @param billingPlanBook the billingPlanBook to set
	 */
	public void setBillingPlanBook(BillingPlanBook billingPlanBook) {
		this.billingPlanBook = billingPlanBook;
	}


	private void createBillingObjectFromInputSheet(Sheet readsheet, int rowIndex) {
		BillingPlan billingPLan = new BillingPlan();

		Cell cell = readsheet.getCell(0, rowIndex);
		billingPLan.setOrderNumber(Integer.valueOf(cell.getContents()));

		cell = readsheet.getCell(1, rowIndex);
		billingPLan.setProjectUnit(cell.getContents());

		cell = readsheet.getCell(2, rowIndex);
		billingPLan.setContractID(cell.getContents());

		cell = readsheet.getCell(3, rowIndex);
		billingPLan.setProjectName(cell.getContents());

		cell = readsheet.getCell(4, rowIndex);
		billingPLan.setProjectLeader(cell.getContents());

		cell = readsheet.getCell(5, rowIndex);
		billingPLan.setProjectID(cell.getContents());

		NumberCell numCell = (NumberCell) readsheet.getCell(6, rowIndex);
		billingPLan.setContractValue(numCell.getValue());

		numCell = (NumberCell) readsheet.getCell(13, rowIndex);
		billingPLan.setInvoiceAmount(numCell.getValue());

		cell = readsheet.getCell(14, rowIndex);
		billingPLan.setPayYear(Integer.parseInt(cell.getContents()));

		cell = readsheet.getCell(15, rowIndex);
		billingPLan.setPayMonth(Integer.parseInt(cell.getContents()));

		numCell = (NumberCell) readsheet.getCell(20, rowIndex);
		billingPLan.setAdministrationExpensesRate(numCell.getValue());

		numCell = (NumberCell) readsheet.getCell(21, rowIndex);
		billingPLan.setAdministrationExpenses(numCell.getValue());
		
		billingPlanBook.addBillingPlan(billingPLan);
	}
	
	public void calculateBillingOutput() {
		for(BillingPlan billingPlan : billingPlanBook.getBillingPlanList()) {
			double invoiceAmount = billingPlan.getInvoiceAmount();
			double administrationExpensesRate = billingPlan.getAdministrationExpensesRate();
			double administrationExpenses = billingPlan.getAdministrationExpenses();

			int payCount = (int) Math.ceil(invoiceAmount * administrationExpensesRate / administrationExpenses);
			double totalAdministrationExpenses = administrationExpenses * payCount;
			double totalPay = invoiceAmount - totalAdministrationExpenses;
			double withdrawalFee = totalPay * 0.001;

			billingPlan.setPayCount(payCount);
			billingPlan.setTotalAdministrationExpenses(totalAdministrationExpenses);
			billingPlan.setTotalPay(totalPay);
			billingPlan.setWithdrawalFee(withdrawalFee);
		}	
	}

	public void writeBillingOutput(String inputFilePath, String outputFilePath)
			throws WriteException, IOException {
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			File inputFile = new File(inputFilePath);
			File outputFille = new File(outputFilePath);
			rwb = Workbook.getWorkbook(inputFile);

			wwb = Workbook.createWorkbook(outputFille, rwb);// copy
			WritableSheet sheet = wwb.getSheet(0);
			
			int rsRows = sheet.getRows();
			for (int r = 2; r < rsRows; r++) {
				BillingPlan billing = billingPlanBook.getBillingPlanList().get(r - 2);
				Number staffNumber = new Number(19, r, billing.getPayCount(), sheet.getCell(19, r).getCellFormat());
				sheet.addCell(staffNumber);

				Number totalAdministrationExpenses = new Number(22, r, billing.getTotalAdministrationExpenses(),
						sheet.getCell(22, r).getCellFormat());
				sheet.addCell(totalAdministrationExpenses);

				Number totalPay = new Number(23, r, billing.getTotalPay(), sheet.getCell(23, r).getCellFormat());
				sheet.addCell(totalPay);

				Number withdrawalFee = new Number(31, r, billing.getWithdrawalFee(), sheet.getCell(31, r).getCellFormat());
				sheet.addCell(withdrawalFee);
			}		
	
			wwb.write();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwb.close();
			wwb.close();
		}

	}
}
