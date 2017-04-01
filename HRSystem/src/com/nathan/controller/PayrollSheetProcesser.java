package com.nathan.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nathan.common.Constant;
import com.nathan.model.BillingPlan;
import com.nathan.model.BillingPlanBook;
import com.nathan.model.Payroll;
import com.nathan.model.PayrollSheet;
import com.nathan.model.ProjectMember;
import com.nathan.model.ProjectMemberRoster;

import jxl.CellType;
import jxl.FormulaCell;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.biff.formula.FormulaException;
import jxl.common.Logger;
import jxl.write.Blank;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class PayrollSheetProcesser {

	private static Logger logger = Logger.getLogger(PayrollSheetProcesser.class);

	/*
	public void processPayrollSheet(BillingPlan billingPlan, ProjectMemberRoster roster, String payrollFile,
			String payrollTemplateFile) throws Exception {
		logger.info("步骤5 - 计算工资表数据...");
		List<PayrollSheet> payrollSheetList = new ArrayList<PayrollSheet>();
		buildPayrollSheet(billingPlan, roster, payrollSheetList);
		logger.info(Constant.LINE1);

		String outputPath = payrollFile.replace("NNN", billingPlan.getProjectLeader());
		outputPath = outputPath.replace("YYYY", String.valueOf(billingPlan.getPayYear()));

		logger.info("步骤6 - 读取工资表输出模板： " + payrollTemplateFile);
		writePayrollSheet(payrollTemplateFile, outputPath, payrollSheetList);
		logger.info(Constant.LINE1);
		logger.info("步骤7 - 保存工资表输出： " + outputPath);
	}
	*/
	
	public void processPayrollSheet(BillingPlanBook billingPlanBook, RosterProcesser rosterProcesser, String payrollFile,
			String payrollTemplateFile) throws Exception {
		
		for (String projectLeader : billingPlanBook.getProjectLeaderBillingPlanMap().keySet()) {
			List<BillingPlan> billingPlanList = billingPlanBook.getBillingByProjectLeader(projectLeader);
			rosterProcesser.processRoster(projectLeader, billingPlanList.get(0).getPayYear());
			
			ProjectMemberRoster roster = rosterProcesser.getRoster();
			
			int totalPayrollCount = billingPlanBook.getPayrollCountByProjectLeader(projectLeader);
			roster.setCurrentPayYear(billingPlanList.get(0).getPayYear());
			roster.setCurrentPayMonth(billingPlanList.get(0).getPayMonth());
			int availablePayCount = roster.getAvailablePayCount();
			logger.info("availablePayCount: " + availablePayCount + ", totalPayrollCount: " + totalPayrollCount);

			logger.info("步骤5 - 计算工资表数据...");
			List<PayrollSheet> payrollSheetList = new ArrayList<PayrollSheet>();
			for (BillingPlan billingPlan : billingPlanList) {
				int currentPayIndex = roster.getCurrentPayIndex();
				logger.debug("currentPayIndex: " +currentPayIndex);
				buildPayrollSheet(billingPlan, roster, payrollSheetList);
				logger.debug("currentPayIndex1: " +roster.getCurrentPayIndex());
			}
			logger.debug("sheet number: " + payrollSheetList.size());
			logger.debug(payrollSheetList);
			logger.info(Constant.LINE1);
			
			String outputPath = Constant.PAYROLL_FILE.replace("NNN", projectLeader);
			outputPath = outputPath.replace("YYYY", String.valueOf(billingPlanList.get(0).getPayYear()));
			
			logger.info("步骤6 - 读取工资表输出模板： " + Constant.PAYROLL_TEMPLATE_FILE);
			writePayrollSheet(Constant.PAYROLL_TEMPLATE_FILE, outputPath, payrollSheetList);
			logger.info(Constant.LINE1);
			logger.info("步骤7 - 保存工资表输出： " + outputPath);
			
		}	
	}

/*
	public void buildPayrollSheet(BillingPlan billingPlan, ProjectMemberRoster roster,
			List<PayrollSheet> payrollSheetList) {
		// 决定工资表数量
		// Todo: unit test

		int payCount = billingPlan.getPayCount();
		int memberCount = roster.getTotalMember();
		int payrollSheetCount = 0;
		if (payCount <= memberCount && payCount > 0) {
			payrollSheetCount = 1;
		} else if (memberCount > 0) {
			payrollSheetCount = payCount / memberCount + 1;
		}
		// logger.debug("payrollSheetCount: " + payrollSheetCount);
		logger.debug("工资单数量: " + payrollSheetCount);
		// 决定工资表金额/人数/月份

		int payYear = billingPlan.getPayYear();
		int payMonth = billingPlan.getPayMonth();
		if (payrollSheetCount > 1) {
			for (int i = 0; i <= payrollSheetCount - 2; i++) {
				buildPayrollSheet(roster, memberCount, payYear, payMonth, billingPlan, payrollSheetList);
				if (payMonth == 12) {
					payMonth = 1;
					payYear++;
				} else {
					payMonth++;
				}
			}

			buildPayrollSheet(roster, payCount % memberCount, payYear, payMonth, billingPlan, payrollSheetList);
		}

		// logger.debug("payrollSheetList: " + payrollSheetList);

	}
*/
	
	public void buildPayrollSheet(BillingPlan billingPlan, ProjectMemberRoster roster,
			List<PayrollSheet> payrollSheetList) {
		// 决定工资表数量
		// Todo: unit test

		int payCount = billingPlan.getPayCount();
		int memberCount = roster.getTotalMember();
//		int availablePayCount = roster.getAvailablePayCount();
		int remainPayCount = memberCount - roster.getCurrentPayIndex() - 1;
		logger.debug("remainPayCount: " + remainPayCount + ", current billing payCount: " + payCount);
		int payrollSheetCount = 0;
		if (payCount <= remainPayCount && payCount > 0) {
			payrollSheetCount = 1;
		} else if (memberCount > 0) {
			payrollSheetCount = (payCount - remainPayCount) / memberCount + 2;
		}
//		logger.debug("payrollSheetCount: " + payrollSheetCount);
		logger.debug("工资单数量: " + payrollSheetCount);
		// 决定工资表金额/人数/月份

		int payYear = billingPlan.getPayYear();
		int payMonth = billingPlan.getPayMonth();
		if (payrollSheetCount > 1) {
			buildPayrollSheet(roster, remainPayCount, payYear, payMonth, billingPlan, payrollSheetList);
			if (payMonth == 12) {
				payMonth = 1;
				payYear++;
			} else {
				payMonth++;
			}
			for (int i = 1; i <= payrollSheetCount - 2; i++) {
				buildPayrollSheet(roster, memberCount, payYear, payMonth, billingPlan, payrollSheetList);
				if (payMonth == 12) {
					payMonth = 1;
					payYear++;
				} else {
					payMonth++;
				}
			}
			buildPayrollSheet(roster, (payCount-remainPayCount) % memberCount, payYear, payMonth, billingPlan, payrollSheetList);
		} else {
			buildPayrollSheet(roster, payCount, payYear, payMonth, billingPlan, payrollSheetList);
		}

//		logger.debug("payrollSheetList: " + payrollSheetList);

	}

	protected void buildPayrollSheet(ProjectMemberRoster roster, int payrollCount, int payYear, int payMonth,
			BillingPlan billing, List<PayrollSheet> payrollSheetList) {
		logger.debug("payrollCount ***: " + payrollCount + "year " + payYear + ", month " + payMonth);
		PayrollSheet payrollSheet = new PayrollSheet();
		payrollSheet.setPayrollNumber(payrollCount);
		payrollSheet.setPayYear(payYear);
		payrollSheet.setPayMonth(payMonth);
		payrollSheet.setLeader(billing.getProjectLeader());
		payrollSheet.setContractID(billing.getContractID());
		payrollSheet.setTotalAmount(calcPayrollSheetTotalAmount(payrollCount, billing));
		buildPayrolls(payrollSheet, roster);
		payrollSheetList.add(payrollSheet);
		roster.setCurrentPayYear(payYear);
		roster.setCurrentPayMonth(payMonth);
		logger.debug("payrollSheet size ***: " + payrollSheetList.size());
	}

/*
	protected void buildPayrolls(PayrollSheet payrollSheet, ProjectMemberRoster roster) {
		int payrollCount = payrollSheet.getPayrollNumber();
		double totalAmount = payrollSheet.getTotalAmount();
		int initWorkingDayCount = calcDraftWorkingDayCount(payrollCount, totalAmount);
		double tempAmount = 0.0;
		for (int i = 0; i < payrollCount; i++) {
			ProjectMember member = roster.getMember(i);
			Payroll payroll = new Payroll();
			payroll.setOrderNumber(i + 1);
			payroll.setName(member.getName());
			payroll.setBasePay(member.getBasePay());
			payroll.setDailyPay(member.getDailyPay());
			payroll.setWorkingDays(initWorkingDayCount);
			payrollSheet.addPayroll(payroll);
			tempAmount += payroll.getTotalPay();
		}

		double remainAmount = totalAmount - tempAmount;
		for (int i = 0; i < payrollCount; i++) {
			if (remainAmount >= Constant.LOW_DAILY_PAY * 2 || remainAmount > Constant.HIGH_DAILY_PAY + 50) {
				Payroll payroll = payrollSheet.getPayrollList().get(i);
				payroll.increaseWorkingDays(1);
				remainAmount -= payroll.getDailyPay();
				if (i == payrollCount - 1) {
					i = -1;
				}
			} else {
				break;
			}
		}
		payrollSheet.getPayrollList().get(0).setOvertimePay(remainAmount);
		logger.debug("分表金额: " + totalAmount + ", 加班费: " + remainAmount);
	}
*/
	
	protected void buildPayrolls(PayrollSheet payrollSheet, ProjectMemberRoster roster) {
		int payrollCount = payrollSheet.getPayrollNumber();
		double totalAmount = payrollSheet.getTotalAmount();
		int initWorkingDayCount = calcDraftWorkingDayCount(payrollCount, totalAmount);
		double tempAmount = 0.0;
		for (int i = 0; i < payrollCount; i++) {
			ProjectMember member = roster.getMember();
			Payroll payroll = new Payroll();
			payroll.setOrderNumber(i + 1);
			payroll.setName(member.getName());
			payroll.setBasePay(member.getBasePay());
			payroll.setDailyPay(member.getDailyPay());
			payroll.setWorkingDays(initWorkingDayCount);
			payrollSheet.addPayroll(payroll);
			tempAmount += payroll.getTotalPay();
		}
		
		double remainAmount = totalAmount - tempAmount;
		for (int i = 0; i < payrollCount; i++) {
			if (remainAmount >= Constant.LOW_DAILY_PAY * 2 || remainAmount > Constant.HIGH_DAILY_PAY + 50) {
				Payroll payroll = payrollSheet.getPayrollList().get(i);
				payroll.increaseWorkingDays(1);
				remainAmount -= payroll.getDailyPay();
				if (i == payrollCount - 1) {
					i = -1;
				}
			} else {
				break;
			}
		}
		payrollSheet.getPayrollList().get(0).setOvertimePay(remainAmount);
		logger.debug("分表金额: " + totalAmount + ", 加班费: " + remainAmount);
	}

	public void writePayrollSheet(String templatePath, String filePath, List<PayrollSheet> payrollSheetList)
			throws WriteException, IOException {
		WritableWorkbook wwb = null;
		Workbook rwb = null;
		try {
			File inputFile = new File(templatePath);
			File outputFille = new File(filePath);
			rwb = Workbook.getWorkbook(inputFile);

			wwb = Workbook.createWorkbook(outputFille, rwb);// copy
			WritableSheet sheet = wwb.getSheet(0);

			int rsColumns = sheet.getColumns();
			int rsRows = sheet.getRows();

			logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

			for (int i = 0; i < payrollSheetList.size(); i++) {
				PayrollSheet payrollSheet = payrollSheetList.get(i);
				wwb.copySheet(0, "表" + payrollSheet.getPayMonth() + "-" + payrollSheet.getContractID(), i + 1);
				WritableSheet newSheet = wwb.getSheet(i + 1);
				updatePayrollInfo(payrollSheet, newSheet);
				fillPayrollSheet(payrollSheet, newSheet);
			}

			wwb.removeSheet(0);

			wwb.write();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wwb.close();
			rwb.close();
		}

	}

	private void updatePayrollInfo(PayrollSheet payrollSheet, WritableSheet sheet) {

		WritableCell cell = sheet.getWritableCell(2, 0);
		String title = ((Label) cell).getString();
		title = title.replace("YYYY", String.valueOf(payrollSheet.getPayYear()));
		title = title.replace("MM", String.valueOf(payrollSheet.getPayMonth()));
		((Label) cell).setString(title);

		cell = sheet.getWritableCell(0, 1);
		String leaderAndContract = ((Label) cell).getString();
		leaderAndContract = leaderAndContract.replace("NNN", payrollSheet.getLeader());
		leaderAndContract = leaderAndContract.replace("CCCCC", payrollSheet.getContractID());
		((Label) cell).setString(leaderAndContract);

		// int rsColumns = sheet.getColumns();
		// for (int c = 0; c < rsColumns; c++) {
		// cell = sheet.getWritableCell(c, 2);
		// logger.debug("c" + c + ",r" + 0 + ", cellType: " + cell.getType() +
		// ", value: " + cell.getContents());
		// }
	}

	private void fillPayrollSheet(PayrollSheet payrollSheet, WritableSheet sheet)
			throws RowsExceededException, WriteException, FormulaException {
		int size = payrollSheet.getPayrollList().size();
		int srcRowIndex = 3;
		int rsColumns = sheet.getColumns();

		for (int r = 0; r < size; r++) {
			int currentRowIndex = r + 4;
			Payroll payroll = payrollSheet.getPayrollList().get(r);
			sheet.insertRow(currentRowIndex);

			for (int c = 0; c < rsColumns; c++) {
				WritableCell cell = sheet.getWritableCell(c, srcRowIndex);
				// logger.debug("c"+c+",r"+(r+4)+", cellType: "
				// +cell.getType());
				WritableCell newCell = new EmptyCell(c, currentRowIndex);
				if (cell.getType() == CellType.LABEL) {
					newCell = ((Label) cell).copyTo(c, currentRowIndex);
				} else if (cell.getType() == CellType.NUMBER) {
					newCell = ((Number) cell).copyTo(c, currentRowIndex);
				} else if (cell.getType() == CellType.NUMBER_FORMULA || cell.getType() == CellType.STRING_FORMULA) {
					String formula = ((FormulaCell) cell).getFormula();
					formula = formula.replaceAll(String.valueOf(srcRowIndex + 1), String.valueOf(currentRowIndex + 1));
					// logger.debug("formula: " + formula);
					newCell = new Formula(c, currentRowIndex, formula);
				} else if (cell.getType() == CellType.EMPTY) {
					newCell = ((Blank) cell).copyTo(c, currentRowIndex);
				} else {
					if (cell.getCellFormat() != null) {
						newCell = new Blank(cell);
					}
				}
				newCell.setCellFormat(cell.getCellFormat());
				if (cell.getWritableCellFeatures() != null) {
					newCell.setCellFeatures(cell.getWritableCellFeatures());
				}

				sheet.addCell(newCell);

				if (c == 0) {
					((Number) newCell).setValue(payroll.getOrderNumber());
				} else if (c == 1) {
					((Label) newCell).setString(payroll.getName());
				} else if (c == 2) {
					((Number) newCell).setValue(payroll.getBasePay());
				} else if (c == 13) {
					((Number) newCell).setValue(payroll.getWorkingDays());
				} else if (c == 15) {
					((Number) newCell).setValue(payroll.getOvertimePay());
				}
			}
		}
		sheet.removeRow(srcRowIndex);
		for (int c = 0; c < rsColumns; c++) {
			WritableCell cell = sheet.getWritableCell(c, srcRowIndex + size);
			// logger.debug("c" + c + ",r" + (srcRowIndex + size) + ", cellType:
			// " + cell.getType());
			if (cell.getType() == CellType.NUMBER_FORMULA) {
				String formula = ((FormulaCell) cell).getFormula();
				// logger.debug("formula1: " + formula);
				// formula = formula.replaceFirst(String.valueOf(srcRowIndex),
				// String.valueOf(srcRowIndex + size));
				// formula = formula.replaceAll(String.valueOf(srcRowIndex),
				// String.valueOf(srcRowIndex + 1));
				// int a = formula.indexOf(':');
				// formula.substring(a+2);
				// logger.debug("substring: " + formula.substring(a+2));
				formula = formula.replace("4)", String.valueOf(srcRowIndex + size) + ")");
				// logger.debug("formula2: " + formula);
				WritableCell newCell = new Formula(c, srcRowIndex + size, formula);
				newCell.setCellFormat(cell.getCellFormat());

				sheet.addCell(newCell);
			}
		}
	}

	protected int calcDraftWorkingDayCount(int payrollCount, double totalAmount) {
		int initWorkingDayCount = (int) Math.floor(totalAmount / payrollCount / Constant.HIGH_DAILY_PAY);
		// logger.debug("initWorkingDayCount: " + initWorkingDayCount);
		return initWorkingDayCount;
	}

	private static double calcPayrollSheetTotalAmount(int payrollNumber, BillingPlan billing) {
		return Math.round(billing.getTotalPay() / billing.getPayCount() * payrollNumber);
	}

}
