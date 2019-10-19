package com.yf.mesmid.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  @author tuwei
 */
@Data
@NoArgsConstructor
public class JobOrder {
	private int number;
	private String producedata;
	private String joborder;
	private String materialorder;
    private String model;
    private String batch;
    private String moid;
    private Procedure procedure;
}
