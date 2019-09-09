package dgdg.project.underthecc;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class parkingActivity extends ABActivity {
    private WebView webView;
    private Handler handler;


    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);


        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
    }

    public void init_webView() { //WebView 설정하고 JavaScript 허용, 웹뷰 url loaod.php 파일주소 설정
        // WebView 설정
        webView = (WebView) findViewById(R.id.web_view);
        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        webView.addJavascriptInterface(new AndroidBridge(), "TestApp");
        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());
        // webview url load. php 파일 주소
        webView.loadUrl("url");
    }


    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    address = String.format("(%s) %s %s", arg1, arg2, arg3);
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    nextActivity();
                    init_webView();
                }
            });
        }
    }

    public void nextActivity() {
        if (address != null) {
            Intent intent = new Intent(this, parkingMapActivity.class);
            intent.putExtra("address_value", address);
            startActivity(intent);
        }
    }
}
