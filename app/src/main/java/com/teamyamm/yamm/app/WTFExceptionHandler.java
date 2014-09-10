package com.teamyamm.yamm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.logentries.android.AndroidLogger;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Created by parkjiho on 9/10/14.
 */
public class WTFExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {

    private final Activity myContext;
    private final String LINE_SEPARATOR = "\n";
    private SharedPreferences prefs;

    public WTFExceptionHandler(Activity context, SharedPreferences prefs) {
        myContext = context;
        this.prefs = prefs;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ User Info ***********\n");
        errorReport.append("Email: " + prefs.getString(BaseActivity.USER_EMAIL, ""));
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ Yamm App Info ***********\n");

        String name, code;
        try {
            PackageInfo pInfo = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0);
            name = myContext.getString(R.string.app_version_name);
            code = pInfo.versionCode+"";
        }catch(PackageManager.NameNotFoundException e){
            Log.e("WTFExceptionHandler/uncaughtException","Cannot Get Package");
            name = "n/a";
            code = "n/a";
        }catch(NullPointerException e){
            Log.e("WTFExceptionHandler/uncaughtException","NullPointer In Package");
            name = "n/a";
            code = "n/a";
        }

        errorReport.append("Application Status: " + BaseActivity.CURRENT_APPLICATION_STATUS);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("App Version Name: " + name);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("App Version Code: " + code + "(Might be Wrong)");

        Intent intent = new Intent(myContext, SplashScreen.class);
        intent.putExtra("error", errorReport.toString());
        Log.wtf("WTFExceptionHandler", errorReport.toString());

        myContext.startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    public static void sendLogToServer(Context context, String report){
        Log.e("WTFExceptionHandler/sendLogToServer","Sending Log to Server");
        AndroidLogger logger = AndroidLogger.getLogger(context, "6036cb0d-331d-4124-8245-e93f91de1388", false);
        logger.error(report);
    }
}
