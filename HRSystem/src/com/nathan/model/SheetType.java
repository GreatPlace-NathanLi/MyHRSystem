package com.nathan.model;

public enum SheetType {
	
	������ϸ��(1), ��Ʊ(2), ֧��֤����(3), ����ƾ��(4), ���ʱ�(5), ���ܱ�(6), ���ڱ�(7), ����ѻ��ܱ�(8);
	
	private SheetType(int sheetID) {
		this.sheetID = sheetID;
	}
	
	private int sheetID;
	
	public int getSheetID() {
		return sheetID;
	}
}
