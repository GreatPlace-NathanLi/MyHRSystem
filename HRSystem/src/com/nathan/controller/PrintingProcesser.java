package com.nathan.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nathan.common.Constant;
import com.nathan.model.SheetType;
import com.nathan.service.ExcelPrinter;
import com.nathan.service.JacobPrinter;
import com.nathan.view.InteractionHandler;

public class PrintingProcesser {

	private static Logger logger = Logger.getLogger(PrintingProcesser.class);
	
	private static List<ExcelPrintTask> A4_PrintTasks;
	private static List<ExcelPrintTask> A5_PrintTasks;
	private static List<ExcelPrintTask> others_PrintTasks;

	private ExcelPrinter printer;

	private boolean isPrintingManualHandling = true;
	private boolean isPaperSwitchManualHandling = true;

	private int totalTaskSize = 0;
	private int completedTaskSize = 0;

	public PrintingProcesser() {
		A4_PrintTasks = new ArrayList<ExcelPrintTask>();
		A5_PrintTasks = new ArrayList<ExcelPrintTask>();
		others_PrintTasks = new ArrayList<ExcelPrintTask>();
	}
	
	public void release() {
		if (printer != null) {
			printer.close();
		}
	}

	public void processPrintTasks() throws Exception {	
		totalTaskSize = A4_PrintTasks.size() + A5_PrintTasks.size() + others_PrintTasks.size();
		if (totalTaskSize == 0) {
			logger.info("没有文件需要打印！");
			return;
		}
		
		isPrintingManualHandling = isPrintingManualHandling();
		
		isPaperSwitchManualHandling = isPaperSwitchManualHandling();
		
		boolean isPrintingGoOn = true;
		if (isPrintingManualHandling) {
			isPrintingGoOn = InteractionHandler.handleIsGoOn("总共有" + totalTaskSize + "张表格需要打印");
		}
		
		if(isPrintingGoOn) {
			try {
				if (printer == null) {
					printer = new JacobPrinter();
				}

				processPrintTasks(A4_PrintTasks);

				processPrintTasks(A5_PrintTasks);

				processPrintTasks(others_PrintTasks);		
			} finally {
				printer.close();
			}
			
			if (isPrintingManualHandling) {
				InteractionHandler.handleProgressCompleted("打印全部完成！");  
			}	
			
		}	
	}

	private void processPrintTasks(List<ExcelPrintTask> printTasks) {
		for (ExcelPrintTask task : printTasks) {
			completedTaskSize++;
			boolean skip = false;
			if (isPrintingManualHandling || isPaperSwitchManualHandling) {
				skip = InteractionHandler.handlePrintTaskConfirmation("即将打印" + task.getSheetType()
						+ (task.getSheetName() != null ? "(" + task.getSheetName() + ")" : "") + task.copies + "份，纸张大小："
						+ task.getPaperSize(), totalTaskSize, completedTaskSize);
			}
			if (!skip) {
				printer.printExcel(task.getFilePath(), task.getFromSheetIndex() + 1, task.getToSheetIndex() + 1,
						task.getCopies());
			} else {
				logger.info("跳过打印任务：" + task);
			}
		}
	}

	private boolean isPrintingManualHandling() {
		String printingHandling = Constant.propUtil.getStringValue("user.打印.默认处理方式", Constant.HANDLE_MANUAL);
		return Constant.HANDLE_MANUAL.equals(printingHandling);
	}
	
	private boolean isPaperSwitchManualHandling() {
		String handling = Constant.propUtil.getStringValue("user.打印.纸张切换方式", Constant.HANDLE_MANUAL);
		return Constant.HANDLE_MANUAL.equals(handling);
	}

	public static void createExcelPrintTask(SheetType sheetType, String filePath, int fromSheetIndex, int toSheetIndex)
			throws Exception {
		createExcelPrintTask(sheetType, filePath, fromSheetIndex, toSheetIndex, null);
	}
	
	public static void createExcelPrintTask(SheetType sheetType, String filePath, int sheetIndex, String sheetName)
			throws Exception {
		createExcelPrintTask(sheetType, filePath, sheetIndex, sheetIndex, sheetName);
	}
	
