package com.example.u2project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity {

    ConstraintLayout mypage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ////////// 전체 인덱스 불러오기 및 저장 //////////
        UseData usedata = (UseData) getApplication();
        usedata.setTotalIndex(0);
        usedata.setScore(0);
        //////////////////////////////////////////////

        ImageView btnTest = findViewById(R.id.btn_test);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ImageView (btn_send) 클릭 시 동작할 코드
                Intent intent = new Intent(HomeActivity.this, TestTime.class);
                startActivity(intent);
            }
        });

        mypage = findViewById(R.id.btn_mypage);

        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,MypageActivity.class);
                startActivity(intent);
            }
        });



    }
}