package com.yf.mesmid;

import java.util.ArrayList;
import java.util.List;

public class MesLsData {
     public static List<String> lcldm = new ArrayList<String>();
     public static String QUIT_BARCODE = "QUIT"; //退出条码
     public static String INPACKQJG_BARCODE = "ACCESSORIES";//进入包装前加工条码
     public static String QIANGZHI_BARCODE = "QIANGZHI";/*后门，不扫描料号强制进入SOP*/
     public static String YULANGSOP_BARCODE = "YULANGSOP";/*后门，扫描预览条码(YULANGSOP),可预览SOP5分钟*/
     public static String REWORK_BARCODE = "REWORK";/*进入返工模式*/
     public static boolean bYuLang = false;
     public static int YuLangTime = 3*60;
     public static int YuLangTimeCount = 0;
}
