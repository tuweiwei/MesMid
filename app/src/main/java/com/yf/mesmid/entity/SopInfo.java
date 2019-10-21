package com.yf.mesmid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuwei
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SopInfo {
	private String jopnumber;
	private String model;
	private String PN;
	private String batch;
	private String outputnums;
	private String acceptednums;
	private String badnums;
	private String passrate;
	private String totaloutputs;
	private String acceptedrate;
	private String OperTips;
}
