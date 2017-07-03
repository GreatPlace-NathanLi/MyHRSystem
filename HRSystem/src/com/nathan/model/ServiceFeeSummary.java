package com.nathan.model;

public class ServiceFeeSummary {
	
	private String company;
	
	private int yearMonthInt;
	
	private double invoiceAmount = 0.0;
	
	private double totalPay = 0.0;
	
	private double totalAdministrationExpenses = 0.0;
	
	private String remark;
	
	public void add(String company, double invoiceAmount, double totalPay, double totalAdministrationExpenses) {
		this.company = company;
		this.invoiceAmount += invoiceAmount;
		this.totalPay += totalPay;
		this.totalAdministrationExpenses += totalAdministrationExpenses;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the yearMonthInt
	 */
	public int getYearMonthInt() {
		return yearMonthInt;
	}

	/**
	 * @param yearMonthInt the yearMonthInt to set
	 */
	public void setYearMonthInt(int yearMonthInt) {
		this.yearMonthInt = yearMonthInt;
	}

	/**
	 * @return the invoiceAmount
	 */
	public double getInvoiceAmount() {
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	public void setInvoiceAmount(double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	/**
	 * @return the totalPay
	 */
	public double getTotalPay() {
		return totalPay;
	}

	/**
	 * @param totalPay the totalPay to set
	 */
	public void setTotalPay(double totalPay) {
		this.totalPay = totalPay;
	}

	/**
	 * @return the totalAdministrationExpenses
	 */
	public double getTotalAdministrationExpenses() {
		return totalAdministrationExpenses;
	}

	/**
	 * @param totalAdministrationExpenses the totalAdministrationExpenses to set
	 */
	public void setTotalAdministrationExpenses(double totalAdministrationExpenses) {
		this.totalAdministrationExpenses = totalAdministrationExpenses;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServiceFeeSummary [company=" + company + ", yearMonthInt=" + yearMonthInt + ", invoiceAmount="
				+ invoiceAmount + ", totalPay=" + totalPay + ", totalAdministrationExpenses="
				+ totalAdministrationExpenses + ", remark=" + remark + "]";
	}
	
}
