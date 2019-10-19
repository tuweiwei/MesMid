package com.yf.mesmid.activity;

public class Material {
	private int number; //���
    private String material; //����
    private String pmgg; //Ʒ�����
    private String materialofcontrol; // �����Ϻ�
    //----------
    private String batchkeymaterial; // �Ϻ�����
    private String clgkztbz; // ״̬��־ 1Ϊ����ܿز��ϣ�0Ϊ���Թܿ�
    private String pcgkztbz; // ״̬��־ 1Ϊ����ɨ�ϵ����κţ�0Ϊ����
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
