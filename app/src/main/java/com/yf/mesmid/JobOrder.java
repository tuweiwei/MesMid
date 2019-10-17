package com.yf.mesmid;

public class JobOrder {
	
	private int number;
	private String producedata;
	private String joborder;
	private String materialorder;
    private String model;
    private String batch;
    private String moid;
    private Procedure procedure;
    
    JobOrder(){}
    
    JobOrder(int number, String producedata, String joborder,  String materialorder,
    		String model , String batch, String moid, Procedure procedure)
    {
    	this.number = number;
    	this.producedata = producedata;
    	this.joborder = joborder;
    	this.materialorder = materialorder;
    	this.model = model;
    	this.batch = batch;
    	this.moid = moid;
    	this.procedure = procedure;
    }
    
    int Getnumber(){
    	return number;
    }
    
    String Getproducedata(){
    	return producedata;
    }
    
    String Getjoborder(){
    	return joborder;
    }
    
    String Getmaterialorder(){
    	return materialorder;
    }
    
    String Getmodel(){
    	return model;
    }
    
    String Getbatch(){
    	return batch;
    }
    
    String Getmoid(){
    	return moid;
    }
    
    Procedure Getprocedure(){
    	return procedure;
    }
    
    void Setnumber(int number){
    	this.number = number;
    }
    
    void Setproducedata(String producedata){
    	this.producedata = producedata;
    }
    
    void Setjoborder(String joborder){
    	this.joborder = joborder;
    }
    
    void Setmaterialorder(String materialorder){
    	this.materialorder = materialorder;
    }
    
    void Setmode(String model){
    	this.model = model;
    }
    
    void Setbatch(String batch){
    	this.batch = batch;
    }
    
    void Setmoid(String moid){
    	this.moid = moid;
    }
    
    void Setprocedure(Procedure procedure){
    	this.procedure = procedure;
    }
}
