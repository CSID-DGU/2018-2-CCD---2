package dgdg.project.myapplication;

import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TMAP_API_KEY = "bac7b8a2-3163-4038-a913-c29e6bd7346a";
    private TMapView tmap;
    private static final String TAG = "ForDGDG";

    TMapMarkerItem marker_cctv;
    TMapPoint point_cctv;
    TMapMarkerItem marker_parking;
    TMapPoint point_parking;
    Button button_cctv;
    Button button_parking;

    String file="서울특별시_중구_CCTV_20181101.xml";
    String result="";
    final ArrayList PointWido = new ArrayList();
    final ArrayList PointKyungdo = new ArrayList ();

    boolean bcctv = false; //화면에 cctv 정보 나오지 않는 상태
    boolean bparking = false; //화면에 주차장 정보 나오지 않는 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_cctv = (Button)findViewById(R.id.cctvBu);
        button_parking=(Button)findViewById(R.id.parkingBu);

        button_cctv.setOnClickListener(this);
        button_parking.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: Tmap 생성");
        RelativeLayout RelativeLayoutTmap = findViewById(R.id.map_view);
        tmap = new TMapView(this);
        tmap.setSKTMapApiKey(TMAP_API_KEY);
        RelativeLayoutTmap.addView(tmap);
        tmap.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정합니다.


        Log.d(TAG, "onStart: 마커 정보 생성");

        marker_cctv = new TMapMarkerItem();
        point_cctv = new TMapPoint(37.570841, 126.985302); // SKT타워

        marker_parking = new TMapMarkerItem();
        point_parking = new TMapPoint(37.5574771,127.0020518); // 동국대학교

        xmlPassing(PointWido, 1); // xml에서 위도정보 배열에 저장
        xmlPassing(PointKyungdo, 2); // xml에서 경도정보 배열에 저장

        super.onStart();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: 버튼이 눌렸네요");
        if(v == button_cctv) {
            if(!bcctv){
                Log.d(TAG, "onClick: cctv버튼, false");
                bcctv = true;

                // 마커 아이콘
                marker_cctv.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
                marker_cctv.setTMapPoint( point_cctv ); // 마커의 좌표 지정
                marker_cctv.setName("SKT타워"); // 마커의 타이틀 지정
                tmap.addMarkerItem("marker_cctv", marker_cctv); // 지도에 마커 추가
            }else{
                Log.d(TAG, "onClick: cctv버튼, true");

                bcctv = false;
                tmap.removeMarkerItem("marker_cctv");
            }


        }
        else if(v == button_parking) {
            if(!bparking){
                Log.d(TAG, "onClick: parking버튼, false");
                bparking = true;

                // 마커 아이콘
                marker_parking.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
                marker_parking.setTMapPoint( point_parking ); // 마커의 좌표 지정
                marker_parking.setName("동국대학교"); // 마커의 타이틀 지정
                tmap.addMarkerItem("marker_parking", marker_parking); // 지도에 마커 추가
            }else{
                Log.d(TAG, "onClick: parking버튼, true");

                bparking = false;
                tmap.removeMarkerItem("marker_parking");
            }
        }
    }

    public ArrayList xmlPassing(ArrayList pointList, int number){
        Log.d(TAG, "xmlPassing: xml 파싱준비");
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
}
