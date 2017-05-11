package com.example.lab4.lab4android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by varun on 4/11/2017.
 */



public class signin extends AppCompatActivity {

    ImageView sback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        sback = (ImageView)findViewById(R.id.sinb);
        sback.setOnClickListener((View v)-> {

                Intent it = new Intent(signin.this,main.class);
                startActivity(it);

        });
    }
}
