package dgdg.project.underthecc;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapCircle;
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
                        case 3:
                            if(tag_name.equals("주차장명") )
                                bSet=true;
                            break;
                        case 4:
                            if(tag_name.equals("소재지지번주소") )
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
        final ArrayList Name_p = new ArrayList();
        final ArrayList Address_p = new ArrayList();
        xmlPassing(PointWido_p, 1);
        xmlPassing(PointKyungdo_p, 2);
        xmlPassing(Name_p, 3);
        xmlPassing(Address_p, 4);


        tmap.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                Log.d(TAG, "주차장 아이콘 클릭 : runTMapTapiT()");
                x = (float)markerItem.latitude;
                y = (float)markerItem.longitude;
                runTMapTapiT();

                Log.d(TAG, "x :   " + x + "   y :    " + y);
            }
        });

        for(int i=0; i<PointWido_p.size(); i++){
            markerItem_p = new TMapMarkerItem();
            // 마커의 좌표 지정
            double p_dwido = Double.valueOf((String) PointWido_p.get(i));
            double p_dkyungdo = Double.valueOf((String) PointKyungdo_p.get(i));
            String p_name = (String)Name_p.get(i);
            String p_address = (String)Address_p.get(i);
            TMapPoint p_tmapPoint = new TMapPoint(p_dwido, p_dkyungdo);
            Bitmap icon_p = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
            markerItem_p.setIcon(icon_p); // 마커 아이콘 지정
            markerItem_p.setTMapPoint(p_tmapPoint);
            tmap.addMarkerItem("markerItem_p"+i, markerItem_p);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.tmapicon);
            markerItem_p.setCanShowCallout(true);
            markerItem_p.setCalloutRightButtonImage(bitmap);
            markerItem_p.setCalloutTitle(p_name);
            markerItem_p.setCalloutSubTitle(p_address);
            //markerItem_p.setCalloutLeftImage(bitmap);
        }
        super.onStart();
    }

    public void runTMapTapiT() {
        tMapTapi = new TMapTapi(this);

        boolean isTmapApp = tMapTapi.isTmapApplicationInstalled();

        if(!isTmapApp){
            AlertDialog.Builder builder = new AlertDialog.Builder(parkingMapActivity.this);
            builder.setTitle("알림");
            builder.setMessage("Tmap 앱을 설치해주세요");
            builder.setCancelable(false);
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ArrayList<String> _ar = tMapTapi.getTMapDownUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_ar.get(0)));
                    startActivity(intent);
                }
            });
            builder.create().show();

        }else {
            tMapTapi.invokeRoute("T타워", y, x);
        }
    }
}