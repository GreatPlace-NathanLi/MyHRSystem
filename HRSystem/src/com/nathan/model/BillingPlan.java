package com.nathan.model;

import java.util.ArrayList;
import java.util.List;

import com.nathan.common.Constant;
import com.nathan.common.Util;

public class BillingPlan {

	private int orderNumber;
	private String projectUnit;
	private String contractID;
	private String projectName;
	private String projectID;
	private String projectLeader;
	private double contractValue;
	private int billingYear;
	private int billingMonth;
	private int billingDay;
	private double invoiceAmount;
	private double administrationExpenses;
	private double administrationExpensesRate;
	private double totalAdministrationExpenses;
	private double totalPay;
	private int payCount;
	private double withdrawalFee;
	private String alternatedProjectLeaderRemark;
	private BillingStatus billingStatus = null;
	private String billingID = null;
	private String startAndEndPayTime;
	private int startPayTimeInInteger;
	private int endPayTimeInInteger;
	private int startPayYear;
	private int endPayYear;
	private int startPayMonth;
	private int endPayMonth;
	private String attendanceSheetFlag;
	
	private int bollowingDateInt;	
	private double bollowingAmount = 0.0;	
	private int repaymentDateInt;	
	private double repaymentAmount = 0.0;
	
	private int rowIndex;
	private transient int processingPayCount = 0;
	private transient double processingTotalPay = 0.0;
	private transient String processingProjectUnit;
	private transient String processingProjectLeader;
	private transient double remainTotalPay;
	private transient String rosterProjectLeader;
	
	private boolean isToCreate = false;
	private boolean isReconstruction = false;
	private boolean isToDelete = false;
	
	private boolean isAttendanceSheetRequired = false;
	
	private boolean isBankPaymentSummaryRequired = false;
	
	private List<SubBillingPlan> subPlanList;
	
	private List<PayrollSheet> payrollSheetList;
	
	public enum BillingStatus {
		待制作, 已制作, 待删除, 已删除
	};
	
	public void initBillingStatus() {
		isToCreate = BillingStatus.待制作.equals(billingStatus);
		isReconstruction = isToCreate() && billingID != null;
		isToDelete = BillingStatus.待删除.equals(billingStatus);
	}
	
	public boolean isToCreate() {
		return isToCreate;
	}

	public boolean isReconstruction() {
		return isReconstruction;
	}
	
	public boolean isToDelete() {
		return isToDelete;
	}
	
	public boolean isAttendanceSheetRequired() {
		return isAttendanceSheetRequired;
	}
	
	public boolean isBankPaymentSummaryRequired() {
		return isBankPaymentSummaryRequired;
	}

	public void setBankPaymentSummaryRequired(boolean isBankPaymentSummaryRequired) {
		this.isBankPaymentSummaryRequired = isBankPaymentSummaryRequired;
	}

	public BillingPlan() {
		this.subPlanList = new ArrayList<SubBillingPlan>();
	}
	
	public SubBillingPlan createSubPlan(String subPlanProjectUnit, String subPlanProjectLeader, int subPlanPayYear,
			int subPlanStartMonth, int subPlanEndMonth, int subPlanPayCount, double subPlanTotalPay, String subPlanRosterType) {
		SubBillingPlan subPlan = new SubBillingPlan();
		subPlan.setSubPlanProjectUnit(subPlanProjectUnit);
		subPlan.setSubPlanProjectLeader(subPlanProjectLeader);
		subPlan.setSubPlanPayYear(subPlanPayYear);
		subPlan.setSubPlanStartMonth(subPlanStartMonth);
		subPlan.setSubPlanEndMonth(subPlanEndMonth);
		subPlan.setSubPlanPayCount(subPlanPayCount);
		subPlan.setSubPlanRosterType(subPlanRosterType);
		subPlan.setProcessingTotalPay(subPlanTotalPay);
		subPlan.setProcessingPayCount(subPlanPayCount);
		subPlanList.add(subPlan);
		System.out.println(subPlan);
		return subPlan;
	}

	public List<SubBillingPlan> getSubPlanList() {
		return this.subPlanList;
	}
	
