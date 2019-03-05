package com.slc.afea.contract;

public interface OnResultListener<T> {
    void onSucceed(T data);

    void onError(String errorCode, String errorMsg);
}