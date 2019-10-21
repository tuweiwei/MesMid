package com.yf.mesmid.entity;

import com.yf.mesmid.ui.activitys.activity.Material;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuwei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Procedure {
	private int number;
    private String procedureName;
    private Material material;
}
