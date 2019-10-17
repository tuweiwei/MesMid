package com.yf.mesmid;

public class GWInfo implements SMTInfo{
	private int number = 0;
	private String barcode=null;
	private String rq=null;
	private String zlzt=null;
	private String kbzt=null;
	private String ry=null;
	private String gid=null;
	GWInfo(int number, String barcode, String rq
			, String zlzt, String kbzt, String ry, String gid)
	{
		this.number = number;
		this.barcode = barcode;
		this.rq = rq;
		this.zlzt = zlzt;
		this.kbzt = kbzt;
		this.ry = ry;
		this.gid = gid;
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
	
	public String Getzlzt() {
		// TODO Auto-generated method stub
		return zlzt;
	}
	
	public String Getkbzt() {
		// TODO Auto-generated method stub
		return kbzt;
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
	
	public void Setzlzt(String zlzt) {
		// TODO Auto-generated method stub
		this.zlzt = zlzt;
	}
	
	public void Setkbzt(String kbzt) {
		// TODO Auto-generated method stub
		this.kbzt = kbzt;
	}
	
	@Override
	public void Setry(String ry) {
		// TODO Auto-generated method stub
		this.ry = ry;
	}

	public String Gettid() {
		// TODO Auto-generated method stub
		return gid;
	}

	public void Settid(String gid) {
		// TODO Auto-generated method stub
		this.gid = gid;
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
