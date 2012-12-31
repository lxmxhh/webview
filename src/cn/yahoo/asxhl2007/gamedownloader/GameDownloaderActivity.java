package cn.yahoo.asxhl2007.gamedownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class GameDownloaderActivity extends Activity {
    public static final int SCREEN_ORIENTATION_IGNORE = 0;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 1;
    public static final int SCREEN_ORIENTATION_PORTRAIT = 2;
    
    
    private String urlRoot;
    private String urlResource;
    private String localRoot;
    private int screenOrientation;
    
    private WebView webView;
    
    private WebViewClient webViewClient;
    private boolean sdcardMounted;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        webView = (WebView) findViewById(R.id.webView1);
        webView.setInitialScale(100);
        webView.setScrollBarStyle(WebView.SCROLLBAR_POSITION_DEFAULT);
        webView.setVerticalScrollbarOverlay(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setHorizontalScrollbarOverlay(false);
//        webView.clearCache(true);
        
        webViewClient = new MyWebViewClient();
        
        webView.setWebViewClient(webViewClient);
        
        webView.setWebChromeClient(new MyWebChromeClient());

        WebSettings settings = webView.getSettings();
        
        settings.setPluginState(PluginState.ON);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setLightTouchEnabled(false);
        
        settings.setDomStorageEnabled(true);
        settings.setSaveFormData(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        
        
        
        urlRoot = getIntent().getStringExtra("urlRoot");
        urlResource = getIntent().getStringExtra("urlResource");
        localRoot = getIntent().getStringExtra("localRoot");
        screenOrientation = getIntent().getIntExtra("landspace", 0);
        if(screenOrientation == SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else if(screenOrientation == SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        webView.loadUrl(urlRoot + urlResource);

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdcardMounted = true;
        }else{
            if(urlRoot.startsWith("http")){
                Toast.makeText(this, "sd卡未加载，将不会下载到本地！", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private class MyWebViewClient extends WebViewClient{

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            if(urlRoot.startsWith("http") && sdcardMounted){
                download(url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(urlRoot.startsWith("http") && sdcardMounted){
                download(url);
            }
        }
    }
    
    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder ad = new AlertDialog.Builder(GameDownloaderActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert).setTitle("提示信息").setMessage(message)
                    .setPositiveButton("确认", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });
            ad.setCancelable(false);
            ad.create();
            ad.show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

            AlertDialog.Builder ad = new AlertDialog.Builder(GameDownloaderActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert).setTitle("提示信息").setMessage(message)
                    .setPositiveButton("确认", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).setNegativeButton("取消", new AlertDialog.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }});
            ad.setCancelable(false);
            ad.create();
            ad.show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                final JsPromptResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("url").setMessage(message);
            final EditText et = new EditText(view.getContext());
            et.setSingleLine();
            et.setText(defaultValue);
            builder.setView(et);
            builder.setPositiveButton("确定", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm(et.getText().toString());
                }
            }).setNeutralButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });

            builder.setOnCancelListener(new OnCancelListener() {
                
                @Override
                public void onCancel(DialogInterface dialog) {
                    result.cancel();
                }
            });
            
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.i("WebViewConsoleMessage", consoleMessage.message());
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            finish();
        }


    }
    
    private void download(final String uri){

        new Thread() {
            public void run() {
                String _uri = "";
                if(!uri.contains("http://")){
                    _uri = urlRoot + uri;
                }else{
                    _uri = uri;
                }
                Log.i("GameDownloader", "下载文件：" + _uri);
                HttpGet httpGet = new HttpGet(_uri);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse;
                try {
                    httpResponse = httpClient.execute(httpGet);
                    String path = localRoot + _uri.substring(urlRoot.length());
                    saveFile(path, httpResponse.getEntity().getContent());
                } catch (Exception e) {
                    Log.i("GameDownloader", "文件下载失败, uri: " + _uri + "    msg:" + e.getMessage());
                    e.printStackTrace();
                    
                }
            }
        }.start();
    }
    
    private void saveFile(String path, InputStream is){
        File file = new File(path);
        if(!file.getParentFile().exists()){
            Log.i("Gamedownloader", "mkdirs: " + file.getParent());
            file.getParentFile().mkdirs();
        }
        
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[8192];
            int count = -1;
            while((count = is.read(buffer)) > -1){
                fos.write(buffer, 0, count);
            }
            Log.i("GameDownloader", "文件下载成功!");
        } catch (IOException e) {
            Log.i("GameDownloader", "文件下载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
//        webView.clearCache(true);
        webView.destroy();
        super.onDestroy();
    }
}