// CannonGame.java
// Main Activity for the Cannon Game app.
package com.deitel.cannongame;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.deitel.cannongame.Topten;

public class CannonGame extends Activity implements OnClickListener {
	private GestureDetector gestureDetector; // listens for double taps
	private CannonView cannonView; // custom view to display the game
	private Button enter, help;
	// added by JunHan 16/08/2014
	private final static String SERVER_URL = "10.9.250.124";
	private static String FILE_NAME = "test";
	
	//private TextView currtscoreView;
	

	private Button btnDownload; // btnDownload. added by JunHan 15/08/2014
	private Button topten;
	private TextView currtscoreView;
	private TextView currtLevel;
	private TextView lastScore;
	
	private int totalscore;
	private int currtlevel;
	private VelocityTracker vt;

	// Handler. added by JunHan 15/08/2014
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
				case 1:
					//score updated
					totalscore +=10;
					currtscoreView.setText("   " + Integer.toString(totalscore));
					break;
				case 2:
					//level up
					currtlevel++;
					currtLevel.setText("   " + Integer.toString(currtlevel));
					break;
				case 3:
					//level reseted
					currtlevel = 1;
					totalscore = 0;
					currtscoreView.setText("   " + Integer.toString(totalscore));
					currtLevel.setText("   " + Integer.toString(currtlevel));
					break;
			}
			// Do main
		}
	};
	
	public void sendMessage(int what) {
		Message msg1 = handler.obtainMessage(what);
		handler.sendMessage(msg1);
	}

	// called when the app first launches
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // call super's onCreate method
		setContentView(R.layout.welcome); // inflate the layout

		totalscore = 0;
		currtlevel = 1;
		enter = (Button) findViewById(R.id.enterBut);

		help = (Button) findViewById(R.id.helpBut);

		btnDownload = (Button) findViewById(R.id.btnDownload);
		
		topten = (Button) findViewById(R.id.topBut);

		enter.setOnClickListener(this);
		help.setOnClickListener(this);
		btnDownload.setOnClickListener(this);
		topten.setOnClickListener(this);
		
		vt=null;

		// allow volume keys to set game volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	} // end method onCreate

	/**
	 * write download file to local Modified by JunHan 20/08/2014
	 * */
	public void writeFiles(String name, String file) {
		// Log.d("log", "writeFiles");
		try {
			FileOutputStream fos = this.openFileOutput(name,
					Context.MODE_PRIVATE);
			fos.write(file.getBytes());
			// Log.d("log", new String(file.getBytes()));
			fos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String readRecord(){
		String res ="";
		try{
			FileInputStream fin = openFileInput("BKT-Gamerecord");
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		}
		catch(Exception e){
			Log.e("FileNotFoundException", "Couldn't find or open policy file");
			//e.printStackTrace();
		}
		return res;
	}
	
	public String highstScore(){
		String res = "";
		String[] records = readRecord().split(",");
		int highest = Integer.parseInt(records[0]);
		for(int i =0; i<records.length;i++){
			if(Integer.parseInt(records[i])>highest){
				highest = Integer.parseInt(records[i]);
			}	
		}
		res = Integer.toString(highest);
		return res;
	}
	
	public void onClick(View v) {

		if (v.getId() == R.id.enterBut) {

			setContentView(R.layout.main);
			// get the CannonView
			cannonView = (CannonView) findViewById(R.id.cannonView);
			lastScore = (TextView) findViewById(R.id.lastScore);
			lastScore.setText(highstScore());
			
			currtscoreView = (TextView) findViewById(R.id.currtscoreView);
			currtscoreView.setText(" "+ Integer.toString(totalscore));
			
			currtLevel = (TextView) findViewById(R.id.currtLevel);
			currtLevel.setText(" "+ Integer.toString(currtlevel));
			
			// initialize the GestureDetector
			gestureDetector = new GestureDetector(this, gestureListener);
		}
		
		
		// download. Added by JunHan 15/08/2014
		if (v.getId() == R.id.btnDownload) {

			new Thread() {
				@Override
				public void run() {
					String url = "http://"
							+ SERVER_URL
							+ ":8080/BreakOutGameServer/DownloadServlet?fileName="
							+ FILE_NAME;
					Download load = new Download(url);
					String value = load.downloadAsString();
					Log.d("log", value);
					writeFiles(FILE_NAME + ".xml", value);
					handler.sendEmptyMessage(0);
				}
			}.start();

		}
		if (v.getId() == R.id.helpBut) {
			// test read file function. added by JunHan 20/08/2014
			try {
				String[] s = this.fileList();
				Log.d("log", s[0].toString());
				int len = -1;
				FileInputStream fis = this.openFileInput(s[0].toString());
				byte[] buffer = new byte[1024];
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				while ((len = fis.read(buffer)) != -1) {
					ostream.write(buffer, 0, len);
				}
				fis.close();
				ostream.close();
				String strFile = new String(buffer, "UTF-8");
				// = readFiles(fis);
				Log.d("log", "readFile->" + strFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(v.getId() == R.id.topBut){
			gotoTopten();
		}
	}
	private void gotoTopten(){
		Intent intent = new Intent();
		intent.setClass(CannonGame.this, Topten.class);
		CannonGame.this.finish();
		CannonGame.this.startActivity(intent);
	}

	// when the app is pushed to the background, pause it
	@Override
	public void onPause() {
		super.onPause(); // call the super method
		//cannonView.stopGame(); // terminates the game
	} // end method onPause

	// release resources
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//cannonView.releaseResources();
	} // end method onDestroy

	// called when the user touches the screen in this Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// get int representing the type of action which caused this event
		int action = event.getAction();

		// the user user touched the screen or dragged along the screen
		if (action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_MOVE) {
			if(vt==null){
				vt=VelocityTracker.obtain();
			}
			else{
				vt.clear();
			}
			vt.addMovement(event);
			cannonView.alignCannon(event); // align the cannon
		} // end if

		// call the GestureDetector's onTouchEvent method
		return gestureDetector.onTouchEvent(event);
	} // end method onTouchEvent

	// listens for touch events sent to the GestureDetector
	SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		// called when the user double taps the screen
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			cannonView.fireCannonball(e); // fire the cannonball
			return true; // the event was handled
		} // end method onDoubleTap

		// control finger fling on the screen
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			cannonView.movePad(e1, e2, distanceX, distanceY);
			vt.computeCurrentVelocity(1000);
			cannonView.padVelocity=vt.getXVelocity();
			return true;
		}
	};
}
