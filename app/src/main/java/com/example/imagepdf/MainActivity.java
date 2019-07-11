package com.example.imagepdf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
LinearLayout ll_createpdf,ll_mergepdf,ll_savedpdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/

        ll_createpdf=findViewById(R.id.ll_createpdf);
        ll_mergepdf=findViewById(R.id.ll_mergepdf);
        ll_savedpdf=findViewById(R.id.ll_savedpdf);

        ll_createpdf.setOnClickListener(this);
        ll_mergepdf.setOnClickListener(this);
        ll_savedpdf.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_createpdf:
                startActivity(new Intent(MainActivity.this,CreatePdfActivity.class));
                break;

            case R.id.ll_mergepdf:
                break;

            case R.id.ll_savedpdf:
                break;
        }
    }
}