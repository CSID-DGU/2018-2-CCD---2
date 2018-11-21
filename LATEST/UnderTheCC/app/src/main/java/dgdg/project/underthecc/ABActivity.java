package dgdg.project.underthecc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ABActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ab);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //xml 파일 불러오기
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch(id) {
            case R.id.action_main:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }
}
