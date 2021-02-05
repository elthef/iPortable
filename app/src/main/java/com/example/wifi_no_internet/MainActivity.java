package com.example.wifi_no_internet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * @author Pierre-Vincent Gouel
 *
 * This class is linked to the main activity.
 * Its role is to explains to the user how the app works and lets him choose the next action
 */
public class MainActivity extends AppCompatActivity{

    ImageButton btnopenweigh,openSetting;
    TextView tvexplanations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//Set the buttons and the explanation text
        btnopenweigh = (ImageButton) findViewById(R.id.imgbtn_openweigh);
        openSetting = (ImageButton) findViewById(R.id.imgbtn_settings);
        tvexplanations = (TextView) findViewById(R.id.tv_explain);

        tvexplanations.setMovementMethod(new ScrollingMovementMethod());

//Links buttons to an action, here the action of opening another activity
        btnopenweigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WeighPatient.class);
                MainActivity.this.startActivity(intent);
            }
        });
        openSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,network_mana.class);
                MainActivity.this.startActivity(intent);
            }
        });

    }
}
