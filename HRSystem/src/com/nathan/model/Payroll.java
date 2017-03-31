package com.nathan.model;

public class Payroll {
	
	private int orderNumber;
	
	private String name;
	
	private double basePay;
	
	private double performancePay;
	
	private double overtimePay;	
	
	private double highTemperatureAllowance;
	
	private double socialSecurity;
	
	private double actualPay;
	
	private double dailyPay;
	
	private int workingDays;
	
	private double administrationExpenses;
	
	public void increaseWorkingDays(int days) {
		this.workingDays+=days;
	}

	public double getTotalPay() {
		return workingDays * dailyPay;
	}
	
	/**
	 * @return the orderNumber
	 */
	public int getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @param orderNumber the orderNumber to set
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
	 * @param name the name to set
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
	 * @param basePay the basePay to set
	 */
	public void setBasePay(double basePay) {
		this.basePay = basePay;
	}

	/**
	 * @return the performancePay
	 */
	public double getPerformancePay() {
		return performancePay;
	}

	/**
	 * @param performancePay the performancePay to set
	 */
	public void setPerformancePay(double performancePay) {
		this.performancePay = performancePay;
	}

	/**
	 * @return the overtimePay
	 */
	public double getOvertimePay() {
		return overtimePay;
	}

	/**
	 * @param overtimePay the overtimePay to set
	 */
	public void setOvertimePay(double overtimePay) {
		this.overtimePay = overtimePay;
	}

	/**
	 * @return the highTemperatureAllowance
	 */
	public double getHighTemperatureAllowance() {
		return highTemperatureAllowance;
	}

	/**
	 * @param highTemperatureAllowance the highTemperatureAllowance to set
	 */
	public void setHighTemperatureAllowance(double highTemperatureAllowance) {
		this.highTemperatureAllowance = highTemperatureAllowance;
	}

	/**
	 * @return the socialSecurity
	 */
	public double getSocialSecurity() {
		return socialSecurity;
	}

	/**
	 * @param socialSecurity the socialSecurity to set
	 */
	public void setSocialSecurity(double socialSecurity) {
		this.socialSecurity = socialSecurity;
	}

	/**
	 * @return the actualPay
	 */
	public double getActualPay() {
		return actualPay;
	}

	/**
	 * @param actualPay the actualPay to set
	 */
	public void setActualPay(double actualPay) {
		this.actualPay = actualPay;
	}

	/**
	 * @return the dailyPay
	 */
	public double getDailyPay() {
		return dailyPay;
	}

	/**
	 * @param dailyPay the dailyPay to set
	 */
	public void setDailyPay(double dailyPay) {
		this.dailyPay = dailyPay;
	}

	/**
	 * @return the workingDays
	 */
	public int getWorkingDays() {
		return workingDays;
	}

	/**
	 * @param workingDays the workingDays to set
	 */
	public void setWorkingDays(int workingDays) {
		this.workingDays = workingDays;
	}

	/**
	 * @return the administrationExpenses
	 */
	public double getAdministrationExpenses() {
		return administrationExpenses;
	}

	/**
	 * @param administrationExpenses the administrationExpenses to set
	 */
	public void setAdministrationExpenses(float administrationExpenses) {
		this.administrationExpenses = administrationExpenses;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Payroll [orderNumber=" + orderNumber + ", name=" + name + ", basePay=" + basePay + ", performancePay="
				+ performancePay + ", overtimePay=" + overtimePay + ", highTemperatureAllowance="
				+ highTemperatureAllowance + ", socialSecurity=" + socialSecurity + ", actualPay=" + actualPay
				+ ", dailyPay=" + dailyPay + ", workingDays=" + workingDays + ", administrationExpenses="
				+ administrationExpenses + "]";
	}

}