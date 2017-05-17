package com.nathan.service;

public interface ExcelPrinter {
	
	public boolean printExcel(String filePath, int fromSheetIndex, int toSheetIndex, int copies);
	
	public void close();
	
}
