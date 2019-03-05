package com.slc.code.exception;

/**
 * mvp没有初始化的异常 * Created by on the way on 2018/7/14.
 */

public class MvpUninitializedException extends IllegalStateException {
    public MvpUninitializedException() {
        super();
    }


    public MvpUninitializedException(String s) {
        super(s);
    }
}
