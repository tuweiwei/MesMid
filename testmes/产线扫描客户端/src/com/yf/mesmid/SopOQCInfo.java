package com.yf.mesmid;

public class SopOQCInfo {

	private String cjnumber; //��쵥��
	private String wldm;  //���ϴ���
	private String AQL; //AQL������׼
	private String sjbatch;  //�ͼ�����
	
	private String cjnums;  //�������
	private String yjnums;  //�Ѽ�����
	private String lpnums;  //��Ʒ����
	private String badnums;  //��������
	private String passrate;  //��  ��
	
	private String OperTips;  //������ʾ
	
	
	
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
		String JopInfo = "                    " + "��쵥��Ϣ\n��쵥��:  " + this.cjnumber +
				"\n��         ��:  " + this.wldm +
				"\n������׼:  " + this.AQL +
				"\n�ͼ�����:  " + this.sjbatch;
		return JopInfo;
	}
	
	String Getjopprogress(){
		String jopprogress = "                    " + "������¼\n�������: " + this.cjnums +
				"\n�Ѽ�����: " + this.yjnums
				+ "\n��Ʒ����: " + this.lpnums +
				"\n��������: " + this.badnums +
				"\n��    ��: " + this.passrate;
				//+ "\nֱ  ͨ  ��: " + this.acceptedrate;
		return jopprogress;
	}
	
	String GetTolOpertips(){
		String Opertips = "                    " + "������ʾ    \n" + this.OperTips;
		return Opertips;
	}
}
