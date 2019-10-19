package com.yf.mesmid.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.yf.mesmid.activity.Material;
import com.yf.mesmid.activity.ScanResult;
import com.yf.mesmid.entity.IOQCInfo;
import com.yf.mesmid.entity.JobOrder;
import com.yf.mesmid.entity.Procedure;
import com.yf.mesmid.entity.SopInfo;
import com.yf.mesmid.entity.WXList;
import com.yf.mesmid.entity.XGInfo;
import com.yf.mesmid.service.UpdataInfo;

import android.database.SQLException;
import android.os.Environment;
import android.util.Log;

public class DatabaseOper {

	public static Connection con = null;
	private  static String TAG = "MESMid";
	public final static String FirstWIFI_MSG = "org.mes.firstwifi";
	public final static String RepeatWIFI_MSG = "org.mes.repeatwifi";
	public  static String ScanResult = null;
	public static String WIFI_CONN = "wifi";
	public static String WIRE_CONN = "wire";
	public static String ConnMode = WIFI_CONN;
	public  static String UserName = "sa";
	public  static String PassWord = "sql2008";
	public  static String Address = "192.168.11.100:1433";
	public  static String DatabaseName = "mes_yf";
	public  static String WifiSSID = "";
	public  static String WifiPWD = "";
	public static boolean bEnter = false;
	public static boolean bClInput = false; //�ж��Ƿ���Ҫ���������
	private static String ProductBarcode = null;
	private static String ClBarcode = "";  //��ʱ����Ҫ
	
	//�ж��������������Ƿ���ģʽ
	public final static int ZC_MODE= 0;//��������ģʽ
	public final static int FG_MODE= 1;//����ģʽ
	public final static int OQC_MODE= 2;//OQCģʽ,ѡ�������Զ�ģ�����ҹ���ѡ����ʱֻ��ʾOQC
	
	//OQCģʽ ��������
	public static String OQC_SJS = "";  //OQC�ͼ���
	public static String OQC_CJS = "";  //OQC�ͼ���
	public static String OQC_AQL = "";  //OQC�ͼ���
	//----
	
	public final static int KEYBOARD_MODE= 10;//С����ģ����ѯ��ʽ, ģ����ѯһ������
	public final static int KEYBOARD_DISPLAYGD= 11;//ģ����ѯ�����������Ϲ���, ֱ����ʾ�๤��
	
	public static int SC_MODE = ZC_MODE; //�ж�ѡ����ʱ�������������Ƿ���ģʽ��OQCģʽ
	public static int GX_MODE = ZC_MODE;//�ж�ѡ����ʱ��������������OQCģ����ѯģʽ
	
	public static int TotalScanNums = 0;
	public static int CurScanNums = 0;
	public static String User = "ADMIN";
	
	public static String DisplayInfo = "";//ɨ������󣬽����ʾ(��������,�����ڰ�װ�������)
	
	public final static int ScanSN = 1;//ɨ����ˮ��ģʽ
	public final static int ScanGD = 2;//ɨ�蹤��ģʽ
	public final static int ScanSMTAOI = 3;//ɨ��SMT AOIģʽ
	public final static int ScanKHLJCheck = 4;//�ͻ������У��ģʽ
	public static int ScanMode = ScanSN;
	
	public static String SNCode = "";
	
	private static List<String> LCLBarcode = new ArrayList<String>();
	
	//ɨ������û�
	public static int mCount = 0;
	public static final int mScanUserTime = 8*60*60; //10*60
	public static boolean mbScanUser = false;
	//ɨ�������Ϻ�
	public static int mBatchCodeCount = 0;
	public static final int mScanBatchCodeTime = 720;//10*720; 2Сʱ�Զ�����list����ɨ���Ϻ�
	public static boolean mbScanBatchCode = false;//�ж��Ƿ���Ҫ2Сʱ��ת
	
	public static boolean mbBL = false; 
	
	public static int mBarcodeNums = 0;//ɨ�������Ϻ���
	public static int mNeedScanBarcodeNums = 0;//���ɨ�������Ϻ���
	public static List<String> lBarcodes = new ArrayList<String>();//��¼��ɨ��������Ϻ�
	
	public static int mBatchNums = 0;//ɨ���Ϻ�������
	public static int mNeedScanBatchNums = 0;//���ɨ���Ϻ�������
	public static List<String> lBatchs = new ArrayList<String>();//��¼��ɨ����Ϻ�����
	
	public static int mljNums=1;
	
	//Ϊ���Ż�������ɨ��Ч��
	public static SopInfo mInfo = new SopInfo("", "", "", "", "", "", "", "", "", "", "");
   
	static public void AddCLBarcode(String CLBarcode)
	{
		LCLBarcode.add(CLBarcode);
	}
	
	static public void ResetCLBarcode()
	{
		LCLBarcode.clear();
	}
	
	static public List<String> GetListBarcode()
	{
		return LCLBarcode;
	}
	
	static public String GetCLBarcode()
	{
		String strCLBarcode = "";
		for(int i = 0; i < LCLBarcode.size(); i++)
		{
			strCLBarcode += LCLBarcode.get(i) + ";";
		}
		return strCLBarcode;
	}
	
