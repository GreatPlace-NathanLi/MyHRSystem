package com.nathan.model;

public abstract class AggregatingResultSheet {

	private String company;
	
	private int startYearMonthInt;
	
	private int endYearMonthInt;

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
	
}
