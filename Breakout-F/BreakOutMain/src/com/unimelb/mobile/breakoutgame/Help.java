package com.unimelb.mobile.breakoutgame;

import com.unimelb.mobile.breakout.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class Help extends Activity implements MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener,OnClickListener{
	
	public static final String TAG = "VideoPlayer";
	private VideoView vv;
	private MediaController mc;
	private Button bt;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.help); 
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		vv = (VideoView) findViewById(R.id.helpvideo);
		bt = (Button) findViewById(R.id.helpback);
		mc = new MediaController(this);
		vv.setMediaController(mc);
		
		vv.setVideoURI(Uri.parse("android.resource://com.unimelb.mobile.breakout/"+R.raw.help));
		
		bt.setOnClickListener(this);
		vv.start();

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		vv.start();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.helpback){
			gotoWelcome();
		}
	}
	
	private void gotoWelcome(){
		Intent intent = new Intent();
		intent.setClass(Help.this, BreakOutMain.class);
		Help.this.finish();
		Help.this.startActivity(intent);
	}

}
