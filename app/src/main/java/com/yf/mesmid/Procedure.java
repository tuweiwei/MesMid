package com.yf.mesmid;

public class Procedure {
	private int number; //���
    private String procedurename; //��������
    //private String name;
    private Material material;
    
    Procedure(){}
    
    Procedure(int number, String procedurename, /* String name, */Material material)
    {
    	this.number = number;
    	this.procedurename = procedurename;
    	//this.name = name;
    	this.material = material;
    }
    
    int Getnumber(){
    	return number;
    }
    
    String Getprocedurename(){
    	return procedurename;
    }
    
    /*String Getname(){
    	return name;
    }*/
    
    Material Getmaterial(){
    	return material;
    }
    
    void Setnumber(int number){
    	this.number = number;
    }
    
    void Setprocedurename(String procedurename){
    	this.procedurename = procedurename;
    }
    
    /*void Setname(String name){
    	this.name = name;
    }*/
    
    void Setmateria(Material material){
    	this.material = material;
    }
}
