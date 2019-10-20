package com.yf.mesmid.tid.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuwei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanResult {
	private String indexback;
	private String infoback;
	private String moinfo;
	private String filename;
}
