package com.unimelb.mobile.breakoutgame;

import java.io.FileInputStream;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.unimelb.mobile.breakout.R;
import com.unimelb.mobile.breakoutgame.*;

public class Topten extends Activity implements OnClickListener{

	private TextView firstScore;
	private TextView secondScore;
	private TextView thirdScore;
	private TextView forthScore;
	private TextView fifthScore;
	private TextView sixthScore;
	private TextView seventhScore;
	private TextView eighthScore;
	private TextView ninethScore;
	private TextView tenthScore;
	
	private TextView firstId;
	private TextView secId;
	private TextView thirdId;
	private TextView forthId;
	private TextView fifthId;
	private TextView sixthId;
	private TextView sevnId;
	private TextView eighttId;
	private TextView nineId;
	private TextView tenId;
	
	private Button back;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // call super's onCreate method
		setContentView(R.layout.topten); // inflate the layout

		// Display the ladder scores:
		firstScore = (TextView) findViewById(R.id.first);
		secondScore = (TextView) findViewById(R.id.second);
		thirdScore = (TextView) findViewById(R.id.third);
		forthScore = (TextView) findViewById(R.id.forth);
		fifthScore = (TextView) findViewById(R.id.fifth);
		sixthScore = (TextView) findViewById(R.id.sixth);
		seventhScore = (TextView) findViewById(R.id.seventh);
		eighthScore = (TextView) findViewById(R.id.eighth);
		ninethScore = (TextView) findViewById(R.id.nineth);
		tenthScore = (TextView) findViewById(R.id.tenth);
		
		firstId = (TextView) findViewById(R.id.bk1);
		secId = (TextView) findViewById(R.id.textView2);
		thirdId = (TextView) findViewById(R.id.textView3);
		forthId= (TextView) findViewById(R.id.textView4);
		fifthId= (TextView) findViewById(R.id.textView5);
		sixthId= (TextView) findViewById(R.id.textView6);
		sevnId= (TextView) findViewById(R.id.sveniD);
		eighttId= (TextView) findViewById(R.id.textView8);
		nineId= (TextView) findViewById(R.id.textView9);
		tenId = (TextView) findViewById(R.id.textView10);
		
		back = (Button) findViewById(R.id.back);
		
		back.setOnClickListener(this);
		displayTopTen();
		
	} // end method onCreate
	
	public void displayTopTen(){
		
		//get top ten scores from record file.
		String[] records = readRecord().split(",");
		String[] playerId = new String[records.length/2];
		String[] playerScr = new String[records.length/2];
		int mark=0;
		for(int i =0; i<records.length;i++){
			if(records.length>1){
				if(i%2==0){
					playerId[mark] = records[i];
					playerScr[mark] = records[i+1];
					mark++;
				}
			}
		}

		for(int i=0;i<playerScr.length;i++){
			for(int j=0;j<playerScr.length;j++){
				String temp;
				String tempId;
				if(Integer.parseInt(playerScr[i]) > Integer.parseInt(playerScr[j])){
					temp = playerScr[i];
					tempId = playerId[i];
					
					playerScr[i] = playerScr[j];
					playerId[i] = playerId[j];
					
					playerScr[j] = temp;
					playerId[j] = tempId;
				}
			}
		}
		
		String[] scores = new String[10];
		String[] ids = new String[10];
		
		for(int i=0;i<10;i++){
			scores[i] = "-";
			ids[i] = "-";
		}
		int num = playerScr.length;
		if(num>=10)
			num = 10;
		for(int i =0; i<num;i++){
			scores[i] = playerScr[i];
			ids[i] = playerId[i];
		}
		
		String blanks = "                  ";
		
		//Display top ten:
		firstScore.setText(scores[0]);
		secondScore.setText(scores[1]);
		thirdScore.setText(scores[2]);
		forthScore.setText(scores[3]);
		fifthScore.setText(scores[4]);
		sixthScore.setText(scores[5]);
		seventhScore.setText(scores[6]);
		eighthScore.setText(scores[7]);
		ninethScore.setText(scores[8]);
		tenthScore.setText(scores[9]);
		
		firstId.setText(ids[0]);
		secId.setText(ids[1]);
		thirdId.setText(ids[2]);
		forthId.setText(ids[3]);
		fifthId.setText(ids[4]);
		sixthId.setText(ids[5]);
		sevnId.setText(ids[6]);
		eighttId.setText(ids[7]);
		nineId.setText(ids[8]);
		tenId.setText(ids[9]);
		
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

	@Override
	public void onClick(View v) {
		
		if (v.getId() == R.id.back) {
			Log.d("click:","button");
			gotoWelcome();
		}
		// TODO Auto-generated method stub
	}
	private void gotoWelcome(){
		Intent intent = new Intent();
		intent.setClass(Topten.this, BreakOutMain.class);
		Topten.this.finish();
		Topten.this.startActivity(intent);
	}
}
