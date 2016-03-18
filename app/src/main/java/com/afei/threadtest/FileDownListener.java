package com.afei.threadtest;

/**
 * Created by xiaofei on 2016/3/18 11:30.
 */
public interface FileDownListener {
    void setMax(int maxSize);
    void updateValue(int downSize);
    void setErrorMsg();
}
