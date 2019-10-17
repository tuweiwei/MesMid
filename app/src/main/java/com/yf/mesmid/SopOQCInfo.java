package com.yf.mesmid;

public class SopOQCInfo {

	private String cjnumber; //抽检单号
	private String wldm;  //物料代码
	private String AQL; //AQL抽样标准
	private String sjbatch;  //送检数量
	
	private String cjnums;  //抽检数量
	private String yjnums;  //已检数量
	private String lpnums;  //良品数量
	private String badnums;  //不良数量
	private String passrate;  //良  率
	
	private String OperTips;  //操作提示
	
	
	
	SopOQCInfo(String cjnumber, String wldm, String AQL,
			String sjbatch, String cjnums, String yjnums,
			String lpnums, String badnums, String passrate, String OperTips)
	{
		this.cjnumber = cjnumber;
		this.wldm = wldm;
		this.AQL = AQL;
		this.sjbatch = sjbatch;
		this.cjnums = cjnums;
		this.yjnums = yjnums;
		this.lpnums = lpnums;
		this.badnums = badnums;
		this.passrate = passrate;
		this.OperTips = OperTips;
	}
	
	public SopOQCInfo() {
		// TODO Auto-generated constructor stub
	}

	void Setcjnumber(String cjnumber){
		this.cjnumber = cjnumber;
	}
	
	void Setwldm(String wldm){
		this.wldm = wldm;
	}
	
	void SetAQL(String AQL){
		this.AQL = AQL;
	}
	
	void Setsjbatch(String sjbatch){
		this.sjbatch = sjbatch;
	}
	
	void Setcjnums(String cjnums){
		this.cjnums = cjnums;
	}
	
	void Setyjnums(String yjnums){
		this.yjnums = yjnums;
	}
	
	void Setlpnums(String lpnums){
		this.lpnums = lpnums;
	}
	
	void Setbadnums(String badnums){
		this.badnums = badnums;
	}
	
	void Setpassrate(String passrate){
		this.passrate = passrate;
	}
	
	void SetOperTips(String OperTips){
		this.OperTips = OperTips;
	}
	
	String Getcjnumber(){
		return this.cjnumber;
	}
	
	String Getwldm(){
		return this.wldm;
	}
	
	String GetAQL(){
		return this.AQL;
	}
	
	String Getsjbatch(){
		return this.sjbatch;
	}
	
	String Getcjnums(){
		return this.cjnums;
	}
	
	String Getyjnums(){
		return this.yjnums;
	}
	
	String Getbadnums(){
		return this.badnums;
	}
	
	String Getlpnums(){
		return this.lpnums;
	}
	
	String Getpassrate(){
		return this.passrate;
	}
	
	String GetOperTips(){
		return this.OperTips;
	}

	String GetjopInfo(){
		String JopInfo = "                    " + "抽检单信息\n抽检单号:  " + this.cjnumber +
				"\n料         号:  " + this.wldm +
				"\n抽样标准:  " + this.AQL +
				"\n送检数量:  " + this.sjbatch;
		return JopInfo;
	}
	
	String Getjopprogress(){
		String jopprogress = "                    " + "抽样记录\n抽检数量: " + this.cjnums +
				"\n已检数量: " + this.yjnums
				+ "\n良品数量: " + this.lpnums +
				"\n不良数量: " + this.badnums +
				"\n良    率: " + this.passrate;
				//+ "\n直  通  率: " + this.acceptedrate;
		return jopprogress;
	}
	
	String GetTolOpertips(){
		String Opertips = "                    " + "操作提示    \n" + this.OperTips;
		return Opertips;
	}
}
