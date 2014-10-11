package com.unimelb.mobile.breakoutgame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;


public class Download {
	private String strUrl;
	private String sdcard;
	private HttpURLConnection urlcon;
	
	public Download(String url)
	{
		this.strUrl = url;
		this.sdcard = Environment.getExternalStorageDirectory() + "/";
		urlcon = getConnection();
	}
	
	public String downloadAsString()
	{
		StringBuilder sb = new StringBuilder();
		String temp = null;
		try {
			Log.d("log", urlcon.toString());
			InputStream is = urlcon.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private HttpURLConnection getConnection()
	{
		URL url;
		HttpURLConnection urlcon = null;
		try {
			url = new URL(strUrl);
			urlcon = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urlcon;
	}
	
	public int getLength()
	{
		return urlcon.getContentLength();
	}
	
	/*
	 * write file to SD card
	 */
	public int down2sd(String dir, String filename, downhandler handler)
	{
		StringBuilder sb = new StringBuilder(sdcard)
							.append(dir);
		File file = new File(sb.toString());
		if (!file.exists()) {
			file.mkdirs();
			Log.d("log", sb.toString());
		}
		sb.append(filename);
		file = new File(sb.toString());
		
		FileOutputStream fos = null;
		try {
			InputStream is = urlcon.getInputStream();
			file.createNewFile();
			fos = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			while ((is.read(buf)) != -1) {
				fos.write(buf);
				handler.setSize(buf.length);
			}
			is.close();
		} catch (Exception e) {
			return 0;
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 1;
	}
	
	public abstract class downhandler
	{
		public abstract void setSize(int size);
	}
}
