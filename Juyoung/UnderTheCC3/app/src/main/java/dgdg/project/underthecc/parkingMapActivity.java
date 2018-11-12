package dgdg.project.underthecc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class parkingMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private Marker currentMarker = null;
    public String data;
    TextView textView;
    TextView textView2;
    static final int REQ_PERMISSION = 1000;

    private static final String TAG = "ParkingMapActivity";
    Double longitude;
    Double latitude;
   //String placeName;
    
    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            map.setMyLocationEnabled(true);
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        Log.d(TAG, "구글맵 보이니");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkingmap);
        Log.d(TAG, "Init GoogleMap Activity");

        Intent intent = getIntent();
        data = intent.getStringExtra("address_value");

        textView = findViewById(R.id.textView);

        textView.setText(data);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchPlace(data);
        Log.d(TAG, "end searchPlace");
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
        LatLng latLng = new LatLng(lat, lon);
        String text = "현재 위도 경도 :" + lat2 + ", " + lon2;
        textView2.setText(text);

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        options.title("찾는 장소");
        Log.d(TAG, "updateMap1");
       // currentMarker = googleMap.addMarker(options);

        //mk.showInfoWindow();
        Log.d(TAG, "updateMap2");

/*        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        googleMap.moveCamera(cameraUpdate);
        Log.d(TAG, "updateMap3");
*/
    }

}