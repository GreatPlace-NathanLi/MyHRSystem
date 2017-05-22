package com.nathan.model;

public enum SheetType {
	
	费用明细表(1), 发票(2), 支出证明单(3), 付款凭据(4), 工资表(5), 汇总表(6), 考勤表(7), 劳务费汇总表(8);
	
	private SheetType(int sheetID) {
		this.sheetID = sheetID;
	}
	
	private int sheetID;
	
	public int getSheetID() {
		return sheetID;
	}
}
