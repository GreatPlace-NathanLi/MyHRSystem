package com.nathan.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nathan.common.Constant;
import com.nathan.exception.RosterProcessException;
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
	
	private int rosterFullUpTime = 0;
	
	public void processPayrollSheet(BillingPlanBook billingPlanBook, RosterProcesser rosterProcesser, String payrollFile,
			String payrollTemplateFile) throws Exception {
		
		for (String projectLeader : billingPlanBook.getProjectLeaderBillingPlanMap().keySet()) {
			List<BillingPlan> billingPlanList = billingPlanBook.getBillingByProjectLeader(projectLeader);
			List<PayrollSheet> payrollSheetList = new ArrayList<PayrollSheet>();
			
			for (BillingPlan billingPlan : billingPlanList) {
				buildPayrollSheetForSingleBillingPlan(billingPlan, payrollSheetList, rosterProcesser);				
			}
			
		}	
	}
	
	private void buildPayrollSheetForSingleBillingPlan(BillingPlan billingPlan, List<PayrollSheet> payrollSheetList,
			RosterProcesser rosterProcesser) throws RosterProcessException, WriteException, IOException {
		
		logger.info(Constant.LINE1);
		logger.info("制作工资表 - 开票计划序号：" + billingPlan.getOrderNumber());
		
		rosterProcesser.processRoster(billingPlan.getProjectLeader(), billingPlan.getStartPayYear());

		ProjectMemberRoster roster = rosterProcesser.getRoster();

		int remainPayCount = buildPayrollSheet(billingPlan, roster, payrollSheetList);
		
		logger.info(Constant.LINE1);
		
		String outputPath = Constant.PAYROLL_FILE.replace("NNN", billingPlan.getProjectLeader());
		outputPath = outputPath.replace("YYYY", String.valueOf(billingPlan.getStartPayYear()));
		
		logger.info("读取工资表输出模板： " + Constant.PAYROLL_TEMPLATE_FILE);
		writePayrollSheet(Constant.PAYROLL_TEMPLATE_FILE, outputPath, payrollSheetList);
		logger.info(Constant.LINE1);
		logger.info("保存工资表输出： " + outputPath);
		logger.info(Constant.LINE1);
		rosterProcesser.updateProjectMemberRoster();
		logger.info(Constant.LINE1);
		if (remainPayCount > 0) {
			handleRosterFullUp(billingPlan, rosterProcesser, remainPayCount);
		}
	}
	
	private void handleRosterFullUp(BillingPlan billingPlan,
			RosterProcesser rosterProcesser, int remainPayCount) throws RosterProcessException, WriteException, IOException {
		logger.info(billingPlan.getProjectLeader() + "花名册名额用完，借人开始...");
		String alternatedProjectLeader = getAlternatedProjectLeader();
		billingPlan.setTotalPay(calcPayrollSheetTotalAmount(remainPayCount, billingPlan));
		billingPlan.setPayCount(remainPayCount);
		billingPlan.setProjectLeader(alternatedProjectLeader);
		rosterFullUpTime++;
		List<PayrollSheet> payrollSheetList = new ArrayList<PayrollSheet>();
		buildPayrollSheetForSingleBillingPlan(billingPlan, payrollSheetList, rosterProcesser);		
	}
	
	private String getAlternatedProjectLeader() {
		
		String alternatedProjectLeader = "李一";
		if(rosterFullUpTime > 0)
			alternatedProjectLeader = "黄六";
		logger.info("从" + alternatedProjectLeader + "花名册中借人");
		return alternatedProjectLeader;
	}
	
	public int buildPayrollSheet(BillingPlan billingPlan, ProjectMemberRoster roster,
			List<PayrollSheet> payrollSheetList) {
		// 决定工资表数量
		// Todo: unit test
		logger.info("计算工资表数据...");
		int payYear = billingPlan.getStartPayYear();
		int startPayMonth = billingPlan.getStartPayMonth();
		int endPayMonth = billingPlan.getEndPayMonth();
		if (billingPlan.getStartPayYear() < billingPlan.getEndPayYear()) {
			endPayMonth = 12;
		}
		int payCount = billingPlan.getPayCount();
		int remainPayCount = payCount;
		
		for (int month = startPayMonth; month <= endPayMonth; month++) {
			int monthAvailableCount = roster.getStatistics().getMonthAvailableCount(month);
			int currentProcessingCount = Math.min(monthAvailableCount, remainPayCount);
			if (currentProcessingCount == 0) {
				continue;
			}
			buildPayrollSheet(roster, currentProcessingCount, payYear, month, billingPlan, payrollSheetList);
			remainPayCount -= currentProcessingCount;
			logger.debug("remainPayCount: " + remainPayCount + ", current processing payCount: " + currentProcessingCount);
			if (remainPayCount ==0) {
				break;
			}
		}
		
		logger.debug("工资单数量: " + payrollSheetList.size());
		if(rosterFullUpTime == 0) {
			billingPlan.setBillingID(payCount - remainPayCount);
		} else {
			billingPlan.setAlternatedProjectLeaderRemark(payCount - remainPayCount);
		}

//		logger.debug("payrollSheetList: " + payrollSheetList);

		return remainPayCount;
	}

	protected void buildPayrollSheet(ProjectMemberRoster roster, int payrollCount, int payYear, int payMonth,
			BillingPlan billingPlan, List<PayrollSheet> payrollSheetList) {
		logger.debug("payrollCount: " + payrollCount + ", year " + payYear + ", month " + payMonth);
		PayrollSheet payrollSheet = new PayrollSheet();
		payrollSheet.setPayrollNumber(payrollCount);
		payrollSheet.setPayYear(payYear);
		payrollSheet.setPayMonth(payMonth);
		payrollSheet.setLeader(billingPlan.getProjectLeader());
		payrollSheet.setContractID(billingPlan.getContractID());
		payrollSheet.setTotalAmount(calcPayrollSheetTotalAmount(payrollCount, billingPlan));
		buildPayrolls(payrollSheet, roster);
		payrollSheetList.add(payrollSheet);
		roster.setCurrentPayYear(payYear);
		roster.setCurrentPayMonth(payMonth);
	}
	
	protected void buildPayrolls(PayrollSheet payrollSheet, ProjectMemberRoster roster) {
		int payrollCount = payrollSheet.getPayrollNumber();
		double totalAmount = payrollSheet.getTotalAmount();
		int initWorkingDayCount = calcDraftWorkingDayCount(payrollCount, totalAmount);
		double tempAmount = 0.0;
		for (int i = 0; i < payrollCount; i++) {
			ProjectMember member = roster.getMemberByMonth(payrollSheet.getPayMonth());
			Payroll payroll = new Payroll();
			payroll.setOrderNumber(i + 1);
			payroll.setName(member.getName());
			payroll.setBasePay(member.getBasePay());
			payroll.setDailyPay(member.getDailyPay());
			payroll.setWorkingDays(initWorkingDayCount);
			payrollSheet.addPayroll(payroll);
			tempAmount += payroll.getTotalPay();
		}
		
		roster.setCursor(payrollSheet.getPayMonth(), payrollSheet.getContractID(), totalAmount);
		
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

	private static double calcPayrollSheetTotalAmount(int payrollNumber, BillingPlan billingPlan) {
		return Math.round(billingPlan.getTotalPay() / billingPlan.getPayCount() * payrollNumber);
	}

}
