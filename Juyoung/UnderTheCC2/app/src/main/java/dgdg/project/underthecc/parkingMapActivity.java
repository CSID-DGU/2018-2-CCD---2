package dgdg.project.underthecc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class parkingMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    static final LatLng defaultposition = new LatLng(37.527089, 127.028480);
    private GoogleMap googleMap;

    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        googleMap.addMarker(new MarkerOptions().position(defaultposition).title("디폴트 위치"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultposition, 17.0f));
    }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_parkingmap);
            Log.d("ParkingMapActivity", "Init GoogleMap Activity");

            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
}

