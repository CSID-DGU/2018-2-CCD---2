package dgdg.project.underthec_cctv;

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

public class MainActivity extends Activity{
    String result="";
    String file="서울 CCTV.xml";
    String TAG = "gkgk";

    ArrayList wido = new ArrayList();
    ArrayList kyungdo = new ArrayList();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "위도 파싱");
        xmlPassing(wido,1);

        Log.d(TAG, "경도파싱");
        xmlPassing(kyungdo,2);

        Log.d(TAG, "위도 배열 사이즈  " + wido.size());
        Log.d(TAG, "경도 배열 사이즈  " + kyungdo.size());

        for(int i = 0; i<wido.size(); i++){
            String pwido = (String) wido.get(i);
            double dpwido = Double.valueOf(pwido);
            String pkyungdo = (String) kyungdo.get(i);
            double dpkyungdo = Double.valueOf(pkyungdo);

            Log.d(TAG, "위도경도 짝맞추기  순서 :  " + i + "     " + dpwido + "   " + dpkyungdo);
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
                    }
                }else if(eventType==XmlPullParser.TEXT){
                    if(bSet){
                        String data = xpp.getText();
                        boolean isPoint = false;
                        for(int j=0; j<pointList.size(); j++) {
                            /*if (data.equals(pointList.get(j))) {
                                //Log.d("데이터를 찾자", "" + data );
                                isPoint = true;
                                Log.d("데이터를 찾자", "" + data );
                                break;
                            }*/
                        }
                        if (!isPoint)
                            pointList.add(data);
                    }
                    bSet = false;

                }else if(eventType==XmlPullParser.END_TAG);
                eventType=xpp.next();
            }
            Log.d(TAG, "xmlPassing: 파싱 끝");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return pointList;
    }
}