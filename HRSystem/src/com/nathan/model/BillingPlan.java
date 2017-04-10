package com.nathan.model;

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
	private double invoiceAmount;
	private double administrationExpenses;
	private double administrationExpensesRate;
	private double totalAdministrationExpenses;
	private double totalPay;
	private int payCount;
	private double withdrawalFee;
	private String billingStatus = null;
	private String billingID = null;
	private String startAndEndPayTime;
	private int startPayTimeInInteger;
	private int endPayTimeInInteger;
	private int startPayYear;
	private int endPayYear;
	private int startPayMonth;
	private int endPayMonth;

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

	public String getBillingStatusAfterBillingCompleted() {
		this.billingStatus = Constant.BILLING_STATUS_COMPLETED;
		return billingStatus;
	}

	/**
	 * @return the billingStatus
	 */
	public String getBillingStatus() {
		return billingStatus;
	}

	/**
	 * @param billingStatus
	 *            the billingStatus to set
	 */
	public void setBillingStatus(String billingStatus) {
		this.billingStatus = billingStatus;
	}

	public String getBillingIDAfterBillingCompleted() {
		this.billingID = this.projectUnit + Constant.DELIMITER0 + this.projectLeader + Constant.DELIMITER0
				+ this.contractID + Constant.DELIMITER0 + Util.getCurrentDateString();
		return billingID;
	}

	/**
	 * @return the billingID
	 */
	public String getBillingID() {
		return billingID;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BillingPlan [orderNumber=" + orderNumber + ", projectUnit=" + projectUnit + ", contractID=" + contractID
				+ ", projectName=" + projectName + ", projectID=" + projectID + ", projectLeader=" + projectLeader
				+ ", contractValue=" + contractValue + ", invoiceAmount=" + invoiceAmount + ", administrationExpenses="
				+ administrationExpenses + ", administrationExpensesRate=" + administrationExpensesRate
				+ ", totalAdministrationExpenses=" + totalAdministrationExpenses + ", totalPay=" + totalPay
				+ ", payCount=" + payCount + ", withdrawalFee=" + withdrawalFee + ", billingStatus=" + billingStatus
				+ ", billingID=" + billingID + ", startAndEndPayTime=" + startAndEndPayTime + ", startPayYear="
				+ startPayYear + ", endPayYear=" + endPayYear + ", startPayMonth=" + startPayMonth + ", endPayMonth="
				+ endPayMonth + "]";
	}
	
	
}
