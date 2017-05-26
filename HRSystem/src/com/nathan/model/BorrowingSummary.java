package com.nathan.model;

public class BorrowingSummary {
	
	private String projectLeader;

	private String company;
	
	private String contractID;
	
	private int borrowingDateInt;
	
	private double borrowingAmount = 0.0;
	
	private int repaymentDateInt;
	
	private double repaymentAmount = 0.0;
	
	private String remark;

	/**
	 * @return the projectLeader
	 */
	public String getProjectLeader() {
		return projectLeader;
	}

	/**
	 * @param projectLeader the projectLeader to set
	 */
	public void setProjectLeader(String projectLeader) {
		this.projectLeader = projectLeader;
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
	 * @return the borrowingDateInt
	 */
	public int getBorrowingDateInt() {
		return borrowingDateInt;
	}

	/**
	 * @param borrowingDateInt the borrowingDateInt to set
	 */
	public void setBorrowingDateInt(int borrowingDateInt) {
		this.borrowingDateInt = borrowingDateInt;
	}

	/**
	 * @return the borrowingAmount
	 */
	public double getBorrowingAmount() {
		return borrowingAmount;
	}

	/**
	 * @param borrowingAmount the borrowingAmount to set
	 */
	public void setBorrowingAmount(double borrowingAmount) {
		this.borrowingAmount = borrowingAmount;
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
		return "BorrowingSummary [projectLeader=" + projectLeader + ", company=" + company + ", contractID="
				+ contractID + ", bollowingDateInt=" + borrowingDateInt + ", bollowingAmount=" + borrowingAmount
				+ ", repaymentDateInt=" + repaymentDateInt + ", repaymentAmount=" + repaymentAmount + ", remark="
				+ remark + "]";
	}
	
}
