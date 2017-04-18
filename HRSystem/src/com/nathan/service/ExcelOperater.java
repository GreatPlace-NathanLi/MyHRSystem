package com.nathan.service;

public interface ExcelOperater {
	
	public void read(String inFile);
	
	public void write(String outFile);
	
	public void write(String inFile, String outFile) throws Exception;
	
	public void modify(String destFile) throws Exception;
	
	public boolean delete(String inFile);

}
