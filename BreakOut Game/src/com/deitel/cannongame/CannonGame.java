// CannonGame.java
// Main Activity for the Cannon Game app.
package com.deitel.cannongame;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CannonGame extends Activity implements OnClickListener {
	private GestureDetector gestureDetector; // listens for double taps
	private CannonView cannonView; // custom view to display the game
	private Button enter, help;
	// added by JunHan 16/08/2014
	private final static String SERVER_URL = "10.9.250.124";
	private static String FILE_NAME = "test";

	private Button btnDownload; // btnDownload. added by JunHan 15/08/2014

	// Handler. added by JunHan 15/08/2014
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// Do main
		}
	};

	// called when the app first launches
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // call super's onCreate method
		setContentView(R.layout.welcome); // inflate the layout

		enter = (Button) findViewById(R.id.enterBut);

		help = (Button) findViewById(R.id.helpBut);

		btnDownload = (Button) findViewById(R.id.btnDownload);

		enter.setOnClickListener(this);
		help.setOnClickListener(this);
		btnDownload.setOnClickListener(this);

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

	public void onClick(View v) {

		if (v.getId() == R.id.enterBut) {

			setContentView(R.layout.main);
			// get the CannonView
			cannonView = (CannonView) findViewById(R.id.cannonView);

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
	}

	// when the app is pushed to the background, pause it
	@Override
	public void onPause() {
		super.onPause(); // call the super method
		cannonView.stopGame(); // terminates the game
	} // end method onPause

	// release resources
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cannonView.releaseResources();
	} // end method onDestroy

	// called when the user touches the screen in this Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// get int representing the type of action which caused this event
		int action = event.getAction();

		// the user user touched the screen or dragged along the screen
		if (action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_MOVE) {
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
			return true;
		}
	}; // end gestureListener
} // end class CannonGame

/*********************************************************************************
 * (C) Copyright 1992-2012 by Deitel & Associates, Inc. and * Pearson Education,
 * * Inc. All Rights Reserved. * * DISCLAIMER: The authors and publisher of this
 * * book have used their * best efforts in preparing the book. These efforts *
 * include the * development, research, and testing of the theories and programs
 * * * to determine their effectiveness. The authors and publisher make * no *
 * warranty of any kind, expressed or implied, with regard to these * programs *
 * or to the documentation contained in these books. The authors * and publisher
 * * shall not be liable in any event for incidental or * consequential damages
 * in * connection with, or arising out of, the * furnishing, performance, or
 * use of * these programs. *
 *********************************************************************************/