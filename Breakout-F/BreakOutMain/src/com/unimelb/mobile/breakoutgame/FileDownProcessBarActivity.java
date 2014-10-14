package com.unimelb.mobile.breakoutgame;

import com.unimelb.mobile.breakout.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FileDownProcessBarActivity extends Activity {
	 /** Called when the activity is first created. */
    private static final String Path="http://192.168.0.3:8080/BreakOutGameServer/DownloadServlet?fileName=levels";
    private ProgressBar progressBar;
    private TextView textView;
    private Button button;
    private int FileLength;
    private int DownedFileLength=0;
    private InputStream inputStream;
    private URLConnection connection;
    private OutputStream outputStream;
    
    public boolean success;
    private boolean dialogIsDisplayed;
    
    public static FileDownProcessBarActivity fpba;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_down_process_bar);
        progressBar=(ProgressBar) findViewById(R.id.progressBar1);
        textView=(TextView) findViewById(R.id.tvDownload);
        fpba = this;
        success = false;
        dialogIsDisplayed = false;
        button=(Button) findViewById(R.id.btnDL);
        button.setOnClickListener(new ButtonListener());
    }
    
    class ButtonListener implements OnClickListener{
 
        @Override
        public void onClick(View v) {
            DownedFileLength=0;
            // TODO Auto-generated method stub
           Thread thread=new Thread(){
             public void run(){
                 try {
                    DownFile(Path);
                } catch (Exception e) {
                    // TODO: handle exception
                }
             }
           };
           thread.start();
        }  
    }
    private Handler handler=new Handler()
    {
         public void handleMessage(Message msg)
        {
        if (!Thread.currentThread().isInterrupted()) {
            switch (msg.what) {
            case 0:
                progressBar.setMax(FileLength);
                Log.i("length", progressBar.getMax()+""); 
                break;
            case 1:
                progressBar.setProgress(DownedFileLength);
                int x=DownedFileLength*100/FileLength;
                textView.setText(x+"%");
                break;
            case 2:
            	success = true;
            	updatedownload();
                Toast.makeText(getApplicationContext(), "Download Completed", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
            }
        }  
        }
          
    };
    
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
 
    private void DownFile(String urlString)
    {
        try {
             URL url=new URL(urlString);
             connection=url.openConnection();
             connection.setReadTimeout(5000);
             inputStream=connection.getInputStream();
             
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
        	showalertDialog();
            e1.printStackTrace();
            return;
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	showalertDialog();
            e.printStackTrace();
            return;
        }

        String savePAth=Environment.getExternalStorageDirectory()+"/DownFile";
        File file1=new File(savePAth);
        if (!file1.exists()) {
            file1.mkdir();
        }
        String savePathString=Environment.getExternalStorageDirectory()+"/DownFile/"+"levels.txt";
        File file =new File(savePathString);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  
        }

        Message message=new Message();
        try {
            outputStream=new FileOutputStream(file);
            byte [] buffer=new byte[1024*4];
            FileLength=connection.getContentLength();
            message.what=0;
            handler.sendMessage(message);
            while (DownedFileLength<FileLength) {
                outputStream.write(buffer);
                DownedFileLength+=inputStream.read(buffer);
                Log.i("-------->", DownedFileLength+"");
                Message message1=new Message();
                message1.what=1;
                handler.sendMessage(message1);
            }
            Message message2=new Message();
            message2.what=2;
            handler.sendMessage(message2);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
	public void showalertDialog() {
		// create a dialog displaying the given String
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		
		dialogBuilder.setTitle("Alert");
		dialogBuilder.setCancelable(false);
		
			// display number of shots fired and total time elapsed
		dialogBuilder.setMessage("Connection Timeout!");
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
