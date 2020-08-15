package com.vivo.pushupstest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.vivo.push.ups.CodeResult;
import com.vivo.push.ups.UPSTurnCallback;
import com.vivo.push.ups.VUpsManager;

public class MyApplication extends Application {
    private static final String TAG = "VIVO_PUSH";
    @Override
    public void onCreate() {
        super.onCreate();
        VUpsManager.getInstance().turnOnPush(this, new UPSTurnCallback() {
            @Override
            public void onResult(CodeResult codeResult) {
                Log.d(TAG, "初始化成功");
                Toast.makeText(MyApplication.this,"初始化成功",Toast.LENGTH_LONG).show();
            }
        });
    }
}
