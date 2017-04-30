package com.nathan.controller;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.exception.BillingPlanProcessException;
import com.nathan.model.BillingPlan;
import com.nathan.model.BillingPlanBook;
import com.nathan.model.BillingPlan.BillingStatus;
import com.nathan.service.AbstractExcelOperater;

import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class BillingPlanProcesser extends AbstractExcelOperater {

	private static Logger logger = Logger.getLogger(BillingPlanProcesser.class);

	// private ArrayList<BillingPlan> billingPlanList;

	private BillingPlanBook billingPlanBook;

	private boolean bypassBillingOutputCalculation = true;

	public BillingPlanProcesser() {
		this.billingPlanBook = new BillingPlanBook();
	}

	public void processBillingPlanInput(String filePath) throws BillingPlanProcessException {

		readBillingInput(filePath);
		if (billingPlanBook.getTotalBillingPlanSize() == 0) {
			throw new BillingPlanProcessException("û�пɴ���Ŀ�Ʊ�ƻ���");
		}

		logger.info("���㿪Ʊ�ƻ����...");
		calculateBillingOutput();
		billingPlanBook.buildProjectLeaderBillingPlanMap();
		billingPlanBook.buildProjectLeaderPayrollCountMap();

		logger.info("��Ʊ�ƻ��� " + billingPlanBook);
	}

	public void readBillingInput(String filePath) throws BillingPlanProcessException {
		try {
			read(filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BillingPlanProcessException("��ȡ��Ʊ�ƻ�����" + e.getMessage());
		}
	}

	protected void readContent(Workbook readwb) throws Exception {
		Sheet readsheet = readwb.getSheet(0);

		int rsColumns = readsheet.getColumns();
		int rsRows = readsheet.getRows();

		logger.debug("��������" + rsColumns + ", ��������" + rsRows);

		for (int r = 2; r < rsRows; r++) {
			createBillingPlanFromInputSheet(readsheet, r);
		}
	}

	/**
	 * @return the billingPlanBook
	 */
	public BillingPlanBook getBillingPlanBook() {
		return billingPlanBook;
	}

	/**
	 * @param billingPlanBook
	 *            the billingPlanBook to set
	 */
	public void setBillingPlanBook(BillingPlanBook billingPlanBook) {
		this.billingPlanBook = billingPlanBook;
	}

	private void createBillingPlanFromInputSheet(Sheet readsheet, int rowIndex) throws BillingPlanProcessException {
		if (isToDoBillingPlan(readsheet, rowIndex)) {
			BillingPlan billingPLan = new BillingPlan();
			billingPLan.setRowIndex(rowIndex);

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

			cell = readsheet.getCell(10, rowIndex);
			billingPLan.setBillingYear(Integer.valueOf(cell.getContents()));

			cell = readsheet.getCell(11, rowIndex);
			billingPLan.setBillingMonth(Integer.valueOf(cell.getContents()));

			cell = readsheet.getCell(12, rowIndex);
			billingPLan.setBillingDay(Integer.valueOf(cell.getContents()));

			cell = readsheet.getCell(13, rowIndex);
			if (cell.getType().equals(CellType.NUMBER)) {
				billingPLan.setInvoiceAmount(((NumberCell) cell).getValue());
			}

			cell = readsheet.getCell(17, rowIndex);
			if (cell.getType().equals(CellType.NUMBER)) {
				billingPLan.setPayCount(Integer.valueOf(cell.getContents()));
			}

			// numCell = (NumberCell) readsheet.getCell(20, rowIndex);
			// billingPLan.setAdministrationExpensesRate(numCell.getValue());

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

			cell = readsheet.getCell(35, rowIndex);
			billingPLan.setAlternatedProjectLeaderRemark(isEmpty(cell) ? null : cell.getContents());

			cell = readsheet.getCell(36, rowIndex);
			billingPLan.setBillingStatus(isEmpty(cell) ? null : BillingStatus.valueOf(cell.getContents()));

			cell = readsheet.getCell(37, rowIndex);
			billingPLan.setStartAndEndPayTime(isEmpty(cell) ? getDefaultStartAndEndPayTime() : cell.getContents());

			cell = readsheet.getCell(38, rowIndex);
			billingPLan.setBillingID(isEmpty(cell) ? null : cell.getContents());

			validateBillingPlan(billingPLan);

			billingPlanBook.addBillingPlan(billingPLan);
		}

	}

	private String getDefaultStartAndEndPayTime() {
		return Constant.propUtil.getStringEnEmpty("user.Ĭ�Ϲ��ʱ�ָ���·�");
	}

	private boolean isToDoBillingPlan(Sheet readsheet, int rowIndex) {
		Cell cell = readsheet.getCell(36, rowIndex);
		String billingStatus = cell.getContents();		
		boolean isToDo = !isEmpty(cell) && (BillingStatus.������.name().equals(billingStatus) || BillingStatus.��ɾ��.name().equals(billingStatus));
		logger.debug("BillingStatus:" + cell.getContents() + ", isToDo:" + isToDo);
		return isToDo;
	}

	private boolean isEmpty(Cell cell) {
		return CellType.EMPTY.equals(cell.getType()) || Constant.EMPTY_STRING.equals(cell.getContents());
	}

	private void validateBillingPlan(BillingPlan billingPlan) throws BillingPlanProcessException {
		if (!bypassBillingOutputCalculation) {
			return;
		}
		if (billingPlan.getProjectLeader() == null || "".equals(billingPlan.getProjectLeader())) {
			throw new BillingPlanProcessException("��Ʊ�ƻ�����Ӳ���Ϊ��! �ƻ���ţ�" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getInvoiceAmount() <= 0) {
			throw new BillingPlanProcessException("��Ʊ�ƻ��п�Ʊ���������0! �ƻ���ţ�" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getPayCount() <= 0) {
			throw new BillingPlanProcessException("��Ʊ�ƻ��д��������������0! �ƻ���ţ�" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getAdministrationExpenses() <= 0) {
			throw new BillingPlanProcessException("��Ʊ�ƻ��й���ѱ������0! �ƻ���ţ�" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getTotalAdministrationExpenses() <= 0) {
			throw new BillingPlanProcessException("��Ʊ�ƻ��й���ѺϼƱ������0! �ƻ���ţ�" + billingPlan.getOrderNumber());
		}
		if (billingPlan.getTotalPay() <= 0) {
			throw new BillingPlanProcessException("��Ʊ�ƻ���Ӧ�����ʱ������0! �ƻ���ţ�" + billingPlan.getOrderNumber());
		}
	}

	public void calculateBillingOutput() {
		if (bypassBillingOutputCalculation) {
			logger.debug("Bypass Billing Output Calculation!");
			return;
		}
		for (BillingPlan billingPlan : billingPlanBook.getBillingPlanList()) {
			double invoiceAmount = billingPlan.getInvoiceAmount();
			double administrationExpensesRate = billingPlan.getAdministrationExpensesRate();
			double administrationExpenses = billingPlan.getAdministrationExpenses();

			if (administrationExpensesRate == 0) {
				administrationExpensesRate = 0.035;
			}
			if (administrationExpenses == 200) {
				administrationExpensesRate = 0.064;
			}

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

	public void writeBillingOutput(String inputFilePath) throws BillingPlanProcessException {
		try {
			setBackupFlag(true);
			write(inputFilePath, inputFilePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BillingPlanProcessException("���濪Ʊ�ƻ�����" + e.getMessage());
		}
	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {
		WritableSheet sheet = wwb.getSheet(0);

		for (BillingPlan billingPlan : billingPlanBook.getBillingPlanList()) {
			int r = billingPlan.getRowIndex();
			if (!bypassBillingOutputCalculation) {
				Number payCount = new Number(17, r, billingPlan.getPayCount(), sheet.getCell(17, r).getCellFormat());
				sheet.addCell(payCount);

				Number totalAdministrationExpenses = new Number(19, r, billingPlan.getTotalAdministrationExpenses(),
						sheet.getCell(19, r).getCellFormat());
				sheet.addCell(totalAdministrationExpenses);

				Number totalPay = new Number(20, r, billingPlan.getTotalPay(), sheet.getCell(20, r).getCellFormat());
				sheet.addCell(totalPay);

				Number withdrawalFee = new Number(28, r, billingPlan.getWithdrawalFee(),
						sheet.getCell(28, r).getCellFormat());
				sheet.addCell(withdrawalFee);
			}

			if (!BillingStatus.��ɾ��.equals(billingPlan.getBillingStatus()) && !BillingStatus.������.equals(billingPlan.getBillingStatus())) {
				return;
			}
			if (BillingStatus.��ɾ��.equals(billingPlan.getBillingStatus())) {
				billingPlan.resetAlternatedProjectLeaderRemark();
				billingPlan.resetBillingID();
			}
			Label alternatedProjectLeaderRemark = new Label(35, r, billingPlan.getAlternatedProjectLeaderRemark(),
					sheet.getCell(35, r).getCellFormat());
			sheet.addCell(alternatedProjectLeaderRemark);

			Label billingStatus = new Label(36, r, billingPlan.getBillingStatus().name(),
					sheet.getCell(36, r).getCellFormat());
			sheet.addCell(billingStatus);

			Label billingID = new Label(38, r, billingPlan.getBillingID(), sheet.getCell(38, r).getCellFormat());
			sheet.addCell(billingID);
		}
	}

	public static void main(String[] args) throws Exception {
		BillingPlanProcesser billingPlanProcesser = new BillingPlanProcesser();

		long startTime = System.nanoTime();

		logger.info("����1 - ��ȡ��Ʊ�ƻ����룺 " + Constant.BILLING_INPUT_FILE);
		billingPlanProcesser.processBillingPlanInput(Constant.BILLING_INPUT_FILE);
		logger.info(Constant.LINE1);

		logger.info("����2 - ���濪Ʊ�ƻ������ " + Constant.BILLING_OUTPUT_FILE);
		billingPlanProcesser.writeBillingOutput(Constant.BILLING_INPUT_FILE);

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("��Ʊ�ƻ���д������ ��ʱ��" + (endTime - startTime) / 1000000 + "����");
		logger.info(Constant.LINE0);
	}
}
