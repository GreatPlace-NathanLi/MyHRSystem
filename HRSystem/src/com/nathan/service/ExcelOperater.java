package com.nathan.service;

public interface ExcelOperater {
	
	public void read(String inFile) throws Exception;
	
	public void write(String outFile) throws Exception;
	
	public void write(String inFile, String outFile) throws Exception;
	
	public void modify(String destFile) throws Exception;
	
	public void copy(String fromFile, String toFile) throws Exception;
	
	public boolean delete(String inFile) throws Exception;
	
	public void print(String inFile) throws Exception;

}
