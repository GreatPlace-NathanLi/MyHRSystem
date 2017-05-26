package com.nathan.controller;

import org.apache.log4j.Logger;

import com.nathan.exception.BillingPlanProcessException;
import com.nathan.model.BillingPlan;

import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;

public class AggregatingInputProcesser extends BillingPlanProcesser {

	private static Logger logger = Logger.getLogger(AggregatingInputProcesser.class);

	private int count = 2;

	protected void createBillingPlanFromInputSheet(Sheet readsheet, int rowIndex) throws BillingPlanProcessException {
		try {
			if (isToDoBillingPlan(readsheet, rowIndex)) {
				count++;
				BillingPlan billingPLan = new BillingPlan();
				billingPLan.setRowIndex(rowIndex);

				Cell cell = readsheet.getCell(0, rowIndex);
				billingPLan.setOrderNumber(Integer.valueOf(cell.getContents()));

				cell = readsheet.getCell(1, rowIndex);
				billingPLan.setProjectUnit(cell.getContents().trim());

				cell = readsheet.getCell(2, rowIndex);
				billingPLan.setContractID(cell.getContents().trim());

				cell = readsheet.getCell(3, rowIndex);
				billingPLan.setProjectName(cell.getContents());

				cell = readsheet.getCell(4, rowIndex);
				billingPLan.setProjectLeader(cell.getContents().trim());

				cell = readsheet.getCell(5, rowIndex);
				billingPLan.setProjectID(cell.getContents());

				billingPLan.setContractValue(getDoubleValue(readsheet.getCell(6, rowIndex)));

				cell = readsheet.getCell(10, rowIndex);
				billingPLan.setBillingYear(getIntValue(cell));

				cell = readsheet.getCell(11, rowIndex);
				billingPLan.setBillingMonth(getIntValue(cell));

				cell = readsheet.getCell(12, rowIndex);
				billingPLan.setBillingDay(getIntValue(cell));

				cell = readsheet.getCell(13, rowIndex);
				if (cell.getType().equals(CellType.NUMBER)) {
					billingPLan.setInvoiceAmount(((NumberCell) cell).getValue());
				}

				cell = readsheet.getCell(17, rowIndex);
				billingPLan.setPayCount(getIntValue(cell));

				cell = readsheet.getCell(18, rowIndex);
				if (cell.getType().equals(CellType.NUMBER)) {
					billingPLan.setAdministrationExpenses(((NumberCell) cell).getValue());
				}

				cell = readsheet.getCell(19, rowIndex);
				if (cell.getType().equals(CellType.NUMBER) || cell.getType().equals(CellType.NUMBER_FORMULA)) {
					billingPLan.setTotalAdministrationExpenses(((NumberCell) cell).getValue());
				}

				cell = readsheet.getCell(20, rowIndex);
				if (cell.getType().equals(CellType.NUMBER) || cell.getType().equals(CellType.NUMBER_FORMULA)) {
					billingPLan.setTotalPay(((NumberCell) cell).getValue());
				}

				cell = readsheet.getCell(28, rowIndex);
				if (cell.getType().equals(CellType.NUMBER)) {
					billingPLan.setWithdrawalFee(((NumberCell) cell).getValue());
				}
				
				try {
					cell = readsheet.getCell(31, rowIndex);
					billingPLan.setBollowingDateInt(getIntValue(cell));
					
					cell = readsheet.getCell(32, rowIndex);
					billingPLan.setBollowingAmount(getDoubleValue(cell));
					
					cell = readsheet.getCell(33, rowIndex);
					billingPLan.setRepaymentDateInt(getIntValue(cell));
					
					cell = readsheet.getCell(34, rowIndex);
					billingPLan.setRepaymentAmount(getDoubleValue(cell));
				} catch (Exception e) {
					logger.error(e.getMessage());					
				}				

				billingPlanBook.addBillingPlan(billingPLan);
			}
		} catch (Exception e) {
			logger.error("第几行出错： " + count);
			e.printStackTrace();
		}

	}

	private double getDoubleValue(Cell cell) {
		if (CellType.NUMBER.equals(cell.getType())) {
			return ((NumberCell) cell).getValue();
		} else {
			try {
				return Double.valueOf(cell.getContents());
			} catch (Exception e) {
				// logger.error(e.getMessage());
				// logger.error("第几行出错： " + count);
			}
		}
		return 0.0;
	}

	private int getIntValue(Cell cell) {

		try {
			return Integer.valueOf(cell.getContents().trim());
		} catch (Exception e) {
			// logger.error(e.getMessage());
			// logger.error("第几行出错： " + count);
		}

		return 0;
	}

	private boolean isToDoBillingPlan(Sheet readsheet, int rowIndex) {
		Cell cell = readsheet.getCell(0, rowIndex);
		boolean isToDo = !isEmpty(cell) && CellType.NUMBER.equals(cell.getType());
		return isToDo;
	}
}
