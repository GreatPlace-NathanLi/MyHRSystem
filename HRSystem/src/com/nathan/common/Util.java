package com.nathan.common;

public class Util {
	
	public static int getMonthIdentifierColumnIndexInRoster(int month) {
		return 2 * month + 3;
	}
	
	public static int getMonthAmountColumnIndexInRoster(int month) {
		return 2 * month + 4;
	}

}
