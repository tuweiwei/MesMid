package com.yf.mesmid;

public class WXInfo {
	private String sn = null;
	private String yjbltm=null;
	private String ejbltm=null;
	private String pcbabx=null;
	private String czry=null;
	private String blyy=null;
	private String wxcs=null;
	private String ndxs=null;
	private String zrbm=null;
	
	WXInfo(String sn, String yjbltm, String ejbltm
			, String pcbabx, String czry, String blyy, String wxcs
			, String ndxs, String zrbm)
	{
		this.sn = sn;
		this.yjbltm = yjbltm;
		this.ejbltm = ejbltm;
		this.pcbabx = pcbabx;
		this.czry = czry;
		this.blyy = blyy;
		this.wxcs = wxcs;
		this.ndxs = ndxs;
		this.zrbm = zrbm;
	}
	
	public String Getsn() {
		return sn;
	}
	
	public String Getyjbltm() {
		return yjbltm;
	}
	
	public String Getejbltm() {
		return ejbltm;
	}
	
	public String Getpcbabx() {
		return pcbabx;
	}
	
	public String Getczry() {
		return czry;
	}
	
	public String Getblyy() {
		return blyy;
	}
	
	public String Getwxcs() {
		return wxcs;
	}
	
	public String Getndxs() {
		return ndxs;
	}
	
	public String Getzrbm() {
		return zrbm;
	}
	
	public void Setsn(String sn) {
		this.sn = sn;
	}
	
	public void Setyjbltm(String yjbltm) {
		this.yjbltm = yjbltm;
	}
	
	public void Setejbltm(String ejbltm) {
		this.ejbltm = ejbltm;
	}
	
	public void Setpcbabx(String pcbabx) {
		this.pcbabx = pcbabx;
	}
	
	public void Setczry(String czry) {
		this.czry = czry;
	}

	public void Setblyy(String blyy) {
		this.blyy = blyy;
	}
	
	public void Setwxcs(String wxcs) {
		this.wxcs = wxcs;
	}
	
	public void Setndxs(String ndxs) {
		this.ndxs = ndxs;
	}
	
	public void Setzrbm(String zrbm) {
		this.zrbm = zrbm;
	}

}
