package com.yf.mesmid.entity;

import com.yf.mesmid.activity.Material;

import lombok.Data;

/**
 * @author tuwei
 */
@Data
public class Procedure {
	private int number;
    private String procedureName;
    private Material material;
}
