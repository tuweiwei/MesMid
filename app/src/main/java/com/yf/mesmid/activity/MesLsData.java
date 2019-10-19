package com.yf.mesmid.activity;

import java.util.ArrayList;
import java.util.List;

public class MesLsData {
     public static List<String> lcldm = new ArrayList<String>();
     public static String QUIT_BARCODE = "QUIT"; //�˳�����
     public static String INPACKQJG_BARCODE = "ACCESSORIES";//�����װǰ�ӹ�����
     public static String QIANGZHI_BARCODE = "QIANGZHI";/*���ţ���ɨ���Ϻ�ǿ�ƽ���SOP*/
     public static String YULANGSOP_BARCODE = "YULANGSOP";/*���ţ�ɨ��Ԥ������(YULANGSOP),��Ԥ��SOP5����*/
     public static String REWORK_BARCODE = "REWORK";/*���뷵��ģʽ*/
     public static boolean bYuLang = false;
     public static int YuLangTime = 3*60;
     public static int YuLangTimeCount = 0;
}
