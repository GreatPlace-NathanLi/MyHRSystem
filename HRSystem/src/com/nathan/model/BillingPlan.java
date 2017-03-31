package com.nathan.model;

import com.nathan.common.Constant;

public class BillingPlan {

	private int orderNumber;
	private String projectUnit;
	private String contractID;
	private String projectName;
	private String projectID;
	private String projectLeader;
	private double contractValue;
	private double invoiceAmount;
	private int payYear;
	private int payMonth;
	private double administrationExpenses;
	private double administrationExpensesRate;
	private double totalAdministrationExpenses;
	private double totalPay;
	private int payCount;
	private double withdrawalFee;
	private String billingStatus = null;
	private String billingID = null;

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
	 * @return the payYear
	 */
	public int getPayYear() {
		return payYear;
	}

	/**
	 * @param payYear
	 *            the payYear to set
	 */
	public void setPayYear(int payYear) {
		this.payYear = payYear;
	}

	/**
	 * @return the payMonth
	 */
	public int getPayMonth() {
		return payMonth;
	}

	/**
	 * @param payMonth
	 *            the payMonth to set
	 */
	public void setPayMonth(int payMonth) {
		this.payMonth = payMonth;
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
				+ this.contractID + Constant.DELIMITER0 + this.orderNumber;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BillingPlan [orderNumber=" + orderNumber + ", projectUnit=" + projectUnit + ", contractID=" + contractID
				+ ", projectName=" + projectName + ", projectID=" + projectID + ", projectLeader=" + projectLeader
				+ ", contractValue=" + contractValue + ", invoiceAmount=" + invoiceAmount + ", payYear=" + payYear
				+ ", payMonth=" + payMonth + ", administrationExpenses=" + administrationExpenses
				+ ", administrationExpensesRate=" + administrationExpensesRate + ", totalAdministrationExpenses="
				+ totalAdministrationExpenses + ", totalPay=" + totalPay + ", payCount=" + payCount + ", withdrawalFee="
				+ withdrawalFee + ", billingStatus=" + billingStatus + ", billingID=" + billingID + "]";
	}

}
