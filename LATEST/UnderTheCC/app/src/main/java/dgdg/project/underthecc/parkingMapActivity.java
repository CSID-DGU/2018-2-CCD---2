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

public class parkingMapActivity extends AppCompatActivity {
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
        setGps();
    }

    public void setGps() {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);

        searchPlace(data);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
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

                TMapData tMapData = new TMapData();
                try {
                    Log.d("TmapTest", ""+tMapPoint.getLatitude());
                    Log.d("TmapTest", ""+tMapPoint.getLongitude());

                    tMapData.convertGpsToAddress(tMapPoint.getLatitude(), tMapPoint.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {

                        @Override
                        public void onConvertToGPSToAddress(String addr) {
                            Log.d("TmapTest","*** updatePositionInfo - addr: "+addr);
                            TextView textView;
                            textView = findViewById(R.id.textView);
                            textView.setText(addr);
                        }
                    });

                } catch (Exception e) {
                    Log.d("error", "*** Exception: "+e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


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

        //cameraUpdate 관련 클래스 삭제했습니다

    }
}