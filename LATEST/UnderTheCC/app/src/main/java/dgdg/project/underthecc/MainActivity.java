package dgdg.project.underthecc;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.skt.Tmap.TMapTapi;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "랄라";
    private final String TMAP_API_KEY = "39b31a17-1bb2-4874-af9e-e0ebd629e1f7";
    private TMapTapi tMapTapi;

    ImageButton button_gps;
    ImageButton button_park;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_gps=(ImageButton)findViewById(R.id.imageButton1);
        button_park=(ImageButton)findViewById(R.id.imageButton2);

        button_gps.setOnClickListener(this);
        button_park.setOnClickListener(this);

        showDialogForRecommendation("앱의 원활한 사용을 위해 Tmap 앱을 설치를 권장합니다!");
    }


    private void showDialogForRecommendation(String msg) {
        Log.d(TAG, "showDialogForRecommendation");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                runTMapTapiT();
            }
        });
        builder.create().show();
    }


    @Override
    public void onClick(View v) {
        if(v==button_gps) {
            Intent intent = new Intent(this, gpsActivity.class);
            startActivity(intent);
        }
        else if(v==button_park) {
            Intent intent = new Intent(this, parkingActivity.class);
            startActivity(intent);
        }
    }


    public void runTMapTapiT() {
        tMapTapi = new TMapTapi(this);
        tMapTapi.setSKTMapAuthentication(TMAP_API_KEY);// tmap api인증키 받기

        //인증 결과에 대한 인터페이스 함수 추가
        tMapTapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() { //ApiKey 인증 성공 시 호출된다.
                Log.d(TAG, "성공");

                boolean isTmapApp = tMapTapi.isTmapApplicationInstalled();
                Log.d(TAG, "" + isTmapApp);
                if (!isTmapApp) {
                    ArrayList<String> _ar = tMapTapi.getTMapDownUrl();
                    Log.d(TAG, "" + _ar);
                    if (_ar != null && _ar.size() > 0) {
                        Log.d(TAG,"_ar.size() : "+ _ar.size());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_ar.get(0)));
                        startActivity(intent);
                    }
                } else {
                    Log.d(TAG, "tMap앱 설치되어있음");
                }
            }
            @Override
            public void SKTMapApikeyFailed(String s) { //ApiKey 인증 실패 시 호출된다.
                Log.d(TAG, "실패" + s);
            }
        });
    }
}