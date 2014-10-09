package com.unimelb.mobile.breakoutgame;

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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.deitel.cannongame.R;
import com.unimelb.mobile.breakout.server.po.User;

public class Login extends Activity {
	protected static final int IS_LOGIN_ERROR = 1;
	protected static final int IS_NET_ERROR = 2;
	public EditText username, password;
	public Button Btn_login, Btn_regist, Btn_forgetpwd;
	public String url_login = "http://10.9.184.221:8080/BreakOutGameServer/LoginServlet";
	public String out = null;
	// public byte[] bytes;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			String errorMsg = "";
			Log.d("what", "what=" + msg.what);
			switch (msg.what) {
			case IS_LOGIN_ERROR:
				// out = msg.obj.toString();
				errorMsg = "Username or Password error! Please input again.";
				break;

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
		setContentView(R.layout.login);
		username = (EditText) findViewById(R.id.username_text);
		password = (EditText) findViewById(R.id.password_text);

		Btn_login = (Button) findViewById(R.id.Button_login);
		Btn_regist = (Button) findViewById(R.id.Button_regist);
		Btn_forgetpwd = (Button) findViewById(R.id.Button_forgetpass);

		Btn_regist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(Login.this, Regist.class);
				startActivity(intent);

			}
		});
		// Login Button
		Btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// final HttpPost httpRequest = new HttpPost(url_login);
				String user = username.getText().toString();
				String pass = password.getText().toString();

				final List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", user));
				params.add(new BasicNameValuePair("password", pass));
				if ((!user.equals("")) && (!pass.equals(""))) {
					Thread thread = new Thread() {
						public void run() {
							// Looper.prepare();
							super.run();
							try {
								HttpEntity requestHttpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
								HttpPost httpPost = new HttpPost(url_login);
								httpPost.setEntity(requestHttpEntity);
								HttpClient httpClient = new DefaultHttpClient();
								HttpResponse httpResponse = httpClient.execute(httpPost);
								if (httpResponse.getStatusLine().getStatusCode() == 200) {
									HttpEntity httpEntity = httpResponse.getEntity();

									/*
									 * int id; String username = ""; int score;
									 */
									byte[] bytes = EntityUtils.toByteArray(httpEntity);
									ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
									User user = new User();
									user = (User) ois.readObject();
									/*
									 * id = user.getUid(); username =
									 * user.getUsername(); score =
									 * user.getScore();
									 */
									ois.close();
									if (user.getUid() != 0) {
										Intent toCannonGame = new Intent(Login.this, CannonGame.class);
										/*
										 * toCannonGame.putExtra("ID", id);
										 * toCannonGame.putExtra("username",
										 * username);
										 * toCannonGame.putExtra("score",
										 * score);
										 */
										Bundle bundle = new Bundle();
										bundle.putSerializable("user", user);
										toCannonGame.putExtras(bundle);
										startActivity(toCannonGame);
										Login.this.finish();
									} else {
										Message message = Message.obtain();
										// message.obj = httpEntity;
										message.what = IS_LOGIN_ERROR;
										mHandler.sendMessage(message);
									}
									// }
								} else {
									Message message = Message.obtain();
									message.what = IS_NET_ERROR;
									mHandler.sendMessage(message);
								}

							} catch (ParseException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						};

					};
					thread.start();

				}
			}
		});

	}
}
