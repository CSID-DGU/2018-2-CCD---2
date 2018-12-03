package dgdg.project.underthecc;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class parkingMapActivity extends ABActivity{

    private final String TMAP_API_KEY = "39b31a17-1bb2-4874-af9e-e0ebd629e1f7";
    private static final String TAG = "ParkingMapActivity";

    private TMapView tmap;
    TMapTapi tMapTapi;
    public String data;
    float x;
    float y;

    TMapMarkerItem markerItem_p;
    String result="";
    String file="서울특별시_주차장정보.xml";
    TextView textView;
    Double longitude;
    Double latitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkingmap);
        Log.d(TAG, "Init TMap Activity");

        Intent intent = getIntent();
        data = intent.getStringExtra("address_value");

        textView = findViewById(R.id.textView);
        textView.setText(data);

        RelativeLayout RelativeLayoutTmap = findViewById(R.id.mapview_p);

        tmap = new TMapView(this);
        tmap.setSKTMapApiKey(TMAP_API_KEY);
        RelativeLayoutTmap.addView(tmap);
        tmap.setIconVisibility(true);//검색한 위치 아이콘으로 표시
        searchPlace(data);
    }

    public void searchPlace(String place) {

        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
        List<Address> addressList = null;

        try {
            Log.d("ParkingMapActivity", "searchPlace");
            addressList = geocoder.getFromLocationName(place, 5);
            if (addressList != null) {
                latitude = addressList.get(0).getLatitude();
                longitude = addressList.get(0).getLongitude();

                updateMap(latitude, longitude);
                Log.d(TAG, "end updateMap");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMap(double lat, double lon){
        Log.d(TAG, "start updateMap");

        tmap.setLocationPoint(lon, lat);
        tmap.setCenterPoint(lon, lat);

        TMapPoint tMapPoint = new TMapPoint(lat, lon);
        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint(tMapPoint);
        tMapCircle.setRadius(200);
        tMapCircle.setCircleWidth(0);
        tMapCircle.setLineColor(Color.TRANSPARENT);
        tMapCircle.setAreaColor(Color.RED);
        tMapCircle.setAreaAlpha(50);
        tmap.addTMapCircle("circle1", tMapCircle);
    }

    public ArrayList xmlPassing(ArrayList pointList, int number){
        Log.d(TAG, "xmlPassing: xml 파싱준비" + file);

        try {
            InputStream is = getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer,"utf-8");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true); //xml 네임스페이스 지원 여부 설정
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();

            boolean bSet = false;
            Log.d(TAG, "xmlPassing: 위도, 경도값 받기 시작");
            while(eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG){
                    String tag_name = xpp.getName();
                    switch(number){
                        case 1:
                            if(tag_name.equals("위도") )
                                bSet=true;
                            break;
                        case 2:
                            if(tag_name.equals("경도") )
                                bSet=true;
                            break;
                    }
                }else if(eventType==XmlPullParser.TEXT){
                    if(bSet){
                        String data = xpp.getText();
                        boolean isPoint = false;
                        for(int j=0; j<pointList.size(); j++) {
                            if (data.equals(pointList.get(j))) {
                                isPoint = true;
                                break;
                            }
                        }
                        if (!isPoint)
                            pointList.add(data);
                    }
                    bSet = false;

                }else if(eventType==XmlPullParser.END_TAG);
                eventType=xpp.next();
            }
            Log.d(TAG, "xmlPassing: 위도, 경도값 받기 끝");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return pointList;
    }

    protected void onStart() {
        Log.d(TAG, "onStart: xml 파싱준비");
        final ArrayList PointWido_p = new ArrayList();
        final ArrayList PointKyungdo_p = new ArrayList();
        xmlPassing(PointWido_p, 1);
        xmlPassing(PointKyungdo_p, 2);
        // 출력
        Log.d(TAG, "onStart: 마커찍기...");

        tmap.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                Log.d(TAG, "모냐");
                runTMapTapiT();
                Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                vib.vibrate(500);
            }
        });

        for(int i=0; i<PointWido_p.size(); i++){
            markerItem_p = new TMapMarkerItem();
            // 마커의 좌표 지정
            String p_wido = (String) PointWido_p.get(i);
            String p_kyungdo = (String) PointKyungdo_p.get(i);
            double p_dwido = Double.valueOf(p_wido);
            double p_dkyungdo = Double.valueOf(p_kyungdo);
            x = markerItem_p.getPositionX();
            y = markerItem_p.getPositionY();
            TMapPoint p_tmapPoint = new TMapPoint(p_dwido, p_dkyungdo);
            Bitmap icon_p = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
            markerItem_p.setIcon(icon_p); // 마커 아이콘 지정
            markerItem_p.setTMapPoint(p_tmapPoint);
            //지도에 마커 추가
            tmap.addMarkerItem("markerItem_p"+i, markerItem_p);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.tmapicon);

            markerItem_p.setCanShowCallout(true);
            markerItem_p.setCalloutRightButtonImage(bitmap);
            markerItem_p.setCalloutTitle("가나다라마바사아자차카타파하");
            markerItem_p.setCalloutSubTitle("가나다라마바사아자차카타파하");
            markerItem_p.setCalloutLeftImage(bitmap);

            //onPressEvent(p_tmapPoint);
            Log.d(TAG, "onStart: 주차장 마커 찍기 완료");

        }
        super.onStart();
    }

    public void runTMapTapiT() {

        tMapTapi = new TMapTapi(this);
        tMapTapi.invokeTmap();
        //tMapTapi.setSKTMapAuthentication(TMAP_API_KEY);
/*
        tMapTapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                Log.d(TAG, "성공");

                boolean isTmapApp = tMapTapi.isTmapApplicationInstalled();
                Log.d(TAG, "" + isTmapApp);
                if (isTmapApp == false) {
                    ArrayList<String> _ar = tMapTapi.getTMapDownUrl();
                    Log.d(TAG, "" + _ar);
                    if (_ar != null && _ar.size() > 0) {
                        Log.d(TAG,"_ar.size() : "+ _ar.size());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_ar.get(0)));
                        startActivity(intent);
                    }
                } else {
                    tMapTapi.invokeTmap();
                }
            }
            @Override
            public void SKTMapApikeyFailed(String s) {
                Log.d(TAG, "실패");

            }
        });*/
    }
}