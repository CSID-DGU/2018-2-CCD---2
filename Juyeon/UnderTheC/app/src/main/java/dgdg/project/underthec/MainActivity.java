package dgdg.project.underthec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button btn;
    TextView result;

    //JSON 형식의 String 상수.
    final String json = "[{\"id\":\"키즈베어\",\"tel\":\"010-1111-2222\"}," +
            "{\"id\":\"김꽃드래\",\"tel\":\"010-3333-4444\"}," +
            "{\"id\":\"민식이냐\",\"tel\":\"010-5555-6666\"}]" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.btn);
        result = (TextView)findViewById(R.id.result);

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParser();
            }
        });
    }

    private void jsonParser(){

        String resultStr = "";

        try {
            //JSON String으로 부터 JSONArray 생성. [](대괄호)
            JSONArray jArr = new JSONArray(json);

            for (int i = 0; i < jArr.length(); i++) {
                //JSONArray에서 i번째 해당하는 JSONObject를 추출.
                JSONObject jObj = jArr.getJSONObject(i);

                //각 이름("id"/"tel")에 해당하는 값을 추출.
                resultStr += String.format("아이디 : %s   전화번호 : %s\n",
                        jObj.getString("id"), jObj.getString("tel"));
            }
            result.setText(resultStr);
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}