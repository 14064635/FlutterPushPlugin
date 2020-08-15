package com.vivo.push.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.ups.VUpsManager;
import com.vivo.pushdemo.test.R;

import java.util.List;

import static com.vivo.push.sample.Utils.updateContent;

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";
    public static MainActivity sMainActivity;
    TextView logText = null;
    ScrollView scrollView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        logText = (TextView) findViewById(R.id.text_log);
        scrollView = (ScrollView) findViewById(R.id.stroll_text);
        PushClient.getInstance(getApplicationContext()).initialize();
    }

    // 更新界面显示内容
    public void updateDisplay() {
        Log.d(TAG, "updateDisplay, cache: "
                + Utils.logStringCache == null ? "null" : Utils.logStringCache.split("\n")[0]);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (logText != null) {
                    logText.setText(Utils.logStringCache);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sMainActivity = this;
        Log.d(TAG, "onResume");
        updateDisplay();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        updateDisplay();
    }

    public void clearLog(View v) {
        Utils.logStringCache = "";
        Utils.setLogText(getApplicationContext(), Utils.logStringCache);
        updateDisplay();
    }

    public void bind(View v) {
        PushClient.getInstance(getApplicationContext()).turnOnPush(new IPushActionListener() {

            @Override
            public void onStateChanged(int state) {
                if (state != 0) {
                    updateContent("打开push异常[" + state + "]");
                } else {
                    updateContent("打开push成功");
                }
            }
        });

    }

    public void unbind(View v) {
        PushClient.getInstance(getApplicationContext()).turnOffPush(new IPushActionListener() {

            @Override
            public void onStateChanged(int state) {
                if (state != 0) {
                    updateContent("关闭push异常[" + state + "]");
                } else {
                    updateContent("关闭push成功");
                }
            }
        });
    }

    // 删除别名
    public void delAlias(View v) {
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textviewGid = new EditText(MainActivity.this);
        textviewGid.setHint("请输入别名");
        layout.addView(textviewGid);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setView(layout);
        builder.setPositiveButton("取消别名",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PushClient.getInstance(getApplicationContext()).unBindAlias(textviewGid
                                .getText().toString(), new IPushActionListener() {

                            @Override
                            public void onStateChanged(int state) {
                                if (state != 0) {
                                    updateContent("取消别名异常[" + state + "]");
                                } else {
                                    updateContent("取消别名成功");
                                }
                            }
                        });
                    }
                });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setLogText(getApplicationContext(), Utils.logStringCache);
    }

    // 设置别名
    public void setAlias(View v) {
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textviewGid = new EditText(MainActivity.this);
        textviewGid.setHint("请输入别名");
        layout.addView(textviewGid);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setView(layout);
        builder.setPositiveButton("设置别名",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PushClient.getInstance(getApplicationContext()).bindAlias(textviewGid
                                .getText().toString(), new IPushActionListener() {

                            @Override
                            public void onStateChanged(int state) {
                                if (state != 0) {
                                    updateContent("设置别名异常[" + state + "]");
                                } else {
                                    updateContent("设置别名成功");
                                }
                            }
                        });
                    }

                });
        builder.show();
    }

    // 设置标签
    public void setTopic(View v) {
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textviewGid = new EditText(MainActivity.this);
        textviewGid.setHint("请输入标签");
        layout.addView(textviewGid);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setView(layout);
        builder.setPositiveButton("设置标签",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PushClient.getInstance(getApplicationContext()).setTopic(textviewGid
                                .getText().toString(), new IPushActionListener() {

                            @Override
                            public void onStateChanged(int state) {
                                if (state != 0) {
                                    updateContent("设置标签异常[" + state + "]");
                                } else {
                                    updateContent("设置标签成功");
                                }
                            }
                        });
                    }

                });
        builder.show();
    }

    // 删除标签
    public void delTopic(View v) {
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textviewGid = new EditText(MainActivity.this);
        textviewGid.setHint("请输入标签");
        layout.addView(textviewGid);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setView(layout);
        builder.setPositiveButton("删除标签",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Push: 删除标签调用方式
                        PushClient.getInstance(getApplicationContext()).delTopic(textviewGid
                                .getText().toString(), new IPushActionListener() {

                            @Override
                            public void onStateChanged(int state) {
                                if (state != 0) {
                                    updateContent("删除标签异常[" + state + "]");
                                } else {
                                    updateContent("删除标签成功");
                                }
                            }
                        });
                    }
                });
        builder.show();
    }

    public void showTopics(View v) {
        List<String> tags = PushClient.getInstance(this).getTopics();
        if (tags == null || tags.size() <= 0) {
            updateContent("当前未设置标签");
        } else {
            StringBuffer sb = new StringBuffer();
            for (String tag : tags) {
                sb.append(tag).append(",");
            }
            sb.setLength(sb.length() - 1);
            updateContent("已设置的标签为：" + sb.toString());
        }
    }

    public void showAlias(View v) {

        String localAlias = PushClient.getInstance(MainActivity.this).getAlias();
        if (TextUtils.isEmpty(localAlias)) {
            updateContent("当前未设置别名");
        } else {
            updateContent("已设置的别名为：" + localAlias);
        }
    }

}
