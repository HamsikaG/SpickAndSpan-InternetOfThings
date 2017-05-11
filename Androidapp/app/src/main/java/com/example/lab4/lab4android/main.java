package com.example.lab4.lab4android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by varun on 4/11/2017.
 */


public class main extends AppCompatActivity {
    TextView sin;
    LinearLayout circle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circle = (LinearLayout)findViewById(R.id.circle);
        sin = (TextView)findViewById(R.id.sin);

        circle.setOnClickListener((View v)-> {

            Intent it = new Intent(main.this, signup.class);
            startActivity(it);
        });

        sin.setOnClickListener((View v) ->{

                Intent it = new Intent(main.this,signin.class);
                startActivity(it);

        });

    }
}