	/**
	 * @return the orderNumber
	 */
	public int getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @param orderNumber
	 *            the orderNumber to set
	 */
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * @return the projectUnit
	 */
	public String getProjectUnit() {
		return projectUnit;
	}

	/**
	 * @param projectUnit
	 *            the projectUnit to set
	 */
	public void setProjectUnit(String projectUnit) {
		this.projectUnit = projectUnit;
	}

	/**
	 * @return the contractID
	 */
	public String getContractID() {
		return contractID;
	}

	/**
	 * @param contractID
	 *            the contractID to set
	 */
	public void setContractID(String contractID) {
		this.contractID = contractID;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName
	 *            the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the projectID
	 */
	public String getProjectID() {
		return projectID;
	}

	/**
	 * @param projectID
	 *            the projectID to set
	 */
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	/**
	 * @return the projectLeader
	 */
	public String getProjectLeader() {
		return projectLeader;
	}

	/**
	 * @param projectLeader
	 *            the projectLeader to set
	 */
	public void setProjectLeader(String projectLeader) {
		this.projectLeader = projectLeader;
		this.processingProjectLeader = this.projectLeader;
		this.rosterProjectLeader = this.projectLeader;
	}

	/**
	 * @return the contractValue
	 */
	public double getContractValue() {
		return contractValue;
	}

	/**
	 * @param contractValue
	 *            the contractValue to set
	 */
	public void setContractValue(double contractValue) {
		this.contractValue = contractValue;
	}

	/**
	 * @return the billingYear
	 */
	public int getBillingYear() {
		return billingYear;
	}

	/**
	 * @param billingYear the billingYear to set
	 */
	public void setBillingYear(int billingYear) {
		this.billingYear = billingYear;
	}

	/**
	 * @return the billingMonth
	 */
	public int getBillingMonth() {
		return billingMonth;
	}

	/**
	 * @param billingMonth the billingMonth to set
	 */
	public void setBillingMonth(int billingMonth) {
		this.billingMonth = billingMonth;
	}

	/**
	 * @return the billingDay
	 */
	public int getBillingDay() {
		return billingDay;
	}

	/**
	 * @param billingDay the billingDay to set
	 */
	public void setBillingDay(int billingDay) {
		this.billingDay = billingDay;
	}

	public String getBillingDateString() {
		return String.valueOf(billingYear * 10000 + billingMonth * 100 + billingDay);
	}
	
	public int getBillingYearMonthInt() {
		return billingYear * 100 + billingMonth;
	}
	
	/**
	 * @return the invoiceAmount
	 */
	public double getInvoiceAmount() {
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount
	 *            the invoiceAmount to set
	 */
	public void setInvoiceAmount(double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	/**
	 * @return the administrationExpenses
	 */
	public double getAdministrationExpenses() {
		return administrationExpenses;
	}

	/**
	 * @param administrationExpenses
	 *            the administrationExpenses to set
	 */
	public void setAdministrationExpenses(double administrationExpenses) {
		this.administrationExpenses = administrationExpenses;
	}

	/**
	 * @return the administrationExpensesRate
	 */
	public double getAdministrationExpensesRate() {
		return administrationExpensesRate;
	}

	/**
	 * @param administrationExpensesRate
	 *            the administrationExpensesRate to set
	 */
	public void setAdministrationExpensesRate(double administrationExpensesRate) {
		this.administrationExpensesRate = administrationExpensesRate;
	}

	/**
	 * @return the totalAdministrationExpenses
	 */
	public double getTotalAdministrationExpenses() {
		return totalAdministrationExpenses;
	}

	/**
	 * @param totalAdministrationExpenses
	 *            the totalAdministrationExpenses to set
	 */
	public void setTotalAdministrationExpenses(double totalAdministrationExpenses) {
		this.totalAdministrationExpenses = totalAdministrationExpenses;
	}

	/**
	 * @return the totalPay
	 */
	public double getTotalPay() {
		return totalPay;
	}

	/**
	 * @param totalPay
	 *            the totalPay to set
	 */
	public void setTotalPay(double totalPay) {
		this.totalPay = totalPay;
		if (this.processingTotalPay == 0.0) {
			setProcessingTotalPay(totalPay);
		}	
	}

	/**
	 * @return the payCount
	 */
	public int getPayCount() {
		return payCount;
	}

	/**
	 * @param payCount
	 *            the payCount to set
	 */
	public void setPayCount(int payCount) {
		this.payCount = payCount;
		if (this.processingPayCount == 0) {
			setProcessingPayCount(payCount);
		}	
	}

	/**
	 * @return the withdrawalFee
	 */
	public double getWithdrawalFee() {
		return withdrawalFee;
	}

	/**
	 * @param withdrawalFee
	 *            the withdrawalFee to set
	 */
	public void setWithdrawalFee(double withdrawalFee) {
		this.withdrawalFee = withdrawalFee;
	}

	/**
	 * @return the alternatedProjectLeaderRemark
	 */
	public String getAlternatedProjectLeaderRemark() {
		return alternatedProjectLeaderRemark;
	}

	/**
	 * @param alternatedProjectLeaderRemark the alternatedProjectLeaderRemark to set
	 */
	public void setAlternatedProjectLeaderRemark(String alternatedProjectLeaderRemark) {
		if (this.alternatedProjectLeaderRemark == null) {
			this.alternatedProjectLeaderRemark = alternatedProjectLeaderRemark;
		} else {
			this.alternatedProjectLeaderRemark = this.alternatedProjectLeaderRemark + Constant.DELIMITER0 + alternatedProjectLeaderRemark;
		}
	}
	
	public void setAlternatedProjectLeaderRemark(int payCount) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getProjectUnit());
		sb.append(Constant.DELIMITER1);
		sb.append(this.getProjectLeader());
		sb.append(Constant.DELIMITER1);
		sb.append(payCount);
		this.setAlternatedProjectLeaderRemark(sb.toString());
	}
	
	public void setAlternatedProjectLeaderRemark(String company, String alternatedProjectLeader, int payYear, int payCount) {
		StringBuilder sb = new StringBuilder();
		sb.append(company);
		sb.append(Constant.DELIMITER1);
		sb.append(alternatedProjectLeader);
		sb.append(Constant.DELIMITER1);
		sb.append(payYear);
		sb.append(Constant.DELIMITER1);
		sb.append(payCount);
		this.setAlternatedProjectLeaderRemark(sb.toString());
	}
	
	public void resetAlternatedProjectLeaderRemark() {
		this.alternatedProjectLeaderRemark = null;
	}
	
	public int getAlternatedProjectLeaderRemarkSize() {
		if(alternatedProjectLeaderRemark != null) {
			String[] s = alternatedProjectLeaderRemark.split(Constant.DELIMITER00);
			return s.length;
		}
		return 0;
	}
	
	public String getProjectUnitFromAlternatedProjectLeaderRemark(int index) {
		String[] s = alternatedProjectLeaderRemark.split(Constant.DELIMITER00);
		return s[index].split(Constant.DELIMITER1)[0];
	}
	
	public String getProjectLeaderFromAlternatedProjectLeaderRemark(int index) {
		String[] s = alternatedProjectLeaderRemark.split(Constant.DELIMITER00);
		return s[index].split(Constant.DELIMITER1)[1];
	}
	
	public int getPayYearFromAlternatedProjectLeaderRemark(int index) {
		String[] s = alternatedProjectLeaderRemark.split(Constant.DELIMITER00);
		return Integer.valueOf(s[index].split(Constant.DELIMITER1)[2]);
	}
	
	public int getPayCountFromAlternatedProjectLeaderRemark(int index) {
		String[] s = alternatedProjectLeaderRemark.split(Constant.DELIMITER00);
		return Integer.valueOf(s[index].split(Constant.DELIMITER1)[3]);
	}

	public BillingStatus getBillingStatusAfterBillingCompleted() {
		this.billingStatus = BillingStatus.已制作;
		return billingStatus;
	}

	/**
	 * @return the billingStatus
	 */
	public BillingStatus getBillingStatus() {
		return billingStatus;
	}

	/**
	 * @param billingStatus
	 *            the billingStatus to set
	 */
	public void setBillingStatus(BillingStatus billingStatus) {
		this.billingStatus = billingStatus;
	}

	public String getBillingIDAfterBillingCompleted() {
		if(billingID == null) {
			setBillingID(this.payCount);
		}	
		return billingID;
	}
	
	public void setBillingID(int payCount) {
		this.setBillingID(this.startPayYear, payCount);
	}
	
	public void setBillingID(int payYear, int payCount) {
		this.billingID = this.projectUnit + Constant.DELIMITER0 + this.projectLeader + Constant.DELIMITER0
				+ this.contractID + Constant.DELIMITER0 + payYear + Constant.DELIMITER0 + payCount + Constant.DELIMITER0 + Util.getCurrentDateString();
	}
	
	public void resetBillingID() {
		this.billingID = null;
	}

	/**
	 * @return the billingID
	 */
	public String getBillingID() {
		return billingID;
	}
	
	public String getProjectUnitFromBillingID() {
		String projectUnit = null;
		if (billingID != null) {
			String[] s = billingID.split(Constant.DELIMITER00);
			projectUnit = s[0];
		}
		return projectUnit;
	}
	
	public String getProjectLeaderFromBillingID() {
		String projectLeader = null;
		if (billingID != null) {
			String[] s = billingID.split(Constant.DELIMITER00);
			projectLeader = s[1];
		}
		return projectLeader;
	}
	
	public String getContractIDFromBillingID() {
		String contractID = null;
		if (billingID != null) {
			String[] s = billingID.split(Constant.DELIMITER00);
			contractID = s[2];
		}
		return contractID;
	}
	
	public int getPayYearFromBillingID() {
		int payYear = 0;
		if (billingID != null) {
			String[] s = billingID.split(Constant.DELIMITER00);
			payYear = Integer.valueOf(s[3]);
		}
		return payYear;
	}
	
	public int getPayCountFromBillingID() {
		int payCount = 0;
		if (billingID != null) {
			String[] s = billingID.split(Constant.DELIMITER00);
			payCount = Integer.valueOf(s[4]);
		}
		return payCount;
	}

	/**
	 * @param billingID
	 *            the billingID to set
	 */
	public void setBillingID(String billingID) {
		this.billingID = billingID;
	}

	/**
	 * @return the startAndEndPayTime
	 */
	public String getStartAndEndPayTime() {
		return startAndEndPayTime;
	}

	/**
	 * @param startAndEndPayTime the startAndEndPayTime to set
	 */
	public void setStartAndEndPayTime(String startAndEndPayTime) {
		this.startAndEndPayTime = startAndEndPayTime;
		parseStartAndEndPayTime();
	}
	
	private void parseStartAndEndPayTime() {
		if(startAndEndPayTime != null && !Constant.EMPTY_STRING.equals(startAndEndPayTime)) {
			String[] s = startAndEndPayTime.split("-");
			this.startPayTimeInInteger = Integer.valueOf(s[0]);
			this.endPayTimeInInteger = Integer.valueOf(s[1]);
		}
		if(startPayTimeInInteger > 0) {
			this.startPayYear = startPayTimeInInteger/100;
			this.startPayMonth = startPayTimeInInteger%100;
		}
		if(endPayTimeInInteger > 0) {
			this.endPayYear = endPayTimeInInteger/100;
			this.endPayMonth = endPayTimeInInteger%100;

			if (Constant.YES.equals(Constant.propUtil.getStringValue(Constant.CONFIG_SYSTEM_isEndPayMonthLaterThanCurrentMonth, Constant.NO))) {
				return;
			}
				
			int currentYearMonth = Util.getCurrentYearMonthInt();
			int currentYear = Util.getCurrentYear();
			int currentMonth = Util.getCurrentMonth();
			if (endPayTimeInInteger >= currentYearMonth) {
				if (Constant.FLAG_YES.equals(Constant.propUtil.getStringEnEmpty(Constant.CONFIG_人员当月是否可用))) {
					this.endPayTimeInInteger = currentYearMonth;
					this.endPayYear = currentYear;
					this.endPayMonth = currentMonth;
					return;
				}
				if (currentMonth == 1) {
					this.endPayYear = currentYear - 1;
					this.endPayMonth = 12;
					this.endPayTimeInInteger = this.endPayYear * 100 + this.endPayMonth;
				} else {
					this.endPayTimeInInteger = currentYearMonth - 1;
					this.endPayYear = currentYear;
					this.endPayMonth = currentMonth - 1;
				}				
			}
		}
	}

	/**
	 * @return the endPayTimeInInteger
	 */
	public int getEndPayTimeInInteger() {
		return endPayTimeInInteger;
	}

	/**
	 * @param endPayTimeInInteger the endPayTimeInInteger to set
	 */
	public void setEndPayTimeInInteger(int endPayTimeInInteger) {
		this.endPayTimeInInteger = endPayTimeInInteger;
	}

	/**
	 * @return the startPayTimeInInteger
	 */
	public int getStartPayTimeInInteger() {
		return startPayTimeInInteger;
	}

	/**
	 * @param startPayTimeInInteger the startPayTimeInInteger to set
	 */
	public void setStartPayTimeInInteger(int startPayTimeInInteger) {
		this.startPayTimeInInteger = startPayTimeInInteger;
	}

	/**
	 * @return the startPayYear
	 */
	public int getStartPayYear() {
		return startPayYear;
	}

	/**
	 * @param startPayYear the startPayYear to set
	 */
	public void setStartPayYear(int startPayYear) {
		this.startPayYear = startPayYear;
	}

	/**
	 * @return the endPayYear
	 */
	public int getEndPayYear() {
		return endPayYear;
	}

	/**
	 * @param endPayYear the endPayYear to set
	 */
	public void setEndPayYear(int endPayYear) {
		this.endPayYear = endPayYear;
	}

	/**
	 * @return the startPayMonth
	 */
	public int getStartPayMonth() {
		return startPayMonth;
	}

	/**
	 * @param startPayMonth the startPayMonth to set
	 */
	public void setStartPayMonth(int startPayMonth) {
		this.startPayMonth = startPayMonth;
	}

	/**
	 * @return the endPayMonth
	 */
	public int getEndPayMonth() {
		return endPayMonth;
	}

	/**
	 * @param endPayMonth the endPayMonth to set
	 */
	public void setEndPayMonth(int endPayMonth) {
		this.endPayMonth = endPayMonth;
	}

	/**
	 * @return the attendanceSheetFlag
	 */
	public String getAttendanceSheetFlag() {
		return attendanceSheetFlag;
	}

	/**
	 * @param attendanceSheetFlag the attendanceSheetFlag to set
	 */
	public void setAttendanceSheetFlag(String attendanceSheetFlag) {
		this.attendanceSheetFlag = attendanceSheetFlag;
		this.isAttendanceSheetRequired = Constant.ATTENDANCE_SHEET_FLAG.equals(attendanceSheetFlag);
	}

	/**
	 * @return the bollowingDateInt
	 */
	public int getBollowingDateInt() {
		return bollowingDateInt;
	}

	/**
	 * @param bollowingDateInt the bollowingDateInt to set
	 */
	public void setBollowingDateInt(int bollowingDateInt) {
		this.bollowingDateInt = bollowingDateInt;
	}

	/**
	 * @return the bollowingAmount
	 */
	public double getBollowingAmount() {
		return bollowingAmount;
	}

	/**
	 * @param bollowingAmount the bollowingAmount to set
	 */
	public void setBollowingAmount(double bollowingAmount) {
		this.bollowingAmount = bollowingAmount;
	}

	/**
	 * @return the repaymentDateInt
	 */
	public int getRepaymentDateInt() {
		return repaymentDateInt;
	}

	/**
	 * @param repaymentDateInt the repaymentDateInt to set
	 */
	public void setRepaymentDateInt(int repaymentDateInt) {
		this.repaymentDateInt = repaymentDateInt;
	}

	/**
	 * @return the repaymentAmount
	 */
	public double getRepaymentAmount() {
		return repaymentAmount;
	}

	/**
	 * @param repaymentAmount the repaymentAmount to set
	 */
	public void setRepaymentAmount(double repaymentAmount) {
		this.repaymentAmount = repaymentAmount;
	}

	/**
	 * @return the rowIndex
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * @param rowIndex the rowIndex to set
	 */
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	/**
	 * @return the processingPayCount
	 */
	public int getProcessingPayCount() {
		return processingPayCount;
	}

	/**
	 * @param processingPayCount the processingPayCount to set
	 */
	public void setProcessingPayCount(int processingPayCount) {
		this.processingPayCount = processingPayCount;
	}

	/**
	 * @return the processingTotalPay
	 */
	public double getProcessingTotalPay() {
		return processingTotalPay;
	}

	/**
	 * @param processingTotalPay the processingTotalPay to set
	 */
	public void setProcessingTotalPay(double processingTotalPay) {
		this.processingTotalPay = processingTotalPay;
		setRemainTotalPay(processingTotalPay);
	}

	/**
	 * @return the processingProjectUnit
	 */
	public String getProcessingProjectUnit() {
		return processingProjectUnit;
	}

	/**
	 * @param processingProjectUnit the processingProjectUnit to set
	 */
	public void setProcessingProjectUnit(String processingProjectUnit) {
		this.processingProjectUnit = processingProjectUnit;
	}

	/**
	 * @return the processingProjectLeader
	 */
	public String getProcessingProjectLeader() {
		return processingProjectLeader;
	}

	/**
	 * @param processingProjectLeader the processingProjectLeader to set
	 */
	public void setProcessingProjectLeader(String processingProjectLeader) {
		this.processingProjectLeader = processingProjectLeader;
	}

	/**
	 * @return the remainTotalPay
	 */
	public double getRemainTotalPay() {
		return remainTotalPay;
	}

	/**
	 * @param remainTotalPay the remainTotalPay to set
	 */
	public void setRemainTotalPay(double remainTotalPay) {
		this.remainTotalPay = remainTotalPay;
	}

	/**
	 * @return the rosterProjectLeader
	 */
	public String getRosterProjectLeader() {
		return rosterProjectLeader;
	}

	/**
	 * @param rosterProjectLeader the rosterProjectLeader to set
	 */
	public void setRosterProjectLeader(String rosterProjectLeader) {
		this.rosterProjectLeader = rosterProjectLeader;
	}

	/**
	 * @return the payrollSheetList
	 */
	public List<PayrollSheet> getPayrollSheetList() {
		return payrollSheetList;
	}

	/**
	 * @param payrollSheetList the payrollSheetList to set
	 */
	public void setPayrollSheetList(List<PayrollSheet> payrollSheetList) {
		this.payrollSheetList = payrollSheetList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BillingPlan [orderNumber=" + orderNumber + ", projectUnit=" + projectUnit + ", contractID=" + contractID
				+ ", projectName=" + projectName + ", projectID=" + projectID + ", projectLeader=" + projectLeader
				+ ", contractValue=" + contractValue + ", billingYear=" + billingYear + ", billingMonth=" + billingMonth
				+ ", billingDay=" + billingDay + ", invoiceAmount=" + invoiceAmount + ", administrationExpenses="
				+ administrationExpenses + ", administrationExpensesRate=" + administrationExpensesRate
				+ ", totalAdministrationExpenses=" + totalAdministrationExpenses + ", totalPay=" + totalPay
				+ ", payCount=" + payCount + ", withdrawalFee=" + withdrawalFee + ", alternatedProjectLeaderRemark="
				+ alternatedProjectLeaderRemark + ", billingStatus=" + billingStatus + ", billingID=" + billingID
				+ ", startAndEndPayTime=" + startAndEndPayTime + ", startPayTimeInInteger=" + startPayTimeInInteger
				+ ", endPayTimeInInteger=" + endPayTimeInInteger + ", startPayYear=" + startPayYear + ", endPayYear="
				+ endPayYear + ", startPayMonth=" + startPayMonth + ", endPayMonth=" + endPayMonth + ", rowIndex="
				+ rowIndex + "]";
	}
	
}