	/**
	 *��ȡ���ݿ������ļ���Ϣ
	 * @param ConfigPath: �����ļ�·��
	 * @return sqlserver���ݿ��Ƿ����ӳɹ�
	*/
	static public void InitDatabaseConfig(String ConfigPath)
	{
		try {
			ConnMode = ConfigurationFile.getProfileString(ConfigPath, "Config",
					"connmode", "wifi");
			UserName = ConfigurationFile.getProfileString(ConfigPath, "Config",
					"username", "sa");
			PassWord = ConfigurationFile.getProfileString(ConfigPath, "Config",
					"password", "sql2008");
			Address = ConfigurationFile.getProfileString(ConfigPath, "Config",
					"address", "192.168.1.114:1433");
			DatabaseName = ConfigurationFile.getProfileString(ConfigPath, "Config",
					"databasename", "mes_yf");
			WifiSSID = ConfigurationFile.getProfileString(ConfigPath, "Config",
					"wifissid", "");
			WifiPWD = ConfigurationFile.getProfileString(ConfigPath, "Config",
					"wifipwd", "");
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG, "�����ļ������ڻ��������");
			//SendDataMessage(ERROR, "���ݿ����ý�������");
			//return;
		}
	}
	
	/**
	 * �ж����ݿ��Ƿ����ӳɹ�
	 * @return sqlserver���ݿ��Ƿ����ӳɹ�
	*/
	static public boolean Connect()
	{
		//Connection con = null;

		try { // ������������
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(
					"jdbc:jtds:sqlserver://" +
					Address +
					"/" +
					DatabaseName
					, UserName,
					PassWord);
		} catch (ClassNotFoundException e) {
			System.out.println("���������������");
			return false;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * �ӷ������˻�ȡAPP�汾��Ϣ
	 * @return �������汾��Ϣ
	  */
	public static UpdataInfo GetUpdateInfo(){
		UpdataInfo updatainfo = new UpdataInfo();
		try {
			String sql = "declare @Ver nvarchar(200) ,@Dsc nvarchar(200) ,@URL nvarchar(200) ";
			sql += "exec pb_App_Update  @Ver output,@Dsc output,@URL output ";
			sql += "select @Ver as version,@Dsc as description,@URL as apkurl";
	
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String strVersion = rs.getString("version");
				updatainfo.setVersion(strVersion);
				String strDsc = rs.getString("description");
				updatainfo.setDescription(strDsc);
				String strUrl= rs.getString("apkurl");
				updatainfo.setApkurl(strUrl);
				Log.i(TAG, "version: " + strVersion);
				Log.i(TAG, "description: " + strDsc);
				Log.i(TAG, "apkurl: " + strUrl);
				
			}else return null;
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return updatainfo;
	}
	
	/**
	 * @param PadName: ƽ����
	 * @return ���������б�
	  */
	static List<JobOrder> GetJobOrderOQC(String PadName) {
		List<JobOrder> ListJobOrder = new ArrayList<JobOrder>();

		try {
			String sql = "exec pb_moe_query " + "'" + PadName+ "',0,"+SC_MODE;
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor

			
			String strWldm="���ϴ���";
			if(DatabaseOper.SC_MODE==DatabaseOper.ZC_MODE) strWldm="���ϴ���";	
			else if(DatabaseOper.SC_MODE==DatabaseOper.FG_MODE) strWldm="�������ϴ���";
			else strWldm="���ϴ���";	
			
			JobOrder order = new JobOrder(0, "��������", "���鵥��", strWldm, "�ͼ���", "�����", "0", null);
			ListJobOrder.add(order);
			int iCount = 1;
			while (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				String strscrq = rs.getString("oqc_jlrq");
				String strzzdh= rs.getString("oqc_dh");
				String strwldm = rs.getString("oqc_wldm");
				String strsjs = rs.getString("oqc_sjs");//�ͼ���
				String strcjs = rs.getString("oqcbl_cjs2");//�����
				String strmoid = rs.getString("oqc_id");//AQL
				
				Log.i(TAG, "scrq: " + strscrq);
				Log.i(TAG, "zzdh: " + strzzdh);
				Log.i(TAG, "wldm: " + strwldm);
				Log.i(TAG, "sjs: " + strsjs);
				Log.i(TAG, "cjs: " + strcjs);
				Log.i(TAG, "moid: " + strmoid);
				order = new JobOrder(iCount, strscrq, strzzdh, strwldm
						, strsjs, strcjs, strmoid, null);
				ListJobOrder.add(order);
				iCount++;
				//Log.i(TAG, "sl: " + strSL);
				/*ScanResult result = new ScanResult(strxh, strmsg, strmoInfo, strSOPPath);
				Message msg = handle.obtainMessage();
				msg.arg1 = 101;
				msg.obj = result;
				handle.sendMessage(msg);*/
				//ExcelOper.readExcel("/mnt/sdcard/WC-BA12-012 CP6953-KA 1.1 ��װ��ҵָ�� .xls");
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return ListJobOrder;
	}
	
	/**
	 * @param PadName: ƽ����
	 * @return ���������б�
	  */
	static List<JobOrder> GetJobOrder(String PadName) {
		List<JobOrder> ListJobOrder = new ArrayList<JobOrder>();

		try {
			String sql = "exec pb_moe_query " + "'" + PadName+ "',"+SC_MODE;
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor

			
			String strWldm="���ϴ���";
			if(DatabaseOper.SC_MODE==DatabaseOper.ZC_MODE) strWldm="���ϴ���";	
			else if(DatabaseOper.SC_MODE==DatabaseOper.FG_MODE) strWldm="�������ϴ���";
			else strWldm="���ϴ���";	
			
			JobOrder order = new JobOrder(0, "��������", "�� �� ��", strWldm, "���ʹ���", "��������", "0", null);
			ListJobOrder.add(order);
			int iCount = 1;
			while (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				String strscrq = rs.getString("scrq");
				String strzzdh= rs.getString("zzdh");
				String strwldm = rs.getString("wldm");
				String strjxdm = rs.getString("jxdm");
				String strzzsl = rs.getString("zzsl");
				String strmoid = rs.getString("moid");
				
				Log.i(TAG, "scrq: " + strscrq);
				Log.i(TAG, "zzdh: " + strzzdh);
				Log.i(TAG, "wldm: " + strwldm);
				Log.i(TAG, "jxdm: " + strjxdm);
				Log.i(TAG, "zzsl: " + strzzsl);
				Log.i(TAG, "moid: " + strmoid);
				order = new JobOrder(iCount, strscrq, strzzdh, strwldm
						, strjxdm, strzzsl, strmoid, null);
				ListJobOrder.add(order);
				iCount++;
				//Log.i(TAG, "sl: " + strSL);
				/*ScanResult result = new ScanResult(strxh, strmsg, strmoInfo, strSOPPath);
				Message msg = handle.obtainMessage();
				msg.arg1 = 101;
				msg.obj = result;
				handle.sendMessage(msg);*/
				//ExcelOper.readExcel("/mnt/sdcard/WC-BA12-012 CP6953-KA 1.1 ��װ��ҵָ�� .xls");
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return ListJobOrder;
	}
	
	/**
	 * @param wldm: ���ϴ���
	 * @return ���������б�
	  */
	static List<Procedure> GetProcedure(String wldm, String PadName, String moeid) {
		List<Procedure> ListProcedure = new ArrayList<Procedure>();

		try {
			String sql = "exec pb_rud_query " + "'" + wldm+ "',"+SC_MODE+ ",'" + PadName+ "',"+moeid;
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			Procedure procedure = new Procedure(0, "��������" , null);
			ListProcedure.add(procedure);
			int iCount = 1;
			while (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				String strxh = rs.getString("xh");
				int ixh = Integer.valueOf(strxh).intValue();
				String strgwmc= rs.getString("gwmc");
				
				Log.i(TAG, "xh: " + strxh);
				Log.i(TAG, "gwmc: " + strgwmc);
				procedure = new Procedure(ixh, strgwmc, null);
				ListProcedure.add(procedure);
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return ListProcedure;
	}
	
	/**
	 * @param moid: �����ų̵�ID
	 * @param wldm: ���ϴ���
	 * @param xh: �������
	 * @param PadName: ƽ����
	 * @return ���������б�
	  */
	static List<Material> GetMaterial(String moid, String wldm, String xh, String PadName) {
		List<Material> ListMaterial = new ArrayList<Material>();

		try {
			String sql = "exec pb_boe_query " + moid + "," + "'" + wldm + "'" + "," + xh + 
					 "," + "'" + PadName + "'";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			Material material = new Material(0, "���ϴ���", "Ʒ�����", "�ܿز���", "�� �� ��", "�Ϲܿ�", "���ιܿ�");
			ListMaterial.add(material);
			int iCount = 1;
			while (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				String strcldm = rs.getString("cldm");
				String strpmgg= rs.getString("pmgg");
				String strgkcl = rs.getString("clgk");
				
				String strclgkztbz = rs.getString("clgkztbz");
				if(strclgkztbz.equals("1")) {
					mNeedScanBarcodeNums++;
					if(! strgkcl.equals("")) mBarcodeNums++;
				}
				//------------
				String strpc = rs.getString("pc");
				String strpcgkztbz = rs.getString("pcgkztbz");
				if(strpcgkztbz.equals("1")) {
					mNeedScanBatchNums++;
					if(! strpc.equals("")) mBatchNums++;
				}
				
				Log.i(TAG, "cldm: " + strcldm);
				Log.i(TAG, "pmgg: " + strpmgg);
				Log.i(TAG, "clgk: " + strgkcl);
				Log.i(TAG, "pc: " + strpc);
				Log.i(TAG, "BarcodeNums: " + mBarcodeNums);
				Log.i(TAG, "clgkztbz: " + strclgkztbz);
				Log.i(TAG, "NeedScanBarcodeNums: " + mNeedScanBarcodeNums);
				Log.i(TAG, "BatchNums: " + mBatchNums);
				Log.i(TAG, "pcgkztbz: " + strpcgkztbz);
				Log.i(TAG, "NeedScanBatchNums: " + mNeedScanBatchNums);
				if(!"".equals(strcldm)){
					material = new Material(iCount, strcldm, strpmgg, strgkcl, strpc, strclgkztbz, strpcgkztbz);
					ListMaterial.add(material);
					iCount++;
				}
				
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return ListMaterial;
	}
	
	/**
	 * @param moid: �����ų̵�ID
	 * @param wldm: ���ϴ���
	 * @param xh: �������
	 * @param PadName: ƽ����
	 * @return ���������б�
	  */
//	static List<Material> GetMaterial(String moid, String wldm, String xh, String PadName) {
//		List<Material> ListMaterial = new ArrayList<Material>();
//
//		try {
//			String sql = "exec pb_boe_query " + moid + "," + "'" + wldm + "'" + "," + xh + 
//					 "," + "'" + PadName + "'";
//	
//			Statement stmt = con.createStatement();//����Statement
//			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
//			Material material = new Material(0, "���ϴ���", "Ʒ�����", "�� �� ��", "״̬��־");
//			ListMaterial.add(material);
//			int iCount = 1;
//			while (rs.next()) {//<code>ResultSet</code>���ָ���һ��
//				String strcldm = rs.getString("cldm");
//				String strpmgg= rs.getString("pmgg");
//				String strpc = rs.getString("pc");
//				String strztbz = rs.getString("ztbz");
//				if(strztbz.equals("1")) {
//					mNeedScanBarcodeNums++;
//					if(! strpc.equals("")) mBarcodeNums++;
//				}
//				
//				Log.i(TAG, "cldm: " + strcldm);
//				Log.i(TAG, "pmgg: " + strpmgg);
//				Log.i(TAG, "pc: " + strpc);
//				Log.i(TAG, "BarcodeNums: " + mBarcodeNums);
//				Log.i(TAG, "ztbz: " + strztbz);
//				Log.i(TAG, "mNeedScanBarcodeNums: " + mNeedScanBarcodeNums);
//				if(!"".equals(strcldm)){
//					material = new Material(iCount, strcldm, strpmgg, strpc, strztbz);
//					ListMaterial.add(material);
//					iCount++;
//				}
//				
//			}
//			
//			rs.close();
//			stmt.close();
//		} catch (Exception e) {
//			System.out.println(e.getMessage().toString());
//			return null;
//		}
//		return ListMaterial;
//	}
	
	public static SopInfo GetInitial(String PadName,String gydm, String moeid, String gwdm)
	{
		SopInfo info=null;
		try {
			String sql = "";
			sql += "exec data_deal_op_count '" + PadName + "','" +  gydm + "'," + moeid +"," + gwdm;
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			if (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				String RetMsg = rs.getString("RetMsg");
				String RetInfo = rs.getString("RetInfo");
				String Ret = rs.getString("Ret");
				int iRet = Integer.valueOf(Ret).intValue();
				Log.i(TAG, "RetMsg: " + RetMsg);
				Log.i(TAG, "RetInfo: " + RetInfo);
				Log.i(TAG, "Ret: " + Ret);
				if(iRet == 100) {
					int Len = RetInfo.length();
					String outputnums=""; String acceptednums="";
					String badnums=""; String passrate=""; String totaloutputs=""; String acceptedrate="";
					int i = 0; int iSPos = 0;
					int iEPos = RetInfo.indexOf(";"); 
					//if(-1 != iPos) ;
					while(-1 != iEPos)
					{
						if(0 == i) outputnums = RetInfo.substring(iSPos, iEPos);
						else if(1 == i) acceptednums = RetInfo.substring(iSPos, iEPos);
						else if(2 == i) badnums = RetInfo.substring(iSPos, iEPos);
						else if(3 == i) passrate = RetInfo.substring(iSPos, iEPos);
						else if(4 == i) totaloutputs = RetInfo.substring(iSPos, iEPos);
						else if(5 == i) acceptedrate = RetInfo.substring(iSPos, iEPos);
						iSPos = iEPos + 1; 
						if(iSPos >= Len) break;
						iEPos = RetInfo.indexOf(";", iSPos);
						if(-1 == iEPos) iEPos = Len;
						i++;
					}
					 info = new SopInfo("", "", "", "", outputnums, acceptednums, 
							badnums, passrate, totaloutputs, acceptedrate, "");
				}
				
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			ScanResult = "ɨ��������ʧ��";
			info = null;
		}
		return info;
	}
	
	static boolean SaveDate(String chsl,String lpsl, String moeid, String gxdm)
	{
		boolean bRet= true;
		try {
			String sql = "";
			sql += "exec data_deal_op_save " + chsl + "," +  lpsl + "," + moeid +",'" + gxdm+"'";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			if (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				;
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			 bRet= false;
		}
		return bRet;
	}
	
	/**ɨ��������룬ȷ�����������ڴ˹�λ����
	 * @param moid: �����ų̵�ID
	 * @param PadName: ƽ����
	 * @param wldm: ���ϴ���
	 * @param xh: �������
	 * @param batchcode: ɨ������
	 * @return true: ɨ��ɹ� false: ɨ��ʧ��
	  */
	static boolean ScanBatchCode(String moid, String PadName, 
			String wldm, String xh, String batchcode) {
        boolean bRet = false;
		try {
			String sql = "declare @retval int ";
			sql += "declare @retmsg nvarchar(200) ";
			sql += "exec pb_std_save " + moid + "," + "'" + PadName + "'" + ",'" + wldm +
					"'," + xh + ",'" + batchcode + "',@retval output,@retmsg output ";
			sql += "select @retval as code,@retmsg as result";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			if (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				ScanResult = rs.getString("result");
				String strCode = rs.getString("code");
				int iCode = Integer.valueOf(strCode).intValue();
				Log.i(TAG, "result: " + ScanResult);
				Log.i(TAG, "code: " + strCode);
				if(iCode <= 0) bRet = false;
				else bRet = true;
			}else bRet = false;
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			ScanResult = "ɨ�������ʧ��";
			bRet = false;
		}
		return bRet;
	}
	
	/**
	 * @param moid: �����ų̵�ID
	 * @param PadName: ƽ����
	 * @param wldm: ���ϴ���
	 * @param xh: �������
	 * @param batchcode: ɨ������
	 * @return true: ɨ��ɹ� false: ɨ��ʧ��
	  */
	static boolean ScanBatchCodeEx(String moid, String PadName, 
			String wldm, String xh, String batchcode) {
        boolean bRet = false;
		try {
			String sql = "declare @retval int ";
			sql += "declare @retmsg nvarchar(200) ";
			sql += "exec pb_std_save " + moid + "," + "'" + PadName + "'" + ",'" + wldm +
					"'," + xh + ",'" + batchcode + "',@retval output,@retmsg output ";
			sql += "select @retval as code,@retmsg as result";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			if (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				ScanResult = rs.getString("result");
				String strCode = rs.getString("code");
				int iCode = Integer.valueOf(strCode).intValue();
				Log.i(TAG, "result: " + ScanResult);
				Log.i(TAG, "code: " + strCode);
				if(iCode <= 0) bRet = false;
				else bRet = true;
			}else bRet = false;
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			ScanResult = "ɨ��������ʧ��";
			bRet = false;
		}
		return bRet;
	}
	
	/**
	 * @param wldm: ��������
	 * @param xh: ��Ӧ���
	 * @return sopurl
	  */
	static String GetSopInfo(String wldm, String xh, String PadName) {
        String SopFile = null;

		try {
			String sql = "";
			sql += "declare @retfile nvarchar(200) ";
			sql += "declare @bz int ";
			sql += "exec pb_ffe_query " ;//���Լ�text
			sql += "'" + wldm + "'," + xh + ",@retfile output "+ ",@bz output"+ ",'" + PadName+ "' ";
			sql += "select @retfile as sopfile" + ",@bz as scanmode";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			if (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				SopFile = rs.getString("sopfile");
				String StrMode = rs.getString("scanmode");
				ScanMode = Integer.valueOf(StrMode).intValue();
				Log.i(TAG, "sopfile: " + SopFile);
				Log.i(TAG, "scanmode: " + StrMode);
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return SopFile;
	}
	
	/**
	 * @param PadName: ��Ӧƽ����
	 * @param barcode: ��Ӧɨ������
	 * @return ɨ����
	  */
	static com.yf.mesmid.activity.ScanResult ScanBarcode(String PadName, String barcode) {
		if(null == barcode) return null;
		ScanResult result = null;
		try {
			String productbarcode = null, blbarcode = "";
			String sql = "if not exists(select 1 from bld_det where bld_bldm='";
			sql += barcode;
			sql += "') " + "select '' as Barcode else select '";
			sql +=barcode;
			sql +="' as Barcode";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				String strBarcode = rs.getString("Barcode");
				if("".equals(strBarcode) && ! bClInput){
					ProductBarcode = barcode;
					blbarcode = "";
				}else if(! "".equals(strBarcode) && ! bClInput){
					blbarcode = barcode;
				}
			}
			if(4 == barcode.length()) blbarcode = barcode;
			String clbarcode = "";
			if(bClInput) {
				clbarcode = barcode;
				Log.i(TAG, "��������: " + clbarcode);
			}
			if(mbBL) {
				blbarcode = barcode;
				Log.i(TAG, "��������: " + blbarcode);
			}
			sql = "";
			//sql=sql+"truncate table tem_mstr \n";
			//sql=sql+"truncate table op100_det \n";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetInfo nvarchar(500) ";
			sql=sql+"declare @RetFile nvarchar(500) ";
			sql=sql+"exec data_deal_op_yf '";//���Լ���test��׺
			sql=sql + PadName;
			sql=sql +	"',";
			sql=sql+ "'" + ProductBarcode+ "'"; 
			sql=sql+",";
			sql=sql+"'"+blbarcode+"'";
			sql=sql+",";
			sql=sql+"'"+clbarcode+"'";
			sql=sql+",'";
			sql=sql+User;
			sql=sql+"',@Ret output ,@RetMsg output,@RetInfo output,@RetFile output ";
			sql=sql+"select @Ret as xh ,@RetMsg as oprmsg ,@RetInfo as moinfo,@RetFile as SopFile ";
	
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				String strxh = rs.getString("xh");
				int ixh = Integer.valueOf(strxh).intValue();
				if(102 ==ixh ){
					bClInput = true;
					DisplayInfo ="\n����: "+barcode+"";//��Ϣ��ʾ
				}/*else if(ixh >= 100 && 102 != ixh 
						&& bClInput == true && CurScanNums == TotalScanNums){
					bClInput = false;
					TotalScanNums = 0;
					CurScanNums = 0;
					LCLBarcode.clear();
				}*/else if(ixh == 100 && bClInput == true && CurScanNums < TotalScanNums){
					CurScanNums++;
					DisplayInfo +="\n���к�"+CurScanNums+": "+barcode+"";//��Ϣ��ʾ
					if(102 != ixh && CurScanNums == TotalScanNums){
						bClInput = false;
						TotalScanNums = 0;
						CurScanNums = 0;
						LCLBarcode.clear();
					}
				}
				String oprmsg= rs.getString("oprmsg"); 
				Log.i(TAG, "oprmsg: " + oprmsg);
				if(null != oprmsg){
					String strmsg = "";
					int index = oprmsg.indexOf(";");
					if(-1 != index && bClInput) {
						strmsg = oprmsg.substring(0, index);
						TotalScanNums = Integer.valueOf(oprmsg.substring(index+1)).intValue();
					}
					else strmsg = oprmsg;
					String strSOPPath = rs.getString("SopFile");
					String strmoInfo = rs.getString("moinfo");
					
					Log.i(TAG, "xh: " + strxh);
					Log.i(TAG, "info: " + strmsg);
					Log.i(TAG, "filename: " + strSOPPath);
					Log.i(TAG, "moinfo: " + strmoInfo);
					result = new ScanResult(strxh, strmsg, strmoInfo, strSOPPath);
					//if(102 != ixh && CurScanNums == TotalScanNums){//��Ϣ��ʾ
					//	DisplayInfo="";
					//}
				}
				
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			//System.out.println(e.getMessage().toString());
			return null;
		}
		return result;
	}
	
	/**
	 * @param PadName: ��Ӧƽ����
	 * @param barcode: ��Ӧɨ������
	 * @return ɨ����
	  */
	static ScanResult ScanBarcodeEx(String PadName, String barcode, String rework) {
		if(null == barcode) return null;
		ScanResult result = null;
		try {
			String productbarcode = null, blbarcode = "";
			String sql = "if not exists(select 1 from bld_det where bld_bldm='";
			sql += barcode;
			sql += "') " + "select '' as Barcode else select '";
			sql +=barcode;
			sql +="' as Barcode";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				String strBarcode = rs.getString("Barcode");
				if("".equals(strBarcode) && ! bClInput){
					ProductBarcode = barcode;
					blbarcode = "";
				}else if(! "".equals(strBarcode) && ! bClInput){
					blbarcode = barcode;
				}
			}
			if(4 == barcode.length()) blbarcode = barcode;
			String clbarcode = "";
			if(bClInput) {
				clbarcode = barcode;
				Log.i(TAG, "��������: " + clbarcode);
			}
			if(mbBL) {
				blbarcode = barcode;
				Log.i(TAG, "��������: " + blbarcode);
			}
			sql = "";
			//sql=sql+"truncate table tem_mstr \n";
			//sql=sql+"truncate table op100_det \n";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetInfo nvarchar(500) ";
			sql=sql+"declare @RetFile nvarchar(500) ";
			//sql=sql+"exec data_deal_op_yf_pc_test '";//���Լ���test��׺
			sql=sql+"exec data_deal_op_yf_test '";//���Լ���test��׺
			sql=sql + PadName;
			sql=sql +	"',";
			sql=sql+ "'" + ProductBarcode+ "'"; 
			sql=sql+",";
			sql=sql+"'"+blbarcode+"'";
			sql=sql+",";
			sql=sql+"'"+clbarcode+"'";
			sql=sql+",'";
			sql=sql+User+"',";
			sql=sql+rework;
			sql=sql+",@Ret output ,@RetMsg output,@RetInfo output,@RetFile output,"+SC_MODE;
			sql=sql+" select @Ret as xh ,@RetMsg as oprmsg ,@RetInfo as moinfo,@RetFile as SopFile ";
			//��������ɨ����䵽����sqllog.txt��
			ConfigurationFile.SqlLog(Environment.getExternalStorageDirectory().getPath()+"/sqllog.txt", sql);
	
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				String strxh = rs.getString("xh");
				int ixh = Integer.valueOf(strxh).intValue();
				if(102 ==ixh ){
					bClInput = true;
					DisplayInfo ="\n����: "+barcode+"";//��Ϣ��ʾ
				}/*else if(ixh >= 100 && 102 != ixh 
						&& bClInput == true && CurScanNums == TotalScanNums){
					bClInput = false;
					TotalScanNums = 0;
					CurScanNums = 0;
					LCLBarcode.clear();
				}*/
				else if(ixh == 100 && bClInput == true && CurScanNums < TotalScanNums){
					CurScanNums++;
					DisplayInfo +="\n���к�"+CurScanNums+": "+barcode+"";//��Ϣ��ʾ
					if(102 != ixh && CurScanNums == TotalScanNums /*&& DatabaseOper.ScanMode != DatabaseOper.ScanKHLJCheck*/){
						bClInput = false;
						TotalScanNums = 0;
						CurScanNums = 0;
						LCLBarcode.clear();
					}
				}
				else{
					/*����ӹ�У��ʧ�������¿�ʼɨ��*/
					if(DatabaseOper.ScanMode == DatabaseOper.ScanKHLJCheck) {
						bClInput = false;
						TotalScanNums = 0;
						CurScanNums = 0;
						LCLBarcode.clear();
					}
				}
				String oprmsg= rs.getString("oprmsg"); 
				Log.i(TAG, "oprmsg: " + oprmsg);
				if(null != oprmsg){
					String strmsg = "";
					int index = oprmsg.indexOf(";");
					if(-1 != index && bClInput) {
						strmsg = oprmsg.substring(0, index);
						TotalScanNums = Integer.valueOf(oprmsg.substring(index+1)).intValue();
						/*����ӹ�У��ģʽ Ĭ��CurScanNums+1*/
						//if(DatabaseOper.ScanMode == DatabaseOper.ScanKHLJCheck) CurScanNums++;
					}
					else strmsg = oprmsg;
					String strSOPPath = rs.getString("SopFile");
					String strmoInfo = rs.getString("moinfo");
					
					Log.i(TAG, "xh: " + strxh);
					Log.i(TAG, "info: " + strmsg);
					Log.i(TAG, "filename: " + strSOPPath);
					Log.i(TAG, "moinfo: " + strmoInfo);
					result = new ScanResult(strxh, strmsg, strmoInfo, strSOPPath);
					//if(102 != ixh && CurScanNums == TotalScanNums){//��Ϣ��ʾ
					//	DisplayInfo="";
					//}
				}
				
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			//System.out.println(e.getMessage().toString());
			return null;
		}
		return result;
	}
	
//	static ScanResult ScanBarcodeEx(String PadName, String barcode, String rework) {
//		if(null == barcode) return null;
//		ScanResult result = null;
//		try {
//			String productbarcode = null, blbarcode = "";
//			String sql = "if not exists(select 1 from bld_det where bld_bldm='";
//			sql += barcode;
//			sql += "') " + "select '' as Barcode else select '";
//			sql +=barcode;
//			sql +="' as Barcode";
//			Statement stmt = con.createStatement();
//			ResultSet rs = stmt.executeQuery(sql);
//			if(rs.next()){
//				String strBarcode = rs.getString("Barcode");
//				if("".equals(strBarcode) && ! bClInput){
//					ProductBarcode = barcode;
//					blbarcode = "";
//				}else if(! "".equals(strBarcode) && ! bClInput){
//					blbarcode = barcode;
//				}
//			}
//			if(4 == barcode.length()) blbarcode = barcode;
//			String clbarcode = "";
//			if(bClInput) {
//				clbarcode = barcode;
//				Log.i(TAG, "��������: " + clbarcode);
//			}
//			if(mbBL) {
//				blbarcode = barcode;
//				Log.i(TAG, "��������: " + blbarcode);
//			}
//			sql = "";
//			//sql=sql+"truncate table tem_mstr \n";
//			//sql=sql+"truncate table op100_det \n";
//			sql=sql+"declare @Ret int ";
//			sql=sql+"declare @RetMsg nvarchar(200) ";
//			sql=sql+"declare @RetInfo nvarchar(500) ";
//			sql=sql+"declare @RetFile nvarchar(500) ";
//			sql=sql+"exec data_deal_op_yf_test '";//���Լ���test��׺
//			sql=sql + PadName;
//			sql=sql +	"',";
//			sql=sql+ "'" + ProductBarcode+ "'"; 
//			sql=sql+",";
//			sql=sql+"'"+blbarcode+"'";
//			sql=sql+",";
//			sql=sql+"'"+clbarcode+"'";
//			sql=sql+",'";
//			sql=sql+User+"',";
//			sql=sql+rework;
//			sql=sql+",@Ret output ,@RetMsg output,@RetInfo output,@RetFile output,"+SC_MODE;
//			sql=sql+" select @Ret as xh ,@RetMsg as oprmsg ,@RetInfo as moinfo,@RetFile as SopFile ";
//			//��������ɨ����䵽����sqllog.txt��
//			ConfigurationFile.SqlLog(Environment.getExternalStorageDirectory().getPath()+"/sqllog.txt", sql);
//	
//			stmt = con.createStatement();
//			rs = stmt.executeQuery(sql);
//
//			if (rs.next()) {
//				String strxh = rs.getString("xh");
//				int ixh = Integer.valueOf(strxh).intValue();
//				if(102 ==ixh ){
//					bClInput = true;
//					DisplayInfo ="\n����: "+barcode+"";//��Ϣ��ʾ
//				}/*else if(ixh >= 100 && 102 != ixh 
//						&& bClInput == true && CurScanNums == TotalScanNums){
//					bClInput = false;
//					TotalScanNums = 0;
//					CurScanNums = 0;
//					LCLBarcode.clear();
//				}*/
//				else if(ixh == 100 && bClInput == true && CurScanNums < TotalScanNums){
//					CurScanNums++;
//					DisplayInfo +="\n���к�"+CurScanNums+": "+barcode+"";//��Ϣ��ʾ
//					if(102 != ixh && CurScanNums == TotalScanNums /*&& DatabaseOper.ScanMode != DatabaseOper.ScanKHLJCheck*/){
//						bClInput = false;
//						TotalScanNums = 0;
//						CurScanNums = 0;
//						LCLBarcode.clear();
//					}
//				}
//				else{
//					/*����ӹ�У��ʧ�������¿�ʼɨ��*/
//					if(DatabaseOper.ScanMode == DatabaseOper.ScanKHLJCheck) {
//						bClInput = false;
//						TotalScanNums = 0;
//						CurScanNums = 0;
//						LCLBarcode.clear();
//					}
//				}
//				String oprmsg= rs.getString("oprmsg"); 
//				Log.i(TAG, "oprmsg: " + oprmsg);
//				if(null != oprmsg){
//					String strmsg = "";
//					int index = oprmsg.indexOf(";");
//					if(-1 != index && bClInput) {
//						strmsg = oprmsg.substring(0, index);
//						TotalScanNums = Integer.valueOf(oprmsg.substring(index+1)).intValue();
//						/*����ӹ�У��ģʽ Ĭ��CurScanNums+1*/
//						//if(DatabaseOper.ScanMode == DatabaseOper.ScanKHLJCheck) CurScanNums++;
//					}
//					else strmsg = oprmsg;
//					String strSOPPath = rs.getString("SopFile");
//					String strmoInfo = rs.getString("moinfo");
//					
//					Log.i(TAG, "xh: " + strxh);
//					Log.i(TAG, "info: " + strmsg);
//					Log.i(TAG, "filename: " + strSOPPath);
//					Log.i(TAG, "moinfo: " + strmoInfo);
//					result = new ScanResult(strxh, strmsg, strmoInfo, strSOPPath);
//					//if(102 != ixh && CurScanNums == TotalScanNums){//��Ϣ��ʾ
//					//	DisplayInfo="";
//					//}
//				}
//				
//			}
//			
//			rs.close();
//			stmt.close();
//		} catch (Exception e) {
//			//System.out.println(e.getMessage().toString());
//			return null;
//		}
//		return result;
//	}
	
	/**
	 * @param PadName: ��Ӧƽ����
	 * @param gd: ��Ӧ������
	 * @param jgtm: ��Ӧ�������,�� ok, ng
	 * @param number: ��Ӧ������
	 * @param bltm: ��������
	 * @return ɨ����
	  */
	static ScanResult ScanSN(String PadName, String gd, String jgtm, String number, String bltm)
	{
		if(null == gd) return null;
		ScanResult result = null;
		try {
			if("".equals(bltm)) SNCode = jgtm+number;
			String blbarcode = bltm; String clbarcode = ""; 
			String sql = "";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetInfo nvarchar(500) ";
			sql=sql+"declare @RetFile nvarchar(500) ";
			sql=sql+"exec data_deal_op_Stack '";//���Լ�test
			sql=sql + PadName;
			sql=sql +	"',";
			sql=sql+ "'" + gd+ "'"; 
			sql=sql+",'"+jgtm+"'";
			sql=sql+",'"+blbarcode+"'";
			sql=sql+",";
			sql=sql+"'"+clbarcode+"'";
			sql=sql+"," +number+",''";
			sql=sql +	",'";
			sql=sql+User;
			sql=sql+"',@Ret output ,@RetMsg output,@RetInfo output,@RetFile output ";
			sql=sql+"select @Ret as xh ,@RetMsg as oprmsg ,@RetInfo as moinfo,@RetFile as SopFile ";
	
			Statement stmt = con.createStatement();
			ResultSet  rs = stmt.executeQuery(sql);

			if (rs.next()) {
				String strxh = rs.getString("xh");
				int ixh = Integer.valueOf(strxh).intValue();
				String oprmsg= rs.getString("oprmsg"); 
				String strSOPPath = rs.getString("SopFile");
				String strmoInfo = rs.getString("moinfo");
				Log.i(TAG, "xh: " + strxh);
				Log.i(TAG, "info: " + oprmsg);
				Log.i(TAG, "filename: " + strSOPPath);
				Log.i(TAG, "moinfo: " + strmoInfo);
				result = new ScanResult(strxh, oprmsg, strmoInfo, strSOPPath);
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return result;
	}
	
	/**
	 * @param PadName: ��Ӧƽ����
	 * @param barcode: ��Ӧɨ������
	 * @return ɨ����
	  */
	static ScanResult ScanSMTAOI(String PadName, String barcode) {
		if(null == barcode) return null;
		ScanResult result = null;
		try {
			String productbarcode = null, blbarcode = "", clbarcode="", jgbarcode="";
			if(barcode.equals("NG")){
				productbarcode = ProductBarcode;
				jgbarcode = barcode;
			}else{
				ProductBarcode = barcode;
			}
			String sql = "";
			//sql=sql+"truncate table tem_mstr \n";
			//sql=sql+"truncate table op100_det \n";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetMOInfo nvarchar(500) ";
			sql=sql+"declare @RetSum nvarchar(500) ";
			sql=sql+"declare @RetpbSum nvarchar(20) ";
			sql=sql+"exec data_deal_op_smt '";//���Լ���test��׺
			sql=sql + PadName;
			sql=sql +	"',";
			sql=sql+ "'" + ProductBarcode+ "'"; 
			sql=sql+",";
			sql=sql+"'"+jgbarcode+"'";
			sql=sql+",";
			sql=sql+"'"+blbarcode+"'";
			sql=sql+",";
			sql=sql+"'"+clbarcode+"',1,''";
			sql=sql+",'";
			sql=sql+User;
			sql=sql+"',@Ret output ,@RetMsg output,@RetMOInfo output,@RetSum output,@RetpbSum output ";
			sql=sql+"select @Ret as xh ,@RetMsg as oprmsg ,@RetMOInfo as moinfo,@RetSum as SopFile,@RetpbSum as pbSum ";
	
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				String strxh = rs.getString("xh");
				int ixh = Integer.valueOf(strxh).intValue();
				String oprmsg= rs.getString("oprmsg"); 
				String strSOPPath = rs.getString("SopFile");
				String strmoInfo = rs.getString("moinfo");
				String strpbSum  = rs.getString("pbSum");
				if(strxh.equals("100") && null != strpbSum && strpbSum.length() > 0){
					mljNums = Integer.valueOf(strpbSum).intValue();
				}
				
				Log.i(TAG, "xh: " + strxh);
				Log.i(TAG, "info: " + oprmsg);
				Log.i(TAG, "filename: " + strSOPPath);
				Log.i(TAG, "moinfo: " + strmoInfo);
				Log.i(TAG, "pbSum: " + strpbSum);
				result = new ScanResult(strxh, oprmsg, strmoInfo, strSOPPath);			
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			//System.out.println(e.getMessage().toString());
			return null;
		}
		return result;
	}
	
	/**
	 * @param PadName: ��Ӧƽ����
	 * @param jgtm: ��Ӧ�������,�� QJGASSYOK1, QJGASSYNG1��15910275;15910275;15920522
	 * @param number: ��Ӧ����
	 * @return ɨ����
	  */
	static ScanResult ScanQJG(String PadName, String jgtm, String number)
	{
		if(null == PadName || null == jgtm || null == number) return null;
		ScanResult result = null;
		try {
			String blbarcode = ""; String clbarcode = number;
			String sql = "";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetInfo nvarchar(500) ";
			sql=sql+"declare @RetFile nvarchar(500) ";
			sql=sql+"exec data_deal_op_yf_qian '";//���Լ���test��׺
			sql=sql + PadName;
			sql=sql +	"',";
			sql=sql+ "'" + jgtm+ "'"; 
			sql=sql+",";
			sql=sql+"'"+blbarcode+"'";
			sql=sql+",";
			sql=sql+"'"+clbarcode+"'";
			sql=sql+",'";
			sql=sql+User;
			sql=sql+"',@Ret output ,@RetMsg output,@RetInfo output,@RetFile output ";
			sql=sql+"select @Ret as xh ,@RetMsg as oprmsg ,@RetInfo as moinfo,@RetFile as SopFile ";
	
			Statement stmt = con.createStatement();
			ResultSet  rs = stmt.executeQuery(sql);

			if (rs.next()) {
				String strxh = rs.getString("xh");
				int ixh = Integer.valueOf(strxh).intValue();
				String oprmsg= rs.getString("oprmsg"); 
				String strSOPPath = rs.getString("SopFile");
				String strmoInfo = rs.getString("moinfo");
				Log.i(TAG, "xh: " + strxh);
				Log.i(TAG, "info: " + oprmsg);
				Log.i(TAG, "filename: " + strSOPPath);
				Log.i(TAG, "moinfo: " + strmoInfo);
				result = new ScanResult(strxh, oprmsg, strmoInfo, strSOPPath);
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return result;
	}
	
	/**
	 * @param PadName: ��Ӧƽ����
	 * @param barcode: ��Ӧɨ������
	 * @return ɨ����
	  */
	static ScanResult ScanBarcodeOQC(String PadName, String barcode, String zzdh) {
		if(null == barcode) return null;
		ScanResult result = null;
		try {
			String productbarcode = null, blbarcode = "";
			String sql = "if not exists(select 1 from bld_det where bld_bldm='";
			sql += barcode;
			sql += "') " + "select '' as Barcode else select '";
			sql +=barcode;
			sql +="' as Barcode";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				String strBarcode = rs.getString("Barcode");
				if("".equals(strBarcode) && ! bClInput){
					ProductBarcode = barcode;
					blbarcode = "";
				}else if(! "".equals(strBarcode) && ! bClInput){
					blbarcode = barcode;
				}
			}
			if(4 == barcode.length()) blbarcode = barcode;
			String clbarcode = "";
			if(mbBL) {
				blbarcode = barcode;
				Log.i(TAG, "��������: " + blbarcode);
			}
			sql = "";
			//sql=sql+"truncate table tem_mstr \n";
			//sql=sql+"truncate table op100_det \n";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetInfo nvarchar(500) ";
			sql=sql+"declare @RetFile nvarchar(500) ";
			sql=sql+"exec data_deal_op_oqc '";//���Լ���test��׺
			sql=sql + PadName;
			sql=sql +	"',";
			sql=sql+ "'" + ProductBarcode+ "'"; 
			sql=sql+",";
			sql=sql+"'"+blbarcode+"'";
			sql=sql+",'";
			sql=sql+User+"',";
			sql=sql+"'"+zzdh+"'";
			sql=sql+",@Ret output ,@RetMsg output,@RetInfo output,@RetFile output";
			sql=sql+" select @Ret as xh ,@RetMsg as oprmsg ,@RetInfo as moinfo,@RetFile as SopFile ";
	
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				String strxh = rs.getString("xh");
				int ixh = Integer.valueOf(strxh).intValue();
				String oprmsg= rs.getString("oprmsg"); 
				String strSOPPath = rs.getString("SopFile");
				String strmoInfo = rs.getString("moinfo");
				Log.i(TAG, "xh: " + strxh);
				Log.i(TAG, "info: " + oprmsg);
				Log.i(TAG, "filename: " + strSOPPath);
				Log.i(TAG, "moinfo: " + strmoInfo);
				result = new ScanResult(strxh, oprmsg, strmoInfo, strSOPPath);	
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			//System.out.println(e.getMessage().toString());
			return null;
		}
		return result;
	}
	
	/**
	 * @param code: ɨ������
	 * @return wldm: ���ؽ��: ���ϴ���
	  */
	static String SpGetWldm(String code)
	{
		String wldm = null;

		try {
			String sql = "";
			sql=sql+"declare @wldm nvarchar(50) ";
			sql += "exec dbo.spGetWldmBypc " + "'" + code + "'," + " @wldm output ";
			sql += "select @wldm as wldm";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			if (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				wldm = rs.getString("wldm");
				Log.i(TAG, "wldm: " + wldm);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
		}
		return wldm;
	}
	
	/**
	 * @param barcode: ɨ������
	 * @param sign: ��־
	 * @param state: ״̬
	 * @return xh: ���ؽ��: 100Ϊɨ��ɹ�, ����Ϊɨ��ʧ��
	  */
	public static int ScanSMTInfo(String barcode, String sign, String state, String kbstate) {
        int xh = 12;

		try {
			String sql = "";
			sql=sql+"declare @Ret int ";
			sql += "declare @RetMsg nvarchar(200) ";
			sql += "exec data_deal_op_stm " + "'" + barcode + "'," + state +"," + kbstate + ","  + "'" + User + "'," + sign + ",@Ret output ,@RetMsg output ";
			sql += "select @Ret as xh ,@RetMsg as oprmsg";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			if (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				String strxh = rs.getString("xh");
				xh = Integer.valueOf(strxh).intValue();
				String oprmsg = rs.getString("oprmsg");
				ScanResult = oprmsg;
				Log.i(TAG, "xh: " + strxh);
				Log.i(TAG, "oprmsg: " + oprmsg);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			ScanResult = "ɨ������ʧ��";
		}
		return xh;
	}
	
	/**
	 * @param barcode: ɨ������
	 * @return List<XGInfo>: ���ؽ��: ������Ϣ��
	  */
	public static List<XGInfo> CXXGInfo(String barcode) {
        List<XGInfo> list = new ArrayList<XGInfo>();
 
		try {
			String sql = "";
			sql=sql+"exec pb_tid_query " + "'" + barcode + "'";
			//sql += "select tid_tm as tm, convert(nvarchar(10),tid_time,120) as jlrq, tid_ztbz as ztbz, tid_jlry as jlry, tid_id as tid ";
			//sql += "from tid_det where tid_tm=" + barcode + " order by tid_time";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			int iCount = 0;
			list.add(new XGInfo(0, "tm", "jlrq", "ztbz", "jlry", "tid"));
			while (rs.next()) {
				String tm = rs.getString("tm");
				String jlrq = rs.getString("jlrq");
				String ztbz = rs.getString("ztbz");
				String jlry = rs.getString("jlry");
				String tid = rs.getString("tid");
				Log.i(TAG, "tm: " + tm);
				Log.i(TAG, "jlrq: " + jlrq);
				Log.i(TAG, "ztbz: " + ztbz);
				Log.i(TAG, "jlry: " + jlry);
				Log.i(TAG, "tid: " + tid);
				iCount++;
			    XGInfo info = new XGInfo(iCount, tm, jlrq, ztbz, jlry, tid);
			    list.add(info);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return list;
	}
	
	/**
	 * @param barcode: ɨ������
	 * @return List<GWInfo>: ���ؽ��: ������Ϣ��
	  */
	static List<GWInfo> CXGWInfo(String barcode) {
        List<GWInfo> list = new ArrayList<GWInfo>();
 
		try {
			String sql = "";
			sql=sql+"exec pb_gqd_query " + "'" + barcode + "'";
			//sql += "select tid_tm as tm, convert(nvarchar(10),tid_time,120) as jlrq, tid_ztbz as ztbz, tid_jlry as jlry, tid_id as tid ";
			//sql += "from tid_det where tid_tm=" + barcode + " order by tid_time";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			int iCount = 0;
			list.add(new GWInfo(0, "tm", "qxrq", "zlbz", "kbbz", "jlry", "gid"));
			while (rs.next()) {
				String tm = rs.getString("tm");
				String jlrq = rs.getString("jlrq");
				String zlbz = rs.getString("zlbz");
				String kbbz = rs.getString("kbbz");
				String jlry = rs.getString("jlry");
				String gid = rs.getString("gid");
				Log.i(TAG, "tm: " + tm);
				Log.i(TAG, "jlrq: " + jlrq);
				Log.i(TAG, "zlbz: " + zlbz);
				Log.i(TAG, "kbbz: " + kbbz);
				Log.i(TAG, "jlry: " + jlry);
				Log.i(TAG, "gid: " + gid);
				iCount++;
				GWInfo info = new GWInfo(iCount, tm, jlrq, zlbz, kbbz, jlry, gid);
			    list.add(info);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return list;
	}
	
	/**
	 * @param barcode: ɨ������
	 * @return List<WXList>: ���ؽ��: ά����Ϣ��
	  */
	static List<WXList> ScanWXBarcode(String barcode) {
        List<WXList> list = new ArrayList<WXList>();
 
		try {
			String sql = "";
			sql=sql+"exec sp_Getbldm " + "'" + barcode + "'";
			//sql += "select tid_tm as tm, convert(nvarchar(10),tid_time,120) as jlrq, tid_ztbz as ztbz, tid_jlry as jlry, tid_id as tid ";
			//sql += "from tid_det where tid_tm=" + barcode + " order by tid_time";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			int iCount = 0;
			list.add(new WXList(0, "mo", "zwxh", "zwxx", "bltm", "blxx", "yytm", "wxyy", 
					"cstm", "wxcs", "rytm", "ryxx", "wxsj"));
			while (rs.next()) {
				String mo = rs.getString("mo");
				String zwxh = rs.getString("zwxh");
				String zwxx = rs.getString("zwxx");
				String bltm = rs.getString("bltm");
				String blxx = rs.getString("blxx");
				String yytm = rs.getString("yytm");
				String wxyy = rs.getString("yyxx");
				String cstm = rs.getString("cstm");
				String wxcs = rs.getString("csxx");
				String rytm = rs.getString("wxry");
				String ryxx = rs.getString("wxxm");
				String wxsj = rs.getString("wxrq");
				Log.i(TAG, "mo: " + mo);
				Log.i(TAG, "zwxh: " + zwxh);
				Log.i(TAG, "zwxx: " + zwxx);
				Log.i(TAG, "bltm: " + bltm);
				Log.i(TAG, "blxx: " + blxx);
				Log.i(TAG, "yytm: " + yytm);
				Log.i(TAG, "wxyy: " + wxyy);
				Log.i(TAG, "cstm: " + cstm);
				Log.i(TAG, "wxcs: " + wxcs);
				Log.i(TAG, "rytm: " + rytm);
				Log.i(TAG, "ryxx: " + ryxx);
				Log.i(TAG, "wxsj: " + wxsj);
				iCount++;
				WXList info = new WXList(iCount, mo, zwxh, zwxx, bltm, blxx, yytm, wxyy, 
						cstm, wxcs, rytm, ryxx, wxsj);
			    list.add(info);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return list;
	}
	
	/**
	 * @param barcode: ɨ������
	 * @return String: ���ؽ��: ���ײ�������
	  */
	public static String ScanEJBarcode(String barcode, int ztbz) {
        String strEJBLXX = null;
 
		try {
			String sql = "";
			sql=sql+" exec sp_Getbldm2 "+ "'" + barcode + "'" + ", "+ztbz;
			//sql += "select tid_tm as tm, convert(nvarchar(10),tid_time,120) as jlrq, tid_ztbz as ztbz, tid_jlry as jlry, tid_id as tid ";
			//sql += "from tid_det where tid_tm=" + barcode + " order by tid_time";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			
			if (rs.next()) {
				strEJBLXX = rs.getString("blmc");
				Log.i(TAG, "ejblxx: " + strEJBLXX);
				
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return strEJBLXX;
	}
	
	/**
	 * @param barcode: ɨ������
	 * @param xsbltm: ��������
	 * @param bltm: ��������
	 * @param czry: ��Ա
	 * @param wxcs: ά�޴�ʩ
	 * @return String: ���ؽ��: ����ά����Ϣ
	  */
	static String DRWXInfo(String barcode, String xsbltm, String bltm, String czry, String wxcs) {
        String strJGF = null;
 
		try {
			String sql = "declare @tm INT";
			sql=sql+" exec InReport  " + "'" + barcode + "', ";
			sql=sql+ "'" + xsbltm + "', ";
			sql=sql+ "'" + bltm + "', ";
			sql=sql+ "'" + czry + "', ";
			sql=sql+ "'" + wxcs + "'";
			sql=sql+ ",@tm output select @tm as jg";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			
			if (rs.next()) {
				strJGF = rs.getString("jg");
				Log.i(TAG, "jg: " + strJGF);
				
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return strJGF;
	}
	
	/**
	 * @param barcode: ɨ������
	 * @return String: ���ؽ��:  OQCɨ������Ϣ
	  */
	static IOQCInfo ScanOQCBarcode(String barcode) {
        IOQCInfo info = null;
		try {
			String OQCjg = "", bltm = "", cptm = "";
			if(barcode.equals("NG") || barcode.equals("ng") || mbBL){
				cptm = ProductBarcode;
				OQCjg="0";
				bltm = barcode;
			}else{
				ProductBarcode = barcode;
				cptm = barcode;
				OQCjg="1";
			}
			String sql = "declare @xh int declare @reg nvarchar(20) ";
			sql=sql+" exec dbo.ShOqc_jc "+ "'" + cptm + "'" + ", "+OQCjg+", '"+bltm+"', '"+User+"', @xh output, @reg output ";
			sql=sql+"select @xh as xh,@reg as info";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			
			if (rs.next()) {
				String strxh = rs.getString("xh");
				String strinfo = rs.getString("info");
				Log.i(TAG, "xh: " + strxh);
				Log.i(TAG, "info: " + strinfo);
				info = new IOQCInfo(strxh, strinfo);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return info;
	}
	
	/**
	 * @param barcode: ɨ������
	 * @param barcode: ƽ����
	 * @return String: ���ؽ��:  IQCɨ������Ϣ
	  */
	static IOQCInfo ScanIQCBarcode(String barcode, String PadName) {
        IOQCInfo info = null;
		try {
			String OQCjg = "", bltm = "", cptm = "";
			
			String sql = "DECLARE @sopfile nvarchar(max),@xh int ";
			sql=sql+" exec dbo.sp_Material_SOP "+ "'" + barcode + "'" +", '"+User+"', '"+PadName+"', @xh output, @sopfile output ";
			sql=sql+"select @xh as xh,@sopfile as sop";
	
			Statement stmt = con.createStatement();//����Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor
			
			if (rs.next()) {
				String strxh = rs.getString("xh");
				String strsop = rs.getString("sop");
				Log.i(TAG, "xh: " + strxh);
				Log.i(TAG, "strsop: " + strsop);
				info = new IOQCInfo(strxh, strsop);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;
		}
		return info;
	}
}
