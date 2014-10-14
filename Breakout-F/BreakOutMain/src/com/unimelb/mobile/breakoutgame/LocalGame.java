package com.unimelb.mobile.breakoutgame;

import java.io.FileInputStream;

import org.apache.http.util.EncodingUtils;

import com.unimelb.mobile.breakout.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LocalGame extends Activity implements OnClickListener{
	
	private EditText choseLvl;
	private Button but;
	public int currtlevel;
	public static LocalGame localgame;
	
	private boolean dialogIsDisplayed;
	private boolean downloaded;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // call super's onCreate method
		setContentView(R.layout.localgame); // inflate the layout

		choseLvl = (EditText) findViewById(R.id.choseLvl);
		but = (Button) findViewById(R.id.decide);
		
		but.setOnClickListener(this);
		dialogIsDisplayed = false;
		currtlevel = 1;
		localgame = this;
		// allow volume keys to set game volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	} // end method onCreate

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.decide){
			currtlevel = Integer.parseInt(choseLvl.getText().toString());
			startGame();
		}
	}
	private void startGame(){
		
		if(currtlevel<1||currtlevel>10){
			showalertDialog(1); 
		}
		
		else if(currtlevel>2&&currtlevel<11){
			
			downloaded = checkDownload();
			
			if(downloaded){
				Intent intent = new Intent();
				intent.setClass(LocalGame.this, CannonGame.class);
				//LocalGame.this.finish();
				LocalGame.this.startActivity(intent);
			}
			else{
				showalertDialog(2);
			}
		}

		else{
			Intent intent = new Intent();
			intent.setClass(LocalGame.this, CannonGame.class);
			//LocalGame.this.finish();
			LocalGame.this.startActivity(intent);
		}
	}
	
	public boolean checkDownload(){
		String res = "false";
		try{
			FileInputStream fin = openFileInput("BKT-DownloadConfig");
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
		return Boolean.parseBoolean(res);
	}
	
	public void showalertDialog(int i) {
		// create a dialog displaying the given String
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		
		dialogBuilder.setTitle("Alert");
		dialogBuilder.setCancelable(false);
		
		if(i==1){
			dialogBuilder.setMessage("Incorrect input, only 1-10 permitted!");
			dialogBuilder.setPositiveButton("ok",
					new DialogInterface.OnClickListener() {
							// called when "Reset Game" Button is pressed
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialogIsDisplayed = false;
							}
						}
						);

			this.runOnUiThread(new Runnable() {
				public void run() {
					dialogIsDisplayed = true;
					dialogBuilder.show(); // display the dialog
				} 
			} 
			);
		}
		
		if(i==2){
			dialogBuilder.setMessage("Please download this level first!");
			dialogBuilder.setPositiveButton("ok",
					new DialogInterface.OnClickListener() {
							// called when "Reset Game" Button is pressed
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialogIsDisplayed = false;
							}
						}
						);

			this.runOnUiThread(new Runnable() {
				public void run() {
					dialogIsDisplayed = true;
					dialogBuilder.show(); // display the dialog
				} 
			} 
			);
		}
	}
}
