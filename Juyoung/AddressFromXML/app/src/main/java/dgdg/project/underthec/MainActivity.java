package dgdg.project.underthec;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv=(TextView)findViewById(R.id.data);
        String file="서울특별시_중구_CCTV_20181101.xml";
        String result="";
        final ArrayList PointWido = new ArrayList();

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
                    if(tag_name.equals("소재지도로명주소"))/*|tag_name.equals("소재지지번주소")|tag_name.equals("위도"))/*|tag_name.equals("경도")*/
                        bSet=true;
                }else if(eventType==XmlPullParser.TEXT){
                    if(bSet){
                        String data = xpp.getText();
                        boolean isPoint = false;
                        for(int j=0; j<PointWido.size(); j++) {
                            if (data.equals(PointWido.get(j))) {
                                isPoint = true;
                                break;
                            }
                        }
                        if (!isPoint)
                                PointWido.add(data);
                    }
                        bSet = false;

                }else if(eventType==XmlPullParser.END_TAG);
                eventType=xpp.next();
            }

            // 출력
            for(int i=0; i<PointWido.size(); i++) {
                String data = (String) PointWido.get(i);
                tv.append(data + "   " + i + "번째\n" );
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
