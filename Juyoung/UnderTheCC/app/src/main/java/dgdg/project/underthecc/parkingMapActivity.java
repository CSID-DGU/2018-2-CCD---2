package dgdg.project.underthecc;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class parkingMapActivity extends ABActivity implements View.OnClickListener{

    private final String TMAP_API_KEY = "39b31a17-1bb2-4874-af9e-e0ebd629e1f7";
    private static final String TAG = "ParkingMapActivity";
    final ArrayList PointWido_p = new ArrayList();
    final ArrayList PointKyungdo_p = new ArrayList();
    final ArrayList Name_p = new ArrayList();
    final ArrayList Phone_p = new ArrayList();
    final ArrayList parkingClass = new ArrayList();
    final ArrayList feeInfo = new ArrayList();
    final ArrayList date_p = new ArrayList();
    final ArrayList fee = new ArrayList();
    final ArrayList time_p = new ArrayList();
    final ArrayList address = new ArrayList();

    private TMapView tmap;
    public String data;
    float x;
    float y;
    int number;

    TMapTapi tMapTapi;
    TMapMarkerItem markerItem_p;
    ImageButton button_navi;
    String result="";
    String file="서울특별시_주차장정보.xml";
    Double longitude;
    Double latitude;

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    TextView textView8;
    TextView textView9;
    TextView textView10;
    TextView textView11;
    TextView textView12;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkingmap);
        Log.d(TAG, "Init TMap Activity");

        Intent intent = getIntent();
        data = intent.getStringExtra("address_value");

        textView1 = findViewById(R.id.textView1);
        textView1.setPaintFlags(textView1.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView3.setPaintFlags(textView3.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView5.setPaintFlags(textView5.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        textView7.setPaintFlags(textView7.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        textView9.setPaintFlags(textView9.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        textView10 = findViewById(R.id.textView10);
        textView11 = findViewById(R.id.textView11);
        textView11.setPaintFlags(textView11.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        textView12 = findViewById(R.id.textView12);

        button_navi = findViewById(R.id.button_navi);
        button_navi.setOnClickListener(this);

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
                            if(tag_name.equals("전화번호") )
                                bSet=true;
                            break;
                        case 5:
                            if(tag_name.equals("주차장구분") )
                                bSet=true;
                            break;
                        case 6:
                            if(tag_name.equals("요금정보") )
                                bSet=true;
                            break;
                        case 7:
                            if(tag_name.equals("운영요일") )
                                bSet=true;
                            break;
                        case 8:
                            if(tag_name.equals("주차기본요금") )
                                bSet=true;
                            break;
                        case 9:
                            if(tag_name.equals("주차기본시간") )
                                bSet=true;
                            break;
                        case 10:
                            if(tag_name.equals("소재지도로명주소") )
                                bSet=true;
                            break;
                    }
                }else if(eventType==XmlPullParser.TEXT){
                    if(bSet){
                        String data = xpp.getText();
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
        Log.d(TAG, "onStart: xml 파싱");

        xmlPassing(PointWido_p, 1);
        xmlPassing(PointKyungdo_p, 2);
        xmlPassing(Name_p, 3);
        xmlPassing(Phone_p, 4);
        xmlPassing(parkingClass, 5);
        xmlPassing(feeInfo, 6);
        xmlPassing(date_p, 7);
        xmlPassing(fee, 8);
        xmlPassing(time_p, 9);
        xmlPassing(address, 10);

        tmap.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                Log.d(TAG, "주차장 아이콘 클릭 : runTMapTapiT()");
                x = (float)markerItem.latitude;
                y = (float)markerItem.longitude;
                for(int i =0; i<PointWido_p.size(); i++){
                    String s = (String)PointWido_p.get(i);
                    Float f = Float.parseFloat(s);
                    if(x == f) {
                        number = i;
                        Log.d(TAG, "주차장 아이콘 클릭 : " + s + "   " + i + "   " + Name_p.get(i));
                    }
                }
                textView2.setText((CharSequence) parkingClass.get(number));
                textView4.setText((CharSequence)feeInfo.get(number));
                textView6.setText((CharSequence)date_p.get(number));
                textView8.setText((CharSequence)fee.get(number) + "원");
                textView10.setText((CharSequence)time_p.get(number) + "시간");
                textView12.setText((CharSequence)address.get(number));

            }
        });


        for(int i=0; i<PointWido_p.size(); i++){
            markerItem_p = new TMapMarkerItem();
            double p_dwido = Double.valueOf((String) PointWido_p.get(i));
            double p_dkyungdo = Double.valueOf((String) PointKyungdo_p.get(i));
            String p_name = (String)Name_p.get(i);
            String p_address = (String)Phone_p.get(i);
            TMapPoint p_tmapPoint = new TMapPoint(p_dwido, p_dkyungdo);
            Bitmap icon_p = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
            markerItem_p.setIcon(icon_p); // 마커 아이콘 지정
            markerItem_p.setTMapPoint(p_tmapPoint);
            tmap.addMarkerItem("markerItem_p"+i, markerItem_p);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.info12);
            markerItem_p.setCanShowCallout(true);
            markerItem_p.setCalloutRightButtonImage(bitmap);
            markerItem_p.setCalloutTitle(p_name);
            markerItem_p.setCalloutSubTitle(p_address);
        }
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if(v == button_navi)
            runTMapTapiT();
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
            String naviName = (String)Name_p.get(number);
            tMapTapi.invokeRoute(naviName, y, x);
        }
    }
}