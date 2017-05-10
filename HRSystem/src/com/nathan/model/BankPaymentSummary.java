package com.nathan.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BankPaymentSummary {
	
	private String ContractID;
	
	private Map<String, Double> namePayMap;
	
	public BankPaymentSummary() {
		namePayMap = new ConcurrentHashMap<String, Double>();
	}

	/**
	 * @return the contractID
	 */
	public String getContractID() {
		return ContractID;
	}

	/**
	 * @param contractID the contractID to set
	 */
	public void setContractID(String contractID) {
		ContractID = contractID;
	}

	/**
	 * @return the namePayMap
	 */
	public Map<String, Double> getNamePayMap() {
		return namePayMap;
	}

	/**
	 * @param namePayMap the namePayMap to set
	 */
	public void setNamePayMap(Map<String, Double> namePayMap) {
		this.namePayMap = namePayMap;
	}
	
	public void putPayment(String name, Double pay) {
		if (namePayMap.get(name) == null) {
			namePayMap.put(name, pay);
		} else {
			Double paySum = namePayMap.get(name) + pay;
			namePayMap.put(name, paySum);
		}
	}
	
	public Double getPayment(String name) {
		return namePayMap.get(name);
	}
}
