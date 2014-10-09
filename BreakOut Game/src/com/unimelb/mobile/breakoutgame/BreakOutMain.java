package com.unimelb.mobile.breakoutgame;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.deitel.cannongame.R;
import com.unimelb.mobile.breakoutgame.*;

public class BreakOutMain extends Activity implements OnClickListener{

	// added by JunHan 16/08/2014
	private final static String SERVER_URL = "10.9.250.124";
	public static BreakOutMain breakout;
	private static String FILE_NAME = "test";
	
	// view settings 	
	private Button enter;
	private Button help;
	private Button btnDownload; // btnDownload. added by JunHan 15/08/2014
	private Button topten;
	private Button localGame;
	
	public boolean choselvl;

	// called when the app first launches
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // call super's onCreate method
		setContentView(R.layout.welcome); // inflate the layout

		enter = (Button) findViewById(R.id.enterBut);

		help = (Button) findViewById(R.id.helpBut);

		btnDownload = (Button) findViewById(R.id.btnDownload);
		
		topten = (Button) findViewById(R.id.topBut);
		
		localGame = (Button) findViewById(R.id.localBut);
		
		choselvl = false;
		breakout = this;

		enter.setOnClickListener(this);
		help.setOnClickListener(this);
		btnDownload.setOnClickListener(this);
		topten.setOnClickListener(this);
		localGame.setOnClickListener(this);
		// allow volume keys to set game volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	} // end method onCreate

	
	public void onClick(View v) {

		if (v.getId() == R.id.enterBut) {
			choselvl = false;
			startGame();
		}
		
		// download. Added by JunHan 15/08/2014
		if (v.getId() == R.id.btnDownload) {
			downLoad();
		}
		
		if (v.getId() == R.id.helpBut) {
			showHelp();
		}
		
		if(v.getId() == R.id.topBut){
			gotoTopten();
		}
		
		if(v.getId() == R.id.localBut){
			choselvl = true;
			gotoChosedGame();
		}
	}
	
	private void gotoTopten(){
		Intent intent = new Intent();
		intent.setClass(BreakOutMain.this, Topten.class);
		BreakOutMain.this.finish();
		BreakOutMain.this.startActivity(intent);
	}

	private void startGame(){
		Intent intent = new Intent();
		intent.setClass(BreakOutMain.this, CannonGame.class);
		//BreakOutMain.this.finish();
		BreakOutMain.this.startActivity(intent);
	}
	
	private void gotoChosedGame(){
		Intent intent = new Intent();
		intent.setClass(BreakOutMain.this, LocalGame.class);
		//BreakOutMain.this.finish();
		BreakOutMain.this.startActivity(intent);
		
	}
	
	private void downLoad(){
		
	}
	
	private void showHelp(){
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
