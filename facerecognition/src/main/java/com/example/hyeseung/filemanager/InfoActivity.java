package com.example.hyeseung.filemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.hyeseung.facerecognition.R;

public class InfoActivity extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_info);
    }

    public void onBackPressed() {
        finish();
    }
}
