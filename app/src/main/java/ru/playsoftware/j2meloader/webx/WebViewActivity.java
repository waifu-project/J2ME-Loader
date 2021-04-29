package ru.playsoftware.j2meloader.webx;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ru.playsoftware.j2meloader.R;
import ru.playsoftware.j2meloader.base.BaseActivity;

public class WebViewActivity extends BaseActivity {

//    private String j2meGameURL = "http://172.16.50.28:5500/index.html";

    private String j2meGameURL = "http://j2me_games.js.cool";

    private String webviewTitle = "j2me游戏下载";

    private String webviewJavascriptKey = "WEBVIEW";

    private WebView webview;

    static class GameInfo {

        public String game_title;

        public String game_url;

        public int game_sort;

        public String getSortString() {
            HashMap<Integer, String> sorts = new HashMap<Integer, String>();
            sorts.put(1, "角色");
            sorts.put(2, "动作");
            sorts.put(3, "益智");
            sorts.put(4, "策略");
            sorts.put(5, "射击");
            sorts.put(6, "其他");
            sorts.put(7, "汉化");
            sorts.put(8, "赛车");
            sorts.put(9, "棋牌");
            String sort = sorts.get(this.game_sort);
            if (sort == null) return "未命名";
            return sort;
        }

        GameInfo(List<String> rawStringList) {
            int sort = Integer.parseInt(rawStringList.get(1).trim());
            this.game_title = rawStringList.get(0).trim();
            this.game_url = rawStringList.get(2).trim();
            this.game_sort = sort;
        }

        GameInfo(String title, String game_url, int game_sort) {
            this.game_title = title;
            this.game_url = game_url;
            this.game_sort = game_sort;
        }

    }

    private void callbackDownloadGame(GameInfo gameInfo) {

    }

    private void callJavaScript(WebView view, String methodName, Object...params){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:try{");
        stringBuilder.append(methodName);
        stringBuilder.append("(");
        String separator = "";
        for (Object param : params) {
            stringBuilder.append(separator);
            separator = ",";
            if(param instanceof String){
                stringBuilder.append("'");
            }
            stringBuilder.append(param.toString().replace("'", "\\'"));
            if(param instanceof String){
                stringBuilder.append("'");
            }

        }
        stringBuilder.append(")}catch(error){console.error(error.message);}");
        final String call = stringBuilder.toString();
        System.out.println(call);


        view.loadUrl(call);
    }

    private void webviewInitFunc() {
        if (!j2meGameURL.startsWith("http://172.16.50.28")) {
            webview.loadUrl("javascript:document.getElementsByTagName(\"h1\")[0].remove()\n");
        }
        getSupportActionBar().setTitle(webviewTitle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        this.setContentView(R.layout.activity_webview);
        webview = (WebView) findViewById(R.id.webviewRuntime);

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                webviewInitFunc();
                super.onPageFinished(view, url);
            }

        });

        WebSettings webviewSettings = webview.getSettings();

        webviewSettings.setJavaScriptEnabled(true);
        webviewSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webview.loadUrl(this.j2meGameURL);

        webview.addJavascriptInterface(new handleJavascriptEvent(), webviewJavascriptKey);

        super.onCreate(savedInstanceState);
    }

    private class handleJavascriptEvent {

        private String symbol = "|";

        @RequiresApi(api = Build.VERSION_CODES.N)
        private GameInfo easyParseString(String rawstring) {
            // https://stackoverflow.com/a/47795244
            Pattern regx = Pattern.compile("\\" + symbol);
            List<String> data = regx.splitAsStream(rawstring).collect(Collectors.toList());
            if (data.size() < 3) return null;
            return new GameInfo(data);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @JavascriptInterface
        public void download(String raw) {
            GameInfo gameRaw = this.easyParseString(raw);
            callbackDownloadGame(gameRaw);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("----");
        System.out.println(data);
        System.out.println("----");
        super.onActivityResult(requestCode, resultCode, data);
    }
}
