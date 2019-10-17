package com.yf.mesmid;

public class WXList {

	int number = 0;
	private String mo = null;
	private String zwxh = null;
	private String zwxx = null;
	private String bltm = null;
	private String blxx = null;
	//private String wxtm = null;
	//private String wxxx = null;
	private String yytm = null;
	private String wxyy = null;
	private String cstm = null;
	private String wxcs = null;
	private String rytm = null;
	private String ryxx = null;
	private String wxsj = null;
	
	WXList(int number, String mo, String zwxh, String zwxx, String bltm, String blxx, String yytm, 
			String wxyy, String cstm, String wxcs, String rytm, String ryxx, String wxsj)
	{
		this.number = number;
		this.mo = mo;
		this.zwxh = zwxh;
		this.zwxx = zwxx;
		this.bltm = bltm;
		this.blxx = blxx;
		this.yytm = yytm;
		this.wxyy = wxyy;
		this.cstm = cstm;
		this.wxcs = wxcs;
		this.rytm = rytm;
		this.ryxx = ryxx;
		this.wxsj = wxsj;
	}
	
	public int Getnumber(){
		return number;
	}
	
	public String Getmo() {
		return mo;
	}
	
	public String Getzwxh() {
		return zwxh;
	}
	
	public String Getzwxx() {
		return zwxx;
	}
	
	public String Getbltm() {
		return bltm;
	}
	
	public String Getblxx() {
		return blxx;
	}
	
	public String Getyytm() {
		return yytm;
	}
	
	public String Getwxyy() {
		return wxyy;
	}
	
	public String Getcstm() {
		return cstm;
	}
	
	public String Getwxcs() {
		return wxcs;
	}
	
	public String Getrytm() {
		return rytm;
	}
	
	public String Getryxx() {
		return ryxx;
	}
	
	public String Getwxsj() {
		return wxsj;
	}
	
	public void Setnumber(int number){
		this.number = number;
	}
	
	public void Setmo(String mo) {
		this.mo = mo;
	}
	
	
	public void Setzwxh(String zwxh) {
		this.zwxh = zwxh;
	}
	
	public void Setzwxx(String zwxx) {
		this.zwxx = zwxx;
	}
	
	public void Setbltm(String bltm) {
		this.bltm = bltm;
	}
	
	public void Setblxx(String blxx) {
		this.blxx = blxx;
	}
	
	public void Setyytm(String yytm) {
		this.yytm = yytm;
	}
	
	public void Setwxyy(String wxyy) {
		this.wxyy = wxyy;
	}

	public void Setcstm(String cstm) {
		this.cstm = cstm;
	}
	
	public void Setwxcs(String wxcs) {
		this.wxcs = wxcs;
	}
	
	public void Setrytm(String rytm) {
		this.rytm = rytm;
	}
	
	public void Setryxx(String ryxx) {
		this.ryxx = ryxx;
	}
	
	public void Setwxsj(String wxsj) {
		this.wxsj = wxsj;
	}
	
}
