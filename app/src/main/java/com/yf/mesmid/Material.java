package com.yf.mesmid;

public class Material {
	private int number; //序号
    private String material; //材料
    private String pmgg; //品名规格
    private String materialofcontrol; // 批次料号
    //----------
    private String batchkeymaterial; // 料号批次
    private String clgkztbz; // 状态标志 1为必须管控材料，0为可以管控
    private String pcgkztbz; // 状态标志 1为必须扫料的批次号，0为不用
    public boolean bEnter = false;
    
    Material(){}
    
    Material(int number, String material
    		, String pmgg, String materialofcontrol, String wlgkztbz)
    {
    	this.number = number;
    	this.material = material;
    	this.pmgg = pmgg;
    	this.materialofcontrol = materialofcontrol;
    	this.clgkztbz = wlgkztbz;
    	bEnter = false;
    }
    
    Material(int number, String material, String pmgg, String materialofcontrol, 
    		String batchkeymaterial, String clgkztbz, String pcgkztbz)
    {
    	this.number = number;
    	this.material = material;
    	this.pmgg = pmgg;
    	this.materialofcontrol = materialofcontrol;
    	this.batchkeymaterial = batchkeymaterial;
    	this.clgkztbz = clgkztbz;
    	this.pcgkztbz = pcgkztbz;
    	bEnter = false;
    }
    
    int Getnumber(){
    	return number;
    }
    
    String Getmaterial(){
    	return material;
    }
    
    String Getpmgg(){
    	return pmgg;
    }
    
    String Getmaterialofcontrol(){
    	return materialofcontrol;
    }
    
    String Getbatchkeymaterial(){
    	return batchkeymaterial;
    }
    
    String Getclgkztbz(){
    	return clgkztbz;
    }
    
    String Getpcgkztbz(){
    	return pcgkztbz;
    }
    
    void Setnumber(int number){
    	this.number = number;
    }
    
    void Setmaterial(String material){
    	this.material = material;
    }
    
    void Setpmgg(String pmgg){
    	this.pmgg = pmgg;
    }
    
    void Setmaterialofcontrol(String materialofcontrol){
    	this.materialofcontrol = materialofcontrol;
    }
    
    void Setbatchkeymaterial(String batchkeymaterial){
    	this.batchkeymaterial = batchkeymaterial;
    }
    
    void Setclgkztbz(String wlgkztbz){
    	this.clgkztbz = wlgkztbz;
    }
    
    void Setpcgkztbz(String pcgkztbz){
    	this.pcgkztbz = pcgkztbz;
    }
}
