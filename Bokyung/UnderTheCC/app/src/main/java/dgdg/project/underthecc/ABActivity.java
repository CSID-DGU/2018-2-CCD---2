package dgdg.project.underthecc;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ABActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ab);
        ActionBar actionBar = getActionBar();
        /*
        if(actionBar !=null){
            actionBar.setHomeAsUpIndicator(R.drawable.homeb);
            }
            */
    }
}