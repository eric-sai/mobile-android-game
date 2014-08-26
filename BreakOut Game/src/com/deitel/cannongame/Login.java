package com.deitel.cannongame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.unimelb.mobile.breakout.server.po.User;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class Login extends Activity {
	public EditText username, password;
	public Button Btn_login, Btn_regist, Btn_forgetpwd;
	public String url_login = "http://10.9.250.229:8080/BreakOutGameServer/LoginServlet";
	public List<NameValuePair> params = new ArrayList<NameValuePair>();
	public String out = null;
	public Object obj = null;
	public byte[] bytes;
	

	// public String url =
	// "http://192.168.1.100:8080/BreakOutGameServer/LoginServlet?username=junhan&password=junhan";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		username = (EditText) findViewById(R.id.username_text);
		password = (EditText) findViewById(R.id.password_text);

		Btn_login = (Button) findViewById(R.id.Button_login);
		Btn_regist = (Button) findViewById(R.id.Button_regist);
		Btn_forgetpwd = (Button) findViewById(R.id.Button_forgetpass);

//		Btn_regist.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(Login.this, Regist.class);
//				startActivity(intent);
//
//			}
//		});
		
		// Login Button
		Btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strUser = username.getText().toString();
				String pass = password.getText().toString();

				
				if ((!strUser.equals("")) && (!pass.equals(""))) 
				{
					NameValuePair nameValuePairUser = new BasicNameValuePair("username", strUser);  
		            NameValuePair nameValuePairPsd = new BasicNameValuePair("password", pass);  
					params.add(nameValuePairUser);
					params.add(nameValuePairPsd);
				}else{}
//				HttpPost httpRequest = new HttpPost(url_login);
				
				
					new Thread(){public void run() {
						// TODO Auto-generated method stub
					
						try {
						Log.d("log", params.size()+"");
						HttpEntity requestHttpEntity = new UrlEncodedFormEntity(params);  
					    HttpPost httpPost = new HttpPost(url_login);  
					    httpPost.setEntity(requestHttpEntity);  
					    HttpClient httpClient = new DefaultHttpClient();  
					    Log.d("log", httpClient.execute(httpPost).getStatusLine().getStatusCode()+"");
					    HttpResponse httpResponse = httpClient.execute(httpPost);  
					    HttpEntity httpEntity = httpResponse.getEntity();  
					    bytes = EntityUtils.toByteArray(httpEntity);
//					    out = EntityUtils.toString(httpEntity);
					    
				        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
				        User user = new User();
				        user = (User)ois.readObject();
				        Log.d("log", user.getUsername());
				        Log.d("log", user.getPassword());
				        Log.d("log", user.getScore()+"");
				        ois.close();
				        
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
					   
						super.run();
					

}
					}.start();
//					 new AlertDialog.Builder(Login.this).setMessage(user.getScore()).create().show();  

				} 
			});
//			else {
//					Toast toast = Toast.makeText(getApplicationContext(),
//							"Please input your username and password!",
//							Toast.LENGTH_LONG);
//					toast.show();
//				}
//			}
//		});

//		Btn_regist.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//			}
//		});
	}
}

