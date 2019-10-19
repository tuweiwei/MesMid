package com.yf.mesmid.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;


public class GetUpdataInfo {
	private Context context ;
	private String mUrlPath = "http://192.168.11.100/update.xml";

	public GetUpdataInfo(Context context) {
		this.context = context;
	}

	public GetUpdataInfo() {
		
	}

	/**
	 * 
	 * @param urlid ������·��string��Ӧ��id
	 * @return ���µ���Ϣ
	 */
	public UpdataInfo getUpdataInfo(int urlid) throws Exception{
		String path = context.getResources().getString(urlid);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(2000);
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();
		return  UpdataInfoParser.getUpdataInfo(is);
	}
	
	/**
	 * 
	 * @param path��Ӧ ������·��
	 * @return ���µ���Ϣ
	 */
	public UpdataInfo getUpdataInfo(String path) throws Exception{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(2000);
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();
		return  UpdataInfoParser.getUpdataInfo(is);
	}
	
	/**
	 * 
	 * @return ���µ���Ϣ
	 */
	public UpdataInfo getUpdataInfo() throws Exception{
		URL url = new URL(mUrlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(2000);
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();
		return  UpdataInfoParser.getUpdataInfo(is);
	}
}
