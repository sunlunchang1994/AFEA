package com.slc.code.exception;

/**
 * mvp模式产生的空指针异常 * Created by on the way on 2018/7/14.
 */

public class MvpNullPointerException extends NullPointerException {

    public MvpNullPointerException() {
        super();
    }


    public MvpNullPointerException(String s) {
        super(s);
    }
}
