package com.webviewsharetext;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/*
* 实现功能
* 1，webview长按选中文字，获取到选中文字
* 2，重写长按后弹出的分享菜单
* 3，webview与h5的js交互
* 4，把选中的文字放入到一个图片模板中，作为一个图片分享出去（待做）
*
*
* 这里有些坑，你自己重写的分享弹窗在vivo,坚果机型上不起效果
* 好奇心日报对这些机型也是没有办法，没有处理的
* 有问题可以加我微信：2501902696（备注安卓）
*
*
* */
public class MainActivity extends AppCompatActivity {
    private WebView webView;
    MenuItem.OnMenuItemClickListener handler;
    private ActionMode mActionMode = null;

    String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        String url = "file:///android_asset/demo.html";
        webView.loadUrl(url);

        //用来监听Contextual Action Bar的点击事件
        handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                // do the menu action
                switch (item.getItemId()) {
                    case 1:
                        //这里调取你自己的分享界面
                        Log.i("you click", "分享");
                        webText();
                        if (mActionMode != null) {
                            mActionMode.finish();
                            webView.clearFocus();//移除高亮显示,如果不移除在三星s6手机上会崩溃
                        }
                        break;
                    case 2:
                        Log.i("you click", "下载");
                        break;
                }
                return true;
            }
        };
    }


    //调用h5的js接口，通过js的getValue()获取选中文字，这个js方法让h5工程师加到网页中
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void webText() {
        webView.evaluateJavascript("getValue()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "youclickvalue=" + value);
                Toast.makeText(MainActivity.this, "你要分享的文案是：" + value, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*看一看menu.add方法的参数：
  第一个int类型的group ID参数，代表的是组概念，你可以将几个菜单项归为一组，以便更好的以组的方式管理你的菜单按钮。
  第二个int类型的item ID参数，代表的是项目编号。这个参数非常重要，一个item ID对应一个menu中的选项。在后面使用菜单的时候，就靠这个item ID来判断你使用的是哪个选项。
  第三个int类型的order ID参数，代表的是菜单项的显示顺序。默认是0，表示菜单的显示顺序就是按照add的显示顺序来显示。
  第四个String类型的title参数，表示选项中显示的文字。*/
    //重写弹出Contextual Action Bar
    @Override
    public void onActionModeStarted(ActionMode mode) {
        if (mActionMode == null) {
            mActionMode = mode;
            Menu menu = mode.getMenu();
            menu.clear();
            menu.add(0, 1, 0, "我的分享").setOnMenuItemClickListener(handler);
            menu.add(0, 2, 0, "我的下载").setOnMenuItemClickListener(handler);
        }
        super.onActionModeStarted(mode);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        mActionMode = null;
        webView.clearFocus();//移除高亮显示,如果不移除在三星s6手机上会崩溃
        super.onActionModeFinished(mode);
    }

}
