package com.unimelb.mobile.breakoutgame;

import com.unimelb.mobile.breakout.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	
	private boolean dialogIsDisplayed;
	
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
		
		if(currtlevel>2){
			if(BreakOutMain.breakout.downloaded){
				Intent intent = new Intent();
				intent.setClass(LocalGame.this, CannonGame.class);
				//LocalGame.this.finish();
				LocalGame.this.startActivity(intent);
			}
			else{
				showalertDialog();
			}
		}

		else{
			Intent intent = new Intent();
			intent.setClass(LocalGame.this, CannonGame.class);
			//LocalGame.this.finish();
			LocalGame.this.startActivity(intent);
		}
	}
	
	public void showalertDialog() {
		// create a dialog displaying the given String
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		
		dialogBuilder.setTitle("Alert");
		dialogBuilder.setCancelable(false);
		
			// display number of shots fired and total time elapsed
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
