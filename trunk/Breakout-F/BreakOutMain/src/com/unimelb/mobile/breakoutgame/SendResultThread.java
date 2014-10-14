package com.unimelb.mobile.breakoutgame;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class SendResultThread extends Thread{
	
	public final List<NameValuePair> params = new ArrayList<NameValuePair>();
	private final static String SERVER_IP = "10.9.251.190";
	private final static String SERVER_PORT = "8080";
	private final static String SERVER_NAME = "BreakOutGameServer";
	
	private final String REQUEST_URL = "http://" + SERVER_IP + ":"
			+ SERVER_PORT + "/" + SERVER_NAME +"/" + "UpdateScore";
	
	public int servScr;
	
	public SendResultThread(int playerId, int score){
		
		params.add(new BasicNameValuePair("uid", playerId+""));
		params.add(new BasicNameValuePair("score", score+""));
	    
	}
	
	public void run(){
		
		try {
			HttpEntity request = new UrlEncodedFormEntity(
					params, HTTP.UTF_8);
			Log.d("url", REQUEST_URL);
			HttpPost post = new HttpPost(REQUEST_URL);
			post.setEntity(request);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				
				byte[] bytes = EntityUtils
						.toByteArray(entity);
				String result = new String(bytes);
				//servScr = Integer.parseInt(result);
				Log.d("result", result);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}