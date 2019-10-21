package com.yf.mesmid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class XGInfo{
	private int number;
	private String barcode;
	private String rq;
	private String zt;
	private String ry;
	private String tid;
}
