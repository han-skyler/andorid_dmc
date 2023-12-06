package com.example.u2project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ImageView mypage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////// 전체 인덱스 불러오기 및 저장 //////////
        UseData usedata = (UseData) getApplication();
        usedata.setTotalIndex(0);
        usedata.setScore(0);
        //////////////////////////////////////////////

        ImageView btnSend = findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ImageView (btn_send) 클릭 시 동작할 코드
                Intent intent = new Intent(MainActivity.this, TestTime.class);
                startActivity(intent);
            }
        });

        mypage = findViewById(R.id.btn_profile);

        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MypageActivity.class);
                startActivity(intent);
            }
        });



    }


}