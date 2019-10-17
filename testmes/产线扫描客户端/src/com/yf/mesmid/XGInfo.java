package com.yf.mesmid;

public class XGInfo implements SMTInfo{
	private int number = 0;
	private String barcode=null;
	private String rq=null;
	private String zt=null;
	private String ry=null;
	private String tid=null;
	XGInfo(int number, String barcode, String rq
			, String zt, String ry, String tid)
	{
		this.number = number;
		this.barcode = barcode;
		this.rq = rq;
		this.zt = zt;
		this.ry = ry;
		this.tid = tid;
	}
	@Override
	public String Getbarcode() {
		// TODO Auto-generated method stub
		return barcode;
	}
	@Override
	public String Getrq() {
		// TODO Auto-generated method stub
		return rq;
	}
	
	public String Getzt() {
		// TODO Auto-generated method stub
		return zt;
	}
	@Override
	public String Getry() {
		// TODO Auto-generated method stub
		return ry;
	}
	@Override
	public void Setbarcode(String barcode) {
		// TODO Auto-generated method stub
		this.barcode = barcode;
	}
	@Override
	public void Setrq(String rq) {
		// TODO Auto-generated method stub
		this.rq = rq;
	}
	
	public void Setzt(String zt) {
		// TODO Auto-generated method stub
		this.zt = zt;
	}
	@Override
	public void Setry(String ry) {
		// TODO Auto-generated method stub
		this.ry = ry;
	}
	
	public String Getgid() {
		// TODO Auto-generated method stub
		return tid;
	}
	
	public void Setgid(String tid) {
		// TODO Auto-generated method stub
		this.tid = tid;
	}
	@Override
	public int Getnumber() {
		// TODO Auto-generated method stub
		return number;
	}
	@Override
	public void Setnumber(int number) {
		// TODO Auto-generated method stub
		this.number = number;
	}

}
