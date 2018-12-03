package dgdg.project.underthecc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.skt.Tmap.TMapTapi;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageButton button_gps;
    ImageButton button_park;
    TMapTapi tMapTapi;
    private final String TMAP_API_KEY = "39b31a17-1bb2-4874-af9e-e0ebd629e1f7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_gps=(ImageButton)findViewById(R.id.imageButton1);
        button_park=(ImageButton)findViewById(R.id.imageButton2);

        button_gps.setOnClickListener(this);
        button_park.setOnClickListener(this);
        runTMapTapiT();
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
        tMapTapi.setSKTMapAuthentication(TMAP_API_KEY);

        tMapTapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {

                boolean isTmapApp = tMapTapi.isTmapApplicationInstalled();
                if (isTmapApp == false) {
                    ArrayList<String> _ar = tMapTapi.getTMapDownUrl();
                    if (_ar != null && _ar.size() > 0) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_ar.get(0)));
                        startActivity(intent);
                    }
                } else {
                }
            }
            @Override
            public void SKTMapApikeyFailed(String s) {

            }
        });
    }
}