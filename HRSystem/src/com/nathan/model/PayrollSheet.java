package com.nathan.model;

import java.util.ArrayList;
import java.util.List;

public class PayrollSheet {
	
	private String title;
	
	private int payYear;
	
	private int payMonth;
	
	private String leader;
	
	private String contractID;
	
	private List<Payroll> payrollList;
	
	private String tabulator;
	
	private int payrollNumber;
	
	private double totalAmount;
	
	public PayrollSheet() {
		this.payrollList = new ArrayList<Payroll>();
	}
	
	public void addPayroll(Payroll payroll) {
		this.payrollList.add(payroll.getOrderNumber()-1, payroll);
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the payYear
	 */
	public int getPayYear() {
		return payYear;
	}

	/**
	 * @param payYear the payYear to set
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
	 * @param payMonth the payMonth to set
	 */
	public void setPayMonth(int payMonth) {
		this.payMonth = payMonth;
	}

	/**
	 * @return the leader
	 */
	public String getLeader() {
		return leader;
	}

	/**
	 * @param leader the leader to set
	 */
	public void setLeader(String leader) {
		this.leader = leader;
	}

	/**
	 * @return the contractID
	 */
	public String getContractID() {
		return contractID;
	}

	/**
	 * @param contractID the contractID to set
	 */
	public void setContractID(String contractID) {
		this.contractID = contractID;
	}

	/**
	 * @return the payrollList
	 */
	public List<Payroll> getPayrollList() {
		return payrollList;
	}

	/**
	 * @param payrollList the payrollList to set
	 */
	public void setPayrollList(List<Payroll> payrollList) {
		this.payrollList = payrollList;
	}

	/**
	 * @return the tabulator
	 */
	public String getTabulator() {
		return tabulator;
	}

	/**
	 * @param tabulator the tabulator to set
	 */
	public void setTabulator(String tabulator) {
		this.tabulator = tabulator;
	}

	/**
	 * @return the totalAmount
	 */
	public double getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the payrollNumber
	 */
	public int getPayrollNumber() {
		return payrollNumber;
	}
	
	/**
	 * @param payrollNumber the payrollNumber to set
	 */
	public void setPayrollNumber(int payrollNumber) {
		this.payrollNumber = payrollNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PayrollSheet [title=" + title + ", payYear=" + payYear + ", payMonth=" + payMonth + ", leader=" + leader
				+ ", contractID=" + contractID + ", payrollList=" + payrollList + ", tabulator=" + tabulator
				+ ", payrollNumber=" + payrollNumber + ", totalAmount=" + totalAmount + "]";
	}

}
