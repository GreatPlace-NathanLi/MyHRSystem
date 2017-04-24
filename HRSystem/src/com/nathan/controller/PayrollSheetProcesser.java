package com.nathan.controller;

import java.util.ArrayList;
import java.util.List;

import com.nathan.common.Constant;
import com.nathan.common.Util;
import com.nathan.exception.PayrollSheetProcessException;
import com.nathan.exception.RosterProcessException;
import com.nathan.model.BillingPlan;
import com.nathan.model.BillingPlanBook;
import com.nathan.model.Payroll;
import com.nathan.model.PayrollSheet;
import com.nathan.model.ProjectMember;
import com.nathan.model.ProjectMemberRoster;
import com.nathan.model.SubBillingPlan;
import com.nathan.service.AbstractExcelOperater;

import jxl.CellType;
import jxl.FormulaCell;
import jxl.Sheet;
import jxl.biff.EmptyCell;
import jxl.biff.formula.FormulaException;
import org.apache.log4j.Logger;
import jxl.write.Blank;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class PayrollSheetProcesser extends AbstractExcelOperater {

	private static Logger logger = Logger.getLogger(PayrollSheetProcesser.class);

	private int rosterFullUpTime = 0;

	private List<PayrollSheet> payrollSheetList;

	private String contractIDToDelete;

	public void processPayrollSheet(BillingPlanBook billingPlanBook, RosterProcesser rosterProcesser,
			String payrollFile, String payrollTemplateFile) throws Exception {

		for (String projectLeader : billingPlanBook.getProjectLeaderBillingPlanMap().keySet()) {
			List<BillingPlan> billingPlanList = billingPlanBook.getBillingByProjectLeader(projectLeader);

			for (BillingPlan billingPlan : billingPlanList) {				
				if (isReconstruction(billingPlan)) {
					deleteOldBillingInfoForSingleBillingPlan(billingPlan, rosterProcesser);
				}
				preProcess(billingPlan, rosterProcesser);
				buildPayrollSheetForSingleBillingPlan(billingPlan, rosterProcesser);
			}

		}
	}
	
	public void preProcess(BillingPlan billingPlan, RosterProcesser rosterProcesser) throws PayrollSheetProcessException, RosterProcessException {
		logger.info("制作工资表预处理...");
		String company = billingPlan.getProjectUnit();
		String processingProjectLeader = billingPlan.getProcessingProjectLeader();
		int startPayYear = billingPlan.getStartPayYear();
		int endPayYear = billingPlan.getEndPayYear();
		int startPayMonth = billingPlan.getStartPayMonth();
		int endPayMonth = billingPlan.getEndPayMonth();
		int totalPayCount = billingPlan.getPayCount();
		int remainPayCount = totalPayCount;
		int processingYear = startPayYear;
		int firstProcessingYear = 0;

		for(; processingYear <= endPayYear; processingYear++) {
			rosterProcesser.processRoster(company, processingProjectLeader, processingYear, false);
			startPayMonth = startPayYear < processingYear ? 1 : billingPlan.getStartPayMonth();
			endPayMonth = processingYear < endPayYear ? 12 : billingPlan.getEndPayMonth();
			int availablePayCount = rosterProcesser.getRoster().getAvailablePayCount(startPayMonth, endPayMonth);
			
			logger.debug("availablePayCount:" + availablePayCount);
			if (availablePayCount <= 0) {
				continue;
			}
			
			if (firstProcessingYear == 0) {
				firstProcessingYear = processingYear;
			}
			
			if (remainPayCount > availablePayCount) {
				billingPlan.createSubPlan(company, processingProjectLeader, processingYear, startPayMonth, endPayMonth, availablePayCount);
				remainPayCount -= availablePayCount;
			} else {
				billingPlan.createSubPlan(company, processingProjectLeader, processingYear, startPayMonth, endPayMonth, remainPayCount);
				remainPayCount = 0;
				break;
			}
		}
		
		billingPlan.setBillingID(Math.max(startPayYear, firstProcessingYear), billingPlan.getPayCount() - remainPayCount);
		
		if (remainPayCount > 0) {
			//Popup: 自动借人 or 指定借人		
			remainPayCount = handleFullUpAuto(billingPlan, rosterProcesser, remainPayCount);
		}
		
		logger.debug(billingPlan.getSubPlanList() + billingPlan.getBillingID() + billingPlan.getAlternatedProjectLeaderRemark());

		if (remainPayCount > 0) {
			throw new PayrollSheetProcessException("花名册人数不足，无法开票！");
		}
	}
	
	private int handleFullUpAuto(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount) throws RosterProcessException {
		logger.info("自动借人预处理...");
		for(int processingYear = billingPlan.getStartPayYear(); processingYear <= billingPlan.getEndPayYear(); processingYear++) {
			remainPayCount = handleFullUpByYear(billingPlan, rosterProcesser, processingYear, remainPayCount);
			if (remainPayCount <= 0) {
				break;
			}
		}
		return remainPayCount;
	}
	
	private int handleFullUpByYear(BillingPlan billingPlan, RosterProcesser rosterProcesser, int processingYear, int remainPayCount) throws RosterProcessException {
		String company = billingPlan.getProjectUnit();
		String processingProjectLeader = billingPlan.getProjectLeader();
		int startPayYear = billingPlan.getStartPayYear();
		int endPayYear = billingPlan.getEndPayYear();
		int startPayMonth = startPayYear < processingYear ? 1 : billingPlan.getStartPayMonth();
		int endPayMonth = processingYear < endPayYear ? 12 : billingPlan.getEndPayMonth();
		
		String maxCountAlternatedProjectLeader = null;
		int maxAvailablePayCount = 0;
		
		for(String alternatedProjectLeader : getAlternatedProjectLeaderList(company, processingYear)) {
			if (processingProjectLeader.equals(alternatedProjectLeader)) {
				logger.debug("Bypass " + alternatedProjectLeader);
				continue;
			}
			rosterProcesser.processRoster(company, alternatedProjectLeader, processingYear, false);
			
			int availablePayCount = rosterProcesser.getRoster().getAvailablePayCount(startPayMonth, endPayMonth);
			logger.debug("availablePayCount1:" + availablePayCount);
			if (availablePayCount <= 0) {
				continue;
			}
			if (remainPayCount <= availablePayCount) {
				billingPlan.createSubPlan(company, alternatedProjectLeader, processingYear, startPayMonth, endPayMonth, remainPayCount);
				billingPlan.setAlternatedProjectLeaderRemark(company, alternatedProjectLeader, processingYear, remainPayCount);
				return 0;
			} else {
				if (availablePayCount > maxAvailablePayCount) {
					maxAvailablePayCount = availablePayCount;
					maxCountAlternatedProjectLeader = alternatedProjectLeader;
				}
			}		
		}
		if(maxAvailablePayCount > 0) {
			billingPlan.createSubPlan(company, maxCountAlternatedProjectLeader, processingYear, startPayMonth, endPayMonth, maxAvailablePayCount);
			billingPlan.setAlternatedProjectLeaderRemark(company, maxCountAlternatedProjectLeader, processingYear, maxAvailablePayCount);
			
		}
		return remainPayCount - maxAvailablePayCount;
	}
	
	private List<String> getAlternatedProjectLeaderList(String company, int year) {
		String path = Constant.propUtil.getStringEnEmpty("user.花名册领队目录");
		path = path.replace("YYYY", String.valueOf(year)).replace("UUUU", company);
		return Util.getFoldersUnderPath(path);
	}

	private void deleteOldBillingInfoForSingleBillingPlan(BillingPlan billingPlan, RosterProcesser rosterProcesser)
			throws PayrollSheetProcessException, RosterProcessException {
		logger.info("处理重新开票...");

		deleteOldBillingInfoForPorjectLeader(billingPlan, rosterProcesser);

		deleteOldBillingInfoForAlternatedPorjectLeader(billingPlan, rosterProcesser);

		billingPlan.resetBillingID();
		billingPlan.resetAlternatedProjectLeaderRemark();
	}

	private void deleteOldBillingInfoForPorjectLeader(BillingPlan billingPlan, RosterProcesser rosterProcesser)
			throws PayrollSheetProcessException, RosterProcessException {
		String company = billingPlan.getProjectUnitFromBillingID();
		String contractID = billingPlan.getContractIDFromBillingID();
		String projectLeader = billingPlan.getProjectLeaderFromBillingID();
		int payYear = billingPlan.getPayYearFromBillingID();
		int payCount = billingPlan.getPayCountFromBillingID();
		int releasedPayCount = 0;
		while(releasedPayCount < payCount && payYear <= billingPlan.getEndPayYear()) {
			releasedPayCount += deleteOldBillingInfo(company, projectLeader, contractID, payYear, rosterProcesser);
			payYear++;
		}		
	}

	private void deleteOldBillingInfoForAlternatedPorjectLeader(BillingPlan billingPlan,
			RosterProcesser rosterProcesser) throws PayrollSheetProcessException, RosterProcessException {
		int size = billingPlan.getAlternatedProjectLeaderRemarkSize();
		if (size == 0) {
			logger.debug("此计划没有借人情况发生");
			return;
		}
		for (int i = 0; i < size; i++) {
			String company = billingPlan.getProjectUnitFromAlternatedProjectLeaderRemark(i);
			String contractID = billingPlan.getContractIDFromBillingID();
			String projectLeader = billingPlan.getProjectLeaderFromAlternatedProjectLeaderRemark(i);
			int payYear = billingPlan.getPayYearFromAlternatedProjectLeaderRemark(i);
			deleteOldBillingInfo(company, projectLeader, contractID, payYear, rosterProcesser);
		}
	}

	private int deleteOldBillingInfo(String company, String projectLeader, String contractID, int payYear, 
			RosterProcesser rosterProcesser) throws PayrollSheetProcessException, RosterProcessException {
		logger.info("删除开票记录， 领队： " + projectLeader + "，合同号：" + contractID + ", 年份：" + payYear);

		rosterProcesser.processRoster(company, projectLeader, payYear, true);
		int releasedPayCount = rosterProcesser.deleteRosterCursorsByContractID(contractID);
		
		if (releasedPayCount > 0) {
			String filePath = buildPayrollSheetFilePath(company, projectLeader, payYear);
			deletePayrollSheetByContractID(filePath, contractID);
		}
		
		return releasedPayCount;
	}

	private void deletePayrollSheetByContractID(String filePath, String contractID)
			throws PayrollSheetProcessException {
		this.contractIDToDelete = contractID;
		logger.info("删除合同号为" + contractID + "的工资表: " + filePath);
		try {
			modify(filePath);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PayrollSheetProcessException("删除工资表出错，" + e.getMessage());
		}
	}

	protected void modifyContent(WritableWorkbook wwb) throws Exception {
		int numberOfSheets = wwb.getNumberOfSheets();
		logger.debug("numberOfSheets: " + numberOfSheets);
		int sheetIndex = 0;
		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = wwb.getSheet(sheetIndex);
			if (sheet.getName().contains(contractIDToDelete)) {
				wwb.removeSheet(i);
				sheetIndex = i;
			} else {
				sheetIndex++;
			}
		}

	}
	
	protected boolean isNeededToDeleteFile(WritableWorkbook wwb) {
		if (wwb.getNumberOfSheets() <= 1) {
			return true;
		}
		return false;
	}

	private boolean isReconstruction(BillingPlan billingPlan) {
		String billingID = billingPlan.getBillingID();
		logger.debug("isReconstruction(): " + billingID);
		if (billingID != null) {
			return true;
		}
		return false;
	}

