package dgdg.project.underthecc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageButton button_gps;
    ImageButton button_park;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_gps=(ImageButton)findViewById(R.id.imageButton1);
        button_park=(ImageButton)findViewById(R.id.imageButton2);

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