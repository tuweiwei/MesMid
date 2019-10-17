package com.yf.mesmid;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.yf.mesupdate.UpdataInfo;

import android.database.SQLException;
import android.os.Environment;
import android.os.Message;
import android.provider.ContactsContract.StatusUpdates;
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
	public static boolean bClInput = false; //判断是否需要输入机器码
	private static String ProductBarcode = null;
	private static String ClBarcode = "";  //暂时不需要
	
	//判读是正常生产还是返工模式
	public final static int ZC_MODE= 0;//正常生产模式
	public final static int FG_MODE= 1;//返工模式
	public final static int OQC_MODE= 2;//OQC模式,选工单或自动模糊查找工单选工序时只显示OQC
	
	//OQC模式 保存数据
	public static String OQC_SJS = "";  //OQC送检数
	public static String OQC_CJS = "";  //OQC送检数
	public static String OQC_AQL = "";  //OQC送检数
	//----
	
	public final static int KEYBOARD_MODE= 10;//小键盘模糊查询方式, 模糊查询一条工单
	public final static int KEYBOARD_DISPLAYGD= 11;//模糊查询到两条及以上工单, 直接显示多工单
	
	public static int SC_MODE = ZC_MODE; //判读选工单时是正常生产还是返工模式或OQC模式
	public static int GX_MODE = ZC_MODE;//判读选工序时是正常生产还是OQC模糊查询模式
	
	public static int TotalScanNums = 0;
	public static int CurScanNums = 0;
	public static String User = "ADMIN";
	
	public static String DisplayInfo = "";//扫描条码后，结果显示(包括条码,多用于包装多条码绑定)
	
	public final static int ScanSN = 1;//扫描流水号模式
	public final static int ScanGD = 2;//扫描工单模式
	public final static int ScanSMTAOI = 3;//扫描SMT AOI模式
	public final static int ScanKHLJCheck = 4;//客户零件号校验模式
	public static int ScanMode = ScanSN;
	
	public static String SNCode = "";
	
	private static List<String> LCLBarcode = new ArrayList<String>();
	
	//扫描操作用户
	public static int mCount = 0;
	public static final int mScanUserTime = 8*60*60; //10*60
	public static boolean mbScanUser = false;
	//扫描批次料号
	public static int mBatchCodeCount = 0;
	public static final int mScanBatchCodeTime = 720;//10*720; 2小时自动跳到list界面扫描料号
	public static boolean mbScanBatchCode = false;//判断是否需要2小时跳转
	
	public static boolean mbBL = false; 
	
	public static int mBarcodeNums = 0;//扫描批次料号数
	public static int mNeedScanBarcodeNums = 0;//务必扫描批次料号数
	public static List<String> lBarcodes = new ArrayList<String>();//记录已扫描的批次料号
	
	public static int mBatchNums = 0;//扫描料号批次数
	public static int mNeedScanBatchNums = 0;//务必扫描料号批次数
	public static List<String> lBatchs = new ArrayList<String>();//记录已扫描的料号批次
	
	public static int mljNums=1;
	
	//为了优化服务器扫描效率
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
	 *获取数据库配置文件信息
	 * @param ConfigPath: 配置文件路径
	 * @return sqlserver数据库是否连接成功
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
			Log.i(TAG, "配置文件不存在或解析错误");
			//SendDataMessage(ERROR, "数据库配置解析错误");
			//return;
		}
	}
	
	/**
	 * 判断数据库是否连接成功
	 * @return sqlserver数据库是否连接成功
	*/
	static public boolean Connect()
	{
		//Connection con = null;

		try { // 加载驱动程序
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(
					"jdbc:jtds:sqlserver://" +
					Address +
					"/" +
					DatabaseName
					, UserName,
					PassWord);
		} catch (ClassNotFoundException e) {
			System.out.println("加载驱动程序出错");
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
	 * 从服务器端获取APP版本信息
	 * @return 服务器版本信息
	  */
	static UpdataInfo GetUpdateInfo(){
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
	 * @param PadName: 平板名
	 * @return 工单名称列表
	  */
	static List<JobOrder> GetJobOrderOQC(String PadName) {
		List<JobOrder> ListJobOrder = new ArrayList<JobOrder>();

		try {
			String sql = "exec pb_moe_query " + "'" + PadName+ "',0,"+SC_MODE;
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor

			
			String strWldm="物料代码";
			if(DatabaseOper.SC_MODE==DatabaseOper.ZC_MODE) strWldm="物料代码";	
			else if(DatabaseOper.SC_MODE==DatabaseOper.FG_MODE) strWldm="返工物料代码";
			else strWldm="物料代码";	
			
			JobOrder order = new JobOrder(0, "建立日期", "检验单号", strWldm, "送检数", "抽检数", "0", null);
			ListJobOrder.add(order);
			int iCount = 1;
			while (rs.next()) {//<code>ResultSet</code>最初指向第一行
				String strscrq = rs.getString("oqc_jlrq");
				String strzzdh= rs.getString("oqc_dh");
				String strwldm = rs.getString("oqc_wldm");
				String strsjs = rs.getString("oqc_sjs");//送检数
				String strcjs = rs.getString("oqcbl_cjs2");//抽检数
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
				//ExcelOper.readExcel("/mnt/sdcard/WC-BA12-012 CP6953-KA 1.1 组装作业指导 .xls");
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
	 * @param PadName: 平板名
	 * @return 工单名称列表
	  */
	static List<JobOrder> GetJobOrder(String PadName) {
		List<JobOrder> ListJobOrder = new ArrayList<JobOrder>();

		try {
			String sql = "exec pb_moe_query " + "'" + PadName+ "',"+SC_MODE;
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor

			
			String strWldm="物料代码";
			if(DatabaseOper.SC_MODE==DatabaseOper.ZC_MODE) strWldm="物料代码";	
			else if(DatabaseOper.SC_MODE==DatabaseOper.FG_MODE) strWldm="返工物料代码";
			else strWldm="物料代码";	
			
			JobOrder order = new JobOrder(0, "生产日期", "工 单 号", strWldm, "机型代码", "生产批量", "0", null);
			ListJobOrder.add(order);
			int iCount = 1;
			while (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
				//ExcelOper.readExcel("/mnt/sdcard/WC-BA12-012 CP6953-KA 1.1 组装作业指导 .xls");
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
	 * @param wldm: 物料代码
	 * @return 工序名称列表
	  */
	static List<Procedure> GetProcedure(String wldm,String PadName, String moeid) {
		List<Procedure> ListProcedure = new ArrayList<Procedure>();

		try {
			String sql = "exec pb_rud_query " + "'" + wldm+ "',"+SC_MODE+ ",'" + PadName+ "',"+moeid;
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			Procedure procedure = new Procedure(0, "工序名称" , null);
			ListProcedure.add(procedure);
			int iCount = 1;
			while (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
	 * @param moid: 工单排程的ID
	 * @param wldm: 物料代码
	 * @param xh: 工序序号
	 * @param PadName: 平板名
	 * @return 材料名称列表
	  */
	static List<Material> GetMaterial(String moid, String wldm, String xh, String PadName) {
		List<Material> ListMaterial = new ArrayList<Material>();

		try {
			String sql = "exec pb_boe_query " + moid + "," + "'" + wldm + "'" + "," + xh + 
					 "," + "'" + PadName + "'";
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			Material material = new Material(0, "材料代码", "品名规格", "管控材料", "批 次 码", "料管控", "批次管控");
			ListMaterial.add(material);
			int iCount = 1;
			while (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
	 * @param moid: 工单排程的ID
	 * @param wldm: 物料代码
	 * @param xh: 工序序号
	 * @param PadName: 平板名
	 * @return 材料名称列表
	  */
//	static List<Material> GetMaterial(String moid, String wldm, String xh, String PadName) {
//		List<Material> ListMaterial = new ArrayList<Material>();
//
//		try {
//			String sql = "exec pb_boe_query " + moid + "," + "'" + wldm + "'" + "," + xh + 
//					 "," + "'" + PadName + "'";
//	
//			Statement stmt = con.createStatement();//创建Statement
//			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
//			Material material = new Material(0, "材料代码", "品名规格", "批 次 码", "状态标志");
//			ListMaterial.add(material);
//			int iCount = 1;
//			while (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
	
	static SopInfo GetInitial(String PadName,String gydm, String moeid, String gwdm)
	{
		SopInfo info=null;
		try {
			String sql = "";
			sql += "exec data_deal_op_count '" + PadName + "','" +  gydm + "'," + moeid +"," + gwdm;
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			if (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
			ScanResult = "扫描批次码失败";
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
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			if (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
	
	/**扫描材料条码，确定材料条码在此工位存在
	 * @param moid: 工单排程的ID
	 * @param PadName: 平板名
	 * @param wldm: 物料代码
	 * @param xh: 工序序号
	 * @param batchcode: 扫描条码
	 * @return true: 扫描成功 false: 扫描失败
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
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			if (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
			ScanResult = "扫描材料码失败";
			bRet = false;
		}
		return bRet;
	}
	
	/**
	 * @param moid: 工单排程的ID
	 * @param PadName: 平板名
	 * @param wldm: 物料代码
	 * @param xh: 工序序号
	 * @param batchcode: 扫描条码
	 * @return true: 扫描成功 false: 扫描失败
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
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			if (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
			ScanResult = "扫描批次码失败";
			bRet = false;
		}
		return bRet;
	}
	
	/**
	 * @param wldm: 物料条码
	 * @param xh: 对应序号
	 * @return sopurl
	  */
	static String GetSopInfo(String wldm, String xh, String PadName) {
        String SopFile = null;

		try {
			String sql = "";
			sql += "declare @retfile nvarchar(200) ";
			sql += "declare @bz int ";
			sql += "exec pb_ffe_query " ;//测试加text
			sql += "'" + wldm + "'," + xh + ",@retfile output "+ ",@bz output"+ ",'" + PadName+ "' ";
			sql += "select @retfile as sopfile" + ",@bz as scanmode";
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			if (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
	 * @param PadName: 对应平板名
	 * @param barcode: 对应扫描条码
	 * @return 扫描结果
	  */
	static ScanResult ScanBarcode(String PadName, String barcode) {
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
				Log.i(TAG, "材料条码: " + clbarcode);
			}
			if(mbBL) {
				blbarcode = barcode;
				Log.i(TAG, "不良条码: " + blbarcode);
			}
			sql = "";
			//sql=sql+"truncate table tem_mstr \n";
			//sql=sql+"truncate table op100_det \n";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetInfo nvarchar(500) ";
			sql=sql+"declare @RetFile nvarchar(500) ";
			sql=sql+"exec data_deal_op_yf '";//测试加了test后缀
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
					DisplayInfo ="\n条码: "+barcode+"";//信息显示
				}/*else if(ixh >= 100 && 102 != ixh 
						&& bClInput == true && CurScanNums == TotalScanNums){
					bClInput = false;
					TotalScanNums = 0;
					CurScanNums = 0;
					LCLBarcode.clear();
				}*/else if(ixh == 100 && bClInput == true && CurScanNums < TotalScanNums){
					CurScanNums++;
					DisplayInfo +="\n序列号"+CurScanNums+": "+barcode+"";//信息显示
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
					//if(102 != ixh && CurScanNums == TotalScanNums){//信息显示
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
	 * @param PadName: 对应平板名
	 * @param barcode: 对应扫描条码
	 * @return 扫描结果
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
				Log.i(TAG, "材料条码: " + clbarcode);
			}
			if(mbBL) {
				blbarcode = barcode;
				Log.i(TAG, "不良条码: " + blbarcode);
			}
			sql = "";
			//sql=sql+"truncate table tem_mstr \n";
			//sql=sql+"truncate table op100_det \n";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetInfo nvarchar(500) ";
			sql=sql+"declare @RetFile nvarchar(500) ";
			//sql=sql+"exec data_deal_op_yf_pc_test '";//测试加了test后缀
			sql=sql+"exec data_deal_op_yf_test '";//测试加了test后缀
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
			//保存条码扫描语句到本地sqllog.txt中
			ConfigurationFile.SqlLog(Environment.getExternalStorageDirectory().getPath()+"/sqllog.txt", sql);
	
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				String strxh = rs.getString("xh");
				int ixh = Integer.valueOf(strxh).intValue();
				if(102 ==ixh ){
					bClInput = true;
					DisplayInfo ="\n条码: "+barcode+"";//信息显示
				}/*else if(ixh >= 100 && 102 != ixh 
						&& bClInput == true && CurScanNums == TotalScanNums){
					bClInput = false;
					TotalScanNums = 0;
					CurScanNums = 0;
					LCLBarcode.clear();
				}*/
				else if(ixh == 100 && bClInput == true && CurScanNums < TotalScanNums){
					CurScanNums++;
					DisplayInfo +="\n序列号"+CurScanNums+": "+barcode+"";//信息显示
					if(102 != ixh && CurScanNums == TotalScanNums /*&& DatabaseOper.ScanMode != DatabaseOper.ScanKHLJCheck*/){
						bClInput = false;
						TotalScanNums = 0;
						CurScanNums = 0;
						LCLBarcode.clear();
					}
				}
				else{
					/*零件加工校验失败则重新开始扫描*/
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
						/*零件加工校验模式 默认CurScanNums+1*/
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
					//if(102 != ixh && CurScanNums == TotalScanNums){//信息显示
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
//				Log.i(TAG, "材料条码: " + clbarcode);
//			}
//			if(mbBL) {
//				blbarcode = barcode;
//				Log.i(TAG, "不良条码: " + blbarcode);
//			}
//			sql = "";
//			//sql=sql+"truncate table tem_mstr \n";
//			//sql=sql+"truncate table op100_det \n";
//			sql=sql+"declare @Ret int ";
//			sql=sql+"declare @RetMsg nvarchar(200) ";
//			sql=sql+"declare @RetInfo nvarchar(500) ";
//			sql=sql+"declare @RetFile nvarchar(500) ";
//			sql=sql+"exec data_deal_op_yf_test '";//测试加了test后缀
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
//			//保存条码扫描语句到本地sqllog.txt中
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
//					DisplayInfo ="\n条码: "+barcode+"";//信息显示
//				}/*else if(ixh >= 100 && 102 != ixh 
//						&& bClInput == true && CurScanNums == TotalScanNums){
//					bClInput = false;
//					TotalScanNums = 0;
//					CurScanNums = 0;
//					LCLBarcode.clear();
//				}*/
//				else if(ixh == 100 && bClInput == true && CurScanNums < TotalScanNums){
//					CurScanNums++;
//					DisplayInfo +="\n序列号"+CurScanNums+": "+barcode+"";//信息显示
//					if(102 != ixh && CurScanNums == TotalScanNums /*&& DatabaseOper.ScanMode != DatabaseOper.ScanKHLJCheck*/){
//						bClInput = false;
//						TotalScanNums = 0;
//						CurScanNums = 0;
//						LCLBarcode.clear();
//					}
//				}
//				else{
//					/*零件加工校验失败则重新开始扫描*/
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
//						/*零件加工校验模式 默认CurScanNums+1*/
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
//					//if(102 != ixh && CurScanNums == TotalScanNums){//信息显示
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
	 * @param PadName: 对应平板名
	 * @param gd: 对应工单号
	 * @param jgtm: 对应结果条码,如 ok, ng
	 * @param number: 对应板数量
	 * @param bltm: 不良条码
	 * @return 扫描结果
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
			sql=sql+"exec data_deal_op_Stack '";//测试加test
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
	 * @param PadName: 对应平板名
	 * @param barcode: 对应扫描条码
	 * @return 扫描结果
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
			sql=sql+"exec data_deal_op_smt '";//测试加了test后缀
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
	 * @param PadName: 对应平板名
	 * @param jgtm: 对应结果条码,如 QJGASSYOK1, QJGASSYNG1或15910275;15910275;15920522
	 * @param number: 对应数量
	 * @return 扫描结果
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
			sql=sql+"exec data_deal_op_yf_qian '";//测试加了test后缀
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
	 * @param PadName: 对应平板名
	 * @param barcode: 对应扫描条码
	 * @return 扫描结果
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
				Log.i(TAG, "不良条码: " + blbarcode);
			}
			sql = "";
			//sql=sql+"truncate table tem_mstr \n";
			//sql=sql+"truncate table op100_det \n";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetInfo nvarchar(500) ";
			sql=sql+"declare @RetFile nvarchar(500) ";
			sql=sql+"exec data_deal_op_oqc '";//测试加了test后缀
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
	 * @param code: 扫描条码
	 * @return wldm: 返回结果: 物料代码
	  */
	static String SpGetWldm(String code)
	{
		String wldm = null;

		try {
			String sql = "";
			sql=sql+"declare @wldm nvarchar(50) ";
			sql += "exec dbo.spGetWldmBypc " + "'" + code + "'," + " @wldm output ";
			sql += "select @wldm as wldm";
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			if (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
	 * @param barcode: 扫描条码
	 * @param sign: 标志
	 * @param state: 状态
	 * @return xh: 返回结果: 100为扫描成功, 其他为扫描失败
	  */
	static int ScanSMTInfo(String barcode, String sign, String state, String kbstate) {
        int xh = 12;

		try {
			String sql = "";
			sql=sql+"declare @Ret int ";
			sql += "declare @RetMsg nvarchar(200) ";
			sql += "exec data_deal_op_stm " + "'" + barcode + "'," + state +"," + kbstate + ","  + "'" + User + "'," + sign + ",@Ret output ,@RetMsg output ";
			sql += "select @Ret as xh ,@RetMsg as oprmsg";
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			if (rs.next()) {//<code>ResultSet</code>最初指向第一行
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
			ScanResult = "扫描条码失败";
		}
		return xh;
	}
	
	/**
	 * @param barcode: 扫描条码
	 * @return List<XGInfo>: 返回结果: 锡膏信息集
	  */
	static List<XGInfo> CXXGInfo(String barcode) {
        List<XGInfo> list = new ArrayList<XGInfo>();
 
		try {
			String sql = "";
			sql=sql+"exec pb_tid_query " + "'" + barcode + "'";
			//sql += "select tid_tm as tm, convert(nvarchar(10),tid_time,120) as jlrq, tid_ztbz as ztbz, tid_jlry as jlry, tid_id as tid ";
			//sql += "from tid_det where tid_tm=" + barcode + " order by tid_time";
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
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
	 * @param barcode: 扫描条码
	 * @return List<GWInfo>: 返回结果: 钢网信息集
	  */
	static List<GWInfo> CXGWInfo(String barcode) {
        List<GWInfo> list = new ArrayList<GWInfo>();
 
		try {
			String sql = "";
			sql=sql+"exec pb_gqd_query " + "'" + barcode + "'";
			//sql += "select tid_tm as tm, convert(nvarchar(10),tid_time,120) as jlrq, tid_ztbz as ztbz, tid_jlry as jlry, tid_id as tid ";
			//sql += "from tid_det where tid_tm=" + barcode + " order by tid_time";
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
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
	 * @param barcode: 扫描条码
	 * @return List<WXList>: 返回结果: 维修信息集
	  */
	static List<WXList> ScanWXBarcode(String barcode) {
        List<WXList> list = new ArrayList<WXList>();
 
		try {
			String sql = "";
			sql=sql+"exec sp_Getbldm " + "'" + barcode + "'";
			//sql += "select tid_tm as tm, convert(nvarchar(10),tid_time,120) as jlrq, tid_ztbz as ztbz, tid_jlry as jlry, tid_id as tid ";
			//sql += "from tid_det where tid_tm=" + barcode + " order by tid_time";
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
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
	 * @param barcode: 扫描条码
	 * @return String: 返回结果: 二阶不良现象
	  */
	static String ScanEJBarcode(String barcode, int ztbz) {
        String strEJBLXX = null;
 
		try {
			String sql = "";
			sql=sql+" exec sp_Getbldm2 "+ "'" + barcode + "'" + ", "+ztbz;
			//sql += "select tid_tm as tm, convert(nvarchar(10),tid_time,120) as jlrq, tid_ztbz as ztbz, tid_jlry as jlry, tid_id as tid ";
			//sql += "from tid_det where tid_tm=" + barcode + " order by tid_time";
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			
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
	 * @param barcode: 扫描条码
	 * @param xsbltm: 不良条码
	 * @param bltm: 不良条码
	 * @param czry: 人员
	 * @param wxcs: 维修措施
	 * @return String: 返回结果: 导入维修信息
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
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			
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
	 * @param barcode: 扫描条码
	 * @return String: 返回结果:  OQC扫描结果信息
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
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			
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
	 * @param barcode: 扫描条码
	 * @param barcode: 平板名
	 * @return String: 返回结果:  IQC扫描结果信息
	  */
	static IOQCInfo ScanIQCBarcode(String barcode, String PadName) {
        IOQCInfo info = null;
		try {
			String OQCjg = "", bltm = "", cptm = "";
			
			String sql = "DECLARE @sopfile nvarchar(max),@xh int ";
			sql=sql+" exec dbo.sp_Material_SOP "+ "'" + barcode + "'" +", '"+User+"', '"+PadName+"', @xh output, @sopfile output ";
			sql=sql+"select @xh as xh,@sopfile as sop";
	
			Statement stmt = con.createStatement();//创建Statement
			ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
			
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