//	private void buildPayrollSheetForSingleBillingPlan(BillingPlan billingPlan, RosterProcesser rosterProcesser)
//			throws RosterProcessException, PayrollSheetProcessException {
//
//		logger.info(Constant.LINE1);
//		logger.info("制作工资表 - 开票计划序号：" + billingPlan.getOrderNumber());
//
//		String processingProjectLeader = billingPlan.getProcessingProjectLeader();
//		
//		rosterProcesser.processRoster(billingPlan.getProjectUnit(), processingProjectLeader, billingPlan.getStartPayYear(), false);
//
//		ProjectMemberRoster roster = rosterProcesser.getRoster();
//		List<PayrollSheet> payrollSheetList = new ArrayList<PayrollSheet>();
//
//		int remainPayCount = buildPayrollSheet(billingPlan, roster, payrollSheetList);
//
//		logger.info(Constant.LINE1);
//
//		if (payrollSheetList.size() > 0) {
//			String outputPath = buildPayrollSheetFilePath(processingProjectLeader,
//					billingPlan.getStartPayYear());
//			String payrollTemplateFile = Constant.propUtil.getStringValue("user.工资表模板路径", Constant.PAYROLL_TEMPLATE_FILE);
//			logger.info("读取工资表输出模板： " + payrollTemplateFile);
//			writePayrollSheet(payrollTemplateFile, outputPath, payrollSheetList);
//			logger.info(Constant.LINE1);
//			logger.info("保存工资表输出： " + outputPath);
//			logger.info(Constant.LINE1);
//			rosterProcesser.updateProjectMemberRoster();
//			logger.info(Constant.LINE1);
//		}
//
//		if (remainPayCount > 0) {
//			handleRosterFullUp(billingPlan, rosterProcesser, remainPayCount);
//		}
//	}
	
	private void buildPayrollSheetForSingleBillingPlan(BillingPlan billingPlan, RosterProcesser rosterProcesser)
			throws RosterProcessException, PayrollSheetProcessException {

		logger.info(Constant.LINE1);
		logger.info("制作工资表 - 开票计划序号：" + billingPlan.getOrderNumber());

		for(SubBillingPlan subPlan : billingPlan.getSubPlanList()) {
			subPlan.setProjectUnit(billingPlan.getProjectUnit());
			subPlan.setProjectLeader(billingPlan.getProjectLeader());
			subPlan.setContractID(billingPlan.getContractID());
			subPlan.setTotalPay(billingPlan.getTotalPay());
			subPlan.setPayCount(billingPlan.getPayCount());
			subPlan.setProcessingProjectLeader(subPlan.getSubPlanProjectLeader());
			
			buildPayrollSheetForSubBillingPlan(subPlan, rosterProcesser);
		}

	}
	
	private void buildPayrollSheetForSubBillingPlan(SubBillingPlan subPlan, RosterProcesser rosterProcesser) 
			throws RosterProcessException, PayrollSheetProcessException {
		String processingProjectLeader = subPlan.getSubPlanProjectLeader();
		int processingPayYear = subPlan.getSubPlanPayYear();
		rosterProcesser.processRoster(subPlan.getSubPlanProjectUnit(), processingProjectLeader, processingPayYear, false);

		ProjectMemberRoster roster = rosterProcesser.getRoster();
		List<PayrollSheet> payrollSheetList = new ArrayList<PayrollSheet>();

		buildPayrollSheet(subPlan, roster, payrollSheetList, processingPayYear, subPlan.getSubPlanStartMonth(), subPlan.getSubPlanEndMonth(), subPlan.getSubPlanPayCount());

		logger.info(Constant.LINE1);

		if (payrollSheetList.size() > 0) {
			String outputPath = buildPayrollSheetFilePath(subPlan.getSubPlanProjectUnit(), processingProjectLeader, processingPayYear);
			String payrollTemplateFile = Constant.propUtil.getStringValue("user.工资表模板路径", Constant.PAYROLL_TEMPLATE_FILE);
			logger.info("读取工资表输出模板： " + payrollTemplateFile);
			writePayrollSheet(payrollTemplateFile, outputPath, payrollSheetList);
			logger.info(Constant.LINE1);
			logger.info("保存工资表输出： " + outputPath);
			logger.info(Constant.LINE1);
			rosterProcesser.updateProjectMemberRoster();
			logger.info(Constant.LINE1);
		}
	}

