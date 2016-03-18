package com.afei.threadtest;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

/**
 * 下载文件线程
 * Created by xiaofei on 2016/3/17 16:27.
 */
public class DownFileThread extends Thread {

    /**
     * 下载url
     */
    private URL url;
    /**
     * 保存文件
     */
    private File file;
    /**
     * 当前线程的id
     */
    private int threadId;

    /**
     * 已下载文件的长度
     */
    private int downloadLength;

    /**
     * 要下载文件的大小
     */
    private int blockSize;

    /**
     * 标志线程是否完成
     */
    private boolean isComplete;

    private FileDownListener fileDownListener;

    public void setFileDownListener (FileDownListener fileDownListener) {
        this.fileDownListener = fileDownListener;
    }

    public DownFileThread (int blockSize, File file, int threadId, URL url) {
        this.blockSize = blockSize;
        this.file = file;
        this.threadId = threadId;
        this.url = url;
    }

    @Override
    public void run () {

        BufferedInputStream bis = null;

        RandomAccessFile raf = null;
        try {

            URLConnection conn = url.openConnection ();
            conn.setAllowUserInteraction (true);

            int startPos = blockSize * (threadId - 1);  //开始位置
            int endPos = blockSize * threadId - 1;   //结束位置

            conn.setRequestProperty ("Range", "bytes=" + startPos + "-" + endPos);

            byte[] buffer = new byte[1024];
            int len = -1;

            bis = new BufferedInputStream (conn.getInputStream ());

            raf = new RandomAccessFile (file, "rwd");

            raf.seek (startPos);

            while ((len = bis.read (buffer, 0, 1024)) != -1) {
                raf.write (buffer, 0, len);
                downloadLength += len;
            }
            Log.e ("TAG", "当前线程下载大小 = " + downloadLength);
            isComplete = true;
        } catch (IOException e) {
            Log.e ("TAG", "error = " + e.getMessage ());
            fileDownListener.setErrorMsg ();
        } finally {
            if (bis != null) {
                try {
                    bis.close ();
                } catch (IOException e) {
                    fileDownListener.setErrorMsg ();
                }
            }
            if (raf != null) {
                try {
                    raf.close ();
                } catch (IOException e) {
                    fileDownListener.setErrorMsg ();
                }
            }
        }
    }

    /**
     * 获取当前下载的长度
     *
     * @return
     */
    public int getDownloadLength () {
        return downloadLength;
    }

    /**
     * 判断当前线程是否下载完成
     *
     * @return
     */
    public boolean isComplete () {
        return isComplete;
    }
}
