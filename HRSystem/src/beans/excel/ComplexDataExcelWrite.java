package beans.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import jxl.Workbook;
import jxl.write.Boolean;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ComplexDataExcelWrite {
    public void createExcel(OutputStream os) throws WriteException,IOException {
        //����������
        WritableWorkbook workbook = Workbook.createWorkbook(os);
        //�����µ�һҳ
        WritableSheet sheet = workbook.createSheet("Second Sheet", 2);
        //����Ҫ��ʾ�ľ�������
        Label formate = new Label(0,0,"���ݸ�ʽ");
        sheet.addCell(formate);
        Label floats = new Label(1,0,"������");
        sheet.addCell(floats);
        Label integers = new Label(2,0,"����");
        sheet.addCell(integers);
        Label booleans = new Label(3,0,"������");
        sheet.addCell(booleans);
        Label dates = new Label(4,0,"���ڸ�ʽ");
        sheet.addCell(dates);
        
        Label example = new Label(0,1,"����ʾ��");
        sheet.addCell(example);
        //��������
        Number number = new Number(1,1,3.1415926535);
        sheet.addCell(number);
        //��������
        Number ints = new Number(2,1,15042699);
        sheet.addCell(ints);
        Boolean bools = new Boolean(3,1,true);
        sheet.addCell(bools);
        //����������
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        WritableCellFormat cf1 = new WritableCellFormat(DateFormats.FORMAT1);
        DateTime dt = new DateTime(4,1,date,cf1);
        sheet.addCell(dt);
        //�Ѵ���������д�뵽������У����ر������
        workbook.write();
        workbook.close();
        os.close();
        
        System.out.println("ComplexDataExcelWrite.createExcel()");
    }
    
    public void createExcel(String targetfile) throws WriteException,IOException {
        //����������
    	WritableWorkbook wwb = Workbook.createWorkbook(new File(targetfile));
        //�����µ�һҳ
        WritableSheet sheet = wwb.createSheet("Second Sheet", 2);
        //����Ҫ��ʾ�ľ�������
        Label formate = new Label(0,0,"���ݸ�ʽ");
        sheet.addCell(formate);
        Label floats = new Label(1,0,"������");
        sheet.addCell(floats);
        Label integers = new Label(2,0,"����");
        sheet.addCell(integers);
        Label booleans = new Label(3,0,"������");
        sheet.addCell(booleans);
        Label dates = new Label(4,0,"���ڸ�ʽ");
        sheet.addCell(dates);
        
        Label example = new Label(0,1,"����ʾ��");
        sheet.addCell(example);
        //��������
        Number number = new Number(1,1,3.1415926535);
        sheet.addCell(number);
        //��������
        Number ints = new Number(2,1,15042699);
        sheet.addCell(ints);
        Boolean bools = new Boolean(3,1,true);
        sheet.addCell(bools);
        //����������
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        WritableCellFormat cf1 = new WritableCellFormat(DateFormats.FORMAT1);
        DateTime dt = new DateTime(4,1,date,cf1);
        sheet.addCell(dt);
        //�Ѵ���������д�뵽������У����ر������
        wwb.write();
        wwb.close();
        //os.close();
        
        System.out.println("ComplexDataExcelWrite.createExcel()");
    }
}