//	private void handleRosterFullUp(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount)
//			throws RosterProcessException, PayrollSheetProcessException {
//		if(rosterFullUpTime > 4) {
//			throw new PayrollSheetProcessException("花名册人数不足");
//		}
//		logger.info(billingPlan.getProcessingProjectLeader() + "花名册名额用完，借人开始...");
//		String alternatedProjectLeader = getAlternatedProjectLeader();
//		billingPlan.setProcessingTotalPay(calcPayrollSheetTotalAmount(remainPayCount, billingPlan));
//		billingPlan.setProcessingPayCount(remainPayCount);
//		billingPlan.setProcessingProjectLeader(alternatedProjectLeader);
//		rosterFullUpTime++;
//
//		buildPayrollSheetForSingleBillingPlan(billingPlan, rosterProcesser);
//	}

//	private String getAlternatedProjectLeader() {
//
//		String alternatedProjectLeader = "李一";
//		if (rosterFullUpTime > 0)
//			alternatedProjectLeader = "黄六";
//		logger.info("从" + alternatedProjectLeader + "花名册中借人");
//		return alternatedProjectLeader;
//	}

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
		
		int remainPayCount = buildPayrollSheet(billingPlan, roster, payrollSheetList, payYear, startPayMonth, endPayMonth, billingPlan.getProcessingPayCount());
		
		if (rosterFullUpTime == 0) {
			billingPlan.setBillingID(billingPlan.getPayCount() - remainPayCount);
		} else if (payrollSheetList.size() > 0) {
			billingPlan.setAlternatedProjectLeaderRemark(billingPlan.getProcessingPayCount() - remainPayCount);
		}

		logger.debug("AlternatedProjectLeaderRemark: " + billingPlan.getAlternatedProjectLeaderRemark());

		return remainPayCount;
	}
	
	public int buildPayrollSheet(BillingPlan billingPlan, ProjectMemberRoster roster,
			List<PayrollSheet> payrollSheetList, int payYear, int startPayMonth, int endPayMonth, int payCount) {
		// 决定工资表数量
		// Todo: unit test
		logger.info("计算工资表数据...");
		
		int remainPayCount = payCount;

		for (int month = startPayMonth; month <= endPayMonth; month++) {
			int monthAvailableCount = roster.getStatistics().getMonthAvailableCount(month);
			int currentProcessingCount = Math.min(monthAvailableCount, remainPayCount);
			if (currentProcessingCount == 0) {
				continue;
			}
			buildPayrollSheet(roster, currentProcessingCount, payYear, month, billingPlan, payrollSheetList);
			remainPayCount -= currentProcessingCount;
			logger.debug(
					"remainPayCount: " + remainPayCount + ", current processing payCount: " + currentProcessingCount);
			if (remainPayCount == 0) {
				break;
			}
		}

		int sheetSize = payrollSheetList.size();

		logger.debug("工资单数量: " + sheetSize);

		return remainPayCount;
	}

	protected void buildPayrollSheet(ProjectMemberRoster roster, int payrollCount, int payYear, int payMonth,
			BillingPlan billingPlan, List<PayrollSheet> payrollSheetList) {
		logger.debug("payrollCount: " + payrollCount + ", year " + payYear + ", month " + payMonth);
		PayrollSheet payrollSheet = new PayrollSheet();
		payrollSheet.setPayrollNumber(payrollCount);
		payrollSheet.setPayYear(payYear);
		payrollSheet.setPayMonth(payMonth);
		payrollSheet.setLeader(billingPlan.getProcessingProjectLeader());
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

		roster.setNewCursor(payrollSheet.getPayMonth(), payrollSheet.getContractID(), totalAmount);

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
			throws PayrollSheetProcessException {
		this.payrollSheetList = payrollSheetList;
		try {
			write(templatePath, filePath);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PayrollSheetProcessException("保存工资表出错，" + e.getMessage());
		}
	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {
		WritableSheet sheet = wwb.getSheet(0);

		int rsColumns = sheet.getColumns();
		int rsRows = sheet.getRows();

		logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

		int sheetNumber = wwb.getNumberOfSheets();
		for (int i = 0; i < payrollSheetList.size(); i++) {
			PayrollSheet payrollSheet = payrollSheetList.get(i);
			wwb.copySheet(0, getPayrollSheetName(payrollSheet.getPayMonth(), payrollSheet.getContractID()), i + sheetNumber);
			WritableSheet newSheet = wwb.getSheet(i + sheetNumber);
			updatePayrollInfo(payrollSheet, newSheet);
			fillPayrollSheet(payrollSheet, newSheet);
		}

//		wwb.removeSheet(0);
	}
	
	private String getPayrollSheetName(int month, String contractID) {
		return "工" + month + "-" + contractID;
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
		
		cell = sheet.getWritableCell(0, 6);
		String tabulator = ((Label) cell).getString();
		tabulator = tabulator.replace("NNN", Util.getTabulator());
		((Label) cell).setString(tabulator);
		
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

	private String buildPayrollSheetFilePath(String company, String projectLeader, int payYear) {
		String payrollFile = Constant.propUtil.getStringValue("user.工资表输出路径", Constant.PAYROLL_FILE);
		String filePath = payrollFile.replace("UUUU", company);
		filePath = filePath.replace("NNN", projectLeader);
		filePath = filePath.replace("YYYY", String.valueOf(payYear));
		return filePath;
	}
	
	public static void main(String[] args) throws Exception {
		PayrollSheetProcesser payrollSheetProcesser = new PayrollSheetProcesser();
		BillingPlanProcesser billingPlanProcesser = new BillingPlanProcesser();
		RosterProcesser rosterProcesser = new RosterProcesser();
		Constant.propUtil.init();

		long startTime = System.nanoTime();
		
		String billingFile = Constant.propUtil.getStringValue("user.开票计划路径", Constant.BILLING_INPUT_FILE);
		logger.info("步骤1 - 读取开票计划输入： " + billingFile);
		billingPlanProcesser.processBillingPlanInput(billingFile);
		logger.info(Constant.LINE1);
		
		logger.info("步骤2 - 开始制作工资表...");
		String payrollFile = Constant.propUtil.getStringValue("user.工资表输出路径", Constant.PAYROLL_FILE);
		String payrollTemplateFile = Constant.propUtil.getStringValue("user.工资表模板路径",
				Constant.PAYROLL_TEMPLATE_FILE);
		payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser,
				payrollFile, payrollTemplateFile);
		logger.info(Constant.LINE1);
		
//		logger.info(Constant.LINE0);
//		logger.info("删除工资表开始...");
//		logger.info(Constant.LINE0);
//
//		String filePath = payrollSheetProcesser.buildPayrollSheetFilePath("李一", 2016);
//		payrollSheetProcesser.deletePayrollSheetByContractID(filePath, "14-289补");

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("处理工资表结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
		logger.info(Constant.LINE0);
	}
}