	public static void createExcelPrintTask(SheetType sheetType, String filePath, int sheetIndex)
			throws Exception {
		createExcelPrintTask(sheetType, filePath, sheetIndex, sheetIndex, null);
	}
	
	private static void createExcelPrintTask(SheetType sheetType, String filePath, int fromSheetIndex, int toSheetIndex, String sheetName)
			throws Exception {
		String paperSize = Constant.propUtil.getStringDisEmpty(getPagerSizeConfigKey(sheetType));
		int copies = Constant.propUtil.getIntValue(getCopiesConfigKey(sheetType), 1);
		ExcelPrintTask printTask = new ExcelPrintTask();
		printTask.setFilePath(filePath);
		printTask.setPaperSize(paperSize);
		printTask.setCopies(copies);
		printTask.setFromSheetIndex(fromSheetIndex);
		printTask.setToSheetIndex(toSheetIndex);
		printTask.setSheetType(sheetType);
		printTask.setSheetName(sheetName);

		logger.debug("创建打印任务：" + printTask);

		if (Constant.PAGE_SIZE_A4.equals(paperSize)) {
			A4_PrintTasks.add(printTask);
		} else if (Constant.PAGE_SIZE_A5.equals(paperSize)) {
			A5_PrintTasks.add(printTask);
		} else {
			others_PrintTasks.add(printTask);
		}
	}

	private static String getPagerSizeConfigKey(SheetType sheetType) {
		return "user.打印." + sheetType.name() + ".纸张大小";
	}

	private static String getCopiesConfigKey(SheetType sheetType) {
		return "user.打印." + sheetType.name() + ".份数";
	}

	private static class ExcelPrintTask {
		private String filePath;
		private String paperSize;
		private int copies;
		private int fromSheetIndex;
		private int toSheetIndex;
		private	SheetType sheetType;
		private String sheetName;

		/**
		 * @return the filePath
		 */
		public String getFilePath() {
			return filePath;
		}

		/**
		 * @param filePath
		 *            the filePath to set
		 */
		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		/**
		 * @return the paperSize
		 */
		public String getPaperSize() {
			return paperSize;
		}

		/**
		 * @param paperSize
		 *            the paperSize to set
		 */
		public void setPaperSize(String paperSize) {
			this.paperSize = paperSize;
		}

		/**
		 * @return the copies
		 */
		public int getCopies() {
			return copies;
		}

		/**
		 * @param copies
		 *            the copies to set
		 */
		public void setCopies(int copies) {
			this.copies = copies;
		}

		/**
		 * @return the fromSheetIndex
		 */
		public int getFromSheetIndex() {
			return fromSheetIndex;
		}

		/**
		 * @param fromSheetIndex
		 *            the fromSheetIndex to set
		 */
		public void setFromSheetIndex(int fromSheetIndex) {
			this.fromSheetIndex = fromSheetIndex;
		}

		/**
		 * @return the toSheetIndex
		 */
		public int getToSheetIndex() {
			return toSheetIndex;
		}

		/**
		 * @param toSheetIndex
		 *            the toSheetIndex to set
		 */
		public void setToSheetIndex(int toSheetIndex) {
			this.toSheetIndex = toSheetIndex;
		}

		/**
		 * @return the sheetType
		 */
		public SheetType getSheetType() {
			return sheetType;
		}

		/**
		 * @param sheetType the sheetType to set
		 */
		public void setSheetType(SheetType sheetType) {
			this.sheetType = sheetType;
		}

		/**
		 * @return the sheetName
		 */
		public String getSheetName() {
			return sheetName;
		}

		/**
		 * @param sheetName the sheetName to set
		 */
		public void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ExcelPrintTask [sheetType=" + sheetType + ", sheetName=" + sheetName + ", filePath=" + filePath
					+ ", paperSize=" + paperSize + ", copies=" + copies + ", fromSheetIndex=" + fromSheetIndex
					+ ", toSheetIndex=" + toSheetIndex + "]";
		}
		
	}
}
