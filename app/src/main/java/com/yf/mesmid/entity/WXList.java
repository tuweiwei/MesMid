package com.yf.mesmid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  @author tuwei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class WXList {
	private Integer number;
	private String mo;
	private String zwxh;
	private String zwxx;
	private String bltm;
	private String blxx;
	private String yytm;
	private String wxyy;
	private String cstm;
	private String wxcs;
	private String rytm;
	private String ryxx;
	private String wxsj;

}
