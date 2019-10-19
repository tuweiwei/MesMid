package com.yf.mesmid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuwei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SopOQCInfo {
	private String cjnumber;
	private String wldm;
	private String AQL;
	private String sjbatch;
	private String cjnums;
	private String yjnums;
	private String lpnums;
	private String badnums;
	private String passrate;
	private String OperTips;
}
