package com.slc.afea.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Locale;

/**
 * Created by achang on 2019/1/29.
 */

public class Intents {
    @SuppressWarnings("all")
    private static Intent getBaseIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(32);
        return intent;
    }

    public static void startApp(Context context, String str) {
        if (context != null) {
            Intent baseIntent = getBaseIntent();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("alipays://platformapi/startapp?appId=");
            stringBuilder.append(str);
            baseIntent.setData(Uri.parse(stringBuilder.toString()));
            context.startActivity(baseIntent);
        }
    }

    public static void start(Context context, String str) {
        if (context != null) {
            Intent baseIntent = getBaseIntent();
            baseIntent.setData(Uri.parse(str));
            context.startActivity(baseIntent);
        }
    }

    public static void chat(Context context, String str) {
        if (context != null) {
            Intent baseIntent = getBaseIntent();
            baseIntent.setData(Uri.parse(String.format(Locale.CHINA, "alipays://platformapi/startapp?appId=20000167&tUserId=%s&tUserType=1&tLoginId=&returnAppId=back&targetAppId=back", new Object[]{str})));
            context.startActivity(baseIntent);
        }
    }

    public static void profile(Context context, String str) {
        if (context != null) {
            Intent baseIntent = getBaseIntent();
            baseIntent.setData(Uri.parse(String.format(Locale.CHINA, "alipays://platformapi/startapp?appId=20000166&actionType=profile&returnAppId=back&targetAppId=back&source=&userId=%s&loginId=", new Object[]{str})));
            context.startActivity(baseIntent);
        }
    }


}
