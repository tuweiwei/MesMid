package com.yf.mesmid;

public class ScanResult {
	private String indexback;
	private String infoback;
	private String moinfo;
	private String filename;
	
	ScanResult(String indexback, String infoback
			,String moinfo ,String filename)
	{
		this.indexback = indexback;
		this.infoback = infoback;
		this.moinfo = moinfo;
		this.filename = filename;
	}
	
	void Setindexback(String indexback){
		this.indexback = indexback;
	}
	
	void Setinfoback(String infoback){
		this.infoback = infoback;
	}
	
	void Setmoinfo(String moinfo){
		this.moinfo = moinfo;
	}
	
	void Setfilename(String filename){
		this.filename = filename;
	}
	
	String Getindexback(){
		return indexback;
	}
	
	String Getinfoback(){
		return infoback;
	}
	
	String Getmoinfo(){
		return moinfo;
	}
	
	String Getfilename(){
		return filename;
	}
}
