package com.unimelb.mobile.breakoutgame;

import com.deitel.cannongame.R;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
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
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // call super's onCreate method
		setContentView(R.layout.localgame); // inflate the layout

		choseLvl = (EditText) findViewById(R.id.choseLvl);
		but = (Button) findViewById(R.id.decide);
		
		but.setOnClickListener(this);
		
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
		Intent intent = new Intent();
		intent.setClass(LocalGame.this, CannonGame.class);
		//LocalGame.this.finish();
		LocalGame.this.startActivity(intent);
	}
}
