package dgdg.project.underthecc;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class gpsActivity extends ABActivity implements View.OnClickListener{

    private final String TMAP_API_KEY = "39b31a17-1bb2-4874-af9e-e0ebd629e1f7";

    Button button_cctv;
    Button button_parking;

    ArrayList mPendingIntentList;
    String intentKey = "CCTVProximity";
    String result="";
    String cctvFile="서울특별시_중구_CCTV_20181101.xml";
    String parkingFile="서울특별시_주차장정보.xml";

    private static final String TAG = "GpsActivity";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    boolean bcctv = false; //화면에 cctv 정보 나오지 않는 상태
    boolean bparking = false; //화면에 주차장 정보 나오지 않는 상태

    private TMapView tmap;
    private LocationManager mLocationManager;
    private IntentReceiver mIntentReceiver;
    private AppCompatActivity mActivity;
    private ProgressDialog loagingDialog;// 로딩화면

    final ArrayList PointWido = new ArrayList();
    final ArrayList PointKyungdo = new ArrayList ();
    final ArrayList PointWido_p = new ArrayList();
    final ArrayList PointKyungdo_p = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gps);

        // 위치 관리자 객체 참조
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mPendingIntentList = new ArrayList();

        button_cctv = (Button)findViewById(R.id.cctvBu);
        button_parking=(Button)findViewById(R.id.parkingBu);

        button_cctv.setOnClickListener(this);
        button_parking.setOnClickListener(this);

        Log.d(TAG, "onCreate : 쓰레드 테스트 시작");
        Background thread_b = new Background();
        thread_b.start();
    }

    class Background extends Thread {
        @Override
        public void run() {

            // 수신자 객체 생성하여 등록
            Log.d(TAG, "Background : 근접 리스너 등록");
            mIntentReceiver = new IntentReceiver(intentKey);
            registerReceiver(mIntentReceiver, mIntentReceiver.getFilter());

            Log.d(TAG, "Background: xml 파일 파싱");
            xmlPassing(PointWido, 1, cctvFile); // CCTV xml에서 위도정보 배열에 저장
            xmlPassing(PointKyungdo, 2, cctvFile); // CCTV xml에서 경도정보 배열에 저장
            xmlPassing(PointWido_p,1, parkingFile); // 주차장 xml에서 위도정보 배열에 저장
            xmlPassing(PointKyungdo_p,2, parkingFile); // 주차장 xml에서 경도정보 배열에 저장

            Log.d(TAG, "Background : register 호출");
            for(int i=1; i<PointWido.size(); i++){
                // 좌표 인텐트로 지정
                String wido = (String) PointWido.get(i);
                String kyungdo = (String) PointKyungdo.get(i);
                double dwido = Double.valueOf(wido);
                double dkyungdo = Double.valueOf(kyungdo);
                register(i,dwido, dkyungdo,600,-1);
                Log.d(TAG, "Background: register 등록 번호 " + i);
            }
            super.run();
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");


        Log.d(TAG, "onStart: Tmap 생성");
        RelativeLayout RelativeLayoutTmap = findViewById(R.id.map_view);
        tmap = new TMapView(this);
        tmap.setSKTMapApiKey(TMAP_API_KEY);
        RelativeLayoutTmap.addView(tmap);

        super.onStart();
    }

    public ArrayList xmlPassing(ArrayList pointList, int number, String filename){
        Log.d(TAG, "xmlPassing: xml 파싱준비" + filename);

        try {
            InputStream is = getAssets().open(filename);
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

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: 버튼이 눌렸네요");
        if(v == button_cctv) {
            if(!bcctv){
                Log.d(TAG, "onClick: cctv버튼, 화면에 보이게 함");
                bcctv = true;

                for(int i=0; i<PointWido.size(); i++){
                    TMapMarkerItem markerItem1 = new TMapMarkerItem();
                    // 마커의 좌표 지정
                    String wido = (String) PointWido.get(i);
                    String kyungdo = (String) PointKyungdo.get(i);
                    double dwido = Double.valueOf(wido);
                    double dkyungdo = Double.valueOf(kyungdo);

                    TMapPoint tmapPoint = new TMapPoint(dwido, dkyungdo);
                    Bitmap icon_c = BitmapFactory.decodeResource(getResources(), R.drawable.cctvi);
                    markerItem1.setIcon(icon_c); // 마커 아이콘 지정
                    markerItem1.setTMapPoint(tmapPoint);
                    //지도에 마커 추가
                    tmap.addMarkerItem("markerItem"+i, markerItem1);

                    markerItem1.setCanShowCallout(true);
                    markerItem1.setCalloutTitle(i + "   위도 : " + wido + "경도 : " + kyungdo);
                }

            }else {
                Log.d(TAG, "onClick: cctv버튼, 화면에 안 보이게 함");

                bcctv = false;
                for (int i = 0; i < PointWido.size(); i++) {
                    tmap.removeMarkerItem("markerItem" + i);
                }

            }
        }


        else if(v == button_parking) {
            if(!bparking){
                Log.d(TAG, "onClick: parking버튼, 화면에 보이게 함");
                bparking = true;

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
                    tmap.addMarkerItem("markerItem_p"+i, markerItem_p);
                    markerItem_p.setCanShowCallout(true);
                    markerItem_p.setCalloutTitle("위도 : " + p_wido + "경도 : " + p_kyungdo);
                }
            }else{
                Log.d(TAG, "onClick: parking버튼, 화면에 안 보이게 함");

                bparking = false;
                for (int i = 0; i < PointWido_p.size(); i++) {
                    tmap.removeMarkerItem("markerItem_p" + i);
                }
            }
        }
    }


    @Override
    public void onResume() {

        super.onResume();

        Log.d(TAG, "onResume : call startLocationUpdates");
        if (!mRequestingLocationUpdates){
            startLocationUpdates();
        }


        //앱 정보에서 퍼미션을 허가했는지를 다시 검사
        if (askPermissionOnceAgain) {
            Log.d(TAG, "onResume : 앱 정보에서 퍼미션 허가했는지 검사");

            //사용자의 OS버전을 체크한다.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//사용자 기기의 sdk버전이 마쉬멜로우 버전보다 높다면
                askPermissionOnceAgain = false;

                Log.d(TAG, "onResume : checkPermissions 호출");
                checkPermissions();
            }
        }
    }

    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates : 퍼미션 확인");
        if (!checkLocationServicesStatus()) { //위치서비스가 비활성화인 상태
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else { //위치 서비스가 활성화인 상태
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");

                return;
            }

            Log.d(TAG, "startLocationUpdates : 위치 업데이트 요청");
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                    500, // 10초 : 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            mRequestingLocationUpdates = true;
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tmap.setLocationPoint(longitude, latitude);
                tmap.setCenterPoint(longitude, latitude);

                Log.d(TAG, "onLocationChanged : 현 위치 표시");
                TMapPoint tMapPoint = new TMapPoint(latitude, longitude);

                TMapCircle tMapCircle = new TMapCircle();
                tMapCircle.setCenterPoint(tMapPoint);
                tMapCircle.setRadius(600);
                tMapCircle.setCircleWidth(0);
                tMapCircle.setLineColor(Color.TRANSPARENT);
                tMapCircle.setAreaColor(Color.RED);
                tMapCircle.setAreaAlpha(50);
                tmap.addTMapCircle("circle1", tMapCircle);
                tmap.setIconVisibility(true);

                Log.d(TAG, "onLocationChanged : geocoding");
                TMapData tMapData = new TMapData();
                try {
                    tMapData.convertGpsToAddress(tMapPoint.getLatitude(), tMapPoint.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {

                        @Override
                        public void onConvertToGPSToAddress(String addr) {
                            TextView textView;
                            textView = findViewById(R.id.textView);
                            textView.setText(addr);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void onProviderDisabled(String provider) { }
        public void onProviderEnabled(String provider) { }
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    };

    //register the proximity intent receiver
    private void register(int id, double latitude, double longitude, float radius, long expiration) {
        Log.d(TAG, "register");
        Intent proximityIntent = new Intent(intentKey);
        proximityIntent.putExtra("id", id);
        proximityIntent.putExtra("latitude", latitude);
        proximityIntent.putExtra("longitude", longitude);

        //아래 파라미터 설명 : this=PendingIntent를 부르려는 컨텍스트, id=원래는 requestcode로 쓰임,proximityIntent=앞으로 불려질 Intent,
        //intentflags=intent에 대한 조건 설정 플래그 여기서는 이미 실행중인 PendingIntent가 있다면 이를 취소하고 새로 만드는 것.
        PendingIntent intent = PendingIntent.getBroadcast(this, id, proximityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.d(TAG, "register : 퍼미션 안가지고 있음");

            return;
        }
        Log.d(TAG, "register 6");
        // 목표지점 동록을 위해 addProximityAlert 메소드 이용
        mLocationManager.addProximityAlert(latitude, longitude, radius, expiration, intent);
        mPendingIntentList.add(intent);
    }

    //브로드캐스팅 메시지를 받았을 때 처리할 수신자 정의
    private class IntentReceiver extends BroadcastReceiver {

        private String mExpectedAction;
        private Intent mLastReceivedIntent;

        public IntentReceiver(String expectedAction) {
            mExpectedAction = expectedAction;
            mLastReceivedIntent = null;
        }

        public IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter(mExpectedAction);
            return filter;
        }

        // 원하는 조건에 맞으면 호출되는 메소드
        // @param content @param intent
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "IntentReceiver : onReceive");

            if (intent != null) {
                mLastReceivedIntent = intent;

                int id = intent.getIntExtra("id", 0);
                double latitude = intent.getDoubleExtra("latitude", 0.0D);
                double longitude = intent.getDoubleExtra("longitude", 0.0D);

                Toast.makeText(context, "CCTV 단속 구역입니다. : " + id + ", " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
                //진동 알림
                Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                vib.vibrate(500);
                //Beep 알림음
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                ringtone.play();
            }
        }
        public Intent getLastReceivedIntent() {
            return mLastReceivedIntent;
        }
        public void clearReceivedIntents() {
            mLastReceivedIntent = null;
        }
    }

    public boolean checkLocationServicesStatus() {
        Log.d(TAG, "checkLocationServicesStatus");

        boolean value = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, "checkLocationServicesStatus : value = " + value);

        //GPS 수신 상태 확인 : GPS가 켜져 있으면 true, 아니면 false 반환
        return value;
    }

    private void showDialogForLocationServiceSetting() {
        Log.d(TAG, "showDialogForLocationServiceSetting");

        AlertDialog.Builder builder = new AlertDialog.Builder(gpsActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");

        //back키 눌렀을 경우 dialog cancle 여부 결정
        builder.setCancelable(true);

        // 설정 버튼을 선택했다면 위치정보 설정이 보임
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override //파라미터로 넘어온 값이 정상적으로 넘어온 값인지를 판단해 현재 위치를 불러오는 로직 짬
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE: //2001
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {//활성화됐으면 다음 줄, 안됐으면 break
                    /*if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");
                        return;
                    }*/
                }
                break;
        }
    }

    @Override // 권한 요청의 결과를 받음
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {
            } else {
                Log.d(TAG, "onRequestPermissionsResult : checkPermissions 호출");
                checkPermissions();
            }
        }
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        Log.d(TAG, "showDialogForPermission");

        AlertDialog.Builder builder = new AlertDialog.Builder(gpsActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {
        Log.d(TAG, "showDialogForPermissionSetting");

        AlertDialog.Builder builder = new AlertDialog.Builder(gpsActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onStop() {
        if (mRequestingLocationUpdates) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }
        super.onStop();
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates : removeLocationUpdates");
        mLocationManager.removeUpdates(mLocationListener);
        mRequestingLocationUpdates = false;
    }
}