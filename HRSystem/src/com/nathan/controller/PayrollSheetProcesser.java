package com.nathan.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.common.Util;
import com.nathan.exception.BillingSuspendException;
import com.nathan.exception.PayrollSheetProcessException;
import com.nathan.exception.RosterProcessException;
import com.nathan.model.BillingPlan;
import com.nathan.model.BillingPlan.BillingStatus;
import com.nathan.model.BillingPlanBook;
import com.nathan.model.Payroll;
import com.nathan.model.PayrollSheet;
import com.nathan.model.ProjectLeader;
import com.nathan.model.ProjectMember;
import com.nathan.model.ProjectMemberRoster;
import com.nathan.model.SheetType;
import com.nathan.model.SubBillingPlan;
import com.nathan.service.AbstractExcelOperater;
import com.nathan.view.InteractionHandler;
import com.nathan.view.InteractionInput;

import jxl.CellType;
import jxl.FormulaCell;
import jxl.Sheet;
import jxl.biff.EmptyCell;
import jxl.write.Blank;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class PayrollSheetProcesser extends AbstractExcelOperater {

	private static Logger logger = Logger.getLogger(PayrollSheetProcesser.class);

	private List<PayrollSheet> payrollSheetList;
	private String filePath;

	private String contractIDToDelete;

	private boolean isNeededToUpdateBillingPlanBook = false;
	private boolean isNeededToCreateBankPaymentSummarySheet = false;

	private boolean isBillingManualHandling = true;
	private boolean isFullUpManualHandling = true;
	private boolean isNeededToCreateAlternatedPorjectLeaderPayrollSheet = false;
	private boolean isShowTabulatorAsProjectLeader = false;

	private int firstProcessingYear = 0;
	
	private double lowDailyPay = 150;
	private double highDailyPay = 170;
	private double overtimePayStep = 10;
	private double minOvertiemPay = 50;
	private double maxOvertimePay = 140;

	public PayrollSheetProcesser() {
		this.isNeededToCreateAlternatedPorjectLeaderPayrollSheet = Constant.YES.equals(Constant.propUtil
				.getStringValue("system.isNeededToCreateAlternatedPorjectLeaderPayrollSheet", Constant.NO));
		this.isShowTabulatorAsProjectLeader = Constant.FLAG_YES
				.equals(Constant.propUtil.getStringValue(Constant.CONFIG_工资表_制表人是否显示为领队, Constant.FLAG_NO));
		this.lowDailyPay = Util.getLowDailyPay();
		this.highDailyPay = Util.getHighDailyPay();
		this.overtimePayStep = Util.getOvertimePayStep();
		this.minOvertiemPay = Util.getMinOvertimePay();
		this.maxOvertimePay = Util.getMaxOvertimePay();
	}

	public void processPayrollSheet(BillingPlanBook billingPlanBook, RosterProcesser rosterProcesser) throws Exception {

		isBillingManualHandling = isBillingManualHandling();
		isFullUpManualHandling = isFullUpManualHandling();

		int totalToDo = billingPlanBook.getTotalBillingPlanSize();
		int totalDone = 0;

		if (isBillingManualHandling) {
			InteractionHandler.handleIsBillingGoOn("总共有" + totalToDo + "个计划需要开票");
		}
		// for (String projectLeader :
		// billingPlanBook.getProjectLeaderBillingPlanMap().keySet()) {
		List<BillingPlan> billingPlanList = billingPlanBook.getBillingPlanList();

		for (BillingPlan billingPlan : billingPlanList) {
			totalDone++;
			billingPlan.initBillingStatus();
			logger.debug("isReconstruction: " + billingPlan.isReconstruction());
			logger.debug("isToDelete: " + billingPlan.isToDelete());
			if ((billingPlan.isReconstruction() || billingPlan.isToDelete())) {
				deleteOldBillingInfoForSingleBillingPlan(billingPlan, rosterProcesser);
				billingPlan.setBillingStatus(BillingStatus.已删除);
				isNeededToUpdateBillingPlanBook = true;
				if (isBillingManualHandling) {
					InteractionHandler.handleBillingProgressReport(billingPlan.getContractID(),
							billingPlan.getBillingStatus().name(), totalToDo, totalDone);
				}
			}
			logger.debug("isToCreate: " + billingPlan.isToCreate());
			if (billingPlan.isToCreate()) {
				preProcess(billingPlan, rosterProcesser);
				buildPayrollSheetForSingleBillingPlan(billingPlan, rosterProcesser);
				billingPlan.setBillingStatus(BillingStatus.已制作);
				isNeededToUpdateBillingPlanBook = true;
				if (isBillingManualHandling) {
					InteractionHandler.handleBillingProgressReport(billingPlan.getContractID(),
							billingPlan.getBillingStatus().name(), totalToDo, totalDone);
				}
			}
		}
		// }
	}

	public boolean isNeededToUpdateBillingPlanBook() {
		return isNeededToUpdateBillingPlanBook;
	}

	/**
	 * @return the isNeededToCreateBankPaymentSummarySheet
	 */
	public boolean isNeededToCreateBankPaymentSummarySheet() {
		return isNeededToCreateBankPaymentSummarySheet;
	}

	/**
	 * @param isNeededToCreateBankPaymentSummarySheet
	 *            the isNeededToCreateBankPaymentSummarySheet to set
	 */
	public void setNeededToCreateBankPaymentSummarySheet(boolean isNeededToCreateBankPaymentSummarySheet) {
		this.isNeededToCreateBankPaymentSummarySheet = isNeededToCreateBankPaymentSummarySheet;

	}

	public void preProcess(BillingPlan billingPlan, RosterProcesser rosterProcesser) throws Exception {
		try {
			logger.info("制作工资表预处理...");
			int remainPayCount = billingPlan.getPayCount();
			this.firstProcessingYear = 0;
			remainPayCount = preProcessOnBankRoster(billingPlan, rosterProcesser, remainPayCount);

			if (remainPayCount > 0) {
				remainPayCount = preProcessOnCashRoster(billingPlan, rosterProcesser, remainPayCount);
			} else {
				setNeededToCreateBankPaymentSummarySheet(true);
				billingPlan.setBankPaymentSummaryRequired(true);
			}

			billingPlan.setBillingID(Math.max(billingPlan.getStartPayYear(), this.firstProcessingYear),
					billingPlan.getPayCount() - remainPayCount);

			if (remainPayCount > 0) {

				if (isFullUpManualHandling) {
					InteractionInput input = null;
					do {
						input = InteractionHandler.handleFullUpManual(billingPlan.getContractID(), remainPayCount);
						remainPayCount = handleFullUpManual(billingPlan, rosterProcesser, remainPayCount, input);
					} while (input != null && remainPayCount > 0);

					if (input == null) {
						remainPayCount = handleFullUpAuto(billingPlan, rosterProcesser, remainPayCount);
					}

				} else {
					remainPayCount = handleFullUpAuto(billingPlan, rosterProcesser, remainPayCount);
				}

				if (remainPayCount <= 0 && isBillingManualHandling) {
					InteractionHandler.handleIsBillingGoOn(billingPlan.getContractID(), "借人已完成");
				}
			}

			logger.debug(billingPlan.getSubPlanList());
			logger.debug("BillingID: " + billingPlan.getBillingID() + ", AlternatedProjectLeaderRemark: "
					+ billingPlan.getAlternatedProjectLeaderRemark());

			if (remainPayCount > 0) {
				throw new PayrollSheetProcessException(billingPlan.getContractID() + "花名册人数不足，无法开票！");
			} else {
				if (isBillingManualHandling) {
					InteractionHandler.handleIsBillingGoOn(billingPlan.getContractID(), "有足够人数开票");
				}
			}
		} catch (Exception e) {
			if (e instanceof BillingSuspendException) {
				throw e;
			}
			logger.debug(e.getMessage(), e);
			throw new PayrollSheetProcessException(" (" + billingPlan.getContractID() + ") " + e.getMessage());
		}		
	}

	private int preProcessOnBankRoster(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount)
			throws Exception {
		logger.debug("preProcessOnBankRoster");
		return preProcessByRosterType(billingPlan, rosterProcesser, remainPayCount, Constant.ROSTER_BANK);
	}

	private int preProcessOnCashRoster(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount)
			throws Exception {
		logger.debug("preProcessOnCashRoster");
		return preProcessByRosterType(billingPlan, rosterProcesser, remainPayCount, Constant.ROSTER_CASH);
	}

	private int preProcessByRosterType(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount,
			String rosterType) throws Exception {
		if (remainPayCount > 0) {
			remainPayCount = preProcessOnOwnRoster(billingPlan, rosterProcesser, remainPayCount, rosterType);
		}
		return remainPayCount;
	}

	private int preProcessOnOwnRoster(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount,
			String rosterType) throws Exception {
		int processingYear = billingPlan.getStartPayYear();
		int endPayYear = billingPlan.getEndPayYear();
		for (; processingYear <= endPayYear; processingYear++) {
			try {
				remainPayCount = preProcessOnOwnRosterByYear(billingPlan, rosterProcesser, remainPayCount, rosterType,
						processingYear);
			} catch (RosterProcessException re) {
				logger.error(re.getMessage());
				if (processingYear < endPayYear) {
					logger.info("继续尝试寻找" + (processingYear + 1) + "年度花名册"); 
					continue;
				} else {
					throw re;
				}		
			}
			
			if (remainPayCount <= 0) {
				break;
			}
		}
		return remainPayCount;
	}

	private int preProcessOnOwnRosterByYear(BillingPlan billingPlan, RosterProcesser rosterProcesser,
			int remainPayCount, String rosterType, int processingYear) throws Exception {
		String company = billingPlan.getProjectUnit();
		String processingProjectLeader = billingPlan.getProcessingProjectLeader();
		ProjectMemberRoster roster = rosterProcesser.processRoster(company, processingProjectLeader, processingYear,
				false);
		if (Constant.ROSTER_BANK.equals(rosterType)) {
			roster = roster.getBankRoster();
		}
		if (roster == null) {
			return remainPayCount;
		}
		int startPayMonth = billingPlan.getStartPayYear() < processingYear ? 1 : billingPlan.getStartPayMonth();
		int endPayMonth = processingYear < billingPlan.getEndPayYear() ? 12 : billingPlan.getEndPayMonth();
		int availablePayCount = roster.getAvailablePayCount(startPayMonth, endPayMonth);

		logger.debug(rosterType + " availablePayCount:" + availablePayCount);
		if (availablePayCount <= 0) {
			return remainPayCount;
		}

		if (this.firstProcessingYear == 0) {
			this.firstProcessingYear = processingYear;
		} else {
			this.firstProcessingYear = Math.min(this.firstProcessingYear, processingYear);
		}

		if (remainPayCount > availablePayCount) {
			billingPlan.createSubPlan(company, processingProjectLeader, processingYear, startPayMonth, endPayMonth,
					availablePayCount, calcSubTotalAmount(availablePayCount, billingPlan), rosterType);
			remainPayCount -= availablePayCount;
		} else {
			billingPlan.createSubPlan(company, processingProjectLeader, processingYear, startPayMonth, endPayMonth,
					remainPayCount, calcSubTotalAmount(remainPayCount, billingPlan), rosterType);
			remainPayCount = 0;
			return remainPayCount;
		}
		return remainPayCount;
	}

	private boolean isBillingManualHandling() {
		String billingHandling = Constant.propUtil.getStringValue("user.默认开票处理方式", Constant.HANDLE_MANUAL);
		return Constant.HANDLE_MANUAL.equals(billingHandling);
	}

	private boolean isFullUpManualHandling() {
		String fullUpHandling = Constant.propUtil.getStringValue("user.默认借人处理方式", Constant.HANDLE_MANUAL);
		return Constant.HANDLE_MANUAL.equals(fullUpHandling);
	}

	private int handleFullUpManual(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount,
			InteractionInput input) throws RosterProcessException {
		if (remainPayCount <= 0 || input == null) {
			return remainPayCount;
		}
		logger.info("人工指定借人预处理：" + input);
		int startPayYear = billingPlan.getStartPayYear();
		int endPayYear = billingPlan.getEndPayYear();
		for (int processingYear = startPayYear; processingYear <= endPayYear; processingYear++) {
			int startPayMonth = startPayYear < processingYear ? 1 : billingPlan.getStartPayMonth();
			int endPayMonth = processingYear < endPayYear ? 12 : billingPlan.getEndPayMonth();
			remainPayCount = handleFullUpByProjectLeader(billingPlan, rosterProcesser, remainPayCount,
					input.getCompany(), input.getProjectLeader(), processingYear, startPayMonth, endPayMonth);
			if (remainPayCount <= 0) {
				break;
			}
		}
		return remainPayCount;
	}

	private int handleFullUpByProjectLeader(BillingPlan billingPlan, RosterProcesser rosterProcesser,
			int remainPayCount, String company, String alternatedProjectLeader, int processingYear, int startPayMonth,
			int endPayMonth) throws RosterProcessException {
		ProjectMemberRoster roster = rosterProcesser.processRoster(company, alternatedProjectLeader, processingYear,
				false);

		int availablePayCount = roster.getAvailablePayCount(startPayMonth, endPayMonth);
		logger.debug("availablePayCount1:" + availablePayCount);
		if (availablePayCount <= 0) {
			return remainPayCount;
		}
		logger.info("向其他领队借人：" + alternatedProjectLeader);
		int currentProcessingCount = Math.min(availablePayCount, remainPayCount);
		billingPlan.createSubPlan(company, alternatedProjectLeader, processingYear, startPayMonth, endPayMonth,
				currentProcessingCount, calcSubTotalAmount(currentProcessingCount, billingPlan), Constant.ROSTER_CASH);
		billingPlan.setAlternatedProjectLeaderRemark(company, alternatedProjectLeader, processingYear,
				currentProcessingCount);

		remainPayCount -= currentProcessingCount;

		return remainPayCount;
	}

	private int handleFullUpAuto(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount)
			throws Exception {
		if (remainPayCount <= 0) {
			return remainPayCount;
		}
		logger.info("自动借人预处理...");
		String company = billingPlan.getProjectUnit();
		remainPayCount = handleFullUpByCompany(billingPlan, rosterProcesser, remainPayCount, company);
		if (remainPayCount > 0) {
			InteractionInput input = null;
			do {
				input = InteractionHandler.handleFullUpFromOtherCompany(company, remainPayCount);
				remainPayCount = handleFullUpByCompany(billingPlan, rosterProcesser, remainPayCount,
						input.getCompany());
			} while (input != null && remainPayCount > 0);
		}
		return remainPayCount;
	}

	private int handleFullUpByCompany(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount,
			String company) throws RosterProcessException {
		logger.info("自动在单位范围内借人：" + company);
		for (int processingYear = billingPlan.getStartPayYear(); processingYear <= billingPlan
				.getEndPayYear(); processingYear++) {
			remainPayCount = handleFullUpByYear(billingPlan, rosterProcesser, remainPayCount, company, processingYear);
			if (remainPayCount <= 0) {
				break;
			}
		}
		return remainPayCount;
	}

	private int handleFullUpByYear(BillingPlan billingPlan, RosterProcesser rosterProcesser, int remainPayCount,
			String company, int processingYear) throws RosterProcessException {
		String processingProjectLeader = billingPlan.getProjectLeader();
		int startPayYear = billingPlan.getStartPayYear();
		int endPayYear = billingPlan.getEndPayYear();
		int startPayMonth = startPayYear < processingYear ? 1 : billingPlan.getStartPayMonth();
		int endPayMonth = processingYear < endPayYear ? 12 : billingPlan.getEndPayMonth();

		ArrayList<ProjectLeader> availableProjectLeaderList = new ArrayList<ProjectLeader>();
		
		List<String> alternatedProjectLeaderList = getAlternatedProjectLeaderList(company, processingYear);
		
		if (!alternatedProjectLeaderList.contains(processingProjectLeader)
				&& company.equals(billingPlan.getProjectUnit())) {
			logger.debug("Replace " + processingProjectLeader + " with " + company);
			processingProjectLeader = company;
		}

		for (String alternatedProjectLeader : alternatedProjectLeaderList) {
			if (processingProjectLeader.equals(alternatedProjectLeader)) {
				logger.debug("Bypass " + alternatedProjectLeader);
				continue;
			}
			logger.info("处理领队：" + alternatedProjectLeader + " " + processingYear);
			ProjectMemberRoster roster = rosterProcesser.processRoster(company, alternatedProjectLeader, processingYear,
					false);

			int availablePayCount = roster.getAvailablePayCount(startPayMonth, endPayMonth);
			logger.debug("availablePayCount1:" + availablePayCount);
			if (availablePayCount <= 0) {
				continue;
			}
			if (remainPayCount <= availablePayCount) {
				billingPlan.createSubPlan(company, alternatedProjectLeader, processingYear, startPayMonth, endPayMonth,
						remainPayCount, calcSubTotalAmount(remainPayCount, billingPlan), Constant.ROSTER_CASH);
				billingPlan.setAlternatedProjectLeaderRemark(company, alternatedProjectLeader, processingYear,
						remainPayCount);
				return 0;
			} else {
				availableProjectLeaderList.add(new ProjectLeader(alternatedProjectLeader, company, availablePayCount));
			}
		}

		if (remainPayCount > 0 && availableProjectLeaderList.size() > 0) {
			Comparator<ProjectLeader> comparator = new Comparator<ProjectLeader>() {

				@Override
				public int compare(ProjectLeader o1, ProjectLeader o2) {
					return o2.getAvailablePayCount() - o1.getAvailablePayCount();
				}

			};
			Collections.sort(availableProjectLeaderList, comparator);
			for (ProjectLeader projectLeader : availableProjectLeaderList) {
				logger.info("向其他领队借人：" + projectLeader);
				int currentProcessingCount = Math.min(projectLeader.getAvailablePayCount(), remainPayCount);
				if (currentProcessingCount == 0) {
					continue;
				}
				billingPlan.createSubPlan(company, projectLeader.getName(), processingYear, startPayMonth, endPayMonth,
						currentProcessingCount, calcSubTotalAmount(currentProcessingCount, billingPlan),
						Constant.ROSTER_CASH);
				billingPlan.setAlternatedProjectLeaderRemark(company, projectLeader.getName(), processingYear,
						currentProcessingCount);

				remainPayCount -= currentProcessingCount;

				if (remainPayCount == 0) {
					break;
				}
			}
		}

		return remainPayCount;
	}

	private List<String> getAlternatedProjectLeaderList(String company, int year) {
		String path = Constant.propUtil.getStringEnEmpty("user.花名册领队目录");
		path = path.replace("YYYY", String.valueOf(year)).replace("UUUU", company);
		return Util.parseProjectLeadersFromRosterFileUnderPath(path);
	}

	private void deleteOldBillingInfoForSingleBillingPlan(BillingPlan billingPlan, RosterProcesser rosterProcesser)
			throws Exception {
		if (isBillingManualHandling) {
			InteractionHandler.handleIsBillingGoOn(billingPlan.getContractID(), "开票信息即将被删除");
		}
		logger.info("删除开票信息：" + billingPlan.getContractID());

		deleteOldBillingInfoForPorjectLeader(billingPlan, rosterProcesser);

		deleteOldBillingInfoForAlternatedPorjectLeader(billingPlan, rosterProcesser);

		billingPlan.resetBillingID();
		billingPlan.resetAlternatedProjectLeaderRemark();
	}

	private void deleteOldBillingInfoForPorjectLeader(BillingPlan billingPlan, RosterProcesser rosterProcesser)
			throws Exception {
		String company = billingPlan.getProjectUnitFromBillingID();
		String contractID = billingPlan.getContractIDFromBillingID();
		String projectLeader = billingPlan.getProjectLeaderFromBillingID();
		int payYear = billingPlan.getPayYearFromBillingID();
		int payCount = billingPlan.getPayCountFromBillingID();
		int releasedPayCount = 0;
		while (releasedPayCount < payCount && payYear <= Util.getCurrentYear()) {
			releasedPayCount += deleteOldRosterCursors(company, projectLeader, contractID, payYear, rosterProcesser);
			deleteOldPayrollSheet(company, projectLeader, contractID, payYear);
			payYear++;
		}
	}

	private void deleteOldBillingInfoForAlternatedPorjectLeader(BillingPlan billingPlan,
			RosterProcesser rosterProcesser) throws Exception {
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
			deleteOldRosterCursors(company, projectLeader, contractID, payYear, rosterProcesser);
			if (isNeededToCreateAlternatedPorjectLeaderPayrollSheet) {
				deleteOldPayrollSheet(company, projectLeader, contractID, payYear);
			} else {
				deleteOldPayrollSheet(company, billingPlan.getProjectLeader(), contractID, payYear);
			}
		}
	}

	private int deleteOldRosterCursors(String company, String projectLeader, String contractID, int payYear,
			RosterProcesser rosterProcesser) throws Exception {
		if (isVirtualBilling()) {
			logger.debug("虚拟开票无需删除花名册游标， 领队： " + projectLeader + "，合同号：" + contractID + ", 年份：" + payYear);
			return 0;
		}
		logger.info("删除花名册游标， 领队： " + projectLeader + "，合同号：" + contractID + ", 年份：" + payYear);

		rosterProcesser.processRoster(company, projectLeader, payYear, true);
		return rosterProcesser.deleteRosterCursorsByContractID(contractID);
	}

	private void deleteOldPayrollSheet(String company, String projectLeader, String contractID, int payYear)
			throws PayrollSheetProcessException {
		String filePath = buildPayrollSheetFilePath(company, projectLeader, payYear);
		try {
			deletePayrollSheetByContractID(filePath, contractID);
		} catch (PayrollSheetProcessException e) {
			if (!e.getMessage().contains("系统找不到指定的文件")) {
				throw e;
			}
			logger.error(e.getMessage() + "文件可能之前已被删除。");
		}
	}

	private void deletePayrollSheetByContractID(String filePath, String contractID)
			throws PayrollSheetProcessException {
		this.contractIDToDelete = contractID;
		logger.info("删除合同号为" + contractID + "的工资表: " + filePath);
		try {
			setBackupFlag(true);
			modify(filePath);
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.debug("删除工资表" + sheet.getName() + ", sheetIndex:" + sheetIndex);
				wwb.removeSheet(sheetIndex);
				// sheetIndex = i;
			} else {
				sheetIndex++;
			}
			logger.debug("sheetIndex:" + sheetIndex + ", index:" + i);
		}

	}

	protected boolean isNeededToDeleteFile(WritableWorkbook wwb) {
		if (wwb.getNumberOfSheets() <= 2) {
			return true;
		}
		return false;
	}

	// private void buildPayrollSheetForSingleBillingPlan(BillingPlan
	// billingPlan, RosterProcesser rosterProcesser)
	// throws RosterProcessException, PayrollSheetProcessException {
	//
	// logger.info(Constant.LINE1);
	// logger.info("制作工资表 - 开票计划序号：" + billingPlan.getOrderNumber());
	//
	// String processingProjectLeader =
	// billingPlan.getProcessingProjectLeader();
	//
	// rosterProcesser.processRoster(billingPlan.getProjectUnit(),
	// processingProjectLeader, billingPlan.getStartPayYear(), false);
	//
	// ProjectMemberRoster roster = rosterProcesser.getRoster();
	// List<PayrollSheet> payrollSheetList = new ArrayList<PayrollSheet>();
	//
	// int remainPayCount = buildPayrollSheet(billingPlan, roster,
	// payrollSheetList);
	//
	// logger.info(Constant.LINE1);
	//
	// if (payrollSheetList.size() > 0) {
	// String outputPath = buildPayrollSheetFilePath(processingProjectLeader,
	// billingPlan.getStartPayYear());
	// String payrollTemplateFile =
	// Constant.propUtil.getStringValue("user.工资表模板路径",
	// Constant.PAYROLL_TEMPLATE_FILE);
	// logger.info("读取工资表输出模板： " + payrollTemplateFile);
	// writePayrollSheet(payrollTemplateFile, outputPath, payrollSheetList);
	// logger.info(Constant.LINE1);
	// logger.info("保存工资表输出： " + outputPath);
	// logger.info(Constant.LINE1);
	// rosterProcesser.updateProjectMemberRoster();
	// logger.info(Constant.LINE1);
	// }
	//
	// if (remainPayCount > 0) {
	// handleRosterFullUp(billingPlan, rosterProcesser, remainPayCount);
	// }
	// }

	private void buildPayrollSheetForSingleBillingPlan(BillingPlan billingPlan, RosterProcesser rosterProcesser)
			throws Exception {

		logger.info(Constant.LINE1);
		logger.info("制作工资表 - 开票计划序号：" + billingPlan.getOrderNumber() + " 合同号：" + billingPlan.getContractID());

		try {
			AttendanceSheetProcesser attendanceSheetProcesser = null;
			if (billingPlan.isAttendanceSheetRequired()) {
				attendanceSheetProcesser = new AttendanceSheetProcesser(billingPlan, isVirtualBilling());
			}

			for (SubBillingPlan subPlan : billingPlan.getSubPlanList()) {
				subPlan.setProjectUnit(billingPlan.getProjectUnit());
				subPlan.setProjectLeader(billingPlan.getProjectLeader());
				subPlan.setContractID(billingPlan.getContractID());
				subPlan.setTotalPay(billingPlan.getTotalPay());
				subPlan.setPayCount(billingPlan.getPayCount());
				subPlan.setProcessingProjectLeader(subPlan.getSubPlanProjectLeader());
				subPlan.setAttendanceSheetFlag(billingPlan.getAttendanceSheetFlag());
				subPlan.setAdministrationExpenses(billingPlan.getAdministrationExpenses());

				buildPayrollSheetForSubBillingPlan(subPlan, rosterProcesser, attendanceSheetProcesser);
			}
		} catch (Exception e) {
			if (e instanceof BillingSuspendException) {
				throw e;
			}
			throw new PayrollSheetProcessException(" (" + billingPlan.getContractID() + ") " + e.getMessage());
		}		

	}

	private void buildPayrollSheetForSubBillingPlan(SubBillingPlan subPlan, RosterProcesser rosterProcesser,
			AttendanceSheetProcesser attendanceSheetProcesser) throws Exception {
		String processingProjectLeader = subPlan.getSubPlanProjectLeader();
		int processingPayYear = subPlan.getSubPlanPayYear();

		ProjectMemberRoster roster = rosterProcesser.processRoster(subPlan.getSubPlanProjectUnit(),
				processingProjectLeader, processingPayYear, false);
		if (Constant.ROSTER_BANK.equals(subPlan.getSubPlanRosterType())) {
			roster = rosterProcesser.getRoster().getBankRoster();
		}

		List<PayrollSheet> payrollSheetList = new ArrayList<PayrollSheet>();

		buildPayrollSheet(subPlan, roster, payrollSheetList, processingPayYear, subPlan.getSubPlanStartMonth(),
				subPlan.getSubPlanEndMonth(), subPlan.getSubPlanPayCount(), subPlan.getProjectUnit());

		logger.info(Constant.LINE1);

		if (payrollSheetList.size() > 0) {
			subPlan.setPayrollSheetList(payrollSheetList);
			String payrollTemplateFile = buildPayrollSheetTemplatePath();
			logger.info("读取工资表模板： " + payrollTemplateFile);
			String outputPath = buildPayrollSheetFilePath(subPlan);
			writePayrollSheet(payrollTemplateFile, outputPath, payrollSheetList);
			logger.info(Constant.LINE1);
			logger.info("保存工资表输出： " + outputPath);
			logger.info(Constant.LINE1);

			if (!isVirtualBilling()) {
				rosterProcesser.updateProjectMemberRoster(true);
				logger.info(Constant.LINE1);
			}

			if (subPlan.isAttendanceSheetRequired()) {
				attendanceSheetProcesser.processAttendanceSheet(subPlan, payrollSheetList);
			}
		}
	}

	public int buildPayrollSheet(SubBillingPlan billingPlan, ProjectMemberRoster roster,
			List<PayrollSheet> payrollSheetList, int payYear, int startPayMonth, int endPayMonth, int payCount,
			String processingProjectUnit) throws Exception {
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
			buildPayrollSheet(roster, currentProcessingCount, payYear, month, billingPlan, payrollSheetList,
					processingProjectUnit);
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
			SubBillingPlan billingPlan, List<PayrollSheet> payrollSheetList, String processingProjectUnit)
			throws Exception {
		logger.debug("payrollCount: " + payrollCount + ", year " + payYear + ", month " + payMonth);
		PayrollSheet payrollSheet = new PayrollSheet();
		payrollSheet.setName(buildPayrollSheetName(payMonth, billingPlan.getContractID(), billingPlan));
		payrollSheet.setPayrollNumber(payrollCount);
		payrollSheet.setPayYear(payYear);
		payrollSheet.setPayMonth(payMonth);
		payrollSheet.setLeader(billingPlan.getProjectLeader());
		payrollSheet.setContractID(billingPlan.getContractID());
		payrollSheet.setTotalAmount(calcSubTotalAmount(payrollCount, billingPlan));
		payrollSheet.setProjectUnit(processingProjectUnit);
		buildPayrolls(payrollSheet, roster, billingPlan.getAdministrationExpenses());
		payrollSheetList.add(payrollSheet);
		roster.setCurrentPayYear(payYear);
		roster.setCurrentPayMonth(payMonth);
	}

	protected void buildPayrolls(PayrollSheet payrollSheet, ProjectMemberRoster roster, double administrationExpenses)
			throws Exception {
		int payrollCount = payrollSheet.getPayrollNumber();
		double totalAmount = payrollSheet.getTotalAmount();
		double highTemperatureAllowance = Util.getHighTemperatureAllowance(payrollSheet.getPayYear(),
				payrollSheet.getPayMonth());
		double socialSecurityAmount = Util.getSocialSecurityAmount(payrollSheet.getPayYear(),
				payrollSheet.getPayMonth());
		double taxThreshold = Util.getIndividualIncomeTaxThreshold(payrollSheet.getPayYear(),
				payrollSheet.getPayMonth());
		int initWorkingDayCount = calcDraftWorkingDayCount(payrollCount, totalAmount, taxThreshold);
		double tempAmount = 0.0;
		for (int i = 0; i < payrollCount; i++) {
			ProjectMember member = roster.getMemberByMonth(payrollSheet.getPayMonth());
			Payroll payroll = new Payroll();
			payroll.setOrderNumber(i + 1);
			payroll.setName(member.getName());
			payroll.setBasePay(member.getBasePay());
			payroll.setDailyPay(Util.getDailyPayByBasePay(member.getBasePay()));
			payroll.setHighTemperatureAllowance(highTemperatureAllowance);
			payroll.setSocialSecurityAmount(socialSecurityAmount);
			payroll.setWorkingDays(initWorkingDayCount);
			payroll.setAdministrationExpenses(administrationExpenses);
			payrollSheet.addPayroll(payroll);
			tempAmount += payroll.getTotalPay();
		}

		roster.setNewCursor(payrollSheet.getPayMonth(), payrollSheet.getContractID(), totalAmount);

		double remainAmount = totalAmount - tempAmount;
		logger.debug("round a - remainAmount:" + remainAmount);
		int loop = 0;
		int i = 0;
		for (; i < payrollCount; i++) {
			if (isRemainAmountTooBig(remainAmount)) {
				Payroll payroll = payrollSheet.getPayrollList().get(i);
				payroll.increaseWorkingDays(1);
				if (payroll.getTotalPay() > taxThreshold) {
					payroll.decreaseWorkingDays(1);
				} else {
					remainAmount -= payroll.getDailyPay();
				}

				if (i == payrollCount - 1) {
					i = -1;
					loop++;
					logger.debug("loop:" + loop);
				}
				if (loop > 5) {
					break;
				}
			} else {
				break;
			}
		}

		logger.debug("round b - remainAmount:" + remainAmount);

		// if (i == 0) {
		// i = payrollCount - 1;
		// }
		// logger.debug("last totalPay: " + payrollSheet.getPayrollList().get(i
		// - 1).getTotalPay());
		// if (payrollSheet.getPayrollList().get(i - 1).getTotalPay() >=
		// taxThreshold - 50) {
		// payrollSheet.getPayrollList().get(i - 1).decreaseWorkingDays(1);
		// remainAmount += payrollSheet.getPayrollList().get(i -
		// 1).getDailyPay();
		// }
		// logger.debug("round c - remainAmount:" + remainAmount);

		// for (int i = 0; i < payrollCount; i++) {
		// if (payrollCount == 1) {
		// payrollSheet.getPayrollList().get(i).setOvertimePay(remainAmount);
		// break;
		// }
		// if (isRemainAmountTooBig(remainAmount) || remainAmount >=
		// Constant.DEFAULT_OVERTIME_PAY * 2) {
		// if (payrollSheet.getPayrollList().get(i).getTotalPay() +
		// Constant.DEFAULT_OVERTIME_PAY > taxThreshold) {
		// continue;
		// }
		// payrollSheet.getPayrollList().get(i).setOvertimePay(Constant.DEFAULT_OVERTIME_PAY);
		// remainAmount -= Constant.DEFAULT_OVERTIME_PAY;
		// } else if (payrollSheet.getPayrollList().get(i).getTotalPay() +
		// remainAmount < taxThreshold) {
		// payrollSheet.getPayrollList().get(i).setOvertimePay(remainAmount);
		// break;
		// }
		// }

		remainAmount = assignOvertimePay(payrollSheet, remainAmount, taxThreshold);

		logger.debug("round c - remainAmount:" + remainAmount);
		if (remainAmount > 0) {
			throw new PayrollSheetProcessException(payrollSheet.getContractID() + "加班费分配无法满足要求！");
		}

		adjustPerformancePay(payrollSheet);

		logger.debug("分表金额: " + totalAmount);
	}

	private double assignOvertimePay(PayrollSheet payrollSheet, double remainAmount, double taxThreshold) {
		int payrollCount = payrollSheet.getPayrollNumber();
		if (payrollCount == 1) {
			payrollSheet.getPayrollList().get(0).setOvertimePay(remainAmount);
			return 0;
		}

		double draftOvertimePay;
		if (remainAmount <= maxOvertimePay) {
			draftOvertimePay = remainAmount;
		} else {
			draftOvertimePay = ((int) (remainAmount / overtimePayStep / payrollCount) + 1) * overtimePayStep;
			draftOvertimePay = Math.max(minOvertiemPay, draftOvertimePay);
		}

		double finalOvertimePay = 0;

		for (double tempOvertimePay = draftOvertimePay; tempOvertimePay <= maxOvertimePay; tempOvertimePay += overtimePayStep) {
			logger.debug("tempOvertimePay: " + tempOvertimePay);
			double result = rehearsal(tempOvertimePay, payrollSheet, remainAmount, taxThreshold);
			if (result <= 0) {
				finalOvertimePay = tempOvertimePay;
				break;
			}
		}
		logger.debug("round 1 finalOvertimePay: " + finalOvertimePay + "  remainAmount:" + remainAmount);

		if (finalOvertimePay <= 0) {
			for (double tempOvertimePay = draftOvertimePay - overtimePayStep; tempOvertimePay >= 0; tempOvertimePay -= overtimePayStep) {
				logger.debug("tempOvertimePay: " + tempOvertimePay);
				double result = rehearsal(tempOvertimePay, payrollSheet, remainAmount, taxThreshold);
				if (result <= 0) {
					finalOvertimePay = tempOvertimePay;
					break;
				}
			}
			logger.debug("round 2 finalOvertimePay: " + finalOvertimePay + "  remainAmount:" + remainAmount);
		}

		for (int i = 0; i < payrollCount; i++) {
			if (isRemainAmountTooBig(remainAmount) || remainAmount >= finalOvertimePay) {
				if (payrollSheet.getPayrollList().get(i).getTotalPay() + finalOvertimePay >= taxThreshold) {
					continue;
				}
				payrollSheet.getPayrollList().get(i).setOvertimePay(finalOvertimePay);
				remainAmount -= finalOvertimePay;
				if (remainAmount <= 0) {
					break;
				}
			} else if (i >= 1 && remainAmount < minOvertiemPay && finalOvertimePay + remainAmount <= maxOvertimePay
					&& payrollSheet.getPayrollList().get(i - 1).getTotalPay() + finalOvertimePay
							+ remainAmount < taxThreshold) {
				payrollSheet.getPayrollList().get(i - 1).setOvertimePay(finalOvertimePay + remainAmount);
				remainAmount -= remainAmount;
				break;
			} else if (payrollSheet.getPayrollList().get(i).getTotalPay() + remainAmount < taxThreshold) {
				payrollSheet.getPayrollList().get(i).setOvertimePay(remainAmount);
				remainAmount -= remainAmount;
				break;
			}
		}
		return remainAmount;
	}

	private double rehearsal(double defaultOvertimePay, PayrollSheet payrollSheet, double remainAmount,
			double taxThreshold) {
		for (int i = 0; i < payrollSheet.getPayrollNumber(); i++) {
			if (isRemainAmountTooBig(remainAmount) || remainAmount >= defaultOvertimePay) {
				if (payrollSheet.getPayrollList().get(i).getTotalPay() + defaultOvertimePay >= taxThreshold) {
					continue;
				}
				remainAmount -= defaultOvertimePay;
				logger.debug(i + " remainAmount: " + remainAmount);
				if (remainAmount <= 0) {
					break;
				}
			} else if (i >= 1 && remainAmount < minOvertiemPay && defaultOvertimePay + remainAmount <= maxOvertimePay
					&& payrollSheet.getPayrollList().get(i - 1).getTotalPay() + defaultOvertimePay
							+ remainAmount < taxThreshold) {
				remainAmount -= remainAmount;
				break;
			} else if (payrollSheet.getPayrollList().get(i).getTotalPay() + remainAmount < taxThreshold) {
				remainAmount -= remainAmount;
				break;
			}
		}
		logger.debug("rehearsal remainAmount: " + remainAmount);
		return remainAmount;
	}

	public void adjustPerformancePay(PayrollSheet payrollSheet) {
		double minPerfPay = Util.getMinPerformancePay();
		for (Payroll payroll : payrollSheet.getPayrollList()) {
			double performancePay = payroll.getPerformancePay();
			double oldBasePay = payroll.getBasePay();

			while (performancePay < minPerfPay) {
				payroll.setBasePay(payroll.getBasePay() - 100);
				performancePay = payroll.getPerformancePay();
			}
			if (oldBasePay != payroll.getBasePay()) {
				logger.info(payroll.getName() + "基本工资调整：" + oldBasePay + " => " + payroll.getBasePay());
			}
		}
	}

	private boolean isRemainAmountTooBig(double remainAmount) {
		return remainAmount >= lowDailyPay * 2 || remainAmount > highDailyPay + 50;
	}

	public void writePayrollSheet(String templatePath, String filePath, List<PayrollSheet> payrollSheetList)
			throws PayrollSheetProcessException {
		this.payrollSheetList = payrollSheetList;
		this.filePath = filePath;
		try {
			setBackupFlag(true);
			write(templatePath, filePath);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new PayrollSheetProcessException("保存工资表出错，" + e.getMessage());
		}
	}

