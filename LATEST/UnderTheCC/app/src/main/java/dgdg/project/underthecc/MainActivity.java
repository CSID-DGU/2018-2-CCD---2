package dgdg.project.underthecc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity_UTC";
    ImageButton button_gps;
    ImageButton button_park;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_gps = findViewById(R.id.button_gps);
        button_park = findViewById(R.id.button_park);

        button_gps.setOnClickListener(this);
        button_park.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==button_gps) {
            Intent intent = new Intent(this, gpsActivity.class);
            startActivity(intent);
        }
        else if(v==button_park) {
            Intent intent = new Intent(this, parkingActivity.class);
            startActivity(intent);
        }
    }
}