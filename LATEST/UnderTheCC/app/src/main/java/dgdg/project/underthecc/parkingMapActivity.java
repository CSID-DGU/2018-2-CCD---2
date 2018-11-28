package dgdg.project.underthecc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class parkingMapActivity extends ABActivity {
    public String data;
    TextView textView;
    private final String TMAP_API_KEY = "39b31a17-1bb2-4874-af9e-e0ebd629e1f7";
    private TMapView tmap;

    private static final String TAG = "ParkingMapActivity";
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

        Log.d(TAG, "searchPlace1");

        try {
            addressList = geocoder.getFromLocationName(place, 5);
            Log.d("ParkingMapActivity", "searchPlace2");
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

        tmap.setLocationPoint(longitude, latitude);
        tmap.setCenterPoint(longitude, latitude);

        TMapPoint tMapPoint = new TMapPoint(latitude, longitude);
        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint(tMapPoint);
        tMapCircle.setRadius(200);
        tMapCircle.setCircleWidth(2);
        tMapCircle.setLineColor(Color.TRANSPARENT);
        tMapCircle.setAreaColor(Color.RED);
        tMapCircle.setAreaAlpha(100);
        tmap.addTMapCircle("circle1", tMapCircle);
    }

    protected void onStart() {
        Log.d(TAG, "onStart: xml 파싱준비");
        String file="서울특별시_주차장정보.xml";
        String result="";
        final ArrayList PointWido_p = new ArrayList();
        final ArrayList PointKyungdo_p = new ArrayList();

        try {
            InputStream is=getAssets().open(file);
            int size=is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            result=new String(buffer,"utf-8");

            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true); //xml 네임스페이스 지원 여부 설정
            XmlPullParser xpp=factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType=xpp.getEventType();

            boolean bSet=false;
            Log.d(TAG, "onStart: 위도값 받기 시작");
            while(eventType!=XmlPullParser.END_DOCUMENT){
                if(eventType== XmlPullParser.START_TAG){
                    String tag_name=xpp.getName();
                    if(tag_name.equals("위도"))
                        bSet=true;
                }else if(eventType==XmlPullParser.TEXT){
                    if(bSet){
                        String data = xpp.getText();
                        boolean isPoint = false;
                        for(int j=0; j<PointWido_p.size(); j++) {
                            if (data.equals(PointWido_p.get(j))) {
                                isPoint = true;
                                break;
                            }
                        }
                        if (!isPoint)
                            PointWido_p.add(data);
                    }
                    bSet = false;

                }else if(eventType==XmlPullParser.END_TAG);
                eventType=xpp.next();
            }
            Log.d(TAG, "onStart: 위도값 받기 끝");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream is=getAssets().open(file);
            int size=is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer,"utf-8");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true); //xml 네임스페이스 지원 여부 설정
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType=xpp.getEventType();

            boolean bSet = false;
            Log.d(TAG, "onStart: 경도값 받기 시작");
            while(eventType!=XmlPullParser.END_DOCUMENT){
                if(eventType== XmlPullParser.START_TAG){
                    String tag_name=xpp.getName();
                    if(tag_name.equals("경도"))
                        bSet=true;
                }else if(eventType==XmlPullParser.TEXT){
                    if(bSet){
                        String data = xpp.getText();
                        boolean isPoint = false;
                        for(int j=0; j<PointKyungdo_p.size(); j++) {
                            if (data.equals(PointKyungdo_p.get(j))) {
                                isPoint = true;
                                break;
                            }
                        }
                        if (!isPoint)
                            PointKyungdo_p.add(data);
                    }
                    bSet = false;

                }else if(eventType==XmlPullParser.END_TAG);
                eventType=xpp.next();
            }
            Log.d(TAG, "onStart: 경도값 받기 끝");
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        // 출력
        Log.d(TAG, "onStart: 마커찍기...");
        for(int i=0; i<PointWido_p.size(); i++){
            TMapMarkerItem markerItem_p = new TMapMarkerItem();
            // 마커의 좌표 지정
            String p_wido = (String) PointWido_p.get(i);
            String p_kyungdo = (String) PointKyungdo_p.get(i);
            double p_dwido = Double.valueOf(p_wido);
            double p_dkyungdo = Double.valueOf(p_kyungdo);
            TMapPoint p_tmapPoint = new TMapPoint(p_dwido, p_dkyungdo);
            Bitmap icon_p = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
            markerItem_p.setIcon(icon_p); // 마커 아이콘 지정
            markerItem_p.setTMapPoint(p_tmapPoint);
            //지도에 마커 추가
            tmap.addMarkerItem("markerItem"+i, markerItem_p);
            markerItem_p.setCanShowCallout(true);
            markerItem_p.setCalloutTitle("위도 : " + p_wido + "경도 : " + p_kyungdo);
            Log.d(TAG, "onStart: 주차장 마커 찍기 완료");
        }
        super.onStart();
    }
}