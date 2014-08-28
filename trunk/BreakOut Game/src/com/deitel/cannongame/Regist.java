package com.deitel.cannongame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.unimelb.mobile.breakout.server.po.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Regist extends Activity {
	public Button btn_regist;
	public EditText username, password, conf_pass;

	protected static final int IS_NET_ERROR = 1;

	public String url_regist = "http://10.9.184.221:8080/BreakOutGameServer/RegisterServlet";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			String errorMsg = "";
			Log.d("what", "what=" + msg.what);
			switch (msg.what) {
			case IS_NET_ERROR:
				errorMsg = "Error 404! try again later, please.";
				break;
			}
			Log.d("errorMsg", "error=" + errorMsg);
			Toast toast = Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG);
			toast.show();

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist);
		username = (EditText) findViewById(R.id.username_text);
		password = (EditText) findViewById(R.id.password_text);
		conf_pass = (EditText) findViewById(R.id.confirmpass_text);

		btn_regist = (Button) findViewById(R.id.Button_regist);
		btn_regist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String user = username.getText().toString();
				String pass = password.getText().toString();
				String confirm = conf_pass.getText().toString();
				if (!user.equals("") && !pass.equals("") && !confirm.equals("")) {
					if (!pass.equals(confirm)) {
						Toast toast = Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_LONG);
						toast.show();
					} else {
						final List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("username", user));
						params.add(new BasicNameValuePair("password", pass));
						Thread thread = new Thread() {
							public void run() {
								super.run();
								{
									try {
										HttpEntity requestHttpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
										HttpPost httpPost = new HttpPost(url_regist);
										httpPost.setEntity(requestHttpEntity);
										HttpClient httpClient = new DefaultHttpClient();
										HttpResponse httpResponse = httpClient.execute(httpPost);
										if (httpResponse.getStatusLine().getStatusCode() == 200) {
											HttpEntity httpEntity = httpResponse.getEntity();
											byte[] bytes = EntityUtils.toByteArray(httpEntity);
											ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
											User user = new User();
											user = (User) ois.readObject();
											ois.close();
											Intent toCannonGame = new Intent(Regist.this, CannonGame.class);
											Bundle bundle = new Bundle();
											bundle.putSerializable("user", user);
											toCannonGame.putExtras(bundle);
											startActivity(toCannonGame);
											Regist.this.finish();
										} else {
											Message message = Message.obtain();
											message.what = IS_NET_ERROR;
											mHandler.sendMessage(message);
										}
									} catch (IOException e) {
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						};
						thread.start();
					}
				}
			}

		});

	}

}
