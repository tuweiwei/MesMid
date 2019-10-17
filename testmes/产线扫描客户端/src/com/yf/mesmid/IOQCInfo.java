package com.yf.mesmid;

public class IOQCInfo {
	private String xh = "";
	private String info = "";
	
	IOQCInfo(String xh, String info)
	{
		this.xh = xh;
		this.info = info;
	}
	
	String Getxh()
	{
		return this.xh;
	}
	
	void Setxh(String xh)
	{
		this.xh = xh;
	}
	
	String Getinfo()
	{
		return this.info;
	}
	
	void Setinfo(String info)
	{
		this.info = info;
	}
}
