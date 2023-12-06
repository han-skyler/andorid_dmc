package com.example.u2project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TestMem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mem);
        ////////// 전체 인덱스 불러오기 및 저장 //////////
        Intent intent = getIntent();

        int score = intent.getIntExtra("score",0);
        int totalIdx = intent.getIntExtra("totalIdx",7);

        Log.e("가져온 점수 : ",Integer.toString(score));
        Log.e("가져온 인덱스 : ",Integer.toString(totalIdx));
        //////////////////////////////////////////////

        // 버튼(btn_next) 초기화
        Button btnNext = findViewById(R.id.btn_next);

        // 버튼을 처음에 비활성화
        btnNext.setEnabled(false);

        // 15초 후에 버튼 활성화
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnNext.setEnabled(true);
            }
        }, 3000);  // 3초를 밀리초 단위로 설정

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestMem.this, TestCal.class);
                intent.putExtra("score",score);
                intent.putExtra("totalIdx",totalIdx);
                startActivity(intent);
                finish();
            }
        });
    }
}