package com.example.u2project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class MypageActivity extends AppCompatActivity {

    FirebaseFirestore db;
    DatabaseReference mDatabaseRef;
    ImageView btn_report;
    TextView txname;

    public String add_dong;
    public String add_si;
    public String add_gu;
    public String idToken;
    public String age;
    public String bmon;
    public String bday;
    public String byear;
    public String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // Firebase Realtime Database에서 사용자 정보 가져오기
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 사용자의 UID
        String path = "DMC/UserAccount/" + uid; // 사용자 정보가 저장된 경로

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(path);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    add_dong = dataSnapshot.child("add_dong").getValue(String.class);
                    add_si = dataSnapshot.child("add_si").getValue(String.class);
                    add_gu = dataSnapshot.child("add_gu").getValue(String.class);
                    idToken = dataSnapshot.child("idToken").getValue(String.class);
                    age = dataSnapshot.child("age").getValue(String.class);
                    bmon = dataSnapshot.child("bmon").getValue(String.class);
                    bday = dataSnapshot.child("bday").getValue(String.class);
                    byear = dataSnapshot.child("byear").getValue(String.class);
                    name = dataSnapshot.child("name").getValue(String.class);

                    // 사용자 정보를 가져온 후 UI를 업데이트
                    updateUI(0, uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 가져오기에 실패한 경우 처리
            }
        });
    }

    private void updateUI(int choose,String uid) {
        btn_report = findViewById(R.id.btn_report);
        txname = findViewById(R.id.tx_name);

        txname.setText(name+" 님");
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this,TestResult.class);
                startActivity(intent);
                finish();
            }
        });
    }
}