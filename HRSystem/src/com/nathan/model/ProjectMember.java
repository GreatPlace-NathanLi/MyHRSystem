package com.nathan.model;

public class ProjectMember {

	private int orderNumber;

	private String name;

	private double basePay;

	private double dailyPay;

	private String contractStartAndEndTime;

	private int contractStartTimeInInteger;

	private int contractEndTimeInInteger;

	private String onJobStartAndEndTime;

	private int onJobStartTimeInInteger;

	private int onJobEndTimeInInteger;

	public boolean isAvailable(int year, int month) {
		int processingTime = year * 10000 + month * 100 + 1;
		boolean isContractExpired = processingTime < contractStartTimeInInteger
				|| processingTime > contractEndTimeInInteger;
		boolean isOffJob = false;
		if (onJobEndTimeInInteger > 0) {
			isOffJob = processingTime < onJobStartTimeInInteger || processingTime >= onJobEndTimeInInteger;
		}
		return !isContractExpired && !isOffJob;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the basePay
	 */
	public double getBasePay() {
		return basePay;
	}

	/**
	 * @param basePay
	 *            the basePay to set
	 */
	public void setBasePay(double basePay) {
		this.basePay = basePay;
	}

	/**
	 * @return the dailyPay
	 */
	public double getDailyPay() {
		return dailyPay;
	}

	/**
	 * @param dailyPay
	 *            the dailyPay to set
	 */
	public void setDailyPay(double dailyPay) {
		this.dailyPay = dailyPay;
	}

	/**
	 * @return the contractStartAndEndTime
	 */
	public String getContractStartAndEndTime() {
		return contractStartAndEndTime;
	}

	/**
	 * @param contractStartAndEndTime
	 *            the contractStartAndEndTime to set
	 */
	public void setContractStartAndEndTime(String contractStartAndEndTime) {
		this.contractStartAndEndTime = contractStartAndEndTime;
		parseContractStartAndEndTime(contractStartAndEndTime);
	}

	private void parseContractStartAndEndTime(String contractStartAndEndTime) {
		if (contractStartAndEndTime != null) {
			String[] s = contractStartAndEndTime.split("-");
			this.contractStartTimeInInteger = Integer.valueOf(s[0]);
			this.contractEndTimeInInteger = Integer.valueOf(s[1]);
		}
	}

	/**
	 * @return the contractStartTimeInInteger
	 */
	public int getContractStartTimeInInteger() {
		return contractStartTimeInInteger;
	}

	/**
	 * @param contractStartTimeInInteger
	 *            the contractStartTimeInInteger to set
	 */
	public void setContractStartTimeInInteger(int contractStartTimeInInteger) {
		this.contractStartTimeInInteger = contractStartTimeInInteger;
	}

	/**
	 * @return the contractEndTimeInInteger
	 */
	public int getContractEndTimeInInteger() {
		return contractEndTimeInInteger;
	}

	/**
	 * @param contractEndTimeInInteger
	 *            the contractEndTimeInInteger to set
	 */
	public void setContractEndTimeInInteger(int contractEndTimeInInteger) {
		this.contractEndTimeInInteger = contractEndTimeInInteger;
	}

	/**
	 * @return the onJobStartAndEndTime
	 */
	public String getOnJobStartAndEndTime() {
		return onJobStartAndEndTime;
	}

	/**
	 * @param onJobStartAndEndTime
	 *            the onJobStartAndEndTime to set
	 */
	public void setOnJobStartAndEndTime(String onJobStartAndEndTime) {
		this.onJobStartAndEndTime = onJobStartAndEndTime;
		parseOnJobStartAndEndTime(onJobStartAndEndTime);
	}

	private void parseOnJobStartAndEndTime(String onJobStartAndEndTime) {
		if (onJobStartAndEndTime != null) {
			String[] s = onJobStartAndEndTime.split("-");
			this.onJobStartTimeInInteger = Integer.valueOf(s[0]);
			this.onJobEndTimeInInteger = Integer.valueOf(s[1]);
		}
	}

	/**
	 * @return the onJobStartTimeInInteger
	 */
	public int getOnJobStartTimeInInteger() {
		return onJobStartTimeInInteger;
	}

	/**
	 * @param onJobStartTimeInInteger
	 *            the onJobStartTimeInInteger to set
	 */
	public void setOnJobStartTimeInInteger(int onJobStartTimeInInteger) {
		this.onJobStartTimeInInteger = onJobStartTimeInInteger;
	}

	/**
	 * @return the onJobEndTimeInInteger
	 */
	public int getOnJobEndTimeInInteger() {
		return onJobEndTimeInInteger;
	}

	/**
	 * @param onJobEndTimeInInteger
	 *            the onJobEndTimeInInteger to set
	 */
	public void setOnJobEndTimeInInteger(int onJobEndTimeInInteger) {
		this.onJobEndTimeInInteger = onJobEndTimeInInteger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProjectMember [orderNumber=" + orderNumber + ", name=" + name + ", basePay=" + basePay + ", dailyPay="
				+ dailyPay + ", contractStartTimeInInteger=" + contractStartTimeInInteger
				+ ", contractEndTimeInInteger=" + contractEndTimeInInteger + ", onJobStartTimeInInteger="
				+ onJobStartTimeInInteger + ", onJobEndTimeInInteger=" + onJobEndTimeInInteger + "]";
	}

}
