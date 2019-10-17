package com.yf.mesmid;

public class SopInfo {

	private String jopnumber; //工  单  号
	private String model;  //机型/版型
	private String PN;  //料       号
	private String batch;  //生产批量
	private String outputnums;  //产出数量
	private String acceptednums;  //良品数量
	private String badnums;  //不良数量
	private String passrate;  //良  率
	private String totaloutputs;  //累计产出
	private String acceptedrate;  //直  通  率
	private String OperTips;  //操作提示
	
	SopInfo(String jopnumber, String model, String PN,
			String batch, String outputnums, String acceptednums,
			String badnums, String passrate, String totaloutputs,
			String acceptedrate, String OperTips)
	{
		this.jopnumber = jopnumber;
		this.model = model;
		this.PN = PN;
		this.batch = batch;
		this.outputnums = outputnums;
		this.acceptednums = acceptednums;
		this.badnums = badnums;
		this.passrate = passrate;
		this.totaloutputs = totaloutputs;
		this.acceptedrate = acceptedrate;
		this.OperTips = OperTips;
	}
	
	public SopInfo() {
		// TODO Auto-generated constructor stub
	}

	void Setjopnumber(String jopnumber){
		this.jopnumber = jopnumber;
	}
	
	void Setmodel(String model){
		this.model = model;
	}
	
	void SetPN(String PN){
		this.PN = PN;
	}
	
	void Setbatch(String batch){
		this.batch = batch;
	}
	
	void Setoutputnums(String outputnums){
		this.outputnums = outputnums;
	}
	
	void Setacceptednums(String acceptednums){
		this.acceptednums = acceptednums;
	}
	
	void Setbadnums(String badnums){
		this.badnums = badnums;
	}
	
	void Settotaloutputs(String Totaloutputs){
		this.totaloutputs = Totaloutputs;
	}
	
	void Setacceptedrate(String acceptedrate){
		this.acceptedrate = acceptedrate;
	}
	
	void Setpassrate(String passrate){
		this.passrate = passrate;
	}
	
	void SetOperTips(String OperTips){
		this.OperTips = OperTips;
	}
	
	void SetPass(int Nums){
		int ioutputnums=Integer.valueOf(this.outputnums).intValue();
		int iacceptednums=Integer.valueOf(this.acceptednums).intValue();
		int ibadnums=Integer.valueOf(this.badnums).intValue();
		int itotaloutputs =Integer.valueOf(this.totaloutputs ).intValue();
		this.outputnums=""+(ioutputnums+Nums);
		this.acceptednums=""+(iacceptednums+Nums);
		//this.passrate =(double) (Math.round((iacceptednums+Nums)/((ioutputnums+Nums))*10000)/100.0)+"%";
		double dbuff=1.0*(iacceptednums+Nums)/(ioutputnums+Nums);
		long   l1   =   Math.round(dbuff*10000);  
		this.passrate =((double) (l1/10000.0))*100.0+"%"; 
		this.totaloutputs=""+(itotaloutputs+Nums);
	}
	
	void SetFail(int Nums){
		int ioutputnums=Integer.valueOf(this.outputnums).intValue();
		int iacceptednums=Integer.valueOf(this.acceptednums).intValue();
		int ibadnums=Integer.valueOf(this.badnums).intValue();
		int itotaloutputs =Integer.valueOf(this.totaloutputs ).intValue();
		this.outputnums=""+(ioutputnums);
		this.acceptednums=""+(iacceptednums-Nums);
		this.badnums=""+(ibadnums+Nums);
		//this.passrate =(double) (Math.round((iacceptednums-Nums)/(ioutputnums)*10000)/100.0)+"%";
		double dbuff=1.0*(iacceptednums-Nums)/(ioutputnums);
		long   l1   =   Math.round(dbuff*10000);  
		this.passrate =((double) (l1/10000.0))*100.0+"%"; 
		this.totaloutputs=""+(itotaloutputs-Nums);
	}
	
	String Getjopnumber(){
		return this.jopnumber;
	}
	
	String Getmodel(){
		return this.jopnumber;
	}
	
	String GetPN(){
		return this.PN;
	}
	
	String Getbatch(){
		return this.batch;
	}
	
	String Getoutputnums(){
		return this.outputnums;
	}
	
	String Getacceptednums(){
		return this.acceptednums;
	}
	
	String Getbadnums(){
		return this.badnums;
	}
	
	String Gettotaloutputs(){
		return this.totaloutputs;
	}
	
	String Getacceptedrate(){
		return this.acceptedrate;
	}
	
	String Getpassrate(){
		return this.passrate;
	}
	
	String GetOperTips(){
		return this.OperTips;
	}

	String GetjopInfo(){
		String JopInfo = "                    " + "工单信息\n工  单   号:  " + this.jopnumber +
				"\n机型/版型: " + this.model +
				"\n料         号:  " + this.PN +
				"\n生产批量:  " + this.batch;
		return JopInfo;
	}
	
	String Getjopprogress(){
		String jopprogress = "                    " + "工单进度\n当日产出: " + this.outputnums +
				"\n良品数量: " + this.acceptednums +
				"\n不良数量: " + this.badnums +
				"\n良    率: " + this.passrate
				+ "\n累计产出: " + this.totaloutputs;
				//+ "\n直  通  率: " + this.acceptedrate;
		return jopprogress;
	}
	
	String GetTolOpertips(){
		String Opertips = "                    " + "操作提示    \n" + this.OperTips;
		return Opertips;
	}
}