//	protected void preWrite(String filePath) {
//		removeVirtualPayrollSheetIfExists(filePath);
//	}
//
//	private void removeVirtualPayrollSheetIfExists(String filePath) {
//		if (!filePath.contains("虚拟_")) {
//			return;
//		}
//		if (delete(filePath)) {
//			logger.debug("删除虚拟工资表：" + filePath);
//		}
//	}

	protected void writeContent(WritableWorkbook wwb) throws Exception {
		WritableSheet sheet = wwb.getSheet(0);

		int rsColumns = sheet.getColumns();
		int rsRows = sheet.getRows();

		logger.debug("总列数：" + rsColumns + ", 总行数：" + rsRows);

		int sheetNumber = wwb.getNumberOfSheets();
		for (int i = 0; i < payrollSheetList.size() * 2; i += 2) {
			int j = i;
			if (i > 0) {
				j = i / 2;
			}
			PayrollSheet payrollSheet = payrollSheetList.get(j);
			writePayrollSheet(wwb, payrollSheet, i + sheetNumber);
			writeSummarySheet(wwb, payrollSheet, i + sheetNumber + 1);
		}

		// wwb.removeSheet(0);
		// wwb.removeSheet(1);
	}

	private void writePayrollSheet(WritableWorkbook wwb, PayrollSheet payrollSheet, int sheetIndex) throws Exception {
		String sheetName = payrollSheet.getPayrollSheetName();
		sheetName = validateSheetName(wwb, sheetName);
		wwb.copySheet(0, sheetName, sheetIndex);
		WritableSheet newSheet = wwb.getSheet(sheetIndex);
		writeSheet(newSheet, payrollSheet, isShowTabulatorAsProjectLeader);
		writeFooter(newSheet, Util.getFooterContents(payrollSheet.getProjectUnit(), payrollSheet.getContractID(),
				SheetType.工资表.getSheetID()));

		PrintingProcesser.createExcelPrintTask(SheetType.工资表, filePath, sheetIndex, sheetName);
	}

	private void writeSummarySheet(WritableWorkbook wwb, PayrollSheet payrollSheet, int sheetIndex) throws Exception {
		String sheetName = payrollSheet.getSummarySheetName();
		sheetName = validateSheetName(wwb, sheetName);
		wwb.copySheet(1, sheetName, sheetIndex);
		writeSheet(wwb.getSheet(sheetIndex), payrollSheet, false);

		if (needToDeleteSummarySheetTabulator(payrollSheet.getProjectUnit())) {
			WritableSheet newSheet = wwb.getSheet(sheetIndex);
			newSheet.removeRow(newSheet.getRows() - 1);
		}

		PrintingProcesser.createExcelPrintTask(SheetType.汇总表, filePath, sheetIndex, sheetName);
	}
	
	private String validateSheetName(WritableWorkbook wwb, String sheetName) {
		Sheet sheet = wwb.getSheet(sheetName);
		if (sheet != null) {
			sheetName = sheetName + Constant.DELIMITER1 + 1;
		}
		return sheetName;
	}

	private void writeSheet(WritableSheet newSheet, PayrollSheet payrollSheet, boolean isShowTabulatorAsProjectLeader) throws Exception {
		updatePayrollInfo(payrollSheet, newSheet, isShowTabulatorAsProjectLeader);
		fillPayrollSheet(payrollSheet, newSheet);
	}

	private String buildPayrollSheetName(int month, String contractID, SubBillingPlan subBillingPlan) {
		String name = month + Constant.DELIMITER1 + contractID;
		String alternatedPorjectLeader = subBillingPlan.getSubPlanProjectLeader();
		if (alternatedPorjectLeader != null && !alternatedPorjectLeader.equals(subBillingPlan.getProjectLeader())) {
			name = name + Constant.DELIMITER1 + "借" + alternatedPorjectLeader;
		}
		return name;
	}

	private void updatePayrollInfo(PayrollSheet payrollSheet, WritableSheet sheet, boolean isShowTabulatorAsProjectLeader) {

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
		tabulator = tabulator.replace("NNN", isShowTabulatorAsProjectLeader ? payrollSheet.getLeader() : Util.getTabulator());
		((Label) cell).setString(tabulator);

		cell = sheet.getWritableCell(15, 6);
		String reviewer = ((Label) cell).getString();
		reviewer = reviewer.replace("RRR", Util.getReviewer());
		((Label) cell).setString(reviewer);

		// cell = sheet.getWritableCell(4, 6);
		// ((Label) cell).setString(reviewer);

		if (needToChangeOvertimePaySubject(payrollSheet.getProjectUnit())) {
			cell = sheet.getWritableCell(4, 2);
			((Label) cell).setString("其他");

			cell = sheet.getWritableCell(15, 2);
			((Label) cell).setString("其他");
		}

		if (needToChangeSummarySheetTitle(payrollSheet.getProjectUnit())) {
			cell = sheet.getWritableCell(12, 0);
			((Label) cell).setString(getSummarySheetTitle(payrollSheet, cell));
		}
	}

	private String getSummarySheetTitle(PayrollSheet payrollSheet, WritableCell cell) {
		return payrollSheet.getPayYear() + "年" + payrollSheet.getPayMonth() + "月" + cell.getContents();
	}

	private boolean needToChangeOvertimePaySubject(String projectUnit) {
		return Util.isConfigMatched(projectUnit, Constant.CONFIG_汇总表加班费显示为其他单位);
	}

	private boolean needToChangeSummarySheetTitle(String projectUnit) {
		return Util.isConfigMatched(projectUnit, Constant.CONFIG_汇总表标题显示时间单位);
	}

	private boolean needToDeleteSummarySheetTabulator(String projectUnit) {
		return Util.isConfigMatched(projectUnit, Constant.CONFIG_汇总表不显示制表人单位);
	}

	private void fillPayrollSheet(PayrollSheet payrollSheet, WritableSheet sheet) throws Exception {
		int size = payrollSheet.getPayrollList().size();
		int srcRowIndex = 3;
		int rsColumns = sheet.getColumns();

		for (int r = 0; r < size; r++) {
			int currentRowIndex = r + 4;
			Payroll payroll = payrollSheet.getPayrollList().get(r);
			sheet.insertRow(currentRowIndex);
			sheet.setRowView(currentRowIndex, sheet.getRowView(srcRowIndex));

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
				} else if (c == 3 && !newCell.getClass().equals(jxl.write.Formula.class)) {
					((Number) newCell).setValue(payroll.getPerformancePay());
				} else if (c == 5) {
					((Number) newCell).setValue(payroll.getHighTemperatureAllowance());
				} else if (c == 6) {
					((Number) newCell).setValue(payroll.getSocialSecurityAmount());
				} else if (c == 12 && !newCell.getClass().equals(jxl.write.Formula.class)) {
					((Number) newCell).setValue(payroll.getDailyPay());
				} else if (c == 13) {
					((Number) newCell).setValue(payroll.getWorkingDays());
				} else if (c == 15) {
					((Number) newCell).setValue(payroll.getOvertimePay());
				} else if (c == 17) {
					((Number) newCell).setValue(payroll.getAdministrationExpenses());
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

	protected int calcDraftWorkingDayCount(int payrollCount, double totalAmount, double taxThreshold) {
		int initWorkingDayCount = (int) Math.floor(totalAmount / payrollCount / highDailyPay);
		while (highDailyPay * initWorkingDayCount > taxThreshold) {
			logger.debug("initWorkingDayCount: " + initWorkingDayCount + ", draft total:"
					+ highDailyPay * initWorkingDayCount);
			initWorkingDayCount--;
		}
		return initWorkingDayCount;
	}

	private static double calcSubTotalAmount(int payrollNumber, BillingPlan billingPlan) {
		double remainTotalPay = billingPlan.getRemainTotalPay();
		double totalPay = billingPlan.getProcessingTotalPay();
		int payCount = billingPlan.getProcessingPayCount();
		double currentAmount = Math.round(totalPay / payCount * payrollNumber);
		logger.debug("TotalPay:" + totalPay + " remainTotalPay:" + remainTotalPay + " payCount:" + payCount
				+ " currentAmount:" + currentAmount);
		if (remainTotalPay > 0 && Math.abs(remainTotalPay - currentAmount) <= 2) {
			currentAmount = remainTotalPay;
		}
		billingPlan.setRemainTotalPay(remainTotalPay - currentAmount);
		return currentAmount;
	}

	private String buildPayrollSheetFilePath(SubBillingPlan subPlan) {
		String company = subPlan.getSubPlanProjectUnit();
		String projectLeader = subPlan.getProjectLeader();
		String processingProjectLeader = subPlan.getSubPlanProjectLeader();
		int processingPayYear = subPlan.getSubPlanPayYear();
		return buildPayrollSheetFilePath(company,
				isNeededToCreateAlternatedPorjectLeaderPayrollSheet ? processingProjectLeader : projectLeader,
				processingPayYear);
	}

	private String buildPayrollSheetFilePath(String company, String projectLeader, int payYear) {
		String payrollFile = Constant.propUtil.getStringValue("user.工资表输出路径", Constant.PAYROLL_FILE);
		String filePath = payrollFile.replace("UUUU", company);
		if (isVirtualBilling()) {
			filePath = filePath.replace("NNN", "虚拟_" + projectLeader);
		} else {
			filePath = filePath.replace("NNN", projectLeader);
		}
		filePath = filePath.replace("YYYY", String.valueOf(payYear));

		return filePath;
	}

	private String buildPayrollSheetTemplatePath() {
		return Constant.propUtil.getStringValue("user.工资表模板路径", Constant.PAYROLL_TEMPLATE_FILE);
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

		payrollSheetProcesser.processPayrollSheet(billingPlanProcesser.getBillingPlanBook(), rosterProcesser);
		logger.info(Constant.LINE1);

		// logger.info(Constant.LINE0);
		// logger.info("删除工资表开始...");
		// logger.info(Constant.LINE0);
		//
		// String filePath =
		// payrollSheetProcesser.buildPayrollSheetFilePath("李一", 2016);
		// payrollSheetProcesser.deletePayrollSheetByContractID(filePath,
		// "14-289补");

		long endTime = System.nanoTime();
		logger.info(Constant.LINE0);
		logger.info("处理工资表结束， 用时：" + (endTime - startTime) / 1000000 + "毫秒");
		logger.info(Constant.LINE0);
	}
}
