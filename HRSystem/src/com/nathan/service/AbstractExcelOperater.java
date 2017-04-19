package com.nathan.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public abstract class AbstractExcelOperater implements ExcelOperater {

	private static Logger logger = Logger.getLogger(AbstractExcelOperater.class);
	
	private boolean backupFlag = false;
	
	protected void setBackupFlag(boolean flag) {
		this.backupFlag = flag;
	}

	@Override
	public void read(String filePath) {

		Workbook readwb = null;

		try {
			// ֱ�Ӵӱ����ļ�����Workbook
			InputStream instream = new FileInputStream(filePath);

			readwb = Workbook.getWorkbook(instream);

			readContent(readwb);

			instream.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readwb.close();
		}

	}

	protected void readContent(Workbook readwb) throws Exception {
		for (Sheet readsheet : readwb.getSheets()) {

			int rsColumns = readsheet.getColumns();
			int rsRows = readsheet.getRows();
			logger.debug("��ȡ���" + readsheet.getName());
			logger.debug("��������" + rsColumns + ", ��������" + rsRows);

			// �������е�Ԫ��

			for (int i = 0; i < rsRows; i++) {

				for (int j = 0; j < rsColumns; j++) {

					Cell cell = readsheet.getCell(j, i);

					logger.debug(cell.getContents() + " c:" + j + " r:" + i);

					logger.debug(cell.getType());
				}
			}
		}	
	}

	protected void logInfo(WritableSheet sheet) {
		int rsColumns = sheet.getColumns();
		int rsRows = sheet.getRows();
		logger.debug("�������" + sheet.getName());
		logger.debug("��������" + rsColumns + ", ��������" + rsRows);
	}
	
	/* (non-Javadoc)
	 * @see com.nathan.service.ExcelOperater#write(java.lang.String)
	 */
	@Override
	public void write(String outFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String inFile, String outFile) throws Exception {
		Workbook rwb = null;
		WritableWorkbook wwb = null;
		try {
			try {
				File inputFile = new File(outFile);
				rwb = Workbook.getWorkbook(inputFile);
			} catch(FileNotFoundException e) {
				rwb = Workbook.getWorkbook(new File(inFile));
			}
			
			File outputFille = new File(outFile);
			wwb = Workbook.createWorkbook(outputFille, rwb);// copy
			
			writeContent(wwb);
			
			if (backupFlag) {
				backup(inFile);
			}
			
			wwb.write();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwb.close();
			wwb.close();
		}	
	}
	
	protected void writeContent(WritableWorkbook wwb) throws Exception {
		
	}

	@Override
	public void modify(String destFile) throws Exception {	
        Workbook rwb = null;
		WritableWorkbook wwb = null;
		boolean needToDelete = false;
		try {
			rwb = Workbook.getWorkbook(new File(destFile));
			wwb = Workbook.createWorkbook(new File(destFile), rwb);
			
			modifyContent(wwb);
			
			if(wwb.getNumberOfSheets() == 0) {
				logger.info("������Ϊ�㣬ɾ���ļ���" + destFile);
				needToDelete = true;
				return;
			}
	
			wwb.write();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwb.close();
			wwb.close();
			if (needToDelete) {
				delete(destFile);
			}
		}	
	}
	
	protected void modifyContent(WritableWorkbook wwb) throws Exception {
		
	}

	@Override
	public boolean delete(String fillPath) {
		boolean flag = false;
//		boolean isFilePathValid = fillPath.matches(Constant.MATCHES);
//		logger.debug("�Ƿ�Ϊ��ȷ��·����:" + isFilePathValid);
//		if (!isFilePathValid) {
//			fillPath = fillPath.replaceAll("/", "\\\\");
//		}
		File file = new File(fillPath);
		if (file.isFile() && file.exists()) {
			flag = file.delete();
//			logger.debug(flag + "ɾ���ļ���" + fillPath);
		}
		return flag;
	}
	
	public void backup(String filePath) {	
		int byteread = 0; // ��ȡ���ֽ���  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(filePath);  
            
            String outputPath = filePath.replace("/in/", "/backup/") + ".backup." + System.currentTimeMillis();
            logger.info("�����ļ���" + outputPath);
            out = new FileOutputStream(outputPath);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
        } catch (FileNotFoundException e) {  
        	e.printStackTrace();
        } catch (IOException e) {  
        	e.printStackTrace();
        } finally { 
        	backupFlag = false;
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        } 
	}
	
}
