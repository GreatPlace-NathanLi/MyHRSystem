package com.nathan.model;

public class ProjectMember {
	
	private int orderNumber;
	
	private String name;
	
	private double basePay;
	
	private double dailyPay;

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
		if(basePay>1500) {
			this.dailyPay = 170;
		} else if (basePay==1500) {
			this.dailyPay = 160;
		} else {
			this.dailyPay = 150;
		}
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProjectMember [orderNumber=" + orderNumber + ", name=" + name + ", basePay=" + basePay + ", dailyPay="
				+ dailyPay + "]";
	}


}
