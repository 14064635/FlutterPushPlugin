package com.vivo.pushupstest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vivo.push.ups.CodeResult;
import com.vivo.push.ups.TokenResult;
import com.vivo.push.ups.UPSRegisterCallback;
import com.vivo.push.ups.UPSTurnCallback;
import com.vivo.push.ups.VUpsManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "VIVO_PUSH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void register(View view) {
        //请分别替换下方的值为应用在开放平台申请的appId appKey appSecret,另外在AndroidManifest.xml中也需要替换
        String appId = "xxx";
        String appKey = "xxx";
        String appSecret = "xxx";
        VUpsManager.getInstance().registerToken(this, appId, appKey, appSecret, new UPSRegisterCallback() {
            @Override
            public void onResult(TokenResult tokenResult) {
                if(tokenResult.getReturnCode() == 0){
                    Log.d(TAG,"注册成功,注册的Token为" + tokenResult.getToken());
                    Toast.makeText(MainActivity.this,"注册成功,注册的Token为" + tokenResult.getToken(),Toast.LENGTH_LONG).show();
                }else{
                    Log.d(TAG,"注册失败");
                    Toast.makeText(MainActivity.this,"注册失败"+tokenResult.getReturnCode(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void unregister(View view) {
        VUpsManager.getInstance().unRegisterToken(this, new UPSRegisterCallback() {
            @Override
            public void onResult(TokenResult tokenResult) {
                if (tokenResult.getReturnCode() == 0) {
                    Log.d(TAG, "解注册成功");
                    Toast.makeText(MainActivity.this,"解注册成功",Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "解注册失败");
                    Toast.makeText(MainActivity.this,"解注册失败",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void turnoff(View view) {
        VUpsManager.getInstance().turnOffPush(this, new UPSTurnCallback() {
            @Override
            public void onResult(CodeResult codeResult) {
                if (codeResult.getReturnCode() == 0) {
                    Log.d(TAG, "关闭成功");
                    Toast.makeText(MainActivity.this,"关闭成功",Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "关闭失败");
                    Toast.makeText(MainActivity.this,"关闭失败",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
