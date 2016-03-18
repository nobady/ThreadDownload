package com.afei.threadtest;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.File;

public class MainActivity extends AppCompatActivity implements FileDownListener{

    private Button button;
    private ProgressBar progressBar;
    private EditText editText;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        button = (Button) findViewById (R.id.button);
        progressBar = (ProgressBar) findViewById (R.id.progressBar);

        editText = (EditText) findViewById (R.id.editText);

        String url = "http://gdown.baidu.com/data/wisegame/91319a5a1dfae322/baidu_16785426.apk";

        String path = Environment.getExternalStorageDirectory()
                + "/afeidownload/";
        File file = new File (path);

        if(!file.exists ()){
            file.mkdirs ();
        }

        progressBar.setProgress (0);

        String fileName = "baidu_16785426.apk";

        DownloadTask task = new DownloadTask (path+fileName,3,url);
        task.setFileDownListener (this);
        task.start ();
    }

    private Handler mHandler = new Handler (){
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage (msg);
            int size = msg.getData ().getInt ("size");
            if(size==-1){
                size = 0;
                Snackbar.make (button,"下载失败",Snackbar.LENGTH_SHORT).show ();
            }
            progressBar.setProgress (size);

            int temp = size / progressBar.getMax ();

            if(temp*100==100){
                editText.setText ("下载完成");
            }

        }
    };


    //设置进度条的最大值
    @Override
    public void setMax (int maxSize) {
        progressBar.setMax (maxSize);
    }

    //更新UI，需要在主线程中完成
    @Override
    public void updateValue (int downSize) {
        Message msg = new Message ();
        msg.getData ().putInt ("size",downSize);
        mHandler.sendMessage (msg);
    }

    @Override
    public void setErrorMsg () {
        Message msg = new Message ();
        msg.getData ().putInt ("size",-1);
        mHandler.sendMessage (msg);
    }
}
