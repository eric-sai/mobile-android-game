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
    private static final String Path="http://10.9.241.246:8080/BreakOutGameServer/DownloadServlet?fileName=test";
    private ProgressBar progressBar;
    private TextView textView;
    private Button button;
    private int FileLength;
    private int DownedFileLength=0;
    private InputStream inputStream;
    private URLConnection connection;
    private OutputStream outputStream;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_down_process_bar);
        progressBar=(ProgressBar) findViewById(R.id.progressBar1);
        textView=(TextView) findViewById(R.id.tvDownload);
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
                Toast.makeText(getApplicationContext(), "Download Completed", Toast.LENGTH_LONG).show();
                break;
                 
            default:
                break;
            }
        }  
        }
          
    };
 
    private void DownFile(String urlString)
    {
         
        /*
         * 连接到服务器
         */
         
        try {
             URL url=new URL(urlString);
             connection=url.openConnection();
             if (connection.getReadTimeout()==5) {
                Log.i("---------->", "当前网络有问题");
                // return;
               }
             inputStream=connection.getInputStream();
             
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
        /*
         * 
         */
        String savePAth=Environment.getExternalStorageDirectory()+"/DownFile";
        File file1=new File(savePAth);
        if (!file1.exists()) {
            file1.mkdir();
        }
        String savePathString=Environment.getExternalStorageDirectory()+"/DownFile/"+"test.xml";
        File file =new File(savePathString);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  
        }
        /*
         * 向SD卡中写入文件,用Handle传递线程
         */
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
}
