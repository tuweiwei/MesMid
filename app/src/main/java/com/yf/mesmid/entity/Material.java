package com.yf.mesmid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
public class Material {
	private int number;
    private String material;
    private String pmgg;
    private String materialofcontrol;
    private String batchkeymaterial;
    private String clgkztbz;
    private String pcgkztbz;
    public boolean bEnter = false;

    Material(int number, String material
    		, String pmgg, String materialofcontrol, String wlgkztbz) {
    	this.number = number;
    	this.material = material;
    	this.pmgg = pmgg;
    	this.materialofcontrol = materialofcontrol;
    	this.clgkztbz = wlgkztbz;
    	bEnter = false;
    }
}
