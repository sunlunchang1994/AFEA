package com.slc.code.exception;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.slc.code.ui.utils.ActivityUtil;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * Created by slc on 18/2/5.
 */
public class DefCrashHandler implements UncaughtExceptionHandler {
    //系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static DefCrashHandler INSTANCE;
    //程序的Context对象
    //private Context mContext;

    private DefCrashHandler() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable ex) {
                        Log.i("handleException", "有异常");
                        if (ex instanceof MvpNullPointerException) {
                            Log.i("handleException", "MainLooper:" + ex.toString());
                        }/* else if (ex instanceof RuntimeException && ex.getCause() instanceof MvpNullPointerException) {
                            Looper.loop();
                            Log.i("handleException", "由MvpNullPointerException导致的RuntimeException");
                            //Log.i("handleException", "MainLooper:" + ex.toString());
                        } */else {
                            ex.printStackTrace();
                            ActivityUtil.getInstance().exit();
                            System.exit(0);
                            break;
                        }
                    }
                }
                Log.i("handleException", "跳出");
            }
        });
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static DefCrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new DefCrashHandler();
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        //mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex instanceof MvpNullPointerException) {
            Log.i("handleException", "不必处理");
            return true;
        }
        return false;
    }

}