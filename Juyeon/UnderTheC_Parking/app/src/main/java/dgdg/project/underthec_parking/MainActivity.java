package dgdg.project.underthec_parking;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


/*
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv=(TextView)findViewById(R.id.data);
        String file="서울특별시_주차장정보.xml";
        String result="";
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
            while(eventType!=XmlPullParser.END_DOCUMENT){
                if(eventType== XmlPullParser.START_TAG){
                    String tag_name=xpp.getName();
                    if(tag_name.equals("주차장명")|tag_name.equals("소재지도로명주소")|tag_name.equals("주차장구분")|tag_name.equals("요금정보")|tag_name.equals("운영요일")|tag_name.equals("주차기본요금")|tag_name.equals("주차기본시간")|tag_name.equals("전화번호")|tag_name.equals("위도")|tag_name.equals("경도"))
                        bSet=true;
                }else if(eventType==XmlPullParser.TEXT){
                    if(bSet){
                        String data=xpp.getText();
                        tv.append(data+"\n");
                        bSet=false;
                    }
                }else if(eventType==XmlPullParser.END_TAG);
                eventType=xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    */



    public class MainActivity extends Activity{
        String result="";
        String file="서울특별시_주차장정보.xml";
        TextView textView;
        String TAG = "gkgk";

        ArrayList wido = new ArrayList();
        ArrayList kyungdo = new ArrayList();
        ArrayList parkingname = new ArrayList();
        ArrayList address = new ArrayList();
        ArrayList parkingclass = new ArrayList();
        ArrayList feeinfo = new ArrayList();
        ArrayList day = new ArrayList();
        ArrayList fee = new ArrayList();
        ArrayList time = new ArrayList();
        ArrayList phone = new ArrayList();

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            TextView tv=(TextView)findViewById(R.id.data);
            xmlPassing(wido,1);
            xmlPassing(kyungdo,2);
            xmlPassing(parkingname, 3);
            xmlPassing(address, 4);
            xmlPassing(parkingclass, 5);
            xmlPassing(feeinfo, 6);
            xmlPassing(day, 7);
            xmlPassing(fee, 8);
            xmlPassing(time, 9);
            xmlPassing(phone, 10);
            Log.d(TAG, "파싱되니");

            Log.d(TAG, "" + wido.size());
            Log.d(TAG, "" + kyungdo.size());
            Log.d(TAG, "" + parkingname.size());
            Log.d(TAG, "" + address.size());
            Log.d(TAG, "" + parkingclass.size());
            Log.d(TAG, ""+feeinfo.size());
            Log.d(TAG, "" + day.size());
            Log.d(TAG, "" + fee.size());
            Log.d(TAG, "" + time.size());
            Log.d(TAG, "" + phone.size());

            for(int i = 0; i<wido.size(); i++){

                String pwido = (String) wido.get(i);
                double dpwido = Double.valueOf(pwido);
                String pkyungdo = (String) kyungdo.get(i);
                double dpkyungdo = Double.valueOf(pkyungdo);
                String pname = (String) parkingname.get(i);
                String paddress = (String) address.get(i);
                String pclass = (String) parkingclass.get(i);
                String pfeeinfo = (String) feeinfo.get(i);
                String pday = (String) day.get(i);
                String pfee = (String) fee.get(i);
                double dpfee = Double.valueOf(pfee);
                String ptime = (String) time.get(i);
                double dptime = Double.valueOf(ptime);
                String pphone = (String) phone.get(i);
                Log.d(TAG, "돌고있니");

                tv.setText("번호 : " + i + "\n" + dpwido + "\n" + dpkyungdo + "\n" + pname + "\n" + paddress + "\n" + pclass + "\n"
                + pfeeinfo + "\n" + pday + "\n" + dpfee + "\n" + dptime + "\n" + pphone + "\n");

                Log.d(TAG, "찍니");
            }
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
                            if(tag_name.equals("소재지도로명주소") )
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
                            if(tag_name.equals("전화번호") )
                                bSet=true;
                            break;
                    }
                }else if(eventType==XmlPullParser.TEXT){
                    if(bSet){
                        String data = xpp.getText();
                        boolean isPoint = false;
                        for(int j=0; j<pointList.size(); j++) {
                            /*if (data.equals(pointList.get(j))) {
                                Log.d("데이터를 찾자", "" + data);
                                isPoint = true;
                                break;
                            }
                            */
                        }
                        if (!isPoint)
                            pointList.add(data);

                    }
                    bSet = false;

                }else if(eventType==XmlPullParser.END_TAG);
                eventType=xpp.next();
            }
            Log.d(TAG, "xmlPassing: 위도, 경도, 주차장명 값 받기 끝");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return pointList;
    }

}