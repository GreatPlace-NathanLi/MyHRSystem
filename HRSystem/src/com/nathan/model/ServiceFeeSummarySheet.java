package com.nathan.model;

import java.util.ArrayList;
import java.util.List;

public class ServiceFeeSummarySheet {
	
	private String company;
	
	private int startYearMonthInt;
	
	private int endYearMonthInt;
	
	private double invoiceAmountSum = 0.0;
	
	private double totalPaySum = 0.0;
	
	private double totalAdministrationExpensesSum = 0.0;
	
	private List<ServiceFeeSummary> serviceFeeSummaryList;
	
	public ServiceFeeSummarySheet() {
		this.serviceFeeSummaryList = new ArrayList<ServiceFeeSummary>();
		this.invoiceAmountSum = 0.0;
		this.totalPaySum = 0.0;
		this.totalAdministrationExpensesSum = 0.0;		
	}

	public void addServiceFeeSummary(ServiceFeeSummary summary) {
		this.serviceFeeSummaryList.add(summary);
		this.invoiceAmountSum += summary.getInvoiceAmount();
		this.totalPaySum += summary.getTotalPay();
		this.totalAdministrationExpensesSum += summary.getTotalAdministrationExpenses();
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
	 * @return the startYearMonthInt
	 */
	public int getStartYearMonthInt() {
		return startYearMonthInt;
	}

	/**
	 * @param startYearMonthInt the startYearMonthInt to set
	 */
	public void setStartYearMonthInt(int startYearMonthInt) {
		this.startYearMonthInt = startYearMonthInt;
	}

	/**
	 * @return the endYearMonthInt
	 */
	public int getEndYearMonthInt() {
		return endYearMonthInt;
	}

	/**
	 * @param endYearMonthInt the endYearMonthInt to set
	 */
	public void setEndYearMonthInt(int endYearMonthInt) {
		this.endYearMonthInt = endYearMonthInt;
	}

	/**
	 * @return the serviceFeeSummaryList
	 */
	public List<ServiceFeeSummary> getServiceFeeSummaryList() {
		return serviceFeeSummaryList;
	}

	/**
	 * @param serviceFeeSummaryList the serviceFeeSummaryList to set
	 */
	public void setServiceFeeSummaryList(List<ServiceFeeSummary> serviceFeeSummaryList) {
		this.serviceFeeSummaryList = serviceFeeSummaryList;
	}

	/**
	 * @return the invoiceAmountSum
	 */
	public double getInvoiceAmountSum() {
		return invoiceAmountSum;
	}

	/**
	 * @param invoiceAmountSum the invoiceAmountSum to set
	 */
	public void setInvoiceAmountSum(double invoiceAmountSum) {
		this.invoiceAmountSum = invoiceAmountSum;
	}

	/**
	 * @return the totalPaySum
	 */
	public double getTotalPaySum() {
		return totalPaySum;
	}

	/**
	 * @param totalPaySum the totalPaySum to set
	 */
	public void setTotalPaySum(double totalPaySum) {
		this.totalPaySum = totalPaySum;
	}

	/**
	 * @return the totalAdministrationExpensesSum
	 */
	public double getTotalAdministrationExpensesSum() {
		return totalAdministrationExpensesSum;
	}

	/**
	 * @param totalAdministrationExpensesSum the totalAdministrationExpensesSum to set
	 */
	public void setTotalAdministrationExpensesSum(double totalAdministrationExpensesSum) {
		this.totalAdministrationExpensesSum = totalAdministrationExpensesSum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServiceFeeSummarySheet [company=" + company + ", startYearMonthInt=" + startYearMonthInt
				+ ", endYearMonthInt=" + endYearMonthInt + ", serviceFeeSummaryList=" + serviceFeeSummaryList + "]";
	}

}
