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

    private static final String TAG = "MainActivity_underthec";
    private final String TMAP_API_KEY = "39b31a17-1bb2-4874-af9e-e0ebd629e1f7";
    private TMapTapi tMapTapi;

    ImageButton button_gps;
    ImageButton button_park;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_gps=(ImageButton)findViewById(R.id.button_gps);
        button_park=(ImageButton)findViewById(R.id.button_park);

        button_gps.setOnClickListener(this);
        button_park.setOnClickListener(this);
        runTMapTapiT();
    }

    private void showDialogForRecommendation(String msg) {
        Log.d(TAG, "showDialogForRecommendation");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "showDialogForRecommendation : 취소 버튼 누름");

            }
        });
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "showDialogForRecommendation : 확인 버튼 누름");
                ArrayList<String> _ar = tMapTapi.getTMapDownUrl();
                if (_ar != null && _ar.size() > 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_ar.get(0)));
                    startActivity(intent);
                }
            }
        });
        builder.create().show();
    }

    public void runTMapTapiT() {
        Log.d(TAG, "runTMapTapiT 호출됨");
        tMapTapi = new TMapTapi(this);
        tMapTapi.setSKTMapAuthentication(TMAP_API_KEY);// tmap api인증키 받기

        //인증 결과에 대한 인터페이스 함수 추가
        tMapTapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() { //ApiKey 인증 성공 시 호출된다.
                Log.d(TAG, "runTMapTapiT : 성공");

                boolean isTmapApp = tMapTapi.isTmapApplicationInstalled();
                Log.d(TAG, "runTMapTapiT : 티맵 설치 여부 - " + isTmapApp);
                if (!isTmapApp) {
                    showDialogForRecommendation("앱의 원활한 사용을 위해 Tmap 앱을 설치를 권장합니다!");
                }else{}
            }
            @Override
            public void SKTMapApikeyFailed(String s) { //ApiKey 인증 실패 시 호출된다.
                Log.d(TAG, "runTMapTapiT : 실패" + s);
            }
        });
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
}