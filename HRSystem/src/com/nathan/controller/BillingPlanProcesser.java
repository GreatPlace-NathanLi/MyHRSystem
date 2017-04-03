package com.nathan.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.nathan.exception.BillingPlanProcessException;
import com.nathan.model.BillingPlan;
import com.nathan.model.BillingPlanBook;

import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class BillingPlanProcesser {
	
	private static Logger logger = Logger.getLogger(BillingPlanProcesser.class);
	
	//private ArrayList<BillingPlan> billingPlanList;
	
	private BillingPlanBook billingPlanBook;
	
	private boolean bypassBillingOutputCalculation = true;
	
	public BillingPlanProcesser() {
		this.billingPlanBook = new BillingPlanBook();
	}
	
	public void processBillingPlanInput(String filePath) throws BillingPlanProcessException {
		logger.info("步骤1 - 读取开票计划输入： " + filePath);
		readBillingInput(filePath);
//		logger.info(LINE1);
		if (billingPlanBook.getTotalBillingPlanSize() == 0) {
			throw new BillingPlanProcessException("没有可处理的开票计划！");
		}
		
		logger.info("步骤2 - 计算开票计划结果...");
		calculateBillingOutput();
		billingPlanBook.buildProjectLeaderBillingPlanMap();
		billingPlanBook.buildProjectLeaderPayrollCountMap();
		
		logger.info("开票计划： " + billingPlanBook);
//		logger.info(LINE1);
	}

	public void readBillingInput(String filePath) throws BillingPlanProcessException {
		
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
			throw new BillingPlanProcessException("读取开票计划出错，" + e.getMessage());
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


	private void createBillingObjectFromInputSheet(Sheet readsheet, int rowIndex) throws BillingPlanProcessException {
		Cell cell = readsheet.getCell(35, rowIndex);
		logger.debug("cell:" + cell.getContents());
		System.out.println(cell.getType());
		if(CellType.EMPTY.equals(cell.getType()) || "".equals(cell.getContents())) {
			BillingPlan billingPLan = new BillingPlan();

			cell = readsheet.getCell(0, rowIndex);
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

			cell = readsheet.getCell(13, rowIndex);
			if (cell.getType().equals(CellType.NUMBER)) {
				billingPLan.setInvoiceAmount(((NumberCell)cell).getValue());
			}		
			
			cell = readsheet.getCell(17, rowIndex);
			if (cell.getType().equals(CellType.NUMBER)) {
				billingPLan.setPayCount(Integer.valueOf(cell.getContents()));
			}	

//			numCell = (NumberCell) readsheet.getCell(20, rowIndex);
//			billingPLan.setAdministrationExpensesRate(numCell.getValue());

			cell = readsheet.getCell(18, rowIndex);
			if (cell.getType().equals(CellType.NUMBER)) {
				billingPLan.setAdministrationExpenses(((NumberCell)cell).getValue());
			}	
			
			cell = readsheet.getCell(19, rowIndex);
			System.out.println(cell.getType());
			if (cell.getType().equals(CellType.NUMBER) || cell.getType().equals(CellType.NUMBER_FORMULA)) {
				billingPLan.setTotalAdministrationExpenses(((NumberCell)cell).getValue());
			}	
			
			cell = readsheet.getCell(20, rowIndex);
			if (cell.getType().equals(CellType.NUMBER) || cell.getType().equals(CellType.NUMBER_FORMULA)) {
				billingPLan.setTotalPay(((NumberCell)cell).getValue());
			}	
			
			numCell = (NumberCell) readsheet.getCell(28, rowIndex);
			billingPLan.setWithdrawalFee(numCell.getValue());
			
			cell = readsheet.getCell(35, rowIndex);
			billingPLan.setBillingStatus(cell.getContents());
			
			cell = readsheet.getCell(37, rowIndex);
			billingPLan.setBillingID(cell.getContents());
			
			cell = readsheet.getCell(38, rowIndex);
			billingPLan.setStartAndEndPayTime(cell.getContents());
			
			validateBillingPlan(billingPLan);
			
			billingPlanBook.addBillingPlan(billingPLan);
		}			
		
	}
	
	private void validateBillingPlan(BillingPlan billingPlan) throws BillingPlanProcessException {
		if (billingPlan.getProjectLeader() == null || "".equals(billingPlan.getProjectLeader())) {
			throw new BillingPlanProcessException("开票计划中领队不能为空! 计划序号：" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getInvoiceAmount()<=0) {
			throw new BillingPlanProcessException("开票计划中开票金额必须大于0! 计划序号：" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getPayCount()<=0) {
			throw new BillingPlanProcessException("开票计划中代理人数必须大于0! 计划序号：" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getAdministrationExpenses()<=0) {
			throw new BillingPlanProcessException("开票计划中管理费必须大于0! 计划序号：" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getTotalAdministrationExpenses()<=0) {
			throw new BillingPlanProcessException("开票计划中管理费合计必须大于0! 计划序号：" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getTotalPay()<=0) {
			throw new BillingPlanProcessException("开票计划中应付工资必须大于0! 计划序号：" + billingPlan.getOrderNumber());
		}
	}

	
	public void calculateBillingOutput() {
		if (bypassBillingOutputCalculation) {
			logger.debug("Bypass Billing Output Calculation!");
			return;
		}
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
				BillingPlan billingPlan = billingPlanBook.getBillingPlanList().get(r - 2);
				if (!bypassBillingOutputCalculation) {
					Number payCount = new Number(17, r, billingPlan.getPayCount(), sheet.getCell(17, r).getCellFormat());
					sheet.addCell(payCount);

					Number totalAdministrationExpenses = new Number(19, r, billingPlan.getTotalAdministrationExpenses(),
							sheet.getCell(19, r).getCellFormat());
					sheet.addCell(totalAdministrationExpenses);

					Number totalPay = new Number(20, r, billingPlan.getTotalPay(), sheet.getCell(20, r).getCellFormat());
					sheet.addCell(totalPay);

					Number withdrawalFee = new Number(28, r, billingPlan.getWithdrawalFee(), sheet.getCell(28, r).getCellFormat());
					sheet.addCell(withdrawalFee);
				}		
				
				Label billingStatus = new Label(35,r,billingPlan.getBillingStatusAfterBillingCompleted(), sheet.getCell(35, r).getCellFormat());
				sheet.addCell(billingStatus);
				
				Label billingID = new Label(37,r,billingPlan.getBillingIDAfterBillingCompleted(), sheet.getCell(37, r).getCellFormat());
				sheet.addCell(billingID);
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
