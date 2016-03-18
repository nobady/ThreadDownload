package com.afei.threadtest;

import android.util.Log;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by xiaofei on 2016/3/18 11:21.
 */
public class DownloadTask extends Thread {

    private String downUrl;
    private int threadNum;
    private int blockSize;
    private String filePath;

    private int downAllLength = 0;

    private FileDownListener fileDownListener;

    public DownloadTask (String filePath, int threadNum, String downUrl) {
        this.filePath = filePath;
        this.threadNum = threadNum;
        this.downUrl = downUrl;
    }

    public void setFileDownListener (FileDownListener fileDownListener) {
        this.fileDownListener = fileDownListener;
    }

    @Override
    public void run () {
        DownFileThread[] threads = new DownFileThread[threadNum];

        try {
            URL url = new URL (downUrl);

            URLConnection conn = url.openConnection ();

            int fileSize = conn.getContentLength ();
            if (fileSize < 0) {
                throw new RuntimeException ("文件读取失败");
            }
            Log.e ("TAG","文件大小 = "+fileSize);
            //UI类需要实现此接口，比如进度条，设置最大值
            if(fileDownListener==null){
                throw new RuntimeException ("没有实现FileDownListener接口");
            }
            fileDownListener.setMax (fileSize);

            //计算每个线程需要下载的文件大小
            blockSize = (fileSize % threadNum == 0) ? fileSize / threadNum : fileSize / threadNum + 1;

            Log.e ("TAG","每个线程大小 = "+blockSize);
            File file = new File (filePath);

            //创建线程进行下载
            for(int i = 0;i<threads.length;i++){
                threads[i] = new DownFileThread (blockSize,file,i+1,url);
                threads[i].setFileDownListener (fileDownListener);
                threads[i].setName ("Thread:"+i);
                threads[i].start ();
            }

            boolean isFinish = false;

            while (!isFinish){
                isFinish = true;
                downAllLength = 0;
                //统计所有线程的下载长度
                for(int i = 0;i<threads.length;i++){
                    downAllLength+=threads[i].getDownloadLength ();
                    //如果至少有一个线程没有下载完成,然后在进行循环
                    if(!threads[i].isComplete ()){
                        isFinish = false;
                    }
                }
                //通知UI进行更新
                fileDownListener.updateValue (downAllLength);

                Thread.sleep (500);
            }
            Log.e ("TAG","下载大小 = "+downAllLength);
        } catch (Exception e) {
            //如果 出现异常，通知UI
            if(fileDownListener==null){
                throw new RuntimeException ("没有实现FileDownListener接口");
            }
            fileDownListener.setErrorMsg ();
            Log.e ("TAG","错误 = "+e.getMessage ());
            e.printStackTrace ();
        }
    }
}
