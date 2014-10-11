package com.unimelb.mobile.breakoutgame;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.unimelb.mobile.breakout.R;
import com.unimelb.mobile.breakoutgame.*;

public class BreakOutMain extends Activity implements OnClickListener{

	public static BreakOutMain breakout;
	
	private SendResultThread sendresult;
	
	// view settings 	
	private Button enter;
	private Button help;
	private Button btnDownload; // btnDownload. added by JunHan 15/08/2014
	private Button topten;
	private Button localGame;
	
	public boolean choselvl;
	public boolean downloaded;
	
	public boolean fromregist;
	
	public int playerId;
	public String playerName;
	public int servRec;

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
		downloaded = checkDownload();
		fromregist = Login.login.fromregist;
		
		if(!fromregist){
			playerId = Login.login.player.getUid();
			playerName = Login.login.player.getUsername();
			servRec = Login.login.player.getScore();
		}
		else{
			playerId = Regist.regist.player.getUid();
			playerName = Regist.regist.player.getUsername();
			servRec = Regist.regist.player.getScore();
		}
		
		if(playerId!=0){
			updateScore();
		}
		
		enter.setOnClickListener(this);
		help.setOnClickListener(this);
		btnDownload.setOnClickListener(this);
		topten.setOnClickListener(this);
		localGame.setOnClickListener(this);
		// allow volume keys to set game volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
	
	public void updateScore(){
		
		String prevRec = readRecord();
		String[] records = prevRec.split(",");
		String[] playerIdd = new String[records.length/2];
		String[] playerScr = new String[records.length/2];
		int mark=0;
		for(int i =0; i<records.length;i++){
			if(records.length>1){
				if(i%2==0){
					playerIdd[mark] = records[i];
					playerScr[mark] = records[i+1];
					mark++;
				}
			}
		}
		
		boolean exist = false;
		for(int i = 0; i<playerIdd.length;i++){
			if(playerName.equals(playerIdd[i])){
				exist = true;
				if(servRec < Integer.parseInt(playerScr[i])){
					// send result to server.
					sendResToServ(playerId,Integer.parseInt(playerScr[i]));
					Log.d("send out:",playerName);
					servRec = Integer.parseInt(playerScr[i]);
				}
				else{
					// record server score to local file
					playerScr[i] = Integer.toString(servRec);
				}
			}
		}
		
		if(!exist){
			// not such record in local, add it.
			prevRec+= playerName +","+servRec+",";
		}
		else{
			prevRec ="";
			for(int i = 0; i<playerIdd.length;i++){
				prevRec +=  playerIdd[i] +","+playerScr[i]+",";
			}
		}

		try {
			FileOutputStream fos = openFileOutput("BKT-Gamerecord",
					Context.MODE_PRIVATE);
			fos.write(prevRec.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendResToServ(int playerId, int score){
		
		sendresult = new SendResultThread(playerId,score);
		sendresult.start();
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

	
	public void onClick(View v) {

		if (v.getId() == R.id.enterBut) {
			choselvl = false;
			startGame();
		}
		
		// download. Added by JunHan 15/08/2014
		if (v.getId() == R.id.btnDownload) {
			updatedownload();
			downloaded = true;
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
	
	public void updatedownload(){
		
		String res = "true";
		try {
			FileOutputStream fos = openFileOutput("BKT-DownloadConfig",
					Context.MODE_PRIVATE);
			fos.write(res.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		Intent intent = new Intent();
		intent.setClass(BreakOutMain.this, FileDownProcessBarActivity.class);
		BreakOutMain.this.startActivity(intent);
		
	}
	
	private void showHelp(){
		Intent intent = new Intent();
		intent.setClass(BreakOutMain.this, Help.class);
		//BreakOutMain.this.finish();
		BreakOutMain.this.startActivity(intent);
		
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
