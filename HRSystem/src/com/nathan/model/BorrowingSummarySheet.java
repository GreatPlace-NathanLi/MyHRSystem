package com.nathan.model;

import java.util.ArrayList;
import java.util.List;

public class BorrowingSummarySheet extends AggregatingResultSheet {

	private String projectLeader;
	
	private double borrowingAmountSum = 0.0;
	
	private double repaymentAmountSum = 0.0;
	
	private List<BorrowingSummary> borrowingSummaryList;

	public BorrowingSummarySheet() {
		this.borrowingSummaryList = new ArrayList<BorrowingSummary>();
		this.borrowingAmountSum = 0.0;
		this.repaymentAmountSum = 0.0;
	}
	
	public void addBorrowingSummary(BorrowingSummary borrowingSummary) {
		this.borrowingSummaryList.add(borrowingSummary);
		this.borrowingAmountSum += borrowingSummary.getBorrowingAmount();
		this.repaymentAmountSum += borrowingSummary.getRepaymentAmount();
	}

	/**
	 * @return the projectLeader
	 */
	public String getProjectLeader() {
		return projectLeader;
	}

	/**
	 * @param projectleader the projectLeader to set
	 */
	public void setProjectLeader(String projectLeader) {
		this.projectLeader = projectLeader;
	}

	/**
	 * @return the borrowingAmountSum
	 */
	public double getBorrowingAmountSum() {
		return borrowingAmountSum;
	}

	/**
	 * @param borrowingAmountSum the borrowingAmountSum to set
	 */
	public void setBorrowingAmountSum(double borrowingAmountSum) {
		this.borrowingAmountSum = borrowingAmountSum;
	}

	/**
	 * @return the repaymentAmountSum
	 */
	public double getRepaymentAmountSum() {
		return repaymentAmountSum;
	}

	/**
	 * @param repaymentAmountSum the repaymentAmountSum to set
	 */
	public void setRepaymentAmountSum(double repaymentAmountSum) {
		this.repaymentAmountSum = repaymentAmountSum;
	}

	/**
	 * @return the borrowingSummaryList
	 */
	public List<BorrowingSummary> getBorrowingSummaryList() {
		return borrowingSummaryList;
	}

	/**
	 * @param borrowingSummaryList the borrowingSummaryList to set
	 */
	public void setBorrowingSummaryList(List<BorrowingSummary> borrowingSummaryList) {
		this.borrowingSummaryList = borrowingSummaryList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BorrowingSummarySheet [company=" + this.getCompany() + ", projectleader=" + projectLeader + ", startYearMonthInt="
				+ this.getStartYearMonthInt() + ", endYearMonthInt=" + this.getEndYearMonthInt() + ", borrowingAmountSum=" + borrowingAmountSum
				+ ", repaymentAmountSum=" + repaymentAmountSum + ", borrowingSummaryList=" + borrowingSummaryList + "]";
	}
	
}
