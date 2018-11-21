package dgdg.project.underthecc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class parkingMapActivity extends ABActivity {
    public String data;
    TextView textView;
    TextView textView2;
    private final String TMAP_API_KEY = "39b31a17-1bb2-4874-af9e-e0ebd629e1f7";
    private TMapView tmap;
    //static final int REQ_PERMISSION = 1000;

    private static final String TAG = "ParkingMapActivity";
    Double longitude;
    Double latitude;
   //String placeName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkingmap);
        Log.d(TAG, "Init GoogleMap Activity");

        Intent intent = getIntent();
        data = intent.getStringExtra("address_value");

        textView = findViewById(R.id.textView);

        textView.setText(data);

        RelativeLayout RelativeLayoutTmap = findViewById(R.id.mapview_p);

        tmap = new TMapView(this);
        tmap.setSKTMapApiKey(TMAP_API_KEY);
        RelativeLayoutTmap.addView(tmap);
        tmap.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정합니다.
        //setGps();
        searchPlace(data);
    }

    public void searchPlace(String place) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);

        List<Address> addressList = null;

        Log.d(TAG, "searchPlace1");

        try {
            addressList = geocoder.getFromLocationName(place, 5);
            Log.d("ParkingMapActivity", "searchPlace2");
            if (addressList != null) {
                latitude = addressList.get(0).getLatitude();
                longitude = addressList.get(0).getLongitude();
                //placeName = addressList.get(0).getFeatureName();

                updateMap(latitude, longitude);
                Log.d(TAG, "end updateMap");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMap(double lat, double lon){
        textView2 = findViewById(R.id.textView2);
        Log.d(TAG, "start updateMap");

        String lat2 = String.valueOf(lat);
        String lon2 = String.valueOf(lon);
        String text = "현재 위도 경도 :" + lat2 + ", " + lon2;
        textView2.setText(text);
        tmap.setLocationPoint(longitude, latitude);
        tmap.setCenterPoint(longitude, latitude);

        TMapPoint tMapPoint = new TMapPoint(latitude, longitude);
        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint( tMapPoint );
        tMapCircle.setRadius(200);
        tMapCircle.setCircleWidth(2);
        tMapCircle.setLineColor(Color.TRANSPARENT);
        tMapCircle.setAreaColor(Color.RED);
        tMapCircle.setAreaAlpha(100);
        tmap.addTMapCircle("circle1", tMapCircle);

        //cameraUpdate 관련 클래스 삭제

    }
}