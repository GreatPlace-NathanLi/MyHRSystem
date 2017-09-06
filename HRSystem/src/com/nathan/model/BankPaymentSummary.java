package com.nathan.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BankPaymentSummary {
	
	private String ContractID;
	
	private Map<String, Double> namePayMap;
	
	private HashSet<String> nameSet;
	
	private ArrayList<String> nameList;
	
	public BankPaymentSummary() {
		namePayMap = new ConcurrentHashMap<String, Double>();
		nameSet = new HashSet<String>();
		nameList = new ArrayList<String>();
	}

	public void addName(String name) {
		if (nameSet.contains(name)) {
			return;
		}
		nameSet.add(name);
		nameList.add(name);
	}
	
	public ArrayList<String> getNameList() {
		return this.nameList;
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
