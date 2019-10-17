package com.yf.mesmid;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelOper {

	public static void readExcel(String strPath) {
	  try {
	   /**
	    * ������������,����Excel�����ͼƬ�Լ������������͵Ķ�ȡ
	    **/
	   InputStream is = new FileInputStream(strPath);
	   Workbook book = Workbook.getWorkbook(new File(strPath));
	   book.getNumberOfSheets();
	   // ��õ�һ�����������
	   Sheet sheet = book.getSheet(0);
	   int Rows = sheet.getRows();
	   int Cols = sheet.getColumns();
	   System.out.println("��ǰ�����������:" + sheet.getName());
	   System.out.println("������:" + Rows);
	   System.out.println("������:" + Cols);
	   for (int i = 0; i < Cols; ++i) {
	    for (int j = 0; j < Rows; ++j) {
	     // getCell(Col,Row)��õ�Ԫ���ֵ
	     System.out
	       .print((sheet.getCell(i, j)).getContents() + "\t");
	    }
	    System.out.print("\n");
	   }
	   // �õ���һ�е�һ�еĵ�Ԫ��
	   Cell cell1 = sheet.getCell(0, 0);
	   String result = cell1.getContents();
	   System.out.println(result);
	   book.close();
	  } catch (Exception e) {
	   System.out.println(e);
	  }
	}
	public static void createExcel() {
	  try {
	   // �������Excel�ļ�
	   WritableWorkbook book = Workbook.createWorkbook(new File(
	     "mnt/sdcard/test.xls"));
	   // ������Ϊ����һҳ���Ĺ�����,����0��ʾ���ǵ�һҳ
	   WritableSheet sheet1 = book.createSheet("��һҳ", 0);
	   WritableSheet sheet2 = book.createSheet("����ҳ", 2);
	   // ��Label����Ĺ��캯����,Ԫ��λ���ǵ�һ�е�һ��(0,0)�Լ���Ԫ������Ϊtest
	   Label label = new Label(0, 0, "test");
	   // ������õĵ�Ԫ����ӵ���������
	   sheet1.addCell(label);
	   /*
	    * ����һ���������ֵĵ�Ԫ��.����ʹ��Number��������·��,�������﷨����
	    */
	   jxl.write.Number number = new jxl.write.Number(1, 0, 555.12541);
	   sheet2.addCell(number);
	   // д�����ݲ��ر��ļ�
	   book.write();
	   book.close();
	  } catch (Exception e) {
	   System.out.println(e);
	  }
	}
	/**
	  * jxl��ʱ���ṩ�޸��Ѿ����ڵ����ݱ�,����ͨ��һ��С�취���ﵽ���Ŀ��,���ʺϴ������ݸ���! ������ͨ������ԭ�ļ������µ�.
	  * 
	  * @param filePath
	  */
	public static void updateExcel(String filePath) {
	  try {
	   Workbook rwb = Workbook.getWorkbook(new File(filePath));
	   WritableWorkbook wwb = Workbook.createWorkbook(new File(
	     "d:/new.xls"), rwb);// copy
	   WritableSheet ws = wwb.getSheet(0);
	   WritableCell wc = ws.getWritableCell(0, 0);
	   // �жϵ�Ԫ�������,������Ӧ��ת��
	   Label label = (Label) wc;
	   label.setString("The value has been modified");
	   wwb.write();
	   wwb.close();
	   rwb.close();
	  } catch (Exception e) {
	   e.printStackTrace();
	  }
	}
	public static void writeExcel(String filePath) {
	  try {
	   // ����������
	   WritableWorkbook wwb = Workbook.createWorkbook(new File(filePath));
	   // ����������
	   WritableSheet ws = wwb.createSheet("Sheet1", 0);
	   // ��ӱ�ǩ�ı�
	   // Random rnd = new Random((new Date()).getTime());
	   // int forNumber = rnd.nextInt(100);
	   // Label label = new Label(0, 0, "test");
	   // for (int i = 0; i < 3; i++) {
	   // ws.addCell(label);
	   // ws.addCell(new jxl.write.Number(rnd.nextInt(50), rnd
	   // .nextInt(50), rnd.nextInt(1000)));
	   // }
	   // ���ͼƬ(ע��˴�jxl��ʱֻ֧��png��ʽ��ͼƬ)
	   // 0,1�ֱ����x,y 2,5�����͸�ռ�ĵ�Ԫ����
	   ws.addImage(new WritableImage(5, 5, 2, 5, new File(
	     "mnt/sdcard/nb.png")));
	   wwb.write();
	   wwb.close();
	  } catch (Exception e) {
	   System.out.println(e.toString());
	  }
	}
}